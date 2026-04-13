package dao.publication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IssueDAO {

    public void insert(Connection conn, int issueId, int publicationId, String issueTitle, Date publicationDate)
            throws SQLException {
        String sql = "INSERT INTO ISSUE (issue_id, publication_id, issue_title, publication_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, publicationId);
            ps.setString(3, issueTitle);
            if (publicationDate == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, publicationDate);
            ps.executeUpdate();
        }
    }

    public boolean printIssue(Connection conn, int issueId) throws SQLException {
        String query = "SELECT * FROM ISSUE WHERE issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Issue not found.");
                return false;
            }
            System.out.println("Current → Title: " + rs.getString("issue_title")
                    + " | Date: " + rs.getDate("publication_date"));
            return true;
        }
    }

    public int updateIssue(Connection conn, int issueId, String title, String dateStr) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ISSUE SET ");
        boolean hasField = false;

        if (title != null && !title.isEmpty()) {
            sql.append("issue_title = ?");
            hasField = true;
        }
        if (dateStr != null && !dateStr.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("publication_date = ?");
            hasField = true;
        }

        if (!hasField) return 0;
        sql.append(" WHERE issue_id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (title != null && !title.isEmpty()) ps.setString(idx++, title);
            if (dateStr != null && !dateStr.isEmpty()) {
                if (dateStr.equalsIgnoreCase("NULL")) ps.setNull(idx++, Types.DATE);
                else ps.setDate(idx++, Date.valueOf(dateStr));
            }
            ps.setInt(idx, issueId);
            return ps.executeUpdate();
        }
    }

    public int countArticles(Connection conn, int issueId) throws SQLException {
        String countSql = "SELECT COUNT(*) AS cnt FROM ARTICLE WHERE issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int countOrdersForIssue(Connection conn, int issueId) throws SQLException {
        String orderSql = "SELECT COUNT(*) AS cnt FROM DIST_ORDER WHERE issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int deleteIssue(Connection conn, int issueId) throws SQLException {
        String sql = "DELETE FROM ISSUE WHERE issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            return ps.executeUpdate();
        }
    }
}
