<?php
/**
 * ログイン画面
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';

session_start();

$error = '';

// 既にログイン済みならリダイレクト
if (isset($_SESSION['user'])) {
    header('Location: table_list.php');
    exit;
}

// ログイン処理
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $sycd = trim($_POST['sycd'] ?? '');
    $pass = trim($_POST['pass'] ?? '');

    if (empty($sycd) || empty($pass)) {
        $error = 'ユーザーIDとパスワードを入力してください';
    } else {
        try {
            $db = Database::getInstance();
            $sql = "SELECT * FROM ZI_USER WHERE KC = '" . KC . "' AND TRIM(SYCD) = :sycd";
            $result = $db->query($sql, [':sycd' => $sycd]);

            if (empty($result)) {
                $error = 'ユーザーIDまたはパスワードが正しくありません';
            } else {
                $user = $result[0];
                // パスワードチェック（trimして比較）
                if (trim($user['PASS'] ?? '') === $pass) {
                    // ログイン成功
                    $_SESSION['user'] = [
                        'sycd' => trim($user['SYCD']),
                        'symei' => trim($user['SYMEI'] ?? ''),
                        'sybu' => trim($user['SYBU'] ?? '')
                    ];
                    header('Location: table_list.php');
                    exit;
                } else {
                    $error = 'ユーザーIDまたはパスワードが正しくありません';
                }
            }
        } catch (Exception $e) {
            $error = 'システムエラー: ' . $e->getMessage();
        }
    }
}
?>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ログイン - DBmente</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Meiryo', sans-serif;
            background: #ecf0f1;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .login-box {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 350px;
        }
        .login-box h1 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
            font-size: 24px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #34495e;
            font-weight: bold;
        }
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #bdc3c7;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group input:focus {
            outline: none;
            border-color: #3498db;
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        .btn-login:hover {
            background: #2980b9;
        }
        .error {
            background: #e74c3c;
            color: white;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <h1>DBmente</h1>
        <?php if ($error): ?>
            <div class="error"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>
        <form method="post" action="">
            <div class="form-group">
                <label>ユーザーID</label>
                <input type="text" name="sycd" value="<?= htmlspecialchars($_POST['sycd'] ?? '') ?>" autofocus>
            </div>
            <div class="form-group">
                <label>パスワード</label>
                <input type="password" name="pass">
            </div>
            <button type="submit" class="btn-login">ログイン</button>
        </form>
    </div>
</body>
</html>
