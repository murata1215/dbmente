<?php
/**
 * テーブル詳細画面（編集可能）
 */
require_once __DIR__ . '/includes/config.php';
require_once __DIR__ . '/includes/Database.php';
require_once __DIR__ . '/includes/auth.php';

$tblnm = strtoupper(trim($_GET['tblnm'] ?? ''));
$isNew = isset($_GET['new']) && $_GET['new'] == '1';
$table = null;
$items = [];
$error = null;

if (!$isNew && !empty($tblnm)) {
    try {
        $db = Database::getInstance();

        // テーブル情報取得
        $sql = "SELECT * FROM ZM_TBL WHERE KC = '" . KC . "' AND TRIM(TBLNM) = :tblnm";
        $result = $db->query($sql, [':tblnm' => $tblnm]);
        if (!empty($result)) {
            $table = $result[0];
        }

        // 項目情報取得
        $sql = "SELECT * FROM ZM_TBLITM WHERE KC = '" . KC . "' AND TRIM(TBLNM) = :tblnm ORDER BY TBLNO";
        $items = $db->query($sql, [':tblnm' => $tblnm]);

    } catch (Exception $e) {
        $error = $e->getMessage();
    }
}

// 新規モードの場合は空のテーブル情報
if ($isNew) {
    $table = ['TBLNM' => '', 'TBLJNM' => ''];
}

$pageTitle = $isNew ? '新規テーブル作成' : 'テーブル詳細 - ' . $tblnm;
include __DIR__ . '/templates/header.php';
?>

