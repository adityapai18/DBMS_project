package dao.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReportDAO {

    public void printTotalRevenue(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(billed_amount), 0) AS total_revenue "
                + "FROM DIST_ORDER WHERE billed_amount IS NOT NULL";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            System.out.printf("Total Revenue: $%.2f%n", rs.getDouble("total_revenue"));
        }
    }

    public void printTotalExpenses(Connection conn) throws SQLException {
        String sql = "SELECT "
                + "  (SELECT COALESCE(SUM(amount), 0) FROM STAFF_PAYMENT) + "
                + "  (SELECT COALESCE(SUM(shipping_cost), 0) FROM DIST_ORDER) AS total_expenses";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            System.out.printf("Total Expenses: $%.2f%n", rs.getDouble("total_expenses"));
        }
    }

    public void printRevenuePerCity(Connection conn) throws SQLException {
        String sql = "SELECT D.city, SUM(DP.amount) AS total_revenue "
                + "FROM DISTRIBUTOR D "
                + "JOIN DISTRIBUTOR_PAYMENT DP ON D.distributor_id = DP.distributor_id "
                + "GROUP BY D.city "
                + "ORDER BY total_revenue DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-20s %-15s%n", "City", "Revenue");
            System.out.println("-".repeat(35));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-20s $%-14.2f%n",
                        rs.getString("city"),
                        rs.getDouble("total_revenue"));
            }
            if (!found) System.out.println("No revenue data found.");
        }
    }

    public void printRevenuePerDistributor(Connection conn) throws SQLException {
        String sql = "SELECT D.name, SUM(DP.amount) AS total_revenue "
                + "FROM DISTRIBUTOR D "
                + "JOIN DISTRIBUTOR_PAYMENT DP ON D.distributor_id = DP.distributor_id "
                + "GROUP BY D.distributor_id, D.name "
                + "ORDER BY total_revenue DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-30s %-15s%n", "Distributor", "Revenue");
            System.out.println("-".repeat(45));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-30s $%-14.2f%n",
                        rs.getString("name"),
                        rs.getDouble("total_revenue"));
            }
            if (!found) System.out.println("No revenue data found.");
        }
    }

    public void printDistributorMismatches(Connection conn) throws SQLException {
        // Subqueries avoid the fan-out that would occur if a single order has multiple
        // PAYMENT_ALLOCATION rows and we joined DO with PA directly (billed_amount
        // would be counted once per allocation row, inflating total_billed).
        String sql = "SELECT D.distributor_id, D.name, "
                + "  COALESCE((SELECT SUM(DO.billed_amount) "
                + "            FROM DIST_ORDER DO WHERE DO.distributor_id = D.distributor_id), 0) AS total_billed, "
                + "  COALESCE((SELECT SUM(PA.allocated_amount) "
                + "            FROM PAYMENT_ALLOCATION PA "
                + "            JOIN DIST_ORDER DO2 ON PA.order_id = DO2.order_id "
                + "            WHERE DO2.distributor_id = D.distributor_id), 0) AS total_paid "
                + "FROM DISTRIBUTOR D "
                + "GROUP BY D.distributor_id, D.name "
                + "HAVING total_billed <> total_paid";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-6s %-30s %-15s %-15s %-15s%n",
                    "ID", "Name", "Total Billed", "Total Paid", "Difference");
            System.out.println("-".repeat(81));
            boolean found = false;
            while (rs.next()) {
                found = true;
                double billed = rs.getDouble("total_billed");
                double paid = rs.getDouble("total_paid");
                System.out.printf("%-6d %-30s $%-14.2f $%-14.2f $%-14.2f%n",
                        rs.getInt("distributor_id"),
                        rs.getString("name"),
                        billed, paid, billed - paid);
            }
            if (!found) System.out.println("All distributors are balanced — no mismatches.");
        }
    }

    public void printDistributorsByTypeAndCity(Connection conn, String category, String city) throws SQLException {
        String sql = "SELECT * FROM DISTRIBUTOR WHERE category = ? AND city = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setString(2, city);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-6s %-25s %-12s %-20s %-15s %-15s%n",
                    "ID", "Name", "Category", "Street", "Phone", "Contact");
            System.out.println("-".repeat(93));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6d %-25s %-12s %-20s %-15s %-15s%n",
                        rs.getInt("distributor_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("street"),
                        rs.getString("phone"),
                        rs.getString("contact_person"));
            }
            if (!found) System.out.println("No distributors found for " + category + " in " + city + ".");
        }
    }

    public void printWeeklyOrders(Connection conn) throws SQLException {
        String sql = "SELECT D.name AS distributor, "
                + "  YEAR(DO.required_date) AS order_year, "
                + "  WEEK(DO.required_date) AS order_week, "
                + "  SUM(DO.quantity) AS total_copies, "
                + "  SUM(DO.quantity * DO.price) AS total_price "
                + "FROM DIST_ORDER DO "
                + "JOIN DISTRIBUTOR D ON DO.distributor_id = D.distributor_id "
                + "GROUP BY D.name, YEAR(DO.required_date), WEEK(DO.required_date) "
                + "ORDER BY D.name, order_year, order_week";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-25s %-6s %-6s %-12s %-12s%n",
                    "Distributor", "Year", "Week", "Copies", "Total Price");
            System.out.println("-".repeat(61));
            while (rs.next()) {
                System.out.printf("%-25s %-6d %-6d %-12d $%-11.2f%n",
                        rs.getString("distributor"),
                        rs.getInt("order_year"),
                        rs.getInt("order_week"),
                        rs.getInt("total_copies"),
                        rs.getDouble("total_price"));
            }
        }
    }

    public void printMonthlyOrders(Connection conn) throws SQLException {
        String sql = "SELECT D.name AS distributor, "
                + "  YEAR(DO.required_date) AS order_year, "
                + "  MONTH(DO.required_date) AS order_month, "
                + "  SUM(DO.quantity) AS total_copies, "
                + "  SUM(DO.quantity * DO.price) AS total_price "
                + "FROM DIST_ORDER DO "
                + "JOIN DISTRIBUTOR D ON DO.distributor_id = D.distributor_id "
                + "GROUP BY D.name, YEAR(DO.required_date), MONTH(DO.required_date) "
                + "ORDER BY D.name, order_year, order_month";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-25s %-6s %-6s %-12s %-12s%n",
                    "Distributor", "Year", "Month", "Copies", "Total Price");
            System.out.println("-".repeat(61));
            while (rs.next()) {
                System.out.printf("%-25s %-6d %-6d %-12d $%-11.2f%n",
                        rs.getString("distributor"),
                        rs.getInt("order_year"),
                        rs.getInt("order_month"),
                        rs.getInt("total_copies"),
                        rs.getDouble("total_price"));
            }
        }
    }

    public void printDistributorCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_distributors FROM DISTRIBUTOR";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            System.out.println("Total current number of distributors: " + rs.getInt("total_distributors"));
        }
    }

    public void printStaffPaymentsPerMonth(Connection conn) throws SQLException {
        String sql = "SELECT DATE_FORMAT(issued_date, '%Y-%m') AS payment_month, "
                + "  SUM(amount) AS total_paid "
                + "FROM STAFF_PAYMENT "
                + "GROUP BY payment_month "
                + "ORDER BY payment_month";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-12s %-15s%n", "Month", "Total Paid");
            System.out.println("-".repeat(27));
            while (rs.next()) {
                System.out.printf("%-12s $%-14.2f%n",
                        rs.getString("payment_month"),
                        rs.getDouble("total_paid"));
            }
        }
    }

    public void printStaffPaymentsPerWorkType(Connection conn) throws SQLException {
        String sql = "SELECT "
                + "  CASE "
                + "    WHEN contribution_reference LIKE '%Chapter%' THEN 'Book Authorship' "
                + "    WHEN contribution_reference LIKE '%Article%' THEN 'Article Authorship' "
                + "    WHEN contribution_reference LIKE '%Salary%' THEN 'Editorial Work' "
                + "    ELSE 'Other' "
                + "  END AS work_type, "
                + "  SUM(amount) AS total_paid "
                + "FROM STAFF_PAYMENT "
                + "GROUP BY work_type "
                + "ORDER BY total_paid DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-25s %-15s%n", "Work Type", "Total Paid");
            System.out.println("-".repeat(40));
            while (rs.next()) {
                System.out.printf("%-25s $%-14.2f%n",
                        rs.getString("work_type"),
                        rs.getDouble("total_paid"));
            }
        }
    }
}
