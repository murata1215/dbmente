<?php
/**
 * テーブル保存処理
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';

// セッションチェック（ログインユーザー取得用）
session_start();
$loginUser = $_SESSION['user']['sycd'] ?? 'unknown';
$loginUserName = $_SESSION['user']['symei'] ?? '';

header('Content-Type: application/json; charset=UTF-8');

/**
 * 更新ログ出力
 */
function writeUpdateLog($userId, $userName, $action, $tblnm, $beforeData, $afterData) {
    $timestamp = date('Y-m-d H:i:s');
    $logLine = "========================================\n";
    $logLine .= "[{$timestamp}] User: {$userId} ({$userName})\n";
    $logLine .= "Action: {$action}\n";
    $logLine .= "Table: {$tblnm}\n";
    $logLine .= "----------------------------------------\n";

    if ($beforeData !== null) {
        $logLine .= "[BEFORE]\n";
        $logLine .= "  TBLNM: " . ($beforeData['table']['TBLNM'] ?? '') . "\n";
        $logLine .= "  TBLJNM: " . ($beforeData['table']['TBLJNM'] ?? '') . "\n";
        $logLine .= "  Items: " . count($beforeData['items']) . " columns\n";
        foreach ($beforeData['items'] as $item) {
            $logLine .= "    - " . trim($item['BNM'] ?? '') . " (" . trim($item['RNM'] ?? '') . ") " . trim($item['KATA'] ?? '') . "(" . ($item['LNG1'] ?? 0) . ")\n";
        }
    } else {
        $logLine .= "[BEFORE] (new table)\n";
    }

    $logLine .= "----------------------------------------\n";
    $logLine .= "[AFTER]\n";
    $logLine .= "  TBLNM: " . ($afterData['tblnm'] ?? '') . "\n";
    $logLine .= "  TBLJNM: " . ($afterData['tbljnm'] ?? '') . "\n";
    $logLine .= "  Items: " . count($afterData['items']) . " columns\n";
    foreach ($afterData['items'] as $item) {
        $logLine .= "    - " . ($item['bnm'] ?? '') . " (" . ($item['rnm'] ?? '') . ") " . ($item['kata'] ?? '') . "(" . ($item['lng1'] ?? 0) . ")\n";
    }
    $logLine .= "========================================\n";

    // PHPの標準エラー出力（ログ）に書き込み
    error_log($logLine);
}

