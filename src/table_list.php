<?php
/**
 * テーブル照会画面
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';

session_start();

// 検索条件の取得
$tblnm = strtoupper(trim($_GET['tblnm'] ?? ''));
$tbljnm = trim($_GET['tbljnm'] ?? '');
$clmnm = strtoupper(trim($_GET['clmnm'] ?? ''));
$clmjnm = trim($_GET['clmjnm'] ?? '');
$page = (int)($_GET['page'] ?? 0);
$mode = $_GET['mode'] ?? 'table'; // table or column

$results = [];
$hasMore = false;
$error = null;
$searched = isset($_GET['search']);

if ($searched) {
    try {
        $db = Database::getInstance();

        // カラム検索モードかどうか
        $isColumnSearch = !empty($clmnm) || !empty($clmjnm);

        if ($isColumnSearch) {
            // カラム検索SQL
            $sql = "SELECT T.TBLNM, T.TBLJNM, I.TBLNO, I.BNM AS CLMNM, I.RNM AS CLMJNM, I.KATA, I.LNG1, I.LNG2
                    FROM ZM_TBL T
                    INNER JOIN ZM_TBLITM I ON T.TBLNM = I.TBLNM AND T.KC = I.KC
                    WHERE T.KC = '" . KC . "'";
            $mode = 'column';
        } else {
            // テーブル検索SQL
            $sql = "SELECT TBLNM, TBLJNM FROM ZM_TBL WHERE KC = '" . KC . "'";
        }

        $params = [];

        if (!empty($tblnm)) {
            $sql .= $isColumnSearch
                ? " AND T.TBLNM LIKE :tblnm"
                : " AND TBLNM LIKE :tblnm";
            $params[':tblnm'] = '%' . $tblnm . '%';
        }
        if (!empty($tbljnm)) {
            $sql .= $isColumnSearch
                ? " AND T.TBLJNM LIKE :tbljnm"
                : " AND TBLJNM LIKE :tbljnm";
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

        $sql .= $isColumnSearch
            ? " ORDER BY T.TBLNM, I.TBLNO"
            : " ORDER BY TBLNM";

        $result = $db->queryWithPaging($sql, $params, $page, PAGE_SIZE);
        $results = $result['data'];
        $hasMore = $result['hasMore'];

    } catch (Exception $e) {
        $error = $e->getMessage();
    }
}

// ページタイトル
$pageTitle = 'テーブル照会';
include __DIR__ . '/templates/header.php';
?>

<?php if ($error): ?>
    <div class="message message-error"><?= htmlspecialchars($error) ?></div>
<?php endif; ?>

<!-- 検索条件 -->
<div class="search-box">
    <h2>検索条件</h2>
    <form method="get" action="">
        <div class="search-row">
            <div class="search-item">
                <label>テーブルID</label>
                <input type="text" name="tblnm" value="<?= htmlspecialchars($tblnm) ?>" maxlength="30" style="text-transform: uppercase;">
            </div>
            <div class="search-item">
                <label>テーブル名</label>
                <input type="text" name="tbljnm" value="<?= htmlspecialchars($tbljnm) ?>" maxlength="40">
            </div>
        </div>
        <div class="search-row">
            <div class="search-item">
                <label>項目ID</label>
                <input type="text" name="clmnm" value="<?= htmlspecialchars($clmnm) ?>" maxlength="30" style="text-transform: uppercase;">
            </div>
            <div class="search-item">
                <label>項目名</label>
                <input type="text" name="clmjnm" value="<?= htmlspecialchars($clmjnm) ?>" maxlength="40">
            </div>
        </div>
        <input type="hidden" name="page" value="0">
        <div class="button-row">
            <button type="submit" name="search" value="1" class="btn btn-primary">検索</button>
            <a href="table_list.php" class="btn btn-secondary">クリア</a>
        </div>
    </form>
</div>

<?php if ($searched): ?>
<!-- 検索結果 -->
<div class="result-box">
    <div class="result-header">
        <span class="result-count">
            <?php if (count($results) > 0): ?>
                <?= $page * PAGE_SIZE + 1 ?>〜<?= $page * PAGE_SIZE + count($results) ?>件表示
                <?php if ($page === 0 && $hasMore): ?>（データの最初）<?php endif; ?>
                <?php if ($page > 0 && $hasMore): ?>（データの途中）<?php endif; ?>
                <?php if (!$hasMore): ?>（データの終わり）<?php endif; ?>
            <?php else: ?>
                該当データなし
            <?php endif; ?>
        </span>
        <?php if (count($results) > 0): ?>
        <div>
            <button type="button" class="btn btn-success" onclick="downloadCsv()">CSV出力</button>
            <button type="button" class="btn btn-success" onclick="downloadSql()">SQL出力</button>
        </div>
        <?php endif; ?>
    </div>

    <div class="result-body">
        <?php if ($mode === 'column'): ?>
        <!-- カラム一覧 -->
        <table class="data-table">
            <thead>
                <tr>
                    <th style="width:50px;">選択</th>
                    <th>テーブル名</th>
                    <th>テーブル日本語名</th>
                    <th>項目名</th>
                    <th>項目日本語名</th>
                    <th style="width:80px;">型</th>
                    <th style="width:60px;">長さ1</th>
                    <th style="width:60px;">長さ2</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($results as $row): ?>
                <tr>
                    <td><input type="radio" name="sel" value="<?= htmlspecialchars($row['TBLNM']) ?>"></td>
                    <td><a href="table_detail.php?tblnm=<?= urlencode($row['TBLNM']) ?>"><?= htmlspecialchars($row['TBLNM']) ?></a></td>
                    <td><?= htmlspecialchars($row['TBLJNM'] ?? '') ?></td>
                    <td><?= htmlspecialchars($row['CLMNM'] ?? '') ?></td>
                    <td><?= htmlspecialchars($row['CLMJNM'] ?? '') ?></td>
                    <td><?= htmlspecialchars($row['KATA'] ?? '') ?></td>
                    <td><?= htmlspecialchars($row['LNG1'] ?? '') ?></td>
                    <td><?= htmlspecialchars($row['LNG2'] ?? '') ?></td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
        <?php else: ?>
        <!-- テーブル一覧 -->
        <table class="data-table">
            <thead>
                <tr>
                    <th style="width:50px;">選択</th>
                    <th>テーブル名</th>
                    <th>テーブル日本語名</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($results as $row): ?>
                <tr>
                    <td><input type="radio" name="sel" value="<?= htmlspecialchars(trim($row['TBLNM'])) ?>"></td>
                    <td><a href="table_detail.php?tblnm=<?= urlencode(trim($row['TBLNM'])) ?>"><?= htmlspecialchars(trim($row['TBLNM'])) ?></a></td>
                    <td><?= htmlspecialchars(trim($row['TBLJNM'] ?? '')) ?></td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
        <?php endif; ?>
    </div>

    <?php if ($page > 0 || $hasMore): ?>
    <div class="paging">
        <?php
            $queryParams = $_GET;
            unset($queryParams['page']);
        ?>
        <?php if ($page > 0): ?>
            <?php $queryParams['page'] = $page - 1; ?>
            <a href="?<?= http_build_query($queryParams) ?>&search=1" class="btn btn-secondary">前頁</a>
        <?php endif; ?>
        <?php if ($hasMore): ?>
            <?php $queryParams['page'] = $page + 1; ?>
            <a href="?<?= http_build_query($queryParams) ?>&search=1" class="btn btn-secondary">次頁</a>
        <?php endif; ?>
    </div>
    <?php endif; ?>
</div>
<?php endif; ?>

<script>
function getSelectedTable() {
    const selected = document.querySelector('input[name="sel"]:checked');
    if (!selected) {
        alert('テーブルを選択してください');
        return null;
    }
    return selected.value.trim();
}

function downloadCsv() {
    const tblnm = getSelectedTable();
    if (tblnm) {
        window.location.href = 'csv_download.php?tblnm=' + encodeURIComponent(tblnm) + '&type=detail';
    }
}

function downloadSql() {
    const tblnm = getSelectedTable();
    if (tblnm) {
        window.location.href = 'sql_download.php?tblnm=' + encodeURIComponent(tblnm);
    }
}
</script>

<?php include __DIR__ . '/templates/footer.php'; ?>
