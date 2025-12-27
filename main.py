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


# ===== Table screens (メタデータ管理) =====
def search_tables(kc: str, sys: str = "", shu: str = "", tblnm: str = "", tbljnm: str = "") -> list[dict]:
    """
    ZM_TBL からテーブル定義一覧を検索
    検索条件: sys(システム区分), shu(種別), tblnm(テーブルID), tbljnm(テーブル日本語名)
    """
    if pool is None:
        return []
    
    sql = "SELECT TBLNM, TBLJNM, SYS, SHU FROM ZM_TBL WHERE KC = :kc"
    params = {"kc": kc}
    
    if sys:
        sql += " AND SYS = :sys"
        params["sys"] = sys
    if shu:
        sql += " AND SHU = :shu"
        params["shu"] = shu
    if tblnm:
        sql += " AND TBLNM LIKE :tblnm"
        params["tblnm"] = f"%{tblnm}%"
    if tbljnm:
        sql += " AND TBLJNM LIKE :tbljnm"
        params["tbljnm"] = f"%{tbljnm}%"
    
    sql += " ORDER BY TBLNM"
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql, params)
                rows = cur.fetchall()
        return [
            {"tblnm": row[0], "tbljnm": row[1], "sys": row[2], "shu": row[3]}
            for row in rows
        ]
    except Exception as e:
        logger.error("search_tables error: %s", e)
        return []


def get_table_definition(kc: str, tblnm: str) -> dict | None:
    """ZM_TBL から指定テーブルの定義を取得"""
    if pool is None:
        return None
    
    sql = "SELECT TBLNM, TBLJNM, SYS, SHU, UPCNT FROM ZM_TBL WHERE KC = :kc AND TBLNM = :tblnm"
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql, {"kc": kc, "tblnm": tblnm})
                row = cur.fetchone()
        if row:
            return {"tblnm": row[0], "tbljnm": row[1], "sys": row[2], "shu": row[3], "upcnt": row[4]}
        return None
    except Exception as e:
        logger.error("get_table_definition error: %s", e)
        return None


def get_column_definitions(kc: str, tblnm: str) -> list[dict]:
    """
    ZM_TBLITM から指定テーブルの項目定義一覧を取得
    """
    if pool is None:
        return []
    
    sql = """
    SELECT TBLNM, TBLNO, RNM, BNM, KATA, LNG1, LNG2, HSU, DFLT, TKEY01, BIKO, UPCNT
    FROM ZM_TBLITM
    WHERE KC = :kc AND TBLNM = :tblnm
    ORDER BY TBLNO
    """
    
    try:
        with pool.acquire() as conn:
            with conn.cursor() as cur:
                cur.execute(sql, {"kc": kc, "tblnm": tblnm})
                rows = cur.fetchall()
        return [
            {
                "tblnm": row[0],
                "tblno": row[1],
                "rnm": row[2],
                "bnm": row[3],
                "kata": row[4],
                "lng1": row[5],
                "lng2": row[6],
                "hsu": row[7],
                "dflt": row[8],
                "tkey01": row[9],
                "biko": row[10],
                "upcnt": row[11],
            }
            for row in rows
        ]
    except Exception as e:
        logger.error("get_column_definitions error: %s", e)
        return []


def get_kata_display(kata: str) -> str:
    """型コードを表示用文字列に変換"""
    kata_map = {"9": "数値", "X": "文字", "V": "可変長", "T": "日時"}
    return kata_map.get(kata, kata or "")


@app.get("/table/list", response_class=HTMLResponse)
def table_list(
    request: Request,
    user: dict = Depends(get_current_user),
    sys: str = "",
    shu: str = "",
    tblnm: str = "",
    tbljnm: str = "",
):
    """
    テーブル一覧画面 (Z101相当)
    ZM_TBL からテーブル定義を検索・表示
    """
    kc = user.get("kc", "")
    tables = search_tables(kc, sys=sys, shu=shu, tblnm=tblnm, tbljnm=tbljnm)
    
    return templates.TemplateResponse(
        "table_list.html",
        {
            "request": request,
            "user": user,
            "tables": tables,
            "search_sys": sys,
            "search_shu": shu,
            "search_tblnm": tblnm,
            "search_tbljnm": tbljnm,
        },
    )


@app.get("/table/maintenance", response_class=HTMLResponse)
def table_maintenance(
    request: Request,
    user: dict = Depends(get_current_user),
    tblnm: str = "",
):
    """
    テーブルメンテナンス画面 (Z102相当)
    ZM_TBLITM から項目定義を表示・編集
    """
    kc = user.get("kc", "")
    table_def: dict | None = None
    columns: list[dict] = []
    
    if tblnm:
        table_def = get_table_definition(kc, tblnm)
        if table_def:
            columns = get_column_definitions(kc, tblnm)
    
    return templates.TemplateResponse(
        "table_maintenance.html",
        {
            "request": request,
            "user": user,
            "tblnm": tblnm,
            "table_def": table_def,
            "columns": columns,
            "get_kata_display": get_kata_display,
        },
    )
