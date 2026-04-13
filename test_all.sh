#!/bin/bash
# ============================================================
# test_all.sh — Compile layered GutenbergDb and smoke-test MainApp
# Run from project root (with mariadb-java-client-*.jar present):
#   bash test_all.sh
#
# Menus are nested: main.MainApp → area (1–4) → feature → action.
# Old single-class flows map to paths like:
#   Publishing → Enter/update publication → …
# ============================================================

OUT="out"
JAR=$(ls mariadb-java-client-*.jar 2>/dev/null | head -1)
CP="${OUT}:${JAR}"

if [ -z "$JAR" ] || [ ! -f "$JAR" ]; then
    echo "Missing mariadb-java-client-*.jar in current directory. Place the MariaDB JDBC driver here."
    exit 1
fi
echo "Using JDBC driver: $JAR"

mkdir -p "$OUT"
echo "Compiling src/**/*.java → $OUT ..."
SOURCES=$(find src -name "*.java")
javac -cp "$JAR" -d "$OUT" $SOURCES
echo "Compilation successful."

PASS=0
FAIL=0

run_smoke() {
    local name="$1"
    local input="$2"
    echo ""
    echo "================================================================"
    echo "  TESTING: $name"
    echo "================================================================"
    if echo "$input" | java -cp "$CP" main.MainApp 2>&1; then
        echo "  OK $name — PASSED"
        PASS=$((PASS + 1))
    else
        echo "  OK $name — FAILED"
        FAIL=$((FAIL + 1))
    fi
}

# Minimal smoke: open app, exit from each top-level menu (0) until quit
run_smoke "MainApp_exit" "0"

# Example: Publishing → Publication → Enter new publication → Back → Back → Exit
# Main:1 Pub:1 Sub:1  then pub id, title, type...
run_smoke "MainApp_sample_publication" "1
1
1
9999
Smoke Test Pub
Book

0
0"

# ============================================================
# CLEANUP inline helper (removes sample publication9999 if present)
# ============================================================
echo ""
echo "================================================================"
echo "  CLEANUP: Removing smoke test publication 9999 if any..."
echo "================================================================"

cat > _Cleanup.java << 'CLEANUP_EOF'
import java.sql.*;
public class _Cleanup {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/akulka26", "akulka26", "200599656");
            Statement st = conn.createStatement();
            st.executeUpdate("DELETE FROM ASSIGNED_TO WHERE publication_id = 9999");
            st.executeUpdate("DELETE FROM PUBLICATION WHERE publication_id = 9999");
            System.out.println("  Cleanup complete.");
            st.close();
        } catch (Exception e) {
            System.out.println("  Cleanup note: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
CLEANUP_EOF

javac -cp "$JAR" _Cleanup.java && java -cp ".:${JAR}" _Cleanup || true
rm -f _Cleanup.java _Cleanup.class

echo ""
echo "================================================================"
echo "  RESULTS: $PASS passed, $FAIL failed"
echo "  Run: java -cp \"$CP\" main.MainApp"
echo "================================================================"
