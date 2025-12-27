import logging
import os
from dotenv import load_dotenv

import oracledb
from fastapi import FastAPI, Request, Form, Depends
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from starlette.middleware.sessions import SessionMiddleware

# ===== logging =====
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger(__name__)


# ===== env =====
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
load_dotenv(os.path.join(BASE_DIR, ".env"))

ORACLE_USER = os.getenv("ORACLE_USER")
ORACLE_PASS = os.getenv("ORACLE_PASS")
ORACLE_DSN  = os.getenv("ORACLE_DSN")
SESSION_SECRET = os.getenv("SESSION_SECRET", "dev-secret-change-me")

# ===== app =====
app = FastAPI()
app.add_middleware(
    SessionMiddleware,
    secret_key=SESSION_SECRET,
    same_site="lax",
    https_only=False,  # https運用なら True 推奨
)

templates = Jinja2Templates(directory=os.path.join(BASE_DIR, "templates"))

pool: oracledb.ConnectionPool | None = None


@app.on_event("startup")
def startup():
    global pool
    pool = oracledb.create_pool(
        user=ORACLE_USER,
        password=ORACLE_PASS,
        dsn=ORACLE_DSN,
        min=1,
        max=4,
        increment=1,
        getmode=oracledb.SPOOL_ATTRVAL_WAIT,
    )


@app.on_event("shutdown")
def shutdown():
    global pool
    if pool:
        pool.close()
        pool = None


def get_current_user(request: Request) -> dict:
    """
    認証済みユーザーを取得する依存関数。
    未ログインの場合は /login へリダイレクト。
    FastAPI の Depends() で使用する。
    """
    user = request.session.get("user")
    if not user:
        raise RedirectResponse(url="/login", status_code=303)
    return user


def verify_user(kc: str, sycd: str, password: str) -> tuple[bool, dict]:
    """
    ZI_USER で会社コード(KC) + ユーザーID(SYCD) + パスワード(PASS) を検証
    CHARの空白埋め対策で TRIM 比較。
    """
    assert pool is not None

    sql = """
    SELECT
        TRIM(KC)   AS KC,
        TRIM(SYCD) AS SYCD,
        TRIM(SYMEI) AS SYMEI
    FROM ZI_USER
    WHERE TRIM(KC) = :kc
      AND TRIM(SYCD) = :sycd
      AND TRIM(PASS) = :pass
    """

    with pool.acquire() as conn:
        with conn.cursor() as cur:
            cur.execute(sql, {"kc": kc, "sycd": sycd, "pass": password})
            row = cur.fetchone()

    if not row:
        return False, {}

    user = {"kc": row[0], "sycd": row[1], "symei": row[2] or ""}
    return True, user


@app.get("/", response_class=HTMLResponse)
def root(request: Request):
    # ログイン済みなら /menu へ、未ログインなら /login へ
    if request.session.get("user"):
        return RedirectResponse(url="/menu", status_code=303)
    return RedirectResponse(url="/login", status_code=303)


# ===== Login =====
@app.get("/login", response_class=HTMLResponse)
def login_page(request: Request):
    return templates.TemplateResponse("login.html", {"request": request, "error": ""})


@app.post("/login")
def login_submit(
    request: Request,
    kc: str = Form(...),
    sycd: str = Form(...),
    password: str = Form(...),
):
    kc = kc.strip()
    sycd = sycd.strip()
    password = password.strip()

    ok, user = verify_user(kc, sycd, password)
    if not ok:
        logger.warning("Login failed: KC=%s, SYCD=%s", kc, sycd)
        return templates.TemplateResponse(
            "login.html",
            {"request": request, "error": "会社コード / ユーザーID / パスワードが正しくありません。"},
            status_code=401,
        )

    # セッションに保存
    request.session["user"] = user
    logger.info("Login success: KC=%s, SYCD=%s, SYMEI=%s", user["kc"], user["sycd"], user["symei"])

    return RedirectResponse(url="/menu", status_code=303)


