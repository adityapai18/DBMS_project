package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection factory.
 * <p>The default URL points at the NCSU class MariaDB host, which typically only resolves when you are on
 * campus network or the university VPN. Override with env vars {@code JDBC_URL}, {@code JDBC_USER},
 * {@code JDBC_PASSWORD} for a local or tunnelled database.</p>
 */
public final class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/akulka26";
    private static final String DEFAULT_USER = "akulka26";
    private static final String DEFAULT_PASSWORD = "200599656";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        String url = firstNonBlank(System.getenv("JDBC_URL"), DEFAULT_URL);
        String user = firstNonBlank(System.getenv("JDBC_USER"), DEFAULT_USER);
        String pass = firstNonBlank(System.getenv("JDBC_PASSWORD"), DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, pass);
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.trim().isEmpty()) return a;
        return b;
    }

    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
                // ignore
            }
        }
    }
}
