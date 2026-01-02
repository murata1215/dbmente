-- DBmente テーブル定義
-- Oracle用

-- テーブルマスタ
CREATE TABLE ZM_TBL (
    TBLNM    VARCHAR2(30) NOT NULL,   -- テーブル名
    TBLJNM   VARCHAR2(100),           -- テーブル名（日本語）
    SYS      VARCHAR2(2),             -- システム区分
    SHU      VARCHAR2(2),             -- テーブル種別
    UPCNT    NUMBER DEFAULT 0,        -- 更新回数
    UPDTIME  TIMESTAMP,               -- 更新日時
    CONSTRAINT PK_ZM_TBL PRIMARY KEY (TBLNM)
);

COMMENT ON TABLE ZM_TBL IS 'テーブルマスタ';
COMMENT ON COLUMN ZM_TBL.TBLNM IS 'テーブル名';
COMMENT ON COLUMN ZM_TBL.TBLJNM IS 'テーブル名（日本語）';
COMMENT ON COLUMN ZM_TBL.SYS IS 'システム区分';
COMMENT ON COLUMN ZM_TBL.SHU IS 'テーブル種別';
COMMENT ON COLUMN ZM_TBL.UPCNT IS '更新回数';
COMMENT ON COLUMN ZM_TBL.UPDTIME IS '更新日時';

-- テーブル項目マスタ
CREATE TABLE ZM_TBLITM (
    TBLNM    VARCHAR2(30) NOT NULL,   -- テーブル名
    TBLNO    NUMBER NOT NULL,         -- 項目NO
    RNM      VARCHAR2(100),           -- 論理名
    BNM      VARCHAR2(30),            -- 物理名
    KATA     VARCHAR2(20),            -- 型
    LNG1     NUMBER DEFAULT 0,        -- 長さ1
    LNG2     NUMBER DEFAULT 0,        -- 長さ2
    HSU      VARCHAR2(1),             -- 必須 (1:必須)
    DFLT     VARCHAR2(100),           -- デフォルト値
    TKEY01   VARCHAR2(1),             -- キー1 (PK)
    TKEY02   VARCHAR2(1),             -- キー2
    TKEY03   VARCHAR2(1),             -- キー3
    TKEY04   VARCHAR2(1),             -- キー4
    TKEY05   VARCHAR2(1),             -- キー5
    TKEY06   VARCHAR2(1),             -- キー6
    TKEY07   VARCHAR2(1),             -- キー7
    TKEY08   VARCHAR2(1),             -- キー8
    TKEY09   VARCHAR2(1),             -- キー9
    TKEY10   VARCHAR2(1),             -- キー10
    TKEY11   VARCHAR2(1),             -- キー11
    TKEY12   VARCHAR2(1),             -- キー12
    TKEY13   VARCHAR2(1),             -- キー13
    TKEY14   VARCHAR2(1),             -- キー14
    TKEY15   VARCHAR2(1),             -- キー15
    TKEY16   VARCHAR2(1),             -- キー16
    TKEY17   VARCHAR2(1),             -- キー17
    TKEY18   VARCHAR2(1),             -- キー18
    TKEY19   VARCHAR2(1),             -- キー19
    TKEY20   VARCHAR2(1),             -- キー20
    BIKO     VARCHAR2(500),           -- 備考
    UPCNT    NUMBER DEFAULT 0,        -- 更新回数
    UPDTIME  TIMESTAMP,               -- 更新日時
    CONSTRAINT PK_ZM_TBLITM PRIMARY KEY (TBLNM, TBLNO),
    CONSTRAINT FK_ZM_TBLITM_TBL FOREIGN KEY (TBLNM) REFERENCES ZM_TBL(TBLNM)
);

COMMENT ON TABLE ZM_TBLITM IS 'テーブル項目マスタ';
COMMENT ON COLUMN ZM_TBLITM.TBLNM IS 'テーブル名';
COMMENT ON COLUMN ZM_TBLITM.TBLNO IS '項目NO';
COMMENT ON COLUMN ZM_TBLITM.RNM IS '論理名';
COMMENT ON COLUMN ZM_TBLITM.BNM IS '物理名';
COMMENT ON COLUMN ZM_TBLITM.KATA IS '型';
COMMENT ON COLUMN ZM_TBLITM.LNG1 IS '長さ1';
COMMENT ON COLUMN ZM_TBLITM.LNG2 IS '長さ2';
COMMENT ON COLUMN ZM_TBLITM.HSU IS '必須';
COMMENT ON COLUMN ZM_TBLITM.DFLT IS 'デフォルト値';
COMMENT ON COLUMN ZM_TBLITM.TKEY01 IS 'キー1(PK)';
COMMENT ON COLUMN ZM_TBLITM.BIKO IS '備考';
COMMENT ON COLUMN ZM_TBLITM.UPCNT IS '更新回数';
COMMENT ON COLUMN ZM_TBLITM.UPDTIME IS '更新日時';

-- インデックス
CREATE INDEX IDX_ZM_TBL_SYS ON ZM_TBL(SYS);
CREATE INDEX IDX_ZM_TBL_SHU ON ZM_TBL(SHU);
CREATE INDEX IDX_ZM_TBLITM_BNM ON ZM_TBLITM(BNM);
CREATE INDEX IDX_ZM_TBLITM_RNM ON ZM_TBLITM(RNM);
