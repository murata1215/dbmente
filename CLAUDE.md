# DBmente - テーブル定義管理システム

## 概要

JBoss Seamで作られた旧dbmenteプロジェクトをPHPで作り直したもの。
開発に利用するテーブル定義を登録・管理し、CREATE SQLやCSV出力ができるシステム。

## 技術スタック

- **言語**: PHP（フレームワークなし、素のPHP）
- **データベース**: Oracle
- **文字コード**: UTF-8

## ディレクトリ構成

```
dbmente/
├── CLAUDE.md              # このファイル
├── reference/             # 旧JBoss Seamプロジェクト（参照用）
├── migration/             # データ移行用スクリプト
│   ├── import_tbl.php     # ZM_TBL移行スクリプト
│   ├── import_tblitm.php  # ZM_TBLITM移行スクリプト
│   └── zm_tblitm.ctl      # SQL*Loader用コントロールファイル
└── src/
    ├── login.php          # ログイン画面
    ├── logout.php         # ログアウト処理
    ├── table_list.php     # テーブル照会（検索・一覧）
    ├── table_detail.php   # テーブル詳細（編集可能）
    ├── table_save.php     # テーブル保存処理（AJAX）
    ├── csv_download.php   # CSV出力
    ├── sql_download.php   # CREATE SQL出力
    ├── includes/
    │   ├── config.php     # 設定ファイル
    │   ├── Database.php   # Oracle DB接続クラス
    │   └── auth.php       # 認証チェック
    └── templates/
        ├── header.php     # 共通ヘッダー（ユーザー情報表示）
        └── footer.php     # 共通フッター
```

## データベース

### テーブル構成

- **ZM_TBL** - テーブルマスタ
  - KC: 会社コード（固定: 85）
  - TBLNM: テーブル名（PK）
  - TBLJNM: テーブル日本語名
  - SYS: システム区分（テーブル名の1文字目）
  - SHU: 種別（テーブル名の2文字目）
  - UPCNT: 更新回数
  - UPDTIME: 更新日時

- **ZM_TBLITM** - テーブル項目マスタ
  - KC: 会社コード（固定: 85）
  - TBLNM: テーブル名（PK）
  - TBLNO: 項目NO（PK）
  - BNM: 物理名
  - RNM: 論理名
  - KATA: 型（9=NUMBER, X=VARCHAR2, V=VARCHAR2, T=TIMESTAMP）
  - LNG1: 長さ1
  - LNG2: 長さ2
  - HSU: 必須フラグ（1=必須→NOT NULL）
  - DFLT: デフォルト値
  - TKEY01: PKフラグ（1=PK→PRIMARY KEY）
  - BIKO: 備考
  - UPCNT: 更新回数
  - UPDTIME: 更新日時

- **ZI_USER** - ユーザーマスタ（ログイン用）
  - KC: 会社コード
  - SYCD: ユーザーID
  - SYMEI: ユーザー名
  - PASS: パスワード

### 会社コード（KC）

全てのSQLで `KC = '85'` を固定で使用。
config.phpで定数定義: `define('KC', '85');`

## 機能

### ログイン（login.php）

- ZI_USERテーブルで認証
- SYCD（ユーザーID）とPASS（パスワード）で認証
- ログイン後はセッションにユーザー情報を保持

### テーブル照会（table_list.php）

- 検索条件: テーブルID、テーブル名、項目ID、項目名
- 項目ID/名で検索するとカラム一覧表示モードになる
- ページング対応（50件ずつ）
- ラジオボタンで選択してCSV/SQL出力
- 新規作成ボタン

### テーブル詳細（table_detail.php）

- テーブル情報の編集（テーブル名、日本語名）
- 項目一覧の編集
  - 全項目が入力可能
  - 必須(HSU)、PK(TKEY01)はチェックボックス
  - 行選択用チェックボックス（左側）
  - 行削除、上に一行追加、下に一行追加ボタン
- 新規モード: CSV取り込み機能
- 保存ボタンでAJAX保存

### CSV出力（csv_download.php）

- 選択したテーブルの項目一覧をCSV出力
- BOM付きUTF-8
- 必須(HSU)=1、PK(TKEY01)=1を明示的に出力

### CSV取り込み（table_detail.php - 新規モードのみ）

