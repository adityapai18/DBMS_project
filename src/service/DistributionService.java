package service;

import dao.distribution.DistributorDAO;
import dao.distribution.OrderDAO;
import dao.payment.PaymentDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class DistributionService {

    private final DistributorDAO distributorDAO = new DistributorDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public void runMenu(Connection conn, Scanner sc) throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Distribution & Payments ===");
            System.out.println("1. Manage distributors");
            System.out.println("2. Manage distributor orders (incl. billing transaction)");
            System.out.println("3. Record payments & allocations");
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
                        distributorMenu(conn, sc);
                        break;
                    case 2:
                        ordersMenu(conn, sc);
                        break;
                    case 3:
                        paymentMenu(conn, sc);
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

    private void distributorMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Distributors ---");
        System.out.println("1. Enter a new distributor");
        System.out.println("2. Update distributor information");
        System.out.println("3. Delete a distributor");
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
                System.out.print("Distributor ID: ");
                int id = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Name: ");
                String name = sc.nextLine().trim();
                System.out.print("Category (Bookstore / Wholesale / Library): ");
                String category = sc.nextLine().trim();
                System.out.print("Street: ");
                String street = sc.nextLine().trim();
                System.out.print("City: ");
                String city = sc.nextLine().trim();
                System.out.print("State: ");
                String state = sc.nextLine().trim();
                System.out.print("Zip: ");
                String zip = sc.nextLine().trim();
                System.out.print("Country: ");
                String country = sc.nextLine().trim();
                System.out.print("Phone: ");
                String phone = sc.nextLine().trim();
                System.out.print("Contact person: ");
                String contact = sc.nextLine().trim();
                distributorDAO.insert(conn, id, name, category, street, city, state, zip, country, phone, contact);
                System.out.println("Distributor added successfully.");
                break;
            case 2:
                System.out.print("Distributor ID to update: ");
                int id2 = Integer.parseInt(sc.nextLine().trim());
                if (!distributorDAO.printDistributor(conn, id2)) return;
                System.out.print("New name (press Enter to skip): ");
                String name2 = sc.nextLine().trim();
                System.out.print("New category (Bookstore/Wholesale/Library, press Enter to skip): ");
                String category2 = sc.nextLine().trim();
                System.out.print("New street (press Enter to skip): ");
                String street2 = sc.nextLine().trim();
                System.out.print("New city (press Enter to skip): ");
                String city2 = sc.nextLine().trim();
                System.out.print("New state (press Enter to skip): ");
                String state2 = sc.nextLine().trim();
                System.out.print("New zip (press Enter to skip): ");
                String zip2 = sc.nextLine().trim();
                System.out.print("New country (press Enter to skip): ");
                String country2 = sc.nextLine().trim();
                System.out.print("New phone (press Enter to skip): ");
                String phone2 = sc.nextLine().trim();
                System.out.print("New contact person (press Enter to skip): ");
                String contact2 = sc.nextLine().trim();
                int rows = distributorDAO.updateDistributor(conn, id2,
                        name2.isEmpty() ? null : name2,
                        category2.isEmpty() ? null : category2,
                        street2.isEmpty() ? null : street2,
                        city2.isEmpty() ? null : city2,
                        state2.isEmpty() ? null : state2,
                        zip2.isEmpty() ? null : zip2,
                        country2.isEmpty() ? null : country2,
                        phone2.isEmpty() ? null : phone2,
                        contact2.isEmpty() ? null : contact2);
                if (rows == 0) System.out.println("Nothing to update.");
                else System.out.println("Distributor updated successfully.");
                break;
            case 3:
                System.out.print("Distributor ID to delete: ");
                int id3 = Integer.parseInt(sc.nextLine().trim());
                if (distributorDAO.countOrders(conn, id3) > 0) {
                    System.out.println("Cannot delete: this distributor has existing orders. Remove orders first.");
                    return;
                }
                if (distributorDAO.countDistributorPayments(conn, id3) > 0) {
                    System.out.println("Cannot delete: this distributor has payment records. Remove payments first.");
                    return;
                }
                System.out.print("Are you sure you want to delete? (yes/no): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    System.out.println("Delete cancelled.");
                    return;
                }
                int rows3 = distributorDAO.delete(conn, id3);
                if (rows3 > 0) System.out.println("Distributor deleted.");
                else System.out.println("Distributor not found.");
                break;
            default:
                break;
        }
    }

    private void ordersMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Manage Distributor Orders ---");
        System.out.println("1. Input an order from a distributor");
        System.out.println("2. Bill a distributor for an order (Transaction)");
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
                System.out.print("Order ID: ");
                int orderId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Distributor ID: ");
                int distId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Edition ID (press Enter if ordering an issue instead): ");
                String edStr = sc.nextLine().trim();
                Integer editionId = edStr.isEmpty() ? null : Integer.parseInt(edStr);
                System.out.print("Issue ID (press Enter if ordering a book edition instead): ");
                String isStr = sc.nextLine().trim();
                Integer issueId = isStr.isEmpty() ? null : Integer.parseInt(isStr);
                if ((editionId == null && issueId == null) || (editionId != null && issueId != null)) {
                    System.out.println("ERROR: Exactly one of edition_id or issue_id must be provided (not both, not neither).");
                    return;
                }
                System.out.print("Quantity: ");
                int quantity = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Price per copy: ");
                double price = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Shipping cost: ");
                double shipping = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Required date (yyyy-MM-dd): ");
                Date reqDate = Date.valueOf(sc.nextLine().trim());
                orderDAO.insertOrder(conn, orderId, distId, editionId, issueId, quantity, price, shipping, reqDate);
                System.out.println("Order " + orderId + " recorded successfully.");
                break;
            case 2:
                System.out.print("Order ID to bill: ");
                int orderId2 = Integer.parseInt(sc.nextLine().trim());
                OrderDAO.BillingLookup lookup = orderDAO.fetchOrderForBilling(conn, orderId2);
                if (lookup.status == OrderDAO.BillingStatus.NOT_FOUND) {
                    System.out.println("Order not found.");
                    return;
                }
                if (lookup.status != OrderDAO.BillingStatus.OK) {
                    return;
                }
                OrderDAO.BillingInfo info = lookup.info;
                System.out.print("Payment ID for the new distributor payment record: ");
                int paymentId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Payment date (yyyy-MM-dd): ");
                Date payDate = Date.valueOf(sc.nextLine().trim());
                conn.setAutoCommit(false);
                try {
                    orderDAO.setBilledAmountFromLineItems(conn, orderId2);
                    paymentDAO.insertDistributorPayment(conn, paymentId, info.distributorId, info.billedAmount, payDate);
                    conn.commit();
                    System.out.printf("Transaction committed: Order %d billed $%.2f. Payment %d created.%n",
                            orderId2, info.billedAmount, paymentId);
                } catch (SQLException e) {
                    conn.rollback();
                    System.out.println("Transaction rolled back: " + e.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                }
                break;
            default:
                break;
        }
    }

    private void paymentMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.println("\n--- Record Payment & Allocate ---");
        System.out.println("1. Record a distributor payment");
        System.out.println("2. Allocate a payment to an order");
        System.out.println("3. Show outstanding balance for a distributor");
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
                System.out.print("Payment ID: ");
                int payId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Distributor ID: ");
                int distId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Amount: ");
                double amount = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Payment date (yyyy-MM-dd): ");
                Date payDate = Date.valueOf(sc.nextLine().trim());
                paymentDAO.insertDistributorPayment(conn, payId, distId, amount, payDate);
                System.out.println("Distributor payment recorded successfully.");
                break;
            case 2:
                System.out.print("Payment ID: ");
                int payId2 = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Order ID: ");
                int orderId = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Amount to allocate: ");
                double amount2 = Double.parseDouble(sc.nextLine().trim());
                paymentDAO.insertAllocation(conn, payId2, orderId, amount2);
                System.out.println("Payment " + payId2 + " allocated $" + amount2 + " to order " + orderId + ".");
                break;
            case 3:
                System.out.print("Distributor ID: ");
                int distId3 = Integer.parseInt(sc.nextLine().trim());
                paymentDAO.printOutstandingBalance(conn, distId3);
                break;
            default:
                break;
        }
    }
}
