<?php
/**
 * ZM_TBL移行スクリプト
 * Usage: php import_tbl.php zm_tbl.csv
 */

// DB接続設定
$user = 'dbmente';
$pass = 'dbmente';
$dsn  = '10.214.6.60:1521/lafitdb';

if ($argc < 2) {
    echo "Usage: php import_tbl.php <csvfile>\n";
    exit(1);
}

$csvFile = $argv[1];
if (!file_exists($csvFile)) {
    echo "File not found: {$csvFile}\n";
    exit(1);
}

try {
    $conn = oci_connect($user, $pass, $dsn, 'AL32UTF8');
    if (!$conn) {
        $e = oci_error();
        throw new Exception('DB接続エラー: ' . $e['message']);
    }

    echo "DB接続成功\n";

    // CSVファイル読み込み
    $handle = fopen($csvFile, 'r');
    if (!$handle) {
        throw new Exception('CSVファイルを開けません: ' . $csvFile);
    }

    // BOMスキップ
    $bom = fread($handle, 3);
    if ($bom !== "\xEF\xBB\xBF") {
        rewind($handle);
    }

    // ヘッダー行スキップ
    $header = fgetcsv($handle);
    echo "ヘッダー: " . implode(', ', $header) . "\n";

    $sql = "INSERT INTO ZM_TBL (KC, TBLNM, TBLJNM, SYS, SHU, UPCNT, UPDTIME)
            VALUES (:kc, :tblnm, :tbljnm, :sys, :shu, :upcnt, TO_TIMESTAMP(:updtime, 'YYYY-MM-DD HH24:MI:SS'))";

    $stmt = oci_parse($conn, $sql);

    $inserted = 0;
    $errors = 0;
    $lineNo = 1;

    while (($row = fgetcsv($handle)) !== false) {
        $lineNo++;

        if (count($row) < 7) {
            echo "Line {$lineNo}: カラム数不足 (" . count($row) . ")\n";
            $errors++;
            continue;
        }

        // « NULL »を空文字に置換 + TRIM処理
        $row = array_map(function($v) {
            $v = trim($v);  // 前後のスペース削除
            return ($v === '« NULL »' || $v === '<NULL>' || $v === 'NULL') ? '' : $v;
        }, $row);

        // データバインド (KC, TBLNM, TBLJNM, SYS, SHU, UPCNT, UPDTIME)
        $kc = $row[0];
        $tblnm = $row[1];
        $tbljnm = $row[2];
        $sys = $row[3];
        $shu = $row[4];
        $upcnt = (int)$row[5];
        $updtime = $row[6];

        // 0000-00-00をnullに
        if ($updtime === '0000-00-00 00:00:00' || empty($updtime)) {
            $updtime = date('Y-m-d H:i:s');
        }

        oci_bind_by_name($stmt, ':kc', $kc);
        oci_bind_by_name($stmt, ':tblnm', $tblnm);
        oci_bind_by_name($stmt, ':tbljnm', $tbljnm);
        oci_bind_by_name($stmt, ':sys', $sys);
        oci_bind_by_name($stmt, ':shu', $shu);
        oci_bind_by_name($stmt, ':upcnt', $upcnt);
        oci_bind_by_name($stmt, ':updtime', $updtime);

        if (oci_execute($stmt, OCI_NO_AUTO_COMMIT)) {
            $inserted++;
            if ($inserted % 100 === 0) {
                echo "Inserted: {$inserted}...\n";
            }
        } else {
            $e = oci_error($stmt);
            echo "Line {$lineNo} Error: {$e['message']}\n";
            echo "  TBLNM={$tblnm}\n";
            $errors++;
        }
    }

    fclose($handle);

    // コミット
    oci_commit($conn);
    echo "\n完了: {$inserted}件挿入, {$errors}件エラー\n";

    oci_close($conn);

} catch (Exception $e) {
    echo "エラー: " . $e->getMessage() . "\n";
    exit(1);
}
