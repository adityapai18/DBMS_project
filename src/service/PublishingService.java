package service;

import dao.production.ProductionDAO;
import dao.publication.ContentDAO;
import dao.publication.PublicationDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class PublishingService {

    private final PublicationDAO publicationDAO = new PublicationDAO();
    private final ProductionDAO productionDAO = new ProductionDAO();
    private final ContentDAO contentDAO = new ContentDAO();

    public void runMenu(Connection conn, Scanner sc) throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Publishing ===");
            System.out.println("1. Enter / update publication");
            System.out.println("2. Assign / remove editor");
            System.out.println("3. Edit table of contents");
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
                    case 1:
                        publicationSubmenu(conn, sc);
                        break;
                    case 2:
                        editorSubmenu(conn, sc);
                        break;
                    case 3:
                        tocSubmenu(conn, sc);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private void publicationSubmenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Publication ---");
        System.out.println("1. Enter a new publication");
        System.out.println("2. Update an existing publication");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1:
                System.out.print("Publication ID: ");
                int pubId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Title: ");
                String title = sc.nextLine().trim();
                System.out.print("Type (Book / Magazine / Journal): ");
                String pubType = sc.nextLine().trim();
                String periodicity = null;
                if (pubType.equalsIgnoreCase("Magazine") || pubType.equalsIgnoreCase("Journal")) {
                    System.out.print("Periodicity (e.g. Weekly, Monthly, Quarterly): ");
                    periodicity = sc.nextLine().trim();
                }
                publicationDAO.insert(conn, pubId, title, pubType, periodicity);
                System.out.println("Publication inserted successfully.");
                break;
            case 2:
                System.out.print("Publication ID to update: ");
                int pubId2 = Integer.parseInt(sc.nextLine().trim());
                if (!publicationDAO.printCurrentPublication(conn, pubId2)) return;
                System.out.print("New title (press Enter to skip): ");
                String title2 = sc.nextLine().trim();
                System.out.print("New type (Book/Magazine/Journal, press Enter to skip): ");
                String pubType2 = sc.nextLine().trim();
                System.out.print("New periodicity (press Enter to skip, type NULL to clear): ");
                String periodicity2 = sc.nextLine().trim();
                int rows = publicationDAO.updatePublication(conn, pubId2,
                        title2.isEmpty() ? null : title2,
                        pubType2.isEmpty() ? null : pubType2,
                        periodicity2.isEmpty() ? null : periodicity2);
                if (rows == 0 && (title2.isEmpty() && pubType2.isEmpty() && periodicity2.isEmpty())) {
                    System.out.println("Nothing to update.");
                } else {
                    System.out.println(rows > 0 ? "Publication updated successfully." : "No rows updated.");
                }
                break;
            default:
                break;
        }
    }

    private void editorSubmenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Assign / Remove Editor ---");
        System.out.println("1. Assign editor to a publication");
        System.out.println("2. Remove editor from a publication");
        System.out.println("3. View all publications assigned to an editor");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1:
                System.out.print("Publication ID: ");
                int pubId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Person ID (editor): ");
                int personId = Integer.parseInt(sc.nextLine().trim());
                productionDAO.assignEditor(conn, pubId, personId);
                System.out.println("Editor " + personId + " assigned to publication " + pubId + " successfully.");
                break;
            case 2:
                System.out.print("Publication ID: ");
                int pubId2 = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Person ID (editor): ");
                int personId2 = Integer.parseInt(sc.nextLine().trim());
                int rows = productionDAO.removeEditor(conn, pubId2, personId2);
                if (rows > 0) {
                    System.out.println("Editor " + personId2 + " removed from publication " + pubId2 + ".");
                } else {
                    System.out.println("No matching assignment found.");
                }
                break;
            case 3:
                System.out.print("Person ID (editor): ");
                int personId3 = Integer.parseInt(sc.nextLine().trim());
                productionDAO.printEditorPublications(conn, personId3);
                break;
            default:
                break;
        }
    }

    private void tocSubmenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Edit Table of Contents ---");
        System.out.println("1. Add article to issue TOC");
        System.out.println("2. Remove article from issue TOC");
        System.out.println("3. Add chapter to book edition TOC");
        System.out.println("4. Remove chapter from book edition TOC");
        System.out.println("0. Back");
        System.out.print("Select: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return;
        }
        switch (choice) {
            case 1:
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
                Date dw = dateStr.isEmpty() ? null : Date.valueOf(dateStr);
                contentDAO.insertArticleToc(conn, issueId, artNum, title, personId, topic, dw);
                System.out.println("Article added to issue " + issueId + " TOC.");
                break;
            case 2:
                System.out.print("Issue ID: ");
                int issueId2 = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Article number: ");
                int artNum2 = Integer.parseInt(sc.nextLine().trim());
                int rows = contentDAO.deleteArticle(conn, issueId2, artNum2);
                if (rows > 0) System.out.println("Article removed from issue TOC.");
                else System.out.println("Article not found.");
                break;
            case 3:
                System.out.print("Edition ID: ");
                int editionId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Chapter number: ");
                int chapNum = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Title: ");
                String title3 = sc.nextLine().trim();
                System.out.print("Author person ID: ");
                int personId3 = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Topic (press Enter to skip): ");
                String topic3 = sc.nextLine().trim();
                System.out.print("Date written (yyyy-MM-dd, press Enter to skip): ");
                String dateStr3 = sc.nextLine().trim();
                Date dw3 = dateStr3.isEmpty() ? null : Date.valueOf(dateStr3);
                contentDAO.insertChapterToc(conn, editionId, chapNum, title3, personId3, topic3, dw3);
                System.out.println("Chapter added to edition " + editionId + " TOC.");
                break;
            case 4:
                System.out.print("Edition ID: ");
                int editionId4 = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Chapter number: ");
                int chapNum4 = Integer.parseInt(sc.nextLine().trim());
                int rows4 = contentDAO.deleteChapter(conn, editionId4, chapNum4);
                if (rows4 > 0) System.out.println("Chapter removed from edition TOC.");
                else System.out.println("Chapter not found.");
                break;
            default:
                break;
        }
    }
}
