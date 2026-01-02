<?php
/**
 * 設定ファイル
 */

// 環境変数から設定を読み込み
$envFile = __DIR__ . '/../../.env';
if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        if (strpos($line, '=') !== false && strpos($line, '#') !== 0) {
            list($key, $value) = explode('=', $line, 2);
            $_ENV[trim($key)] = trim($value);
        }
    }
}

// Oracle接続設定
define('ORACLE_USER', $_ENV['ORACLE_USER'] ?? '');
define('ORACLE_PASS', $_ENV['ORACLE_PASS'] ?? '');
define('ORACLE_DSN', $_ENV['ORACLE_DSN'] ?? '');

// セッション設定
define('SESSION_SECRET', $_ENV['SESSION_SECRET'] ?? 'change_this_secret');

// ページング設定
define('PAGE_SIZE', 50);

// 会社コード（固定）
define('KC', '85');

// 文字コード
mb_internal_encoding('UTF-8');
