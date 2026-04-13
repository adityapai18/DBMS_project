package service;

import dao.reports.ReportDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public void runMenu(Connection conn, Scanner sc) throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Reports ===");
            System.out.println("1. Revenue & expense reports");
            System.out.println("2. Distributor reports");
            System.out.println("3. Weekly / monthly distributor orders");
            System.out.println("4. Distributor count");
            System.out.println("5. Staff payment reports");
            System.out.println("0. Back");
            System.out.print("Select: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ignored) {
                continue;
            }
            switch (choice) {
                case 1:
                    revenueExpenseMenu(conn, sc);
                    break;
                case 2:
                    distributorReportsMenu(conn, sc);
                    break;
                case 3:
                    weeklyMonthlyMenu(conn, sc);
                    break;
                case 4:
                    reportDAO.printDistributorCount(conn);
                    break;
                case 5:
                    staffPaymentReportsMenu(conn, sc);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void revenueExpenseMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Revenue & Expense Reports ---");
        System.out.println("1. Total revenue of the publishing house");
        System.out.println("2. Total expenses (shipping + staff payments)");
        System.out.println("3. Total revenue per city");
        System.out.println("4. Total revenue per distributor");
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
                reportDAO.printTotalRevenue(conn);
                break;
            case 2:
                reportDAO.printTotalExpenses(conn);
                break;
            case 3:
                reportDAO.printRevenuePerCity(conn);
                break;
            case 4:
                reportDAO.printRevenuePerDistributor(conn);
                break;
            default:
                break;
        }
    }

    private void distributorReportsMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Distributor Reports ---");
        System.out.println("1. Identify distributors with billing/payment mismatch");
        System.out.println("2. List all distributors of a specific type in a city");
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
                reportDAO.printDistributorMismatches(conn);
                break;
            case 2:
                System.out.print("Category (Bookstore / Wholesale / Library): ");
                String category = sc.nextLine().trim();
                System.out.print("City: ");
                String city = sc.nextLine().trim();
                reportDAO.printDistributorsByTypeAndCity(conn, category, city);
                break;
            default:
                break;
        }
    }

    private void weeklyMonthlyMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Weekly / Monthly Distributor Report ---");
        System.out.println("1. Copies and total price per distributor per WEEK");
        System.out.println("2. Copies and total price per distributor per MONTH");
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
                reportDAO.printWeeklyOrders(conn);
                break;
            case 2:
                reportDAO.printMonthlyOrders(conn);
                break;
            default:
                break;
        }
    }

    private void staffPaymentReportsMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Staff Payment Reports ---");
        System.out.println("1. Total payments to editors/authors per month");
        System.out.println("2. Total payments per work type");
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
                reportDAO.printStaffPaymentsPerMonth(conn);
                break;
            case 2:
                reportDAO.printStaffPaymentsPerWorkType(conn);
                break;
            default:
                break;
        }
    }
}
