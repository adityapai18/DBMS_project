package main;

import db.DBConnection;
import service.DistributionService;
import service.ProductionService;
import service.PublishingService;
import service.ReportService;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * GutenbergDb — consolidated console application (layered architecture).
 */
public class MainApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(true);

            PublishingService publishingService = new PublishingService();
            ProductionService productionService = new ProductionService();
            DistributionService distributionService = new DistributionService();
            ReportService reportService = new ReportService();

            boolean running = true;
            while (running) {
                System.out.println("\n========== GutenbergDb Main Menu ==========");
                System.out.println("1. Publishing (publications, editors, TOC)");
                System.out.println("2. Production (editions, issues, content, staff pay)");
                System.out.println("3. Distribution & distributor payments");
                System.out.println("4. Reports");
                System.out.println("0. Exit");
                System.out.print("Select: ");
                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                switch (choice) {
                    case 1:
                        publishingService.runMenu(conn, sc);
                        break;
                    case 2:
                        productionService.runMenu(conn, sc);
                        break;
                    case 3:
                        distributionService.runMenu(conn, sc);
                        break;
                    case 4:
                        reportService.runMenu(conn, sc);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
            System.out.println("Goodbye!");
        } catch (SQLException e) {
            if (isLikelyUnreachableDb(e)) {
                System.err.println();
                System.err.println("Could not connect to the database server.");
                System.err.println("If you use the default NCSU class DB (classdb2.csc.ncsu.edu), connect to");
                System.err.println("the campus VPN (or campus network) so that hostname resolves, then try again.");
                System.err.println();
                System.err.println("Or point JDBC at another MariaDB instance, for example:");
                System.err.println("  export JDBC_URL='jdbc:mariadb://127.0.0.1:3306/your_database'");
                System.err.println("  export JDBC_USER='your_user'");
                System.err.println("  export JDBC_PASSWORD='your_password'");
                System.err.println("  java -cp \"out:mariadb-java-client-3.5.7.jar\" main.MainApp");
                System.err.println();
            }
            e.printStackTrace();
        } finally {
            DBConnection.closeQuietly(conn);
            sc.close();
        }
    }

    private static boolean isLikelyUnreachableDb(Throwable e) {
        while (e != null) {
            if (e instanceof UnknownHostException || e instanceof ConnectException) {
                return true;
            }
            String m = e.getMessage();
            if (m != null && (m.contains("UnknownHost") || m.toLowerCase().contains("unknown host"))) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
