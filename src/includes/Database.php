<?php
/**
 * Oracle DB接続クラス
 */
class Database
{
    private static $instance = null;
    private $conn = null;

	private function __construct()
	{
	    $user = 'dbmente';
	    $pass = 'dbmente';
	    $dsn  = '10.214.6.60:1521/lafitdb';
	    $charset = 'AL32UTF8';

	    $this->conn = @oci_connect($user, $pass, $dsn, $charset);

	    if (!$this->conn) {
	        $e = oci_error();

	        // ログ用（詳細）
	        error_log(
	            "[Oracle Connect Error]\n" .
	            "user={$user}\n" .
	            "dsn={$dsn}\n" .
	            "charset={$charset}\n" .
	            "error={$e['message']}"
	        );

	        // 画面用（最小限）
	        throw new Exception(
	            "DB接続エラー（接続先={$dsn}）"
	        );
	    }
	}


    public static function getInstance(): self
    {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    public function getConnection()
    {
        return $this->conn;
    }

    /**
     * SELECT文を実行し、結果を配列で返す
     */
    public function query(string $sql, array $params = []): array
    {
        $stmt = oci_parse($this->conn, $sql);
        if (!$stmt) {
            $e = oci_error($this->conn);
            throw new Exception('SQLパースエラー: ' . $e['message']);
        }

        foreach ($params as $key => $value) {
            oci_bind_by_name($stmt, $key, $params[$key]);
        }

        if (!oci_execute($stmt)) {
            $e = oci_error($stmt);
            throw new Exception('SQL実行エラー: ' . $e['message']);
        }

        $results = [];
        while ($row = oci_fetch_assoc($stmt)) {
            $results[] = $row;
        }

        oci_free_statement($stmt);
        return $results;
    }

    /**
     * INSERT/UPDATE/DELETE文を実行
     */
    public function execute(string $sql, array $params = []): bool
    {
        $stmt = oci_parse($this->conn, $sql);
        if (!$stmt) {
            $e = oci_error($this->conn);
            throw new Exception('SQLパースエラー: ' . $e['message']);
        }

        foreach ($params as $key => $value) {
            oci_bind_by_name($stmt, $key, $params[$key]);
        }

        if (!oci_execute($stmt, OCI_COMMIT_ON_SUCCESS)) {
            $e = oci_error($stmt);
            oci_free_statement($stmt);
            throw new Exception('SQL実行エラー: ' . $e['message']);
        }

        oci_free_statement($stmt);
        return true;
    }

    /**
     * ページング付きクエリ
     */
    public function queryWithPaging(string $sql, array $params, int $page, int $pageSize): array
    {
        $offset = $page * $pageSize;
        $limit = $pageSize + 1; // 次ページ有無判定用に+1件取得

        $pagedSql = "SELECT * FROM (
            SELECT a.*, ROWNUM rnum FROM (
                {$sql}
            ) a WHERE ROWNUM <= :limit
        ) WHERE rnum > :offset";

        $params[':limit'] = $offset + $limit;
        $params[':offset'] = $offset;

        $results = $this->query($pagedSql, $params);

        $hasMore = count($results) > $pageSize;
        if ($hasMore) {
            array_pop($results);
        }

        return [
            'data' => $results,
            'hasMore' => $hasMore,
            'page' => $page
        ];
    }

    public function __destruct()
    {
        if ($this->conn) {
            oci_close($this->conn);
        }
    }
}