<style>
.edit-table { width: 100%; border-collapse: collapse; }
.edit-table th { background: #34495e; color: white; padding: 8px; text-align: center; font-weight: normal; font-size: 12px; }
.edit-table td { padding: 4px; border-bottom: 1px solid #ddd; }
.edit-table input[type="text"], .edit-table input[type="number"] {
    width: 100%; padding: 4px; border: 1px solid #ccc; border-radius: 3px; box-sizing: border-box;
}
.edit-table input[type="checkbox"] { width: 18px; height: 18px; }
.edit-table tr:hover { background: #f5f5f5; }
.edit-table .col-check { width: 30px; text-align: center; }
.edit-table .col-no { width: 40px; }
.edit-table .col-bnm { width: 120px; }
.edit-table .col-rnm { width: 150px; }
.edit-table .col-kata { width: 80px; }
.edit-table .col-lng { width: 50px; }
.edit-table .col-cb { width: 40px; text-align: center; }
.edit-table .col-dflt { width: 100px; }
.edit-table .col-biko { width: 150px; }
.action-buttons { display: flex; gap: 10px; }
.action-buttons .btn { padding: 6px 15px; font-size: 13px; }
#csvFile { display: none; }
</style>

<?php if ($error): ?>
    <div class="message message-error"><?= htmlspecialchars($error) ?></div>
<?php endif; ?>

<?php if ($table !== null): ?>
<!-- テーブル基本情報 -->
<div class="search-box">
    <h2><?= $isNew ? '新規テーブル作成' : 'テーブル情報' ?></h2>
    <form id="tableForm">
        <input type="hidden" name="tblnm_org" value="<?= htmlspecialchars(trim($table['TBLNM'] ?? '')) ?>">
        <input type="hidden" name="is_new" value="<?= $isNew ? '1' : '0' ?>">
        <table style="width: 100%;">
            <tr>
                <td style="width: 120px; font-weight: bold; padding: 5px;">テーブル名</td>
                <td style="padding: 5px;">
                    <input type="text" name="tblnm" value="<?= htmlspecialchars(trim($table['TBLNM'] ?? '')) ?>" style="width: 200px; text-transform: uppercase;" placeholder="例: USER_MASTER">
                </td>
                <td style="width: 120px; font-weight: bold; padding: 5px;">日本語名</td>
                <td style="padding: 5px;">
                    <input type="text" name="tbljnm" value="<?= htmlspecialchars(trim($table['TBLJNM'] ?? '')) ?>" style="width: 300px;" placeholder="例: ユーザーマスタ">
                </td>
            </tr>
        </table>
    </form>
    <div class="button-row">
        <button type="button" class="btn btn-primary" onclick="saveTable()">保存</button>
        <?php if ($isNew): ?>
        <input type="file" id="csvFile" accept=".csv">
        <button type="button" class="btn btn-success" onclick="document.getElementById('csvFile').click()">CSV取り込み</button>
        <?php else: ?>
        <a href="csv_download.php?tblnm=<?= urlencode($tblnm) ?>&type=detail" class="btn btn-success">CSV出力</a>
        <a href="sql_download.php?tblnm=<?= urlencode($tblnm) ?>" class="btn btn-success">CREATE SQL</a>
        <?php endif; ?>
        <a href="table_list.php" class="btn btn-secondary">戻る</a>
    </div>
</div>

<!-- 項目一覧 -->
<div class="result-box">
    <div class="result-header">
        <span class="result-count">項目数: <span id="itemCount"><?= count($items) ?></span>件</span>
        <div class="action-buttons">
            <button type="button" class="btn btn-secondary" onclick="deleteRows()">行削除</button>
            <button type="button" class="btn btn-secondary" onclick="addRowAbove()">上に一行追加</button>
            <button type="button" class="btn btn-secondary" onclick="addRowBelow()">下に一行追加</button>
        </div>
    </div>
    <div class="result-body" style="max-height: none; overflow: visible;">
        <table class="edit-table" id="itemTable">
            <thead>
                <tr>
                    <th class="col-check"><input type="checkbox" id="checkAll" onclick="toggleAll(this)"></th>
                    <th class="col-no">No</th>
                    <th class="col-bnm">物理名</th>
                    <th class="col-rnm">論理名</th>
                    <th class="col-kata">型</th>
                    <th class="col-lng">長さ1</th>
                    <th class="col-lng">長さ2</th>
                    <th class="col-cb">必須</th>
                    <th class="col-dflt">デフォルト</th>
                    <th class="col-cb">PK</th>
                    <th class="col-biko">備考</th>
                </tr>
            </thead>
            <tbody id="itemBody">
                <?php foreach ($items as $index => $item): ?>
                <tr data-row="<?= $index ?>">
                    <td class="col-check"><input type="checkbox" name="row_check" value="<?= $index ?>"></td>
                    <td class="col-no"><input type="text" name="tblno[]" value="<?= htmlspecialchars($item['TBLNO']) ?>" style="width:35px; text-align:center;"></td>
                    <td class="col-bnm"><input type="text" name="bnm[]" value="<?= htmlspecialchars(trim($item['BNM'] ?? '')) ?>" style="text-transform: uppercase;"></td>
                    <td class="col-rnm"><input type="text" name="rnm[]" value="<?= htmlspecialchars(trim($item['RNM'] ?? '')) ?>"></td>
                    <td class="col-kata"><input type="text" name="kata[]" value="<?= htmlspecialchars(trim($item['KATA'] ?? '')) ?>" style="text-transform: uppercase;"></td>
                    <td class="col-lng"><input type="number" name="lng1[]" value="<?= (int)$item['LNG1'] ?>"></td>
                    <td class="col-lng"><input type="number" name="lng2[]" value="<?= (int)$item['LNG2'] ?>"></td>
                    <td class="col-cb"><input type="checkbox" name="hsu[]" value="1" <?= trim($item['HSU'] ?? '') === '1' ? 'checked' : '' ?>></td>
                    <td class="col-dflt"><input type="text" name="dflt[]" value="<?= htmlspecialchars(trim($item['DFLT'] ?? '')) ?>"></td>
                    <td class="col-cb"><input type="checkbox" name="tkey01[]" value="1" <?= trim($item['TKEY01'] ?? '') === '1' ? 'checked' : '' ?>></td>
                    <td class="col-biko"><input type="text" name="biko[]" value="<?= htmlspecialchars(trim($item['BIKO'] ?? '')) ?>"></td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </div>
</div>

<script>
let rowCounter = <?= count($items) ?>;
const isNew = <?= $isNew ? 'true' : 'false' ?>;

// 全選択/解除
function toggleAll(checkbox) {
    document.querySelectorAll('input[name="row_check"]').forEach(cb => {
        cb.checked = checkbox.checked;
    });
}

// 選択行を取得
function getSelectedRows() {
    const checked = document.querySelectorAll('input[name="row_check"]:checked');
    return Array.from(checked).map(cb => cb.closest('tr'));
}

// 行削除
function deleteRows() {
    const rows = getSelectedRows();
    if (rows.length === 0) {
        alert('削除する行を選択してください');
        return;
    }
    if (confirm(rows.length + '行を削除しますか？')) {
        rows.forEach(row => row.remove());
        updateItemCount();
        renumberRows();
    }
}

// 新しい行のHTMLを生成
function createNewRow(no, data = {}) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td class="col-check"><input type="checkbox" name="row_check" value="${rowCounter}"></td>
        <td class="col-no"><input type="text" name="tblno[]" value="${no}" style="width:35px; text-align:center;"></td>
        <td class="col-bnm"><input type="text" name="bnm[]" value="${data.bnm || ''}" style="text-transform: uppercase;"></td>
        <td class="col-rnm"><input type="text" name="rnm[]" value="${data.rnm || ''}"></td>
        <td class="col-kata"><input type="text" name="kata[]" value="${data.kata || ''}" style="text-transform: uppercase;"></td>
        <td class="col-lng"><input type="number" name="lng1[]" value="${data.lng1 || 0}"></td>
        <td class="col-lng"><input type="number" name="lng2[]" value="${data.lng2 || 0}"></td>
        <td class="col-cb"><input type="checkbox" name="hsu[]" value="1" ${data.hsu === '1' ? 'checked' : ''}></td>
        <td class="col-dflt"><input type="text" name="dflt[]" value="${data.dflt || ''}"></td>
        <td class="col-cb"><input type="checkbox" name="tkey01[]" value="1" ${data.tkey01 === '1' ? 'checked' : ''}></td>
        <td class="col-biko"><input type="text" name="biko[]" value="${data.biko || ''}"></td>
    `;
    tr.dataset.row = rowCounter++;
    return tr;
}

// 上に一行追加
function addRowAbove() {
    const rows = getSelectedRows();
    const tbody = document.getElementById('itemBody');

    if (rows.length === 0) {
        const newRow = createNewRow(1);
        tbody.insertBefore(newRow, tbody.firstChild);
    } else {
        rows.forEach(row => {
            const currentNo = parseInt(row.querySelector('input[name="tblno[]"]').value) || 1;
            const newRow = createNewRow(currentNo);
            row.parentNode.insertBefore(newRow, row);
        });
    }
    updateItemCount();
    renumberRows();
}

// 下に一行追加
function addRowBelow() {
    const rows = getSelectedRows();
    const tbody = document.getElementById('itemBody');

    if (rows.length === 0) {
        const lastNo = tbody.rows.length + 1;
        const newRow = createNewRow(lastNo);
        tbody.appendChild(newRow);
    } else {
        [...rows].reverse().forEach(row => {
            const currentNo = parseInt(row.querySelector('input[name="tblno[]"]').value) || 1;
            const newRow = createNewRow(currentNo + 1);
            row.parentNode.insertBefore(newRow, row.nextSibling);
        });
    }
    updateItemCount();
    renumberRows();
}

// 行番号を振り直す
function renumberRows() {
    const rows = document.querySelectorAll('#itemBody tr');
    rows.forEach((row, index) => {
        row.querySelector('input[name="tblno[]"]').value = index + 1;
    });
}

// 件数更新
function updateItemCount() {
    document.getElementById('itemCount').textContent = document.querySelectorAll('#itemBody tr').length;
}

// 保存処理
function saveTable() {
    const tblnmValue = document.querySelector('input[name="tblnm"]').value.toUpperCase().trim();

    if (!tblnmValue) {
        alert('テーブル名を入力してください');
        return;
    }

    const formData = {
        tblnm_org: document.querySelector('input[name="tblnm_org"]').value,
        tblnm: tblnmValue,
        tbljnm: document.querySelector('input[name="tbljnm"]').value.trim(),
        items: []
    };

    const rows = document.querySelectorAll('#itemBody tr');
    rows.forEach(row => {
        const item = {
            tblno: row.querySelector('input[name="tblno[]"]').value,
            bnm: row.querySelector('input[name="bnm[]"]').value.toUpperCase().trim(),
            rnm: row.querySelector('input[name="rnm[]"]').value.trim(),
            kata: row.querySelector('input[name="kata[]"]').value.toUpperCase().trim(),
            lng1: row.querySelector('input[name="lng1[]"]').value || 0,
            lng2: row.querySelector('input[name="lng2[]"]').value || 0,
            hsu: row.querySelector('input[name="hsu[]"]').checked ? '1' : '0',
            dflt: row.querySelector('input[name="dflt[]"]').value.trim(),
            tkey01: row.querySelector('input[name="tkey01[]"]').checked ? '1' : '0',
            biko: row.querySelector('input[name="biko[]"]').value.trim()
        };
        formData.items.push(item);
    });

    fetch('table_save.php', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert('保存しました');
            // 保存後は編集モードのURLに遷移
            window.location.href = 'table_detail.php?tblnm=' + encodeURIComponent(formData.tblnm);
        } else {
            alert('エラー: ' + data.error);
        }
    })
    .catch(err => {
        alert('通信エラー: ' + err);
    });
}

// CSV取り込み処理
document.getElementById('csvFile')?.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(e) {
        const content = e.target.result;
        importCsv(content);
    };
    reader.readAsText(file, 'UTF-8');
});

function importCsv(content) {
    const lines = content.split(/\r?\n/);
    const tbody = document.getElementById('itemBody');

    // 既存行をクリア
    tbody.innerHTML = '';
    rowCounter = 0;

    let rowNo = 1;
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (!line) continue;

        // CSVパース（簡易版：カンマ区切り、ダブルクォート対応）
        const cols = parseCsvLine(line);

        // ヘッダー行をスキップ（1行目がNo,物理名...で始まる場合）
        if (i === 0 && (cols[0] === 'No' || cols[0] === 'NO' || cols[0] === 'no')) {
            continue;
        }

        // CSVレイアウト: No, 物理名, 論理名, 型, 長さ1, 長さ2, 必須, デフォルト, PK, 備考
        // Noはスキップして自動採番
        const data = {
            bnm: (cols[1] || '').trim(),
            rnm: (cols[2] || '').trim(),
            kata: (cols[3] || '').trim(),
            lng1: parseInt(cols[4]) || 0,
            lng2: parseInt(cols[5]) || 0,
            hsu: (cols[6] || '').trim() === '1' ? '1' : '0',
            dflt: (cols[7] || '').trim(),
            tkey01: (cols[8] || '').trim() === '1' ? '1' : '0',
            biko: (cols[9] || '').trim()
        };

        // 物理名が空の行はスキップ
        if (!data.bnm) continue;

        const newRow = createNewRow(rowNo++, data);
        tbody.appendChild(newRow);
    }

    updateItemCount();
    alert('CSVを取り込みました: ' + (rowNo - 1) + '件');

    // ファイル選択をリセット
    document.getElementById('csvFile').value = '';
}

// CSV行パース（ダブルクォート対応）
function parseCsvLine(line) {
    const result = [];
    let current = '';
    let inQuotes = false;

    for (let i = 0; i < line.length; i++) {
        const char = line[i];

        if (inQuotes) {
            if (char === '"') {
                if (line[i + 1] === '"') {
                    current += '"';
                    i++;
                } else {
                    inQuotes = false;
                }
            } else {
                current += char;
            }
        } else {
            if (char === '"') {
                inQuotes = true;
            } else if (char === ',') {
                result.push(current);
                current = '';
            } else {
                current += char;
            }
        }
    }
    result.push(current);

    return result;
}
</script>

<?php else: ?>
<div class="message message-error">テーブルが見つかりません: <?= htmlspecialchars($tblnm) ?></div>
<a href="table_list.php" class="btn btn-secondary">戻る</a>
<?php endif; ?>

<?php include __DIR__ . '/templates/footer.php'; ?>
