<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= htmlspecialchars($pageTitle ?? 'DBmente') ?></title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Meiryo', 'MS Gothic', sans-serif; font-size: 14px; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; padding: 20px; }

        /* ヘッダー */
        .header { background: #2c3e50; color: white; padding: 15px 20px; margin-bottom: 20px; }
        .header h1 { font-size: 18px; font-weight: normal; }
        .header nav { margin-top: 10px; }
        .header nav a { color: #ecf0f1; text-decoration: none; margin-right: 20px; }
        .header nav a:hover { text-decoration: underline; }

        /* 検索条件ブロック */
        .search-box { background: white; padding: 20px; border: 1px solid #ddd; margin-bottom: 20px; }
        .search-box h2 { font-size: 14px; color: #333; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
        .search-row { display: flex; flex-wrap: wrap; gap: 15px; margin-bottom: 10px; }
        .search-item { display: flex; align-items: center; }
        .search-item label { width: 100px; font-weight: bold; color: #555; }
        .search-item input, .search-item select { padding: 6px 10px; border: 1px solid #ccc; border-radius: 3px; }
        .search-item input { width: 200px; }
        .search-item select { width: 150px; }

        /* ボタン */
        .btn { padding: 8px 20px; border: none; border-radius: 3px; cursor: pointer; font-size: 14px; }
        .btn-primary { background: #3498db; color: white; }
        .btn-primary:hover { background: #2980b9; }
        .btn-secondary { background: #95a5a6; color: white; }
        .btn-secondary:hover { background: #7f8c8d; }
        .btn-success { background: #27ae60; color: white; }
        .btn-success:hover { background: #219a52; }
        .button-row { margin-top: 15px; display: flex; gap: 10px; }

        /* テーブル */
        .data-table { width: 100%; border-collapse: collapse; background: white; }
        .data-table th { background: #34495e; color: white; padding: 10px; text-align: left; font-weight: normal; }
        .data-table td { padding: 8px 10px; border-bottom: 1px solid #eee; }
        .data-table tr:hover { background: #f9f9f9; }
        .data-table tr:nth-child(even) { background: #fafafa; }
        .data-table tr:nth-child(even):hover { background: #f0f0f0; }

        /* 結果エリア */
        .result-box { background: white; border: 1px solid #ddd; }
        .result-header { padding: 10px 15px; border-bottom: 1px solid #ddd; display: flex; justify-content: space-between; align-items: center; }
        .result-count { color: #666; }
        .result-body { max-height: 500px; overflow: auto; }

        /* ページング */
        .paging { padding: 15px; display: flex; justify-content: center; gap: 10px; border-top: 1px solid #ddd; }

        /* メッセージ */
        .message { padding: 10px 15px; margin-bottom: 15px; border-radius: 3px; }
        .message-info { background: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }
        .message-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        /* リンク */
        a { color: #3498db; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="header">
    <h1>DBmente - テーブル定義管理</h1>
    <nav>
        <a href="table_list.php">テーブル照会</a>
        <a href="table_edit.php">テーブルメンテ</a>
    </nav>
</div>
<div class="container">
