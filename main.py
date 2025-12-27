import os
from dotenv import load_dotenv

import oracledb
from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from starlette.middleware.sessions import SessionMiddleware


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


def require_login(request: Request):
    """未ログインなら /login へ飛ばす（簡易ガード）"""
    user = request.session.get("user")
    if not user:
        raise RedirectResponse(url="/login", status_code=303)


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
    # ログイン後トップへ誘導
    if request.session.get("user"):
        return RedirectResponse(url="/home", status_code=303)
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
        return templates.TemplateResponse(
            "login.html",
            {"request": request, "error": "会社コード / ユーザーID / パスワードが正しくありません。"},
            status_code=401,
        )

    # セッションに保存（超簡易）
    request.session["user"] = user

    return RedirectResponse(url="/home", status_code=303)


@app.post("/logout")
def logout(request: Request):
    request.session.clear()
    return RedirectResponse(url="/login", status_code=303)


# ===== After login pages =====
@app.get("/home", response_class=HTMLResponse)
def home(request: Request):
    if not request.session.get("user"):
        return RedirectResponse(url="/login", status_code=303)
    return templates.TemplateResponse("home.html", {"request": request, "user": request.session["user"]})


@app.get("/menu", response_class=HTMLResponse)
def menu(request: Request):
    if not request.session.get("user"):
        return RedirectResponse(url="/login", status_code=303)
    return templates.TemplateResponse("menu.html", {"request": request, "user": request.session["user"]})


@app.get("/work", response_class=HTMLResponse)
def work(request: Request):
    if not request.session.get("user"):
        return RedirectResponse(url="/login", status_code=303)
    return templates.TemplateResponse("work.html", {"request": request, "user": request.session["user"]})
