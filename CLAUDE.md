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
├── .env                   # 環境設定（git管理外）
├── .env.example           # 環境設定サンプル
├── reference/             # 旧JBoss Seamプロジェクト（参照用）
├── sql/
│   └── create_tables.sql  # テーブル作成SQL
└── src/
    ├── index.php          # トップ（table_listへリダイレクト）
    ├── table_list.php     # テーブル照会（検索・一覧）
    ├── table_detail.php   # テーブル詳細（編集可能）
    ├── table_save.php     # テーブル保存処理（AJAX）
    ├── csv_download.php   # CSV出力
    ├── sql_download.php   # CREATE SQL出力
    ├── includes/
    │   ├── config.php     # 設定ファイル
    │   └── Database.php   # Oracle DB接続クラス
    └── templates/
        ├── header.php     # 共通ヘッダー
        └── footer.php     # 共通フッター
```

## データベース

### テーブル構成

- **ZM_TBL** - テーブルマスタ
  - KC: 会社コード（固定: 85）
  - TBLNM: テーブル名（PK）
  - TBLJNM: テーブル日本語名
  - SYS: システム区分
  - SHU: 種別
  - UPCNT: 更新回数
  - UPDTIME: 更新日時

- **ZM_TBLITM** - テーブル項目マスタ
  - KC: 会社コード（固定: 85）
  - TBLNM: テーブル名（PK）
  - TBLNO: 項目NO（PK）
  - BNM: 物理名
  - RNM: 論理名
  - KATA: 型（9=NUMBER, X=VARCHAR2, T=TIMESTAMP）
  - LNG1: 長さ1
  - LNG2: 長さ2
  - HSU: 必須フラグ（1=必須）
  - DFLT: デフォルト値
  - TKEY01: PKフラグ（1=PK）
  - BIKO: 備考
  - UPCNT: 更新回数
  - UPDTIME: 更新日時

### 会社コード（KC）

全てのSQLで `KC = '85'` を固定で使用。
config.phpで定数定義: `define('KC', '85');`

## 機能

### テーブル照会（table_list.php）

- 検索条件: テーブルID、テーブル名、項目ID、項目名
- 項目ID/名で検索するとカラム一覧表示モードになる
- ページング対応（50件ずつ）
- ラジオボタンで選択してCSV/SQL出力

### テーブル詳細（table_detail.php）

- テーブル情報の編集（テーブル名、日本語名）
- 項目一覧の編集
  - 全項目が入力可能
  - 必須、PKはチェックボックス
  - 行選択用チェックボックス（左側）
  - 行削除、上に一行追加、下に一行追加ボタン
- 保存ボタンでAJAX保存

### CSV出力（csv_download.php）

- 選択したテーブルの項目一覧をCSV出力
- BOM付きUTF-8

### SQL出力（sql_download.php）

生成されるSQL形式:
```sql
--テーブル日本語名
drop table TBLNAME
;
drop index TBLNAME_PK
;

create table TBLNAME(
  COL1 NUMBER (14) NOT NULL
 ,COL2 VARCHAR2(1) NOT NULL
 ,UPDTIME TIMESTAMP NOT NULL
 ,constraint TBLNAME_PKC primary key (COL1)
)
;

DROP SEQUENCE TBLNAME_ID_SEQ
;

CREATE SEQUENCE TBLNAME_ID_SEQ
 START WITH 1 INCREMENT BY 1
 MAXVALUE 99999999999999999999999999999999999999 MINVALUE 1 NOCYCLE NOCACHE
;

create unique index TBLNAME_pk on
 TBLNAME(COL2,COL3)
;
```

**データ型変換ルール:**
- 9 → NUMBER
- X → VARCHAR2
- T → TIMESTAMP

**UNIQUE INDEX:**
- HSU=1（必須）かつUPDTIME以外のカラム
- 最初のカラム（PK）は除外

## 設定

### .env

```
ORACLE_USER=dbmente
ORACLE_PASS=dbmente
ORACLE_DSN=ホスト:ポート/サービス名
SESSION_SECRET=ランダム文字列
```

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

## 開発経緯

1. JBoss Seam版dbmenteをreferenceフォルダに配置
2. PHP + Oracleでテーブル照会機能を作成
3. 検索条件からシステム区分・種別を削除（シンプル化）
4. SQL出力フォーマットをカスタマイズ（DROP TABLE/INDEX、SEQUENCE、UNIQUE INDEX追加）
5. テーブル詳細を編集可能に修正
6. KC（会社コード）=85を全SQLに追加
