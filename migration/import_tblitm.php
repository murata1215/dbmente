<?php
/**
 * ZM_TBLITM移行スクリプト
 * Usage: php import_tblitm.php zm_tblitm.csv
 */

// DB接続設定
$user = 'dbmente';
$pass = 'dbmente';
$dsn  = '10.214.6.60:1521/lafitdb';

if ($argc < 2) {
    echo "Usage: php import_tblitm.php <csvfile>\n";
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
    echo "ヘッダー: " . implode(', ', array_slice($header, 0, 5)) . "...\n";

    $sql = "INSERT INTO ZM_TBLITM (KC, TBLNM, TBLNO, RNM, BNM, KATA, LNG1, LNG2, HSU, DFLT,
            TKEY01, TKEY02, TKEY03, TKEY04, TKEY05, TKEY06, TKEY07, TKEY08, TKEY09, TKEY10,
            TKEY11, TKEY12, TKEY13, TKEY14, TKEY15, TKEY16, TKEY17, TKEY18, TKEY19, TKEY20,
            BIKO, UPCNT, UPDTIME)
            VALUES (:kc, :tblnm, :tblno, :rnm, :bnm, :kata, :lng1, :lng2, :hsu, :dflt,
            :tkey01, :tkey02, :tkey03, :tkey04, :tkey05, :tkey06, :tkey07, :tkey08, :tkey09, :tkey10,
            :tkey11, :tkey12, :tkey13, :tkey14, :tkey15, :tkey16, :tkey17, :tkey18, :tkey19, :tkey20,
            :biko, :upcnt, TO_TIMESTAMP(:updtime, 'YYYY-MM-DD HH24:MI:SS'))";

    $stmt = oci_parse($conn, $sql);

    $inserted = 0;
    $errors = 0;
    $lineNo = 1;

    while (($row = fgetcsv($handle)) !== false) {
        $lineNo++;

        if (count($row) < 33) {
            echo "Line {$lineNo}: カラム数不足 (" . count($row) . ")\n";
            $errors++;
            continue;
        }

        // « NULL »を空文字に置換 + TRIM処理
        $row = array_map(function($v) {
            $v = trim($v);  // 前後のスペース削除
            return ($v === '« NULL »' || $v === '<NULL>' || $v === 'NULL') ? '' : $v;
        }, $row);

        // データバインド
        $kc = $row[0];
        $tblnm = $row[1];
        $tblno = (int)$row[2];
        $rnm = $row[3];
        $bnm = $row[4];
        $kata = $row[5];
        $lng1 = (int)$row[6];
        $lng2 = (int)$row[7];
        $hsu = $row[8];
        $dflt = $row[9];
        $tkey01 = $row[10];
        $tkey02 = $row[11];
        $tkey03 = $row[12];
        $tkey04 = $row[13];
        $tkey05 = $row[14];
        $tkey06 = $row[15];
        $tkey07 = $row[16];
        $tkey08 = $row[17];
        $tkey09 = $row[18];
        $tkey10 = $row[19];
        $tkey11 = $row[20];
        $tkey12 = $row[21];
        $tkey13 = $row[22];
        $tkey14 = $row[23];
        $tkey15 = $row[24];
        $tkey16 = $row[25];
        $tkey17 = $row[26];
        $tkey18 = $row[27];
        $tkey19 = $row[28];
        $tkey20 = $row[29];
        $biko = $row[30];
        $upcnt = (int)$row[31];
        $updtime = $row[32];

        // 0000-00-00をnullに
        if ($updtime === '0000-00-00 00:00:00' || empty($updtime)) {
            $updtime = date('Y-m-d H:i:s');
        }

        oci_bind_by_name($stmt, ':kc', $kc);
        oci_bind_by_name($stmt, ':tblnm', $tblnm);
        oci_bind_by_name($stmt, ':tblno', $tblno);
        oci_bind_by_name($stmt, ':rnm', $rnm);
        oci_bind_by_name($stmt, ':bnm', $bnm);
        oci_bind_by_name($stmt, ':kata', $kata);
        oci_bind_by_name($stmt, ':lng1', $lng1);
        oci_bind_by_name($stmt, ':lng2', $lng2);
        oci_bind_by_name($stmt, ':hsu', $hsu);
        oci_bind_by_name($stmt, ':dflt', $dflt);
        oci_bind_by_name($stmt, ':tkey01', $tkey01);
        oci_bind_by_name($stmt, ':tkey02', $tkey02);
        oci_bind_by_name($stmt, ':tkey03', $tkey03);
        oci_bind_by_name($stmt, ':tkey04', $tkey04);
        oci_bind_by_name($stmt, ':tkey05', $tkey05);
        oci_bind_by_name($stmt, ':tkey06', $tkey06);
        oci_bind_by_name($stmt, ':tkey07', $tkey07);
        oci_bind_by_name($stmt, ':tkey08', $tkey08);
        oci_bind_by_name($stmt, ':tkey09', $tkey09);
        oci_bind_by_name($stmt, ':tkey10', $tkey10);
        oci_bind_by_name($stmt, ':tkey11', $tkey11);
        oci_bind_by_name($stmt, ':tkey12', $tkey12);
        oci_bind_by_name($stmt, ':tkey13', $tkey13);
        oci_bind_by_name($stmt, ':tkey14', $tkey14);
        oci_bind_by_name($stmt, ':tkey15', $tkey15);
        oci_bind_by_name($stmt, ':tkey16', $tkey16);
        oci_bind_by_name($stmt, ':tkey17', $tkey17);
        oci_bind_by_name($stmt, ':tkey18', $tkey18);
        oci_bind_by_name($stmt, ':tkey19', $tkey19);
        oci_bind_by_name($stmt, ':tkey20', $tkey20);
        oci_bind_by_name($stmt, ':biko', $biko);
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
            echo "  TBLNM={$tblnm}, TBLNO={$tblno}, BNM={$bnm}\n";
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