- CSVフォーマット: No, 物理名, 論理名, 型, 長さ1, 長さ2, 必須, デフォルト, PK, 備考
- Noは自動採番（CSVの値は無視）
- 必須=1、PK=1でチェックON

### SQL出力（sql_download.php）

生成されるSQL形式:
```sql
--テーブル日本語名
drop table TBLNAME
;
drop index TBLNAME_PK
;

create table TBLNAME(
  COL1 NUMBER (14)
 ,COL2 VARCHAR2(7) NOT NULL
 ,COL3 VARCHAR2(3) NOT NULL
 ,UPDTIME TIMESTAMP NOT NULL
 ,constraint TBLNAME_PKC primary key (COL1, COL2, COL3)
)
;

DROP SEQUENCE TBLNAME_ID_SEQ
;

CREATE SEQUENCE TBLNAME_ID_SEQ
 START WITH 1 INCREMENT BY 1
 MAXVALUE 99999999999999999999999999999999999999 MINVALUE 1 NOCYCLE NOCACHE
;

create unique index TBLNAME_pk on
 TBLNAME(COL1,COL2,COL3)
;
```

**データ型変換ルール:**
- 9, NUMBER → NUMBER
- V, X, VARCHAR, VARCHAR2 → VARCHAR2
- T, TIMESTAMP → TIMESTAMP
- CHAR → CHAR
- DATE → DATE
- CLOB → CLOB
- BLOB → BLOB

**制約ルール:**
- HSU=1 → NOT NULL
- TKEY01=1 → PRIMARY KEY（複数可）
- UNIQUE INDEX: TKEY01=1の全カラム

### 更新ログ（table_save.php）

保存時にPHP error_log()で更新ログを出力:
```
========================================
[2026-01-02 10:30:00] User: 97010 (村田　圭助)
Action: UPDATE
Table: AI_TEST
----------------------------------------
[BEFORE]
  TBLNM: AI_TEST
  TBLJNM: テストテーブル
  Items: 34 columns
    - AI_TESTID (テストID) 9(14)
    ...
----------------------------------------
[AFTER]
  TBLNM: AI_TEST
  TBLJNM: テストテーブル
  Items: 35 columns
    ...
========================================
```

Action種別: CREATE, UPDATE, RENAME

## データ移行

### migration/import_tblitm.php

ZM_TBLITMテーブルへのCSVインポート:
```bash
php import_tblitm.php zm_tblitm.csv
```

### migration/import_tbl.php

ZM_TBLテーブルへのCSVインポート:
```bash
php import_tbl.php zm_tbl.csv
```

**データクレンジング:**
- `« NULL »`, `<NULL>`, `NULL` → 空文字に置換
- 全項目TRIM処理
- `0000-00-00 00:00:00` → 現在日時に置換

## 設定

### Database.php

現在は直接接続情報を記述:
```php
$user = 'dbmente';
$pass = 'dbmente';
$dsn  = '10.214.6.60:1521/lafitdb';
```

## 注意事項

- DBのTBLNMがCHAR型で固定長のため、検索時は `TRIM(TBLNM)` を使用
- 全ての値は表示・出力時にtrim()を適用
- 保存時はUPCNT=0を明示的に指定（NOT NULL制約対応）
- 新規テーブル作成時、SYS=テーブル名1文字目、SHU=テーブル名2文字目

## 開発経緯

1. JBoss Seam版dbmenteをreferenceフォルダに配置
2. PHP + Oracleでテーブル照会機能を作成
3. 検索条件からシステム区分・種別を削除（シンプル化）
4. SQL出力フォーマットをカスタマイズ（DROP TABLE/INDEX、SEQUENCE、UNIQUE INDEX追加）
5. テーブル詳細を編集可能に修正
6. KC（会社コード）=85を全SQLに追加
7. 新規テーブル作成機能を追加
8. CSV取り込み機能を追加（新規テーブル用）
9. CSV出力時、必須=1/PK=1を明示的に出力
10. 新規作成時SYS/SHU自動設定（テーブル名の1-2文字目）
11. 型変換にV→VARCHAR2を追加
12. SQL出力のPRIMARY KEY/UNIQUE INDEXをTKEY01ベースに変更
13. ログイン機能追加（ZI_USERテーブル認証）
14. 更新ログ機能追加（error_log出力）
15. データ移行スクリプト作成（import_tbl.php, import_tblitm.php）
