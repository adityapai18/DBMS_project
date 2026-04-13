package dao.payment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDAO {

    public void insertDistributorPayment(Connection conn, int paymentId, int distributorId, double amount,
                                         Date paymentDate) throws SQLException {
        String sql = "INSERT INTO DISTRIBUTOR_PAYMENT (payment_id, distributor_id, amount, payment_date) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, distributorId);
            ps.setDouble(3, amount);
            ps.setDate(4, paymentDate);
            ps.executeUpdate();
        }
    }

    public void insertAllocation(Connection conn, int paymentId, int orderId, double amount) throws SQLException {
        String sql = "INSERT INTO PAYMENT_ALLOCATION (payment_id, order_id, allocated_amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, orderId);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    public void printOutstandingBalance(Connection conn, int distributorId) throws SQLException {
        String sql = "SELECT "
                + "  COALESCE(SUM(DO.billed_amount), 0) AS total_billed, "
                + "  COALESCE((SELECT SUM(PA.allocated_amount) "
                + "            FROM PAYMENT_ALLOCATION PA "
                + "            JOIN DIST_ORDER DO2 ON PA.order_id = DO2.order_id "
                + "            WHERE DO2.distributor_id = ?), 0) AS total_allocated "
                + "FROM DIST_ORDER DO "
                + "WHERE DO.distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, distributorId);
            ps.setInt(2, distributorId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            double billed = rs.getDouble("total_billed");
            double allocated = rs.getDouble("total_allocated");
            double balance = billed - allocated;
            System.out.printf("Total Billed:    $%.2f%n", billed);
            System.out.printf("Total Allocated: $%.2f%n", allocated);
            System.out.printf("Outstanding:     $%.2f%n", balance);
        }
    }
}
