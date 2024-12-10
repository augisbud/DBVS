import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class Invoices {
    public static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Couldn't find driver class!");
            cnfe.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        Connection postGresConn = null;
        try {
            postGresConn = DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "aubu0192", "noretum,ane");
        } catch (SQLException sqle) {
            System.out.println("Couldn't connect to database!");
            sqle.printStackTrace();
            return null;
        }
        System.out.println("Successfully connected to Postgres Database");
        return postGresConn;
    }

    public static void searchInvoicesByClientEmail(Connection conn) {
        String query = """
                SELECT i."Id", i."Title", i."CreatedAt", i."Discount", i."Markup"
                FROM "Invoices" i
                JOIN "Clients" c ON i."ClientId" = c."Id"
                WHERE c."Email" = ?
                """;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter client email: ");
            String email = scanner.nextLine();

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                System.out.println("\nInvoices:");
                while (rs.next()) {
                    System.out.printf("Invoice ID: %d | Title: %s | Created At: %s | Discount: %.2f | Markup: %d%n",
                            rs.getInt("Id"),
                            rs.getString("Title"),
                            rs.getTimestamp("CreatedAt"),
                            rs.getBigDecimal("Discount"),
                            rs.getInt("Markup"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving invoices!");
            e.printStackTrace();
        }
    }

    public static void addInvoice(Connection conn) {
        String findClientIdQuery = "SELECT \"Id\" FROM \"Clients\" WHERE \"Email\" = ?";
        String insertInvoiceQuery = """
                INSERT INTO "Invoices" ("Title", "Markup", "Discount", "Notes", "ClientId")
                VALUES (?, ?, ?, ?, ?) RETURNING "Id"
                """;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter client email: ");
            String email = scanner.nextLine();

            int clientId;
            try (PreparedStatement stmt = conn.prepareStatement(findClientIdQuery)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    clientId = rs.getInt("Id");
                } else {
                    System.out.println("Client not found!");
                    return;
                }
            }

            System.out.print("Enter invoice title: ");
            String title = scanner.nextLine();
            System.out.print("Enter markup: ");
            int markup = scanner.nextInt();
            System.out.print("Enter discount: ");
            BigDecimal discount = scanner.nextBigDecimal();
            scanner.nextLine(); // consume newline
            System.out.print("Enter notes: ");
            String notes = scanner.nextLine();

            try (PreparedStatement stmt = conn.prepareStatement(insertInvoiceQuery)) {
                stmt.setString(1, title);
                stmt.setInt(2, markup);
                stmt.setBigDecimal(3, discount);
                stmt.setString(4, notes);
                stmt.setInt(5, clientId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Invoice created successfully with ID: " + rs.getInt("Id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding a new invoice!");
            e.printStackTrace();
        }
    }

    public static void updateInvoice(Connection conn) {
        String updateInvoiceQuery = """
                UPDATE "Invoices"
                SET "Title" = ?, "Markup" = ?, "Discount" = ?, "Notes" = ?
                WHERE "Id" = ?
                """;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter invoice ID to update: ");
            int invoiceId = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.print("Enter new title: ");
            String title = scanner.nextLine();
            System.out.print("Enter new markup: ");
            int markup = scanner.nextInt();
            System.out.print("Enter new discount: ");
            BigDecimal discount = scanner.nextBigDecimal();
            scanner.nextLine(); // consume newline
            System.out.print("Enter new notes: ");
            String notes = scanner.nextLine();

            try (PreparedStatement stmt = conn.prepareStatement(updateInvoiceQuery)) {
                stmt.setString(1, title);
                stmt.setInt(2, markup);
                stmt.setBigDecimal(3, discount);
                stmt.setString(4, notes);
                stmt.setInt(5, invoiceId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Invoice updated successfully.");
                } else {
                    System.out.println("Failed to update the invoice. Please check the invoice ID.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating the invoice!");
            e.printStackTrace();
        }
    }

    public static void deleteInvoice(Connection conn) {
        String deleteInvoiceQuery = "DELETE FROM \"Invoices\" WHERE \"Id\" = ?";
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter invoice ID to delete: ");
            int invoiceId = scanner.nextInt();

            try (PreparedStatement stmt = conn.prepareStatement(deleteInvoiceQuery)) {
                stmt.setInt(1, invoiceId);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Invoice deleted successfully.");
                } else {
                    System.out.println("Failed to delete the invoice. Please check the invoice ID.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting the invoice!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadDriver();
        try (Connection conn = getConnection();
             Scanner scanner = new Scanner(System.in)) {

            if (conn == null) return;

            while (true) {
                System.out.println("\n=== Invoice Management System ===");
                System.out.println("1. Search Invoices by Client Email");
                System.out.println("2. Add New Invoice");
                System.out.println("3. Update Invoice");
                System.out.println("4. Delete Invoice");
                System.out.println("5. Exit");
                System.out.print("Select an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> searchInvoicesByClientEmail(conn);
                    case 2 -> addInvoice(conn);
                    case 3 -> updateInvoice(conn);
                    case 4 -> deleteInvoice(conn);
                    case 5 -> {
                        System.out.println("Exiting. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error occurred.");
            e.printStackTrace();
        }
    }
}
