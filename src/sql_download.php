<?php
/**
 * CREATE SQL出力
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';
require_once __DIR__ . '/includes/auth.php';

$tblnm = strtoupper(trim($_GET['tblnm'] ?? ''));

if (empty($tblnm)) {
    header('Content-Type: text/plain; charset=UTF-8');
    echo 'テーブル名が指定されていません';
    exit;
}

try {
    $db = Database::getInstance();

    // テーブル情報取得
    $sql = "SELECT * FROM ZM_TBL WHERE KC = '" . KC . "' AND TRIM(TBLNM) = :tblnm";
    $result = $db->query($sql, [':tblnm' => $tblnm]);
    if (empty($result)) {
        throw new Exception('テーブルが見つかりません: ' . $tblnm);
    }
    $table = $result[0];

    // 項目情報取得
    $sql = "SELECT * FROM ZM_TBLITM WHERE KC = '" . KC . "' AND TRIM(TBLNM) = :tblnm ORDER BY TBLNO";
    $items = $db->query($sql, [':tblnm' => $tblnm]);

    // CREATE文生成
    $lines = [];
    $tbljnm = trim($table['TBLJNM'] ?? $tblnm);

    // コメント
    $lines[] = "--" . $tbljnm;

    // DROP TABLE
    $lines[] = "drop table " . $tblnm;
    $lines[] = ";";

    // DROP INDEX
    $lines[] = "drop index " . $tblnm . "_PK";
    $lines[] = ";";
    $lines[] = "";

    // CREATE TABLE
    $lines[] = "create table " . $tblnm . "(";

    $columnLines = [];
    $pkColumn = null;           // PRIMARY KEY用（テーブル名+ID）
    $uniqueIndexColumns = [];   // UNIQUE INDEX用（TKEY01=1）

    // PRIMARY KEYカラム名（テーブル名+ID）
    $expectedPkColumn = $tblnm . 'ID';

    foreach ($items as $item) {
        $bnm = trim($item['BNM'] ?? '');
        $rnm = trim($item['RNM'] ?? '');
        $kata = strtoupper(trim($item['KATA'] ?? ''));
        $lng1 = (int)($item['LNG1'] ?? 0);
        $lng2 = (int)($item['LNG2'] ?? 0);
        $hsu = trim($item['HSU'] ?? '');
        $tkey01 = trim($item['TKEY01'] ?? '');
        $dflt = trim($item['DFLT'] ?? '');

        // PRIMARY KEY用（テーブル名+IDのカラム）
        if (strtoupper($bnm) === $expectedPkColumn) {
            $pkColumn = $bnm;
        }

        // UNIQUE INDEX用（TKEY01に値が入っている場合）
        if (!empty($tkey01)) {
            $uniqueIndexColumns[] = $bnm;
        }

        // カラム定義開始
        $colDef = "  " . $bnm;

        // データ型変換
        switch ($kata) {
            case '9':
            case 'NUMBER':
                if ($lng1 > 0 && $lng2 > 0) {
                    // NUMBER(精度, スケール) 例: NUMBER(4,2)
                    $colDef .= " NUMBER(" . $lng1 . "," . $lng2 . ")";
                } elseif ($lng1 > 0) {
                    $colDef .= " NUMBER(" . $lng1 . ")";
                } else {
                    $colDef .= " NUMBER";
                }
                break;
            case 'V':
            case 'X':
            case 'VARCHAR':
            case 'VARCHAR2':
                $colDef .= " VARCHAR2(" . ($lng1 ?: 100) . ")";
                break;
            case 'T':
            case 'TIMESTAMP':
                $colDef .= " TIMESTAMP";
                break;
            case 'CHAR':
                $colDef .= " CHAR(" . ($lng1 ?: 1) . ")";
                break;
            case 'DATE':
                $colDef .= " DATE";
                break;
            case 'CLOB':
                $colDef .= " CLOB";
                break;
            case 'BLOB':
                $colDef .= " BLOB";
                break;
            default:
                // 不明な型はそのまま
                if ($lng1 > 0) {
                    $colDef .= " " . $kata . "(" . $lng1 . ")";
                } else {
                    $colDef .= " " . $kata;
                }
        }

        // デフォルト値
        if (!empty($dflt)) {
            $colDef .= " default " . $dflt;
        } elseif ($kata === '9' || $kata === 'NUMBER') {
            // NUMBER型はデフォルト0を必ず設定
            $colDef .= " default 0";
        }

        // NOT NULL
        // 数値型でも文字列型でも対応できるよう緩い比較
        if ($hsu == '1' || $hsu === 1) {
            $colDef .= " NOT NULL";
        }

        $columnLines[] = $colDef;
    }

    // PRIMARY KEY制約（テーブル名+IDのカラム）
    if ($pkColumn) {
        $columnLines[] = " constraint " . $tblnm . "_PKC primary key (" . $pkColumn . ")";
    }

    $lines[] = implode("\n ,", $columnLines);
    $lines[] = ")";
    $lines[] = ";";
    $lines[] = "";

    // DROP SEQUENCE
    $lines[] = "DROP SEQUENCE " . $tblnm . "_ID_SEQ";
    $lines[] = ";";
    $lines[] = "";

    // CREATE SEQUENCE
    $lines[] = "CREATE SEQUENCE " . $tblnm . "_ID_SEQ";
    $lines[] = " START WITH 1 INCREMENT BY 1";
    $lines[] = " MAXVALUE 99999999999999999999999999999999999999 MINVALUE 1 NOCYCLE NOCACHE";
    $lines[] = ";";
    $lines[] = "";

    // CREATE UNIQUE INDEX（TKEY01=1の全カラム）
    if (!empty($uniqueIndexColumns)) {
        $lines[] = "create unique index " . $tblnm . "_pk on";
        $lines[] = " " . $tblnm . "(" . implode(",", $uniqueIndexColumns) . ")";
        $lines[] = ";";
    }

    $sqlContent = implode("\n", $lines);

    // ファイル出力
    header('Content-Type: text/plain; charset=UTF-8');
    header('Content-Disposition: attachment; filename="' . $tblnm . '.sql"');
    header('Cache-Control: no-cache');

    echo $sqlContent;

} catch (Exception $e) {
    header('Content-Type: text/plain; charset=UTF-8');
    echo 'エラー: ' . $e->getMessage();
}
