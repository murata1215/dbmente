<?php
/**
 * パスワード変更画面
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';
require_once __DIR__ . '/includes/auth.php';

$pageTitle = 'パスワード変更 - DBmente';
$message = '';
$messageType = '';

// パスワード変更処理
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $currentPass = trim($_POST['current_pass'] ?? '');
    $newPass = trim($_POST['new_pass'] ?? '');
    $confirmPass = trim($_POST['confirm_pass'] ?? '');

    if (empty($currentPass) || empty($newPass) || empty($confirmPass)) {
        $message = 'すべての項目を入力してください';
        $messageType = 'error';
    } elseif ($newPass !== $confirmPass) {
        $message = '新しいパスワードと確認用パスワードが一致しません';
        $messageType = 'error';
    } else {
        try {
            $db = Database::getInstance();
            $sycd = $_SESSION['user']['sycd'];

            // 現在のパスワードを確認
            $sql = "SELECT PASS FROM ZI_USER WHERE KC = '" . KC . "' AND TRIM(SYCD) = :sycd";
            $result = $db->query($sql, [':sycd' => $sycd]);

            if (empty($result) || trim($result[0]['PASS'] ?? '') !== $currentPass) {
                $message = '現在のパスワードが正しくありません';
                $messageType = 'error';
            } else {
                // パスワード更新
                $updateSql = "UPDATE ZI_USER SET PASS = :new_pass WHERE KC = '" . KC . "' AND TRIM(SYCD) = :sycd";
                $db->execute($updateSql, [':new_pass' => $newPass, ':sycd' => $sycd]);

                $message = 'パスワードを変更しました';
                $messageType = 'info';
            }
        } catch (Exception $e) {
            $message = 'システムエラー: ' . $e->getMessage();
            $messageType = 'error';
        }
    }
}

include __DIR__ . '/templates/header.php';
?>

<h2 style="margin-bottom: 20px;">パスワード変更</h2>

<?php if ($message): ?>
<div class="message message-<?= $messageType ?>">
    <?= htmlspecialchars($message) ?>
</div>
<?php endif; ?>

<div class="search-box" style="max-width: 500px;">
    <form method="post" action="">
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>現在のパスワード</label>
                <input type="password" name="current_pass" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>新しいパスワード</label>
                <input type="password" name="new_pass" style="flex: 1;">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item" style="width: 100%;">
                <label>パスワード確認</label>
                <input type="password" name="confirm_pass" style="flex: 1;">
            </div>
        </div>
        <div class="button-row">
            <button type="submit" class="btn btn-primary">変更</button>
            <a href="table_list.php" class="btn btn-secondary" style="text-decoration: none;">戻る</a>
        </div>
    </form>
</div>

<?php include __DIR__ . '/templates/footer.php'; ?>
