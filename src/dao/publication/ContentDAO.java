package dao.publication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ContentDAO {

    public void insertArticleFull(Connection conn, int issueId, int articleNumber, String title, int personId,
                                  String topic, Date dateWritten, String fullText) throws SQLException {
        String sql = "INSERT INTO ARTICLE (issue_id, article_number, title, person_id, topic, date_written, full_text) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, articleNumber);
            ps.setString(3, title);
            ps.setInt(4, personId);
            if (topic == null || topic.isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, topic);
            if (dateWritten == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, dateWritten);
            if (fullText == null || fullText.isEmpty()) ps.setNull(7, Types.LONGVARCHAR);
            else ps.setString(7, fullText);
            ps.executeUpdate();
        }
    }

    public boolean printArticleMeta(Connection conn, int issueId, int articleNumber) throws SQLException {
        String query = "SELECT A.*, P.name AS author_name FROM ARTICLE A "
                + "JOIN PERSON P ON A.person_id = P.person_id "
                + "WHERE A.issue_id = ? AND A.article_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, issueId);
            ps.setInt(2, articleNumber);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Article not found.");
                return false;
            }
            System.out.println("Current → Title: " + rs.getString("title")
                    + " | Author: " + rs.getString("author_name") + " (ID " + rs.getInt("person_id") + ")"
                    + " | Topic: " + rs.getString("topic")
                    + " | Date: " + rs.getDate("date_written"));
            return true;
        }
    }

    public int updateArticleMeta(Connection conn, int issueId, int articleNumber,
                                 String title, Integer personId, String topic, String dateStr) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ARTICLE SET ");
        boolean hasField = false;

        if (title != null && !title.isEmpty()) {
            sql.append("title = ?");
            hasField = true;
        }
        if (personId != null) {
            if (hasField) sql.append(", ");
            sql.append("person_id = ?");
            hasField = true;
        }
        if (topic != null && !topic.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("topic = ?");
            hasField = true;
        }
        if (dateStr != null && !dateStr.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("date_written = ?");
            hasField = true;
        }

        if (!hasField) return 0;
        sql.append(" WHERE issue_id = ? AND article_number = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (title != null && !title.isEmpty()) ps.setString(idx++, title);
            if (personId != null) ps.setInt(idx++, personId);
            if (topic != null && !topic.isEmpty()) {
                if (topic.equalsIgnoreCase("NULL")) ps.setNull(idx++, Types.VARCHAR);
                else ps.setString(idx++, topic);
            }
            if (dateStr != null && !dateStr.isEmpty()) {
                if (dateStr.equalsIgnoreCase("NULL")) ps.setNull(idx++, Types.DATE);
                else ps.setDate(idx++, Date.valueOf(dateStr));
            }
            ps.setInt(idx++, issueId);
            ps.setInt(idx, articleNumber);
            return ps.executeUpdate();
        }
    }

    public int updateArticleFullText(Connection conn, int issueId, int articleNumber, String fullText)
            throws SQLException {
        String sql = "UPDATE ARTICLE SET full_text = ? WHERE issue_id = ? AND article_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullText);
            ps.setInt(2, issueId);
            ps.setInt(3, articleNumber);
            return ps.executeUpdate();
        }
    }

    public void insertChapterFull(Connection conn, int editionId, int chapterNumber, String title, int personId,
                                  String topic, Date dateWritten, String fullText) throws SQLException {
        String sql = "INSERT INTO CHAPTER (edition_id, chapter_number, title, person_id, topic, date_written, full_text) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editionId);
            ps.setInt(2, chapterNumber);
            ps.setString(3, title);
            ps.setInt(4, personId);
            if (topic == null || topic.isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, topic);
            if (dateWritten == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, dateWritten);
            if (fullText == null || fullText.isEmpty()) ps.setNull(7, Types.LONGVARCHAR);
            else ps.setString(7, fullText);
            ps.executeUpdate();
        }
    }

    public boolean printChapterMeta(Connection conn, int editionId, int chapterNumber) throws SQLException {
        String query = "SELECT C.*, P.name AS author_name FROM CHAPTER C "
                + "JOIN PERSON P ON C.person_id = P.person_id "
                + "WHERE C.edition_id = ? AND C.chapter_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, editionId);
            ps.setInt(2, chapterNumber);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Chapter not found.");
                return false;
            }
            System.out.println("Current → Title: " + rs.getString("title")
                    + " | Author: " + rs.getString("author_name") + " (ID " + rs.getInt("person_id") + ")"
                    + " | Topic: " + rs.getString("topic")
                    + " | Date: " + rs.getDate("date_written"));
            return true;
        }
    }

    public int updateChapterMeta(Connection conn, int editionId, int chapterNumber,
                                 String title, Integer personId, String topic, String dateStr) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE CHAPTER SET ");
        boolean hasField = false;

        if (title != null && !title.isEmpty()) {
            sql.append("title = ?");
            hasField = true;
        }
        if (personId != null) {
            if (hasField) sql.append(", ");
            sql.append("person_id = ?");
            hasField = true;
        }
        if (topic != null && !topic.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("topic = ?");
            hasField = true;
        }
        if (dateStr != null && !dateStr.isEmpty()) {
            if (hasField) sql.append(", ");
            sql.append("date_written = ?");
            hasField = true;
        }

        if (!hasField) return 0;
        sql.append(" WHERE edition_id = ? AND chapter_number = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (title != null && !title.isEmpty()) ps.setString(idx++, title);
            if (personId != null) ps.setInt(idx++, personId);
            if (topic != null && !topic.isEmpty()) {
                if (topic.equalsIgnoreCase("NULL")) ps.setNull(idx++, Types.VARCHAR);
                else ps.setString(idx++, topic);
            }
            if (dateStr != null && !dateStr.isEmpty()) {
                if (dateStr.equalsIgnoreCase("NULL")) ps.setNull(idx++, Types.DATE);
                else ps.setDate(idx++, Date.valueOf(dateStr));
            }
            ps.setInt(idx++, editionId);
            ps.setInt(idx, chapterNumber);
            return ps.executeUpdate();
        }
    }

    public int updateChapterFullText(Connection conn, int editionId, int chapterNumber, String fullText)
            throws SQLException {
        String sql = "UPDATE CHAPTER SET full_text = ? WHERE edition_id = ? AND chapter_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullText);
            ps.setInt(2, editionId);
            ps.setInt(3, chapterNumber);
            return ps.executeUpdate();
        }
    }

    public void insertArticleToc(Connection conn, int issueId, int articleNumber, String title, int personId,
                                 String topic, Date dateWritten) throws SQLException {
        String sql = "INSERT INTO ARTICLE (issue_id, article_number, title, person_id, topic, date_written) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, articleNumber);
            ps.setString(3, title);
            ps.setInt(4, personId);
            if (topic == null || topic.isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, topic);
            if (dateWritten == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, dateWritten);
            ps.executeUpdate();
        }
    }

    public int deleteArticle(Connection conn, int issueId, int articleNumber) throws SQLException {
        String sql = "DELETE FROM ARTICLE WHERE issue_id = ? AND article_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, articleNumber);
            return ps.executeUpdate();
        }
    }

    public void insertChapterToc(Connection conn, int editionId, int chapterNumber, String title, int personId,
                                   String topic, Date dateWritten) throws SQLException {
        String sql = "INSERT INTO CHAPTER (edition_id, chapter_number, title, person_id, topic, date_written) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editionId);
            ps.setInt(2, chapterNumber);
            ps.setString(3, title);
            ps.setInt(4, personId);
            if (topic == null || topic.isEmpty()) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, topic);
            if (dateWritten == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, dateWritten);
            ps.executeUpdate();
        }
    }

    public int deleteChapter(Connection conn, int editionId, int chapterNumber) throws SQLException {
        String sql = "DELETE FROM CHAPTER WHERE edition_id = ? AND chapter_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, editionId);
            ps.setInt(2, chapterNumber);
            return ps.executeUpdate();
        }
    }

    public void printFindByTopic(Connection conn, String topic) throws SQLException {
        String sql = "SELECT 'Article' AS type, A.title, P.title AS publication, A.date_written "
                + "FROM ARTICLE A "
                + "JOIN ISSUE I ON A.issue_id = I.issue_id "
                + "JOIN PUBLICATION P ON I.publication_id = P.publication_id "
                + "WHERE A.topic = ? "
                + "UNION "
                + "SELECT 'Chapter' AS type, C.title, P.title AS publication, C.date_written "
                + "FROM CHAPTER C "
                + "JOIN BOOK_EDITION BE ON C.edition_id = BE.edition_id "
                + "JOIN PUBLICATION P ON BE.publication_id = P.publication_id "
                + "WHERE C.topic = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, topic);
            ps.setString(2, topic);
            printWorkResults(ps.executeQuery());
        }
    }

    public void printFindByDateRange(Connection conn, Date start, Date end) throws SQLException {
        String sql = "SELECT 'Article' AS type, A.title, P.title AS publication, A.date_written "
                + "FROM ARTICLE A "
                + "JOIN ISSUE I ON A.issue_id = I.issue_id "
                + "JOIN PUBLICATION P ON I.publication_id = P.publication_id "
                + "WHERE A.date_written BETWEEN ? AND ? "
                + "UNION "
                + "SELECT 'Chapter' AS type, C.title, P.title AS publication, C.date_written "
                + "FROM CHAPTER C "
                + "JOIN BOOK_EDITION BE ON C.edition_id = BE.edition_id "
                + "JOIN PUBLICATION P ON BE.publication_id = P.publication_id "
                + "WHERE C.date_written BETWEEN ? AND ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, start);
            ps.setDate(2, end);
            ps.setDate(3, start);
            ps.setDate(4, end);
            printWorkResults(ps.executeQuery());
        }
    }

    public void printFindByAuthorName(Connection conn, String name) throws SQLException {
        String sql = "SELECT 'Article' AS type, A.title, P.title AS publication, A.date_written "
                + "FROM ARTICLE A "
                + "JOIN ISSUE I ON A.issue_id = I.issue_id "
                + "JOIN PUBLICATION P ON I.publication_id = P.publication_id "
                + "WHERE A.person_id = (SELECT person_id FROM PERSON WHERE name = ?) "
                + "UNION "
                + "SELECT 'Chapter' AS type, C.title, P.title AS publication, C.date_written "
                + "FROM CHAPTER C "
                + "JOIN BOOK_EDITION BE ON C.edition_id = BE.edition_id "
                + "JOIN PUBLICATION P ON BE.publication_id = P.publication_id "
                + "WHERE C.person_id = (SELECT person_id FROM PERSON WHERE name = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, name);
            printWorkResults(ps.executeQuery());
        }
    }

    private static void printWorkResults(ResultSet rs) throws SQLException {
        System.out.printf("%-10s %-35s %-30s %-12s%n", "Type", "Title", "Publication", "Date Written");
        System.out.println("-".repeat(87));
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-10s %-35s %-30s %-12s%n",
                    rs.getString("type"),
                    rs.getString("title"),
                    rs.getString("publication"),
                    rs.getDate("date_written"));
        }
        if (!found) System.out.println("No results found.");
    }

    public void printCompareIssues(Connection conn, int issue1, int issue2) throws SQLException {
        String sql = "SELECT I.issue_id, I.issue_title, A.article_number, A.title "
                + "FROM ISSUE I "
                + "JOIN ARTICLE A ON I.issue_id = A.issue_id "
                + "WHERE I.issue_id IN (?, ?) "
                + "ORDER BY I.issue_id, A.article_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issue1);
            ps.setInt(2, issue2);
            ResultSet rs = ps.executeQuery();
            int currentIssue = -1;
            boolean found = false;
            while (rs.next()) {
                found = true;
                int iid = rs.getInt("issue_id");
                if (iid != currentIssue) {
                    currentIssue = iid;
                    System.out.println("\n--- Issue " + iid + ": " + rs.getString("issue_title") + " ---");
                    System.out.printf("  %-6s %-50s%n", "Art#", "Title");
                    System.out.println("  " + "-".repeat(56));
                }
                System.out.printf("  %-6d %-50s%n",
                        rs.getInt("article_number"),
                        rs.getString("title"));
            }
            if (!found) System.out.println("No articles found for the given issue IDs.");
        }
    }
}
