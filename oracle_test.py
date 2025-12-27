import os
from dotenv import load_dotenv
import oracledb

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
load_dotenv(os.path.join(BASE_DIR, ".env"))

user = os.getenv("ORACLE_USER")
pw   = os.getenv("ORACLE_PASS")
dsn  = os.getenv("ORACLE_DSN")

print("connecting to:", dsn)

# --- Thin mode (default) ---
conn = oracledb.connect(
    user=user,
    password=pw,
    dsn=dsn
)

cur = conn.cursor()
cur.execute("select sysdate from dual")
print("DB time:", cur.fetchone()[0])

cur.close()
conn.close()
print("Oracle connection OK")
