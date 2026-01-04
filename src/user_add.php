<?php
/**
 * ユーザー追加画面
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';
require_once __DIR__ . '/includes/auth.php';

$pageTitle = 'ユーザー追加 - DBmente';
$message = '';
$messageType = '';

// ユーザー追加処理
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $sycd = trim($_POST['sycd'] ?? '');
    $symei = trim($_POST['symei'] ?? '');
    $sybu = trim($_POST['sybu'] ?? '');
    $pass = trim($_POST['pass'] ?? '');
    $confirmPass = trim($_POST['confirm_pass'] ?? '');

    if (empty($sycd) || empty($pass)) {
        $message = 'ユーザーIDとパスワードは必須です';
        $messageType = 'error';
    } elseif ($pass !== $confirmPass) {
        $message = 'パスワードと確認用パスワードが一致しません';
        $messageType = 'error';
    } else {
        try {
            $db = Database::getInstance();

            // 既存ユーザーチェック
            $checkSql = "SELECT COUNT(*) AS CNT FROM ZI_USER WHERE KC = '" . KC . "' AND TRIM(SYCD) = :sycd";
            $result = $db->query($checkSql, [':sycd' => $sycd]);

            if ($result[0]['CNT'] > 0) {
                $message = 'このユーザーIDは既に登録されています';
                $messageType = 'error';
            } else {
                // ユーザー追加
                $insertSql = "INSERT INTO ZI_USER (KC, SYCD, SYMEI, SYBU, PASS, UPCNT, UPDTIME) VALUES ('" . KC . "', :sycd, :symei, :sybu, :pass, 0, SYSTIMESTAMP)";
                $db->execute($insertSql, [
                    ':sycd' => $sycd,
                    ':symei' => $symei,
                    ':sybu' => $sybu,
                    ':pass' => $pass
                ]);

                $message = 'ユーザーを追加しました: ' . $sycd;
                $messageType = 'info';

                // フォームクリア
                $sycd = $symei = $sybu = '';
            }
        } catch (Exception $e) {
            $message = 'システムエラー: ' . $e->getMessage();
            $messageType = 'error';
        }
    }
}

include __DIR__ . '/templates/header.php';
?>

<h2 style="margin-bottom: 20px;">ユーザー追加</h2>

<?php if ($message): ?>
<div class="message message-<?= $messageType ?>">
    <?= htmlspecialchars($message) ?>
</div>
<?php endif; ?>

<div class="search-box" style="max-width: 500px;">
    <form method="post" action="">
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>ユーザーID <span style="color: red;">*</span></label>
                <input type="text" name="sycd" value="<?= htmlspecialchars($sycd ?? '') ?>" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>ユーザー名</label>
                <input type="text" name="symei" value="<?= htmlspecialchars($symei ?? '') ?>" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>所属</label>
                <input type="text" name="sybu" value="<?= htmlspecialchars($sybu ?? '') ?>" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>パスワード <span style="color: red;">*</span></label>
                <input type="password" name="pass" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>パスワード確認 <span style="color: red;">*</span></label>
                <input type="password" name="confirm_pass" style="flex: 1;">
            </div>
        </div>
        <div class="button-row">
            <button type="submit" class="btn btn-success">追加</button>
            <a href="table_list.php" class="btn btn-secondary" style="text-decoration: none;">戻る</a>
        </div>
    </form>
</div>

<?php include __DIR__ . '/templates/footer.php'; ?>
