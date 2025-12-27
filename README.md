# FastAPI DB Maintenance Tool

FastAPI + Oracle を使った社内向け DB メンテナンス Web アプリ。

## Features
- ログイン認証（ZI_USER）
- メニュー画面
- 業務画面（今後拡張予定）
- Oracle 接続（python-oracledb / Thin mode）
- Windows サービス（NSSM）

## Requirements
- Python 3.12 以上
- Oracle（接続先）
- Windows 10/11

## Setup
```bash
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

## Environment Variables
.env を作成してください（値は環境に応じて設定）。

ORACLE_USER=
ORACLE_PASS=
ORACLE_DSN=
SESSION_SECRET=

## Run (Development)
```bash
python -m uvicorn main:app --host 127.0.0.1 --port 8000 --reload
```

## Run (Service)
- NSSM で python -m uvicorn main:app --host 0.0.0.0 --port 8000 をサービス登録

## Access
- http://127.0.0.1:8000/login

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

