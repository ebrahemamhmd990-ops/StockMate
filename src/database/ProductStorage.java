
package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import model.Product;

public class ProductStorage {

    private static final String LEGACY_FILE = "data/products.txt";

    // Save Products in SQLite
    public static boolean saveProducts(ArrayList<Product> products) {

        String sql = """
            INSERT INTO products (id, name, category, quantity, price)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (Statement deleteStatement = connection.createStatement();
                 PreparedStatement insertStatement =
                     connection.prepareStatement(sql)) {

                deleteStatement.executeUpdate("DELETE FROM products");

                for (Product product : products) {

                    insertStatement.setInt(1, product.getId());
                    insertStatement.setString(2, product.getName());
                    insertStatement.setString(3, product.getCategory());
                    insertStatement.setInt(4, product.getQuantity());
                    insertStatement.setDouble(5, product.getPrice());

                    insertStatement.addBatch();
                }

                insertStatement.executeBatch();
                connection.commit();

                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error saving products: " + e.getMessage());
            return false;
        }
    }

    // Load Products from SQLite
    public static ArrayList<Product> loadProducts() {

        ArrayList<Product> products = new ArrayList<>();

        String sql = """
            SELECT id, name, category, quantity, price
            FROM products
            ORDER BY id
            """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            migrateLegacyData(connection);

            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                while (result.next()) {

                    Product product = new Product(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("category"),
                        result.getInt("quantity"),
                        result.getDouble("price")
                    );

                    products.add(product);
                }
            }

        } catch (SQLException | IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }

        return products;
    }

    // Import Old Text File Once
    private static void migrateLegacyData(Connection connection)
            throws SQLException, IOException {

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS app_settings (
                    setting_key TEXT PRIMARY KEY,
                    setting_value TEXT NOT NULL
                )
                """);
        }

        String checkSql = """
            SELECT setting_value
            FROM app_settings
            WHERE setting_key = 'legacy_import_done'
            """;

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(checkSql)) {

            if (result.next()) {
                return;
            }
        }

        Path path = Paths.get(LEGACY_FILE);

        if (Files.exists(path)) {

            String insertSql = """
                INSERT OR IGNORE INTO products
                (id, name, category, quantity, price)
                VALUES (?, ?, ?, ?, ?)
                """;

            try (PreparedStatement preparedStatement =
                     connection.prepareStatement(insertSql)) {

                for (String line : Files.readAllLines(path)) {

                    String[] data = line.split("\\|", -1);

                    if (data.length != 5) {
                        continue;
                    }

                    try {
                        int id = Integer.parseInt(data[0]);
                        String name = data[1];
                        String category = data[2];
                        int quantity = Integer.parseInt(data[3]);
                        double price = Double.parseDouble(data[4]);

                        if (id <= 0 || name.trim().isEmpty()
                                || category.trim().isEmpty()
                                || quantity < 0 || price < 0
                                || !Double.isFinite(price)) {
                            continue;
                        }

                        preparedStatement.setInt(1, id);
                        preparedStatement.setString(2, name);
                        preparedStatement.setString(3, category);
                        preparedStatement.setInt(4, quantity);
                        preparedStatement.setDouble(5, price);

                        preparedStatement.executeUpdate();

                    } catch (NumberFormatException e) {
                        System.out.println("Skipped invalid legacy row.");
                    }
                }
            }
        }

        try (PreparedStatement preparedStatement =
                 connection.prepareStatement("""
                     INSERT INTO app_settings
                     (setting_key, setting_value)
                     VALUES ('legacy_import_done', 'true')
                     """)) {

            preparedStatement.executeUpdate();
        }

        System.out.println("Legacy data migration completed.");
    }
}
