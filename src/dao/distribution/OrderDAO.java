package dao.distribution;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class OrderDAO {

    public void insertOrder(Connection conn, int orderId, int distributorId, Integer editionId, Integer issueId,
                            int quantity, double price, double shipping, Date requiredDate) throws SQLException {
        String sql = "INSERT INTO DIST_ORDER "
                + "(order_id, distributor_id, edition_id, issue_id, quantity, price, shipping_cost, required_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, distributorId);
            if (editionId != null) ps.setInt(3, editionId);
            else ps.setNull(3, Types.INTEGER);
            if (issueId != null) ps.setInt(4, issueId);
            else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, quantity);
            ps.setDouble(6, price);
            ps.setDouble(7, shipping);
            ps.setDate(8, requiredDate);
            ps.executeUpdate();
        }
    }

    public static class BillingInfo {
        public final int distributorId;
        public final double billedAmount;

        public BillingInfo(int distributorId, double billedAmount) {
            this.distributorId = distributorId;
            this.billedAmount = billedAmount;
        }
    }

    public enum BillingStatus { NOT_FOUND, ALREADY_BILLED, OK }

    public static class BillingLookup {
        public final BillingStatus status;
        public final BillingInfo info;

        private BillingLookup(BillingStatus status, BillingInfo info) {
            this.status = status;
            this.info = info;
        }

        public static BillingLookup notFound() {
            return new BillingLookup(BillingStatus.NOT_FOUND, null);
        }

        public static BillingLookup alreadyBilled() {
            return new BillingLookup(BillingStatus.ALREADY_BILLED, null);
        }

        public static BillingLookup ok(BillingInfo info) {
            return new BillingLookup(BillingStatus.OK, info);
        }
    }

    public BillingLookup fetchOrderForBilling(Connection conn, int orderId) throws SQLException {
        String checkSql = "SELECT distributor_id, quantity, price, shipping_cost, billed_amount "
                + "FROM DIST_ORDER WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return BillingLookup.notFound();
            if (rs.getObject("billed_amount") != null) {
                System.out.println("Order is already billed (amount: $" + rs.getBigDecimal("billed_amount") + ").");
                return BillingLookup.alreadyBilled();
            }
            int distId = rs.getInt("distributor_id");
            double billedAmount = rs.getInt("quantity") * rs.getDouble("price") + rs.getDouble("shipping_cost");
            return BillingLookup.ok(new BillingInfo(distId, billedAmount));
        }
    }

    public void setBilledAmountFromLineItems(Connection conn, int orderId) throws SQLException {
        String updateSql = "UPDATE DIST_ORDER SET billed_amount = (quantity * price) + shipping_cost WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
}
