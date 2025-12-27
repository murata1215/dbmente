# FastAPI DB Maintenance Tool (DBメンテ)

FastAPI + Oracle を使った社内向け DB メンテナンス Web アプリ。

## Features
- ログイン認証（ZI_USER テーブル）
- メインメニュー画面
- テーブル一覧画面
- テーブルメンテナンス画面
- Oracle 接続（python-oracledb / Thin mode）
- Windows サービス（NSSM）対応

## Screen Flow (画面フロー)

```
/login (ログイン画面)
    ↓ ログイン成功
/menu (メインメニュー)
    ├── /table/list (テーブル一覧)
    │       └── /table/maintenance?table_name=XXX (テーブルメンテナンス)
    └── /table/maintenance (テーブルメンテナンス)
```

認証が必要な画面（/menu, /table/*, /work）に未ログインでアクセスすると /login へリダイレクトされます。

## Requirements
- Python 3.12 以上
- Oracle（接続先）
- Windows 10/11 または Linux

## Setup
```bash
# Windows
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt

# Linux/Mac
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Environment Variables
.env ファイルを作成してください（値は環境に応じて設定）。

```
ORACLE_USER=your_oracle_user
ORACLE_PASS=your_oracle_password
ORACLE_DSN=your_oracle_dsn
SESSION_SECRET=your_random_secret_key
```

## Run (Development)
```bash
python -m uvicorn main:app --host 127.0.0.1 --port 8000 --reload
```

## Run (Production / Service)
```bash
# Windows: NSSM でサービス登録
nssm install dbmente "C:\path\to\.venv\Scripts\python.exe" "-m uvicorn main:app --host 0.0.0.0 --port 8000"

# Linux: systemd または直接実行
python -m uvicorn main:app --host 0.0.0.0 --port 8000
```

## Access
- 開発環境: http://127.0.0.1:8000/
- ログイン画面: http://127.0.0.1:8000/login

## Logging
ログイン成功/失敗は標準出力にログ出力されます（パスワードは出力されません）。

```
2025-01-01 12:00:00 [INFO] Login success: KC=XX, SYCD=XXXXX, SYMEI=氏名
2025-01-01 12:00:00 [WARNING] Login failed: KC=XX, SYCD=XXXXX
```

## Reference Java Code (Read-only)

This directory contains legacy Java (JBoss/Seam) code.
It is provided **for reference only**.

Purpose:
- Understand screen flow after login
- Understand menu structure and authorization logic
- Reproduce similar behavior in FastAPI

Rules:
- Do NOT modify these files
- Do NOT port Java code line-by-line
- Use this only as a functional and structural reference

