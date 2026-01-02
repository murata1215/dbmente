-- SQL*Loader Control File for ZM_TBLITM
-- Usage: sqlldr userid=dbmente/dbmente@10.214.6.60:1521/lafitdb control=zm_tblitm.ctl

OPTIONS (SKIP=1, ERRORS=100)
LOAD DATA
CHARACTERSET UTF8
INFILE 'zm_tblitm.csv'
BADFILE 'zm_tblitm.bad'
DISCARDFILE 'zm_tblitm.dsc'
APPEND INTO TABLE ZM_TBLITM
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
    KC,
    TBLNM,
    TBLNO,
    RNM,
    BNM,
    KATA,
    LNG1,
    LNG2,
    HSU,
    DFLT,
    TKEY01,
    TKEY02,
    TKEY03,
    TKEY04,
    TKEY05,
    TKEY06,
    TKEY07,
    TKEY08,
    TKEY09,
    TKEY10,
    TKEY11,
    TKEY12,
    TKEY13,
    TKEY14,
    TKEY15,
    TKEY16,
    TKEY17,
    TKEY18,
    TKEY19,
    TKEY20,
    BIKO,
    UPCNT,
    UPDTIME "TO_TIMESTAMP(:UPDTIME, 'YYYY-MM-DD HH24:MI:SS')"
)