@app.post("/logout")
def logout(request: Request):
    request.session.clear()
    return RedirectResponse(url="/login", status_code=303)


# ===== After login pages =====
@app.get("/home", response_class=HTMLResponse)
def home(request: Request):
    # /home は廃止、/menu へリダイレクト
    return RedirectResponse(url="/menu", status_code=303)


@app.get("/menu", response_class=HTMLResponse)
def menu(request: Request, user: dict = Depends(get_current_user)):
    menu_sections = [
        {
            "title": "テーブル照会",
            "items": [
                {"type": "link", "label": "テーブル一覧", "url": "/table/list"},
            ],
        },
        {
            "title": "テーブルメンテ",
            "items": [
                {"type": "link", "label": "テーブルメンテナンス", "url": "/table/maintenance"},
            ],
        },
    ]

    return templates.TemplateResponse(
        "menu.html",
        {
            "request": request,
            "user": user,
            "title": "メインメニュー",
            "menu_sections": menu_sections,
        },
    )


@app.get("/work", response_class=HTMLResponse)
def work(request: Request, user: dict = Depends(get_current_user)):
    return templates.TemplateResponse("work.html", {"request": request, "user": user})


# ===== Table screens =====
def get_all_tables() -> list[dict]:
    """Oracle内の全テーブル一覧を取得"""
    if pool is None:
        return []
    
    sql = """
    SELECT TABLE_NAME, NUM_ROWS, LAST_ANALYZED
    FROM USER_TABLES
    ORDER BY TABLE_NAME
    """
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql)
                rows = cur.fetchall()
        return [
            {"table_name": row[0], "num_rows": row[1] or 0, "last_analyzed": row[2]}
            for row in rows
        ]
    except Exception:
        return []


def get_table_columns(table_name: str) -> list[dict]:
    """指定テーブルのカラム情報を取得"""
    if pool is None:
        return []
    
    sql = """
    SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = :table_name
    ORDER BY COLUMN_ID
    """
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql, {"table_name": table_name.upper()})
                rows = cur.fetchall()
        return [
            {"column_name": row[0], "data_type": row[1], "data_length": row[2], "nullable": row[3]}
            for row in rows
        ]
    except Exception:
        return []


def get_table_data(table_name: str, limit: int = 100) -> tuple[list[str], list[list]]:
    """指定テーブルのデータを取得 (最大limit件)"""
    if pool is None:
        return [], []
    
    columns = get_table_columns(table_name)
    if not columns:
        return [], []
    
    column_names = [c["column_name"] for c in columns]
    sql = f"SELECT * FROM {table_name} WHERE ROWNUM <= :limit"
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql, {"limit": limit})
                rows = cur.fetchall()
        return column_names, [list(row) for row in rows]
    except Exception:
        return column_names, []


@app.get("/table/list", response_class=HTMLResponse)
def table_list(request: Request, user: dict = Depends(get_current_user)):
    """テーブル一覧画面"""
    tables = get_all_tables()
    
    return templates.TemplateResponse(
        "table_list.html",
        {
            "request": request,
            "user": user,
            "tables": tables,
        },
    )


@app.get("/table/maintenance", response_class=HTMLResponse)
def table_maintenance(request: Request, table_name: str = "", user: dict = Depends(get_current_user)):
    """テーブルメンテナンス画面"""
    tables = get_all_tables()
    columns: list[dict] = []
    column_names: list[str] = []
    rows: list[list] = []
    
    if table_name:
        columns = get_table_columns(table_name)
        column_names, rows = get_table_data(table_name)
    
    return templates.TemplateResponse(
        "table_maintenance.html",
        {
            "request": request,
            "user": user,
            "tables": tables,
            "selected_table": table_name,
            "columns": columns,
            "column_names": column_names,
            "rows": rows,
        },
    )
