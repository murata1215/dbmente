<?php
/**
 * 認証チェック
 */
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// ログインチェック
if (!isset($_SESSION['user'])) {
    header('Location: login.php');
    exit;
}

/**
 * ログインユーザー情報取得
 */
function getLoginUser() {
    return $_SESSION['user'] ?? null;
}

/**
 * ログインユーザーID取得
 */
function getLoginUserId() {
    return $_SESSION['user']['sycd'] ?? '';
}

/**
 * ログインユーザー名取得
 */
function getLoginUserName() {
    return $_SESSION['user']['symei'] ?? '';
}
