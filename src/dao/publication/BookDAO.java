package dao.publication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void insert(Connection conn, int editionId, int publicationId, int editionNumber,
                       String isbn, Date publicationDate) throws SQLException {
        String sql = "INSERT INTO BOOK_EDITION (edition_id, publication_id, edition_number, ISBN, publication_date) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editionId);
            ps.setInt(2, publicationId);
            ps.setInt(3, editionNumber);
            if (isbn == null || isbn.isEmpty()) ps.setNull(4, Types.VARCHAR);
            else ps.setString(4, isbn);
            if (publicationDate == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, publicationDate);
            ps.executeUpdate();
        }
    }

    public boolean printEdition(Connection conn, int editionId) throws SQLException {
        String query = "SELECT * FROM BOOK_EDITION WHERE edition_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, editionId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Edition not found.");
                return false;
            }
            System.out.println("Current → Edition #" + rs.getInt("edition_number")
                    + " | ISBN: " + rs.getString("ISBN")
                    + " | Date: " + rs.getDate("publication_date"));
            return true;
        }
    }

    public int updateEdition(Connection conn, int editionId, Integer editionNumber, String isbn, String dateStr)
            throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE BOOK_EDITION SET ");
        List<Object[]> params = new ArrayList<>();
        boolean hasField = false;

        if (editionNumber != null) {
            sql.append("edition_number = ?");
            params.add(new Object[]{"INT", editionNumber});
            hasField = true;
        }
        if (isbn != null && !isbn.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("ISBN = ?");
            params.add(new Object[]{"STRING", isbn.equalsIgnoreCase("NULL") ? null : isbn});
            hasField = true;
        }
        if (dateStr != null && !dateStr.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("publication_date = ?");
            params.add(new Object[]{"DATE", dateStr.equalsIgnoreCase("NULL") ? null : dateStr});
            hasField = true;
        }

        if (!hasField) return 0;
        sql.append(" WHERE edition_id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object[] p : params) {
                String type = (String) p[0];
                Object val = p[1];
                if (val == null) {
                    ps.setNull(idx++, type.equals("DATE") ? Types.DATE : Types.VARCHAR);
                } else if (type.equals("INT")) {
                    ps.setInt(idx++, (Integer) val);
                } else if (type.equals("DATE")) {
                    ps.setDate(idx++, Date.valueOf((String) val));
                } else {
                    ps.setString(idx++, (String) val);
                }
            }
            ps.setInt(idx, editionId);
            return ps.executeUpdate();
        }
    }

    public int countChapters(Connection conn, int editionId) throws SQLException {
        String countSql = "SELECT COUNT(*) AS cnt FROM CHAPTER WHERE edition_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, editionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int countOrdersForEdition(Connection conn, int editionId) throws SQLException {
        String orderSql = "SELECT COUNT(*) AS cnt FROM DIST_ORDER WHERE edition_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
            ps.setInt(1, editionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int deleteEdition(Connection conn, int editionId) throws SQLException {
        String sql = "DELETE FROM BOOK_EDITION WHERE edition_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editionId);
            return ps.executeUpdate();
        }
    }
}
