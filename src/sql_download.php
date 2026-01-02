<?php
/**
 * CREATE SQL出力
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';

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
    $firstColumn = null;
    $uniqueIndexColumns = [];

    foreach ($items as $item) {
        $bnm = trim($item['BNM'] ?? '');
        $rnm = trim($item['RNM'] ?? '');
        $kata = strtoupper(trim($item['KATA'] ?? ''));
        $lng1 = (int)($item['LNG1'] ?? 0);
        $lng2 = (int)($item['LNG2'] ?? 0);
        $hsu = trim($item['HSU'] ?? '');
        $dflt = trim($item['DFLT'] ?? '');

        // 最初のカラムを記憶（PK用）
        if ($firstColumn === null) {
            $firstColumn = $bnm;
        }

        // UNIQUE INDEX用（HSU=1 かつ UPDTIME以外）
        if ($hsu === '1' && strtoupper($bnm) !== 'UPDTIME') {
            $uniqueIndexColumns[] = $bnm;
        }

        // カラム定義開始
        $colDef = "  " . $bnm;

        // データ型変換
        switch ($kata) {
            case '9':
            case 'NUMBER':
                if ($lng1 > 0) {
                    $colDef .= " NUMBER (" . $lng1 . ")";
                } else {
                    $colDef .= " NUMBER";
                }
                break;
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
        }

        // NOT NULL
        if ($hsu === '1') {
            $colDef .= " NOT NULL";
        }

        $columnLines[] = $colDef;
    }

    // PRIMARY KEY制約
    if ($firstColumn) {
        $columnLines[] = " ,constraint " . $tblnm . "_PKC primary key (" . $firstColumn . ")";
    }

    $lines[] = implode(" \n ,", $columnLines);
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

    // CREATE UNIQUE INDEX（HSU=1でUPDTIME以外のカラム、最初のPKカラムは除く）
    // 最初のカラム（PK）を除外
    $indexColumns = array_filter($uniqueIndexColumns, function($col) use ($firstColumn) {
        return $col !== $firstColumn;
    });

    if (!empty($indexColumns)) {
        $lines[] = "create unique index " . $tblnm . "_pk on";
        $lines[] = " " . $tblnm . "(" . implode(",", $indexColumns) . ")";
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
