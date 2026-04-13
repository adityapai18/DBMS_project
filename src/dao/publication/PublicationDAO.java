package dao.publication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PublicationDAO {

    public void insert(Connection conn, int publicationId, String title, String pubType, String periodicity)
            throws SQLException {
        String sql = "INSERT INTO PUBLICATION (publication_id, title, pub_type, periodicity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, publicationId);
            ps.setString(2, title);
            ps.setString(3, pubType);
            if (periodicity != null) {
                ps.setString(4, periodicity);
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.executeUpdate();
        }
    }

    public boolean printCurrentPublication(Connection conn, int publicationId) throws SQLException {
        String query = "SELECT * FROM PUBLICATION WHERE publication_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, publicationId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Publication not found.");
                return false;
            }
            System.out.println("Current → Title: " + rs.getString("title")
                    + " | Type: " + rs.getString("pub_type")
                    + " | Periodicity: " + rs.getString("periodicity"));
            return true;
        }
    }

    public int updatePublication(Connection conn, int publicationId, String title, String pubType, String periodicity)
            throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE PUBLICATION SET ");
        List<Object> params = new ArrayList<>();
        boolean hasField = false;

        if (title != null && !title.isEmpty()) {
            sql.append("title = ?");
            params.add(title);
            hasField = true;
        }
        if (pubType != null && !pubType.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("pub_type = ?");
            params.add(pubType);
            hasField = true;
        }
        if (periodicity != null && !periodicity.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("periodicity = ?");
            params.add(periodicity.equalsIgnoreCase("NULL") ? null : periodicity);
            hasField = true;
        }

        if (!hasField) {
            return 0;
        }
        sql.append(" WHERE publication_id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p == null) {
                    ps.setNull(idx++, Types.VARCHAR);
                } else {
                    ps.setString(idx++, (String) p);
                }
            }
            ps.setInt(idx, publicationId);
            return ps.executeUpdate();
        }
    }
}
