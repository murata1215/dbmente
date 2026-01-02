<?php
/**
 * CSV出力
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';

$type = $_GET['type'] ?? 'detail';
$tblnm = strtoupper(trim($_GET['tblnm'] ?? ''));

try {
    $db = Database::getInstance();
    $filename = '';
    $data = [];
    $headers = [];

    if ($type === 'detail' && !empty($tblnm)) {
        // 単一テーブルの項目一覧をCSV出力
        $sql = "SELECT TBLNO, BNM, RNM, KATA, LNG1, LNG2, HSU, DFLT, TKEY01, BIKO
                FROM ZM_TBLITM WHERE KC = '" . KC . "' AND TRIM(TBLNM) = :tblnm ORDER BY TBLNO";
        $data = $db->query($sql, [':tblnm' => $tblnm]);
        $headers = ['No', '物理名', '論理名', '型', '長さ1', '長さ2', '必須', 'デフォルト', 'PK', '備考'];
        $filename = $tblnm . '_items.csv';

    } else {
        // 検索結果一覧をCSV出力
        $tbljnm = trim($_GET['tbljnm'] ?? '');
        $clmnm = strtoupper(trim($_GET['clmnm'] ?? ''));
        $clmjnm = trim($_GET['clmjnm'] ?? '');

        $isColumnSearch = !empty($clmnm) || !empty($clmjnm);

        if ($isColumnSearch) {
            $sql = "SELECT T.TBLNM, T.TBLJNM, I.TBLNO, I.BNM, I.RNM, I.KATA, I.LNG1, I.LNG2
                    FROM ZM_TBL T
                    INNER JOIN ZM_TBLITM I ON T.TBLNM = I.TBLNM AND T.KC = I.KC
                    WHERE T.KC = '" . KC . "'";
            $headers = ['テーブル名', 'テーブル日本語名', 'No', '物理名', '論理名', '型', '長さ1', '長さ2'];
        } else {
            $sql = "SELECT TBLNM, TBLJNM FROM ZM_TBL WHERE KC = '" . KC . "'";
            $headers = ['テーブル名', 'テーブル日本語名'];
        }

        $params = [];

        if (!empty($tblnm)) {
            $sql .= $isColumnSearch ? " AND T.TBLNM LIKE :tblnm" : " AND TBLNM LIKE :tblnm";
            $params[':tblnm'] = '%' . $tblnm . '%';
        }
        if (!empty($tbljnm)) {
            $sql .= $isColumnSearch ? " AND T.TBLJNM LIKE :tbljnm" : " AND TBLJNM LIKE :tbljnm";
            $params[':tbljnm'] = '%' . $tbljnm . '%';
        }
        if (!empty($clmnm)) {
            $sql .= " AND I.BNM LIKE :clmnm";
            $params[':clmnm'] = '%' . $clmnm . '%';
        }
        if (!empty($clmjnm)) {
            $sql .= " AND I.RNM LIKE :clmjnm";
            $params[':clmjnm'] = '%' . $clmjnm . '%';
        }

        $sql .= $isColumnSearch ? " ORDER BY T.TBLNM, I.TBLNO" : " ORDER BY TBLNM";

        $data = $db->query($sql, $params);
        $filename = 'table_list_' . date('Ymd_His') . '.csv';
    }

    // CSV出力
    header('Content-Type: text/csv; charset=UTF-8');
    header('Content-Disposition: attachment; filename="' . $filename . '"');
    header('Cache-Control: no-cache');

    // BOM付きUTF-8
    echo "\xEF\xBB\xBF";

    $output = fopen('php://output', 'w');

    // ヘッダー行
    fputcsv($output, $headers);

    // データ行（値をtrim）
    foreach ($data as $row) {
        $trimmedRow = array_map(function($v) { return is_string($v) ? trim($v) : $v; }, $row);
        fputcsv($output, array_values($trimmedRow));
    }

    fclose($output);

} catch (Exception $e) {
    header('Content-Type: text/plain; charset=UTF-8');
    echo 'エラー: ' . $e->getMessage();
}