try {
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        throw new Exception('リクエストデータが不正です');
    }

    $kc = KC; // 会社コード固定
    $tblnmOrg = strtoupper(trim($input['tblnm_org'] ?? ''));
    $tblnm = strtoupper(trim($input['tblnm'] ?? ''));
    $tbljnm = trim($input['tbljnm'] ?? '');
    $items = $input['items'] ?? [];

    if (empty($tblnm)) {
        throw new Exception('テーブル名は必須です');
    }

    $db = Database::getInstance();
    $conn = $db->getConnection();

    // テーブル名変更チェック
    $isRename = !empty($tblnmOrg) && $tblnm !== $tblnmOrg;

    // 新規テーブル名の重複チェック
    if ($isRename) {
        $sql = "SELECT COUNT(*) AS CNT FROM ZM_TBL WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
        $result = $db->query($sql, [':kc' => $kc, ':tblnm' => $tblnm]);
        if ($result[0]['CNT'] > 0) {
            throw new Exception('テーブル名が既に存在します: ' . $tblnm);
        }
    }

    // 既存テーブルの存在確認
    $sql = "SELECT COUNT(*) AS CNT FROM ZM_TBL WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
    $checkName = $isRename ? $tblnmOrg : $tblnm;
    $result = $db->query($sql, [':kc' => $kc, ':tblnm' => $checkName]);
    $isNew = ($result[0]['CNT'] == 0);

    // 更新前データ取得（ログ用）
    $beforeData = null;
    if (!$isNew) {
        $beforeTblnm = $isRename ? $tblnmOrg : $tblnm;
        $sql = "SELECT * FROM ZM_TBL WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
        $tableResult = $db->query($sql, [':kc' => $kc, ':tblnm' => $beforeTblnm]);
        $sql = "SELECT * FROM ZM_TBLITM WHERE KC = :kc AND TRIM(TBLNM) = :tblnm ORDER BY TBLNO";
        $itemsResult = $db->query($sql, [':kc' => $kc, ':tblnm' => $beforeTblnm]);
        $beforeData = [
            'table' => $tableResult[0] ?? [],
            'items' => $itemsResult
        ];
    }

    // テーブル名からSYS（1文字目）とSHU（2文字目）を取得
    $sys = strlen($tblnm) >= 1 ? substr($tblnm, 0, 1) : '';
    $shu = strlen($tblnm) >= 2 ? substr($tblnm, 1, 1) : '';

    if ($isNew) {
        // 新規登録
        $sql = "INSERT INTO ZM_TBL (KC, TBLNM, TBLJNM, SYS, SHU, UPCNT, UPDTIME) VALUES (:kc, :tblnm, :tbljnm, :sys, :shu, 0, SYSTIMESTAMP)";
        $stmt = oci_parse($conn, $sql);
        oci_bind_by_name($stmt, ':kc', $kc);
        oci_bind_by_name($stmt, ':tblnm', $tblnm);
        oci_bind_by_name($stmt, ':tbljnm', $tbljnm);
        oci_bind_by_name($stmt, ':sys', $sys);
        oci_bind_by_name($stmt, ':shu', $shu);
        if (!oci_execute($stmt, OCI_NO_AUTO_COMMIT)) {
            $e = oci_error($stmt);
            throw new Exception('テーブル登録エラー: ' . $e['message']);
        }
        $action = 'CREATE';
    } else {
        // 更新
        if ($isRename) {
            // テーブル名変更時：新しいレコードを作成（SYS/SHUは新テーブル名から取得）
            $sql = "INSERT INTO ZM_TBL (KC, TBLNM, TBLJNM, SYS, SHU, UPCNT, UPDTIME)
                    SELECT KC, :tblnm_new, :tbljnm, :sys, :shu, UPCNT + 1, SYSTIMESTAMP
                    FROM ZM_TBL WHERE KC = :kc AND TRIM(TBLNM) = :tblnm_org";
            $stmt = oci_parse($conn, $sql);
            oci_bind_by_name($stmt, ':tblnm_new', $tblnm);
            oci_bind_by_name($stmt, ':tbljnm', $tbljnm);
            oci_bind_by_name($stmt, ':sys', $sys);
            oci_bind_by_name($stmt, ':shu', $shu);
            oci_bind_by_name($stmt, ':kc', $kc);
            oci_bind_by_name($stmt, ':tblnm_org', $tblnmOrg);
            if (!oci_execute($stmt, OCI_NO_AUTO_COMMIT)) {
                $e = oci_error($stmt);
                throw new Exception('テーブル名変更エラー: ' . $e['message']);
            }

            // 旧テーブル削除
            $sql = "DELETE FROM ZM_TBL WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
            $stmt = oci_parse($conn, $sql);
            oci_bind_by_name($stmt, ':kc', $kc);
            oci_bind_by_name($stmt, ':tblnm', $tblnmOrg);
            oci_execute($stmt, OCI_NO_AUTO_COMMIT);

            // 旧項目削除
            $sql = "DELETE FROM ZM_TBLITM WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
            $stmt = oci_parse($conn, $sql);
            oci_bind_by_name($stmt, ':kc', $kc);
            oci_bind_by_name($stmt, ':tblnm', $tblnmOrg);
            oci_execute($stmt, OCI_NO_AUTO_COMMIT);

            $action = 'RENAME';
        } else {
            // テーブル情報更新
            $sql = "UPDATE ZM_TBL SET TBLJNM = :tbljnm, UPCNT = UPCNT + 1, UPDTIME = SYSTIMESTAMP WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
            $stmt = oci_parse($conn, $sql);
            oci_bind_by_name($stmt, ':tbljnm', $tbljnm);
            oci_bind_by_name($stmt, ':kc', $kc);
            oci_bind_by_name($stmt, ':tblnm', $tblnm);
            if (!oci_execute($stmt, OCI_NO_AUTO_COMMIT)) {
                $e = oci_error($stmt);
                throw new Exception('テーブル更新エラー: ' . $e['message']);
            }

            // 既存項目削除
            $sql = "DELETE FROM ZM_TBLITM WHERE KC = :kc AND TRIM(TBLNM) = :tblnm";
            $stmt = oci_parse($conn, $sql);
            oci_bind_by_name($stmt, ':kc', $kc);
            oci_bind_by_name($stmt, ':tblnm', $tblnm);
            oci_execute($stmt, OCI_NO_AUTO_COMMIT);

            $action = 'UPDATE';
        }
    }

    // 項目登録
    foreach ($items as $item) {
        $tblno = (int)$item['tblno'];
        $bnm = strtoupper(trim($item['bnm'] ?? ''));
        $rnm = trim($item['rnm'] ?? '');
        $kata = strtoupper(trim($item['kata'] ?? ''));
        $lng1 = (int)($item['lng1'] ?? 0);
        $lng2 = (int)($item['lng2'] ?? 0);
        $hsu = $item['hsu'] ?? '0';
        $dflt = trim($item['dflt'] ?? '');
        $tkey01 = $item['tkey01'] ?? '0';
        $biko = trim($item['biko'] ?? '');

        if (empty($bnm)) {
            continue; // 物理名が空の行はスキップ
        }

        $sql = "INSERT INTO ZM_TBLITM (KC, TBLNM, TBLNO, BNM, RNM, KATA, LNG1, LNG2, HSU, DFLT, TKEY01, BIKO, UPCNT, UPDTIME)
                VALUES (:kc, :tblnm, :tblno, :bnm, :rnm, :kata, :lng1, :lng2, :hsu, :dflt, :tkey01, :biko, 0, SYSTIMESTAMP)";
        $stmt = oci_parse($conn, $sql);
        oci_bind_by_name($stmt, ':kc', $kc);
        oci_bind_by_name($stmt, ':tblnm', $tblnm);
        oci_bind_by_name($stmt, ':tblno', $tblno);
        oci_bind_by_name($stmt, ':bnm', $bnm);
        oci_bind_by_name($stmt, ':rnm', $rnm);
        oci_bind_by_name($stmt, ':kata', $kata);
        oci_bind_by_name($stmt, ':lng1', $lng1);
        oci_bind_by_name($stmt, ':lng2', $lng2);
        oci_bind_by_name($stmt, ':hsu', $hsu);
        oci_bind_by_name($stmt, ':dflt', $dflt);
        oci_bind_by_name($stmt, ':tkey01', $tkey01);
        oci_bind_by_name($stmt, ':biko', $biko);

        if (!oci_execute($stmt, OCI_NO_AUTO_COMMIT)) {
            $e = oci_error($stmt);
            throw new Exception('項目登録エラー (No.' . $tblno . '): ' . $e['message']);
        }
    }

    // コミット
    oci_commit($conn);

    // 更新ログ出力
    $afterData = [
        'tblnm' => $tblnm,
        'tbljnm' => $tbljnm,
        'items' => $items
    ];
    writeUpdateLog($loginUser, $loginUserName, $action, $tblnm, $beforeData, $afterData);

    echo json_encode(['success' => true]);

} catch (Exception $e) {
    // ロールバック
    if (isset($conn)) {
        oci_rollback($conn);
    }
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
}
