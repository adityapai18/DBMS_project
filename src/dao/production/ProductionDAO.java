package dao.production;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ProductionDAO {

    public void assignEditor(Connection conn, int publicationId, int personId) throws SQLException {
        String sql = "INSERT INTO ASSIGNED_TO (publication_id, person_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            ps.setInt(2, personId);
            ps.executeUpdate();
        }
    }

    public int removeEditor(Connection conn, int publicationId, int personId) throws SQLException {
        String sql = "DELETE FROM ASSIGNED_TO WHERE publication_id = ? AND person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            ps.setInt(2, personId);
            return ps.executeUpdate();
        }
    }

    public void printEditorPublications(Connection conn, int personId) throws SQLException {
        String sql = "SELECT P.publication_id, P.title, P.pub_type "
                + "FROM PUBLICATION P "
                + "JOIN ASSIGNED_TO A ON P.publication_id = A.publication_id "
                + "WHERE A.person_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-6s %-40s %-12s%n", "ID", "Title", "Type");
            System.out.println("-".repeat(58));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6d %-40s %-12s%n",
                        rs.getInt("publication_id"),
                        rs.getString("title"),
                        rs.getString("pub_type"));
            }
            if (!found) System.out.println("No publications found for editor " + personId + ".");
        }
    }

    public void insertStaffPayment(Connection conn, int paymentId, int personId, double amount,
                                   Date issuedDate, Date claimedDate, String contributionReference)
            throws SQLException {
        String sql = "INSERT INTO STAFF_PAYMENT "
                + "(payment_id, person_id, amount, issued_date, claimed_date, contribution_reference) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, personId);
            ps.setDouble(3, amount);
            ps.setDate(4, issuedDate);
            if (claimedDate == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, claimedDate);
            if (contributionReference == null || contributionReference.isEmpty()) ps.setNull(6, Types.VARCHAR);
            else ps.setString(6, contributionReference);
            ps.executeUpdate();
        }
    }

    public void insertStaffPaymentAndClaim(Connection conn, int paymentId, int personId, double amount,
                                           Date issuedDate, String contributionReference, Date claimedDate)
            throws SQLException {
        String insertSql = "INSERT INTO STAFF_PAYMENT "
                + "(payment_id, person_id, amount, issued_date, claimed_date, contribution_reference) "
                + "VALUES (?, ?, ?, ?, NULL, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, personId);
            ps.setDouble(3, amount);
            ps.setDate(4, issuedDate);
            if (contributionReference == null || contributionReference.isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, contributionReference);
            ps.executeUpdate();
        }
        String updateSql = "UPDATE STAFF_PAYMENT SET claimed_date = ? WHERE payment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDate(1, claimedDate);
            ps.setInt(2, paymentId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Failed to update claimed date — payment not found after insert.");
        }
    }

    public void printUnclaimedStaffPayments(Connection conn, Date start, Date end) throws SQLException {
        String sql = "SELECT SP.payment_id, SP.person_id, P.name, SP.amount, "
                + "SP.issued_date, SP.contribution_reference "
                + "FROM STAFF_PAYMENT SP "
                + "JOIN PERSON P ON SP.person_id = P.person_id "
                + "WHERE SP.claimed_date IS NULL AND SP.issued_date BETWEEN ? AND ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, start);
            ps.setDate(2, end);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-8s %-6s %-20s %-10s %-12s %-30s%n",
                    "PayID", "PID", "Name", "Amount", "Issued", "Reference");
            System.out.println("-".repeat(86));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-8d %-6d %-20s $%-9.2f %-12s %-30s%n",
                        rs.getInt("payment_id"),
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getDouble("amount"),
                        rs.getDate("issued_date"),
                        rs.getString("contribution_reference"));
            }
            if (!found) System.out.println("No unclaimed payments found in the given range.");
        }
    }
}
