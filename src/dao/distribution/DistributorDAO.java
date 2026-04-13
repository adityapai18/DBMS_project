package dao.distribution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributorDAO {

    public void insert(Connection conn, int id, String name, String category, String street, String city,
                       String state, String zip, String country, String phone, String contact) throws SQLException {
        String sql = "INSERT INTO DISTRIBUTOR "
                + "(distributor_id, name, category, street, city, state, zip, country, phone, contact_person) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setString(4, street);
            ps.setString(5, city);
            ps.setString(6, state);
            ps.setString(7, zip);
            ps.setString(8, country);
            ps.setString(9, phone);
            ps.setString(10, contact);
            ps.executeUpdate();
        }
    }

    public boolean printDistributor(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM DISTRIBUTOR WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Distributor not found.");
                return false;
            }
            System.out.println("Current → " + rs.getString("name")
                    + " | " + rs.getString("category")
                    + " | " + rs.getString("city") + ", " + rs.getString("state"));
            return true;
        }
    }

    public int updateDistributor(Connection conn, int id, String name, String category, String street,
                                 String city, String state, String zip, String country, String phone,
                                 String contact) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE DISTRIBUTOR SET ");
        List<String> params = new ArrayList<>();
        boolean hasField = false;

        if (name != null && !name.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("name = ?");
            params.add(name);
            hasField = true;
        }
        if (category != null && !category.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("category = ?");
            params.add(category);
            hasField = true;
        }
        if (street != null && !street.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("street = ?");
            params.add(street);
            hasField = true;
        }
        if (city != null && !city.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("city = ?");
            params.add(city);
            hasField = true;
        }
        if (state != null && !state.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("state = ?");
            params.add(state);
            hasField = true;
        }
        if (zip != null && !zip.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("zip = ?");
            params.add(zip);
            hasField = true;
        }
        if (country != null && !country.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("country = ?");
            params.add(country);
            hasField = true;
        }
        if (phone != null && !phone.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("phone = ?");
            params.add(phone);
            hasField = true;
        }
        if (contact != null && !contact.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("contact_person = ?");
            params.add(contact);
            hasField = true;
        }

        if (!hasField) return 0;
        sql.append(" WHERE distributor_id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (String p : params) ps.setString(idx++, p);
            ps.setInt(idx, id);
            return ps.executeUpdate();
        }
    }

    public int countOrders(Connection conn, int distributorId) throws SQLException {
        String checkSql = "SELECT COUNT(*) AS cnt FROM DIST_ORDER WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, distributorId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int countDistributorPayments(Connection conn, int distributorId) throws SQLException {
        String checkPay = "SELECT COUNT(*) AS cnt FROM DISTRIBUTOR_PAYMENT WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkPay)) {
            ps.setInt(1, distributorId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        }
    }

    public int delete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM DISTRIBUTOR WHERE distributor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
