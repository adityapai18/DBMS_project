package service;

import dao.production.ProductionDAO;
import dao.publication.BookDAO;
import dao.publication.ContentDAO;
import dao.publication.IssueDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class ProductionService {

    private final BookDAO bookDAO = new BookDAO();
    private final IssueDAO issueDAO = new IssueDAO();
    private final ContentDAO contentDAO = new ContentDAO();
    private final ProductionDAO productionDAO = new ProductionDAO();

    public void runMenu(Connection conn, Scanner sc) throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Production ===");
            System.out.println("1. Manage book editions");
            System.out.println("2. Manage publication issues");
            System.out.println("3. Manage articles");
            System.out.println("4. Manage chapters");
            System.out.println("5. Find books and articles");
            System.out.println("6. Manage staff payments");
            System.out.println("7. Compare two issues");
            System.out.println("0. Back");
            System.out.print("Select: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ignored) {
                continue;
            }
            try {
                switch (choice) {
                    case 1 -> bookEditionMenu(conn, sc);
                    case 2 -> issueMenu(conn, sc);
                    case 3 -> articleMenu(conn, sc);
                    case 4 -> chapterMenu(conn, sc);
                    case 5 -> findWorksMenu(conn, sc);
                    case 6 -> staffPaymentMenu(conn, sc);
                    case 7 -> compareIssues(conn, sc);
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private void bookEditionMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Book Editions ---");
        System.out.println("1. Enter a new book edition");
        System.out.println("2. Update a book edition");
        System.out.println("3. Delete a book edition");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Edition ID: ");
                int edId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Publication ID: ");
                int pubId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Edition number: ");
                int edNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("ISBN (press Enter to skip): ");
                String isbn = sc.nextLine().trim();
                System.out.print("Publication date (yyyy-MM-dd, press Enter to skip): ");
                String dateStr = sc.nextLine().trim();
                Date pubDate = dateStr.isEmpty() ? null : Date.valueOf(dateStr);
                bookDAO.insert(conn, edId, pubId, edNum, isbn, pubDate);
                System.out.println("Book edition inserted successfully.");
            }
            case 2 -> {
                System.out.print("Edition ID to update: ");
                int edId = Integer.parseInt(sc.nextLine().trim());
                if (!bookDAO.printEdition(conn, edId)) return;
                System.out.print("New edition number (press Enter to skip): ");
                String edNumStr = sc.nextLine().trim();
                System.out.print("New ISBN (press Enter to skip, type NULL to clear): ");
                String isbn = sc.nextLine().trim();
                System.out.print("New publication date (yyyy-MM-dd, press Enter to skip, type NULL to clear): ");
                String dateStr = sc.nextLine().trim();
                Integer edNum = edNumStr.isEmpty() ? null : Integer.parseInt(edNumStr);
                int rows = bookDAO.updateEdition(conn, edId, edNum,
                        isbn.isEmpty() ? null : isbn,
                        dateStr.isEmpty() ? null : dateStr);
                if (rows == 0 && edNum == null && isbn.isEmpty() && dateStr.isEmpty()) {
                    System.out.println("Nothing to update.");
                } else {
                    System.out.println(rows > 0 ? "Book edition updated successfully." : "No rows updated.");
                }
            }
            case 3 -> {
                System.out.print("Edition ID to delete: ");
                int edId = Integer.parseInt(sc.nextLine().trim());
                int chapCount = bookDAO.countChapters(conn, edId);
                if (chapCount > 0) {
                    System.out.println("WARNING: Deleting this edition will CASCADE-delete " + chapCount + " chapter(s).");
                }
                if (bookDAO.countOrdersForEdition(conn, edId) > 0) {
                    System.out.println("Cannot delete: distributor orders reference this edition.");
                    return;
                }
                System.out.print("Are you sure you want to delete? (yes/no): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    System.out.println("Delete cancelled.");
                    return;
                }
                int rows = bookDAO.deleteEdition(conn, edId);
                if (rows > 0) System.out.println("Book edition deleted (chapters cascade-deleted).");
                else System.out.println("Edition not found.");
            }
            default -> { }
        }
    }

    private void issueMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Publication Issues ---");
        System.out.println("1. Enter a new publication issue");
        System.out.println("2. Update a publication issue");
        System.out.println("3. Delete a publication issue");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Issue ID: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Publication ID: ");
                int pubId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Issue title: ");
                String title = sc.nextLine().trim();
                System.out.print("Publication date (yyyy-MM-dd, press Enter to skip): ");
                String dateStr = sc.nextLine().trim();
                Date pd = dateStr.isEmpty() ? null : Date.valueOf(dateStr);
                issueDAO.insert(conn, issueId, pubId, title, pd);
                System.out.println("Issue inserted successfully.");
            }
            case 2 -> {
                System.out.print("Issue ID to update: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                if (!issueDAO.printIssue(conn, issueId)) return;
                System.out.print("New title (press Enter to skip): ");
                String title = sc.nextLine().trim();
                System.out.print("New publication date (yyyy-MM-dd, press Enter to skip, type NULL to clear): ");
                String dateStr = sc.nextLine().trim();
                int rows = issueDAO.updateIssue(conn, issueId,
                        title.isEmpty() ? null : title,
                        dateStr.isEmpty() ? null : dateStr);
                if (rows == 0 && title.isEmpty() && dateStr.isEmpty()) {
                    System.out.println("Nothing to update.");
                } else {
                    System.out.println(rows > 0 ? "Issue updated successfully." : "No rows updated.");
                }
            }
            case 3 -> {
                System.out.print("Issue ID to delete: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                int artCount = issueDAO.countArticles(conn, issueId);
                if (artCount > 0) {
                    System.out.println("WARNING: Deleting this issue will CASCADE-delete " + artCount + " article(s).");
                }
                if (issueDAO.countOrdersForIssue(conn, issueId) > 0) {
                    System.out.println("Cannot delete: distributor orders reference this issue.");
                    return;
                }
                System.out.print("Are you sure you want to delete? (yes/no): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    System.out.println("Delete cancelled.");
                    return;
                }
                int rows = issueDAO.deleteIssue(conn, issueId);
                if (rows > 0) System.out.println("Issue deleted (articles cascade-deleted).");
                else System.out.println("Issue not found.");
            }
            default -> { }
        }
    }

    private void articleMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Articles ---");
        System.out.println("1. Enter a new article");
        System.out.println("2. Update article metadata");
        System.out.println("3. Enter or update article full text");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Issue ID: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Article number: ");
                int artNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Title: ");
                String title = sc.nextLine().trim();
                System.out.print("Author person ID: ");
                int personId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Topic (press Enter to skip): ");
                String topic = sc.nextLine().trim();
                System.out.print("Date written (yyyy-MM-dd, press Enter to skip): ");
                String dateStr = sc.nextLine().trim();
                System.out.print("Full text (press Enter to skip): ");
                String fullText = sc.nextLine().trim();
                Date dw = dateStr.isEmpty() ? null : Date.valueOf(dateStr);
                contentDAO.insertArticleFull(conn, issueId, artNum, title, personId, topic, dw, fullText);
                System.out.println("Article inserted successfully.");
            }
            case 2 -> {
                System.out.print("Issue ID: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Article number: ");
                int artNum = Integer.parseInt(sc.nextLine().trim());
                if (!contentDAO.printArticleMeta(conn, issueId, artNum)) return;
                System.out.print("New title (press Enter to skip): ");
                String title = sc.nextLine().trim();
                System.out.print("New author person ID (press Enter to skip): ");
                String personStr = sc.nextLine().trim();
                System.out.print("New topic (press Enter to skip, type NULL to clear): ");
                String topic = sc.nextLine().trim();
                System.out.print("New date written (yyyy-MM-dd, press Enter to skip, type NULL to clear): ");
                String dateStr = sc.nextLine().trim();
                Integer personId = personStr.isEmpty() ? null : Integer.parseInt(personStr);
                int rows = contentDAO.updateArticleMeta(conn, issueId, artNum,
                        title.isEmpty() ? null : title,
                        personId,
                        topic.isEmpty() ? null : topic,
                        dateStr.isEmpty() ? null : dateStr);
                if (rows == 0) System.out.println("Nothing to update.");
                else System.out.println(rows > 0 ? "Article metadata updated." : "No rows updated.");
            }
            case 3 -> {
                System.out.print("Issue ID: ");
                int issueId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Article number: ");
                int artNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter full text: ");
                String fullText = sc.nextLine().trim();
                int rows = contentDAO.updateArticleFullText(conn, issueId, artNum, fullText);
                if (rows > 0) System.out.println("Article full text updated.");
                else System.out.println("Article not found.");
            }
            default -> { }
        }
    }

    private void chapterMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Chapters ---");
        System.out.println("1. Enter a new chapter");
        System.out.println("2. Update chapter metadata");
        System.out.println("3. Enter or update chapter full text");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Edition ID: ");
                int editionId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Chapter number: ");
                int chapNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Title: ");
                String title = sc.nextLine().trim();
                System.out.print("Author person ID: ");
                int personId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Topic (press Enter to skip): ");
                String topic = sc.nextLine().trim();
                System.out.print("Date written (yyyy-MM-dd, press Enter to skip): ");
                String dateStr = sc.nextLine().trim();
                System.out.print("Full text (press Enter to skip): ");
                String fullText = sc.nextLine().trim();
                Date dw = dateStr.isEmpty() ? null : Date.valueOf(dateStr);
                contentDAO.insertChapterFull(conn, editionId, chapNum, title, personId, topic, dw, fullText);
                System.out.println("Chapter inserted successfully.");
            }
            case 2 -> {
                System.out.print("Edition ID: ");
                int editionId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Chapter number: ");
                int chapNum = Integer.parseInt(sc.nextLine().trim());
                if (!contentDAO.printChapterMeta(conn, editionId, chapNum)) return;
                System.out.print("New title (press Enter to skip): ");
                String title = sc.nextLine().trim();
                System.out.print("New author person ID (press Enter to skip): ");
                String personStr = sc.nextLine().trim();
                System.out.print("New topic (press Enter to skip, type NULL to clear): ");
                String topic = sc.nextLine().trim();
                System.out.print("New date written (yyyy-MM-dd, press Enter to skip, type NULL to clear): ");
                String dateStr = sc.nextLine().trim();
                Integer personId = personStr.isEmpty() ? null : Integer.parseInt(personStr);
                int rows = contentDAO.updateChapterMeta(conn, editionId, chapNum,
                        title.isEmpty() ? null : title,
                        personId,
                        topic.isEmpty() ? null : topic,
                        dateStr.isEmpty() ? null : dateStr);
                if (rows == 0) System.out.println("Nothing to update.");
                else System.out.println(rows > 0 ? "Chapter metadata updated." : "No rows updated.");
            }
            case 3 -> {
                System.out.print("Edition ID: ");
                int editionId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Chapter number: ");
                int chapNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter full text: ");
                String fullText = sc.nextLine().trim();
                int rows = contentDAO.updateChapterFullText(conn, editionId, chapNum, fullText);
                if (rows > 0) System.out.println("Chapter full text updated.");
                else System.out.println("Chapter not found.");
            }
            default -> { }
        }
    }

    private void findWorksMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Find Books and Articles ---");
        System.out.println("1. Find by topic");
        System.out.println("2. Find by date range");
        System.out.println("3. Find by author name");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Topic: ");
                String topic = sc.nextLine().trim();
                contentDAO.printFindByTopic(conn, topic);
            }
            case 2 -> {
                System.out.print("Start date (yyyy-MM-dd): ");
                String start = sc.nextLine().trim();
                System.out.print("End date (yyyy-MM-dd): ");
                String end = sc.nextLine().trim();
                contentDAO.printFindByDateRange(conn, Date.valueOf(start), Date.valueOf(end));
            }
            case 3 -> {
                System.out.print("Author name: ");
                String name = sc.nextLine().trim();
                contentDAO.printFindByAuthorName(conn, name);
            }
            default -> { }
        }
    }

    private void staffPaymentMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Staff Payments ---");
        System.out.println("1. Enter payment for author or editor");
        System.out.println("2. Enter payment and record as claimed (Transaction)");
        System.out.println("3. List unclaimed payments within a date range");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1 -> {
                System.out.print("Payment ID: ");
                int payId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Person ID: ");
                int personId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Amount: ");
                double amount = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Issued date (yyyy-MM-dd): ");
                Date issuedDate = Date.valueOf(sc.nextLine().trim());
                System.out.print("Claimed date (yyyy-MM-dd, press Enter if not claimed yet): ");
                String claimedStr = sc.nextLine().trim();
                System.out.print("Contribution reference: ");
                String contribRef = sc.nextLine().trim();
                Date claimed = claimedStr.isEmpty() ? null : Date.valueOf(claimedStr);
                productionDAO.insertStaffPayment(conn, payId, personId, amount, issuedDate, claimed, contribRef);
                System.out.println("Staff payment recorded successfully.");
            }
            case 2 -> {
                System.out.print("Payment ID: ");
                int payId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Person ID: ");
                int personId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Amount: ");
                double amount = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Issued date (yyyy-MM-dd): ");
                Date issuedDate = Date.valueOf(sc.nextLine().trim());
                System.out.print("Contribution reference: ");
                String contribRef = sc.nextLine().trim();
                System.out.print("Claimed date (yyyy-MM-dd): ");
                Date claimedDate = Date.valueOf(sc.nextLine().trim());
                conn.setAutoCommit(false);
                try {
                    productionDAO.insertStaffPaymentAndClaim(conn, payId, personId, amount, issuedDate, contribRef, claimedDate);
                    conn.commit();
                    System.out.println("Transaction committed: payment " + payId + " inserted and claimed on " + claimedDate + ".");
                } catch (SQLException e) {
                    conn.rollback();
                    System.out.println("Transaction rolled back: " + e.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                }
            }
            case 3 -> {
                System.out.print("Start issued date (yyyy-MM-dd): ");
                Date start = Date.valueOf(sc.nextLine().trim());
                System.out.print("End issued date (yyyy-MM-dd): ");
                Date end = Date.valueOf(sc.nextLine().trim());
                productionDAO.printUnclaimedStaffPayments(conn, start, end);
            }
            default -> { }
        }
    }

    private void compareIssues(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Compare Two Issues ---");
        System.out.print("First Issue ID: ");
        int issue1 = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Second Issue ID: ");
        int issue2 = Integer.parseInt(sc.nextLine().trim());
        contentDAO.printCompareIssues(conn, issue1, issue2);
    }
}
