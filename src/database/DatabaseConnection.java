
package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:data/stockmate.db";

    // Connect to Database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Create Database and Tables
    public static void initializeDatabase()
            throws SQLException, IOException {

        Files.createDirectories(Paths.get("data"));

        String productsTable = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                quantity INTEGER NOT NULL CHECK(quantity >= 0),
                price REAL NOT NULL CHECK(price >= 0)
            )
            """;

        String movementsTable = """
            CREATE TABLE IF NOT EXISTS stock_movements (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL,
                product_name TEXT NOT NULL,
                movement_type TEXT NOT NULL
                    CHECK(movement_type IN ('IN', 'OUT')),
                quantity INTEGER NOT NULL CHECK(quantity > 0),
                previous_quantity INTEGER NOT NULL
                    CHECK(previous_quantity >= 0),
                new_quantity INTEGER NOT NULL
                    CHECK(new_quantity >= 0),
                movement_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(productsTable);
            statement.execute(movementsTable);

            statement.execute("""
                CREATE INDEX IF NOT EXISTS
                idx_movements_product_id
                ON stock_movements(product_id)
                """);

            statement.execute("""
                CREATE INDEX IF NOT EXISTS
                idx_movements_date
                ON stock_movements(movement_date)
                """);
        }
    }
}
