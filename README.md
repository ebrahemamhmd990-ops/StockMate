# StockMate

A desktop inventory management application built with **Java Swing** and **SQLite**.

StockMate helps users manage product records through a simple graphical interface, while keeping inventory data stored locally in an SQLite database.

## Features

- **Product management (CRUD):** add, view, update, and delete products.
- **Search:** find products by name or category.
- **Sorting:** sort by ID, name, quantity, or price in ascending or descending order.
- **Dashboard:** view total products, total quantity, total stock value, and low-stock product count.
- **Low-stock highlighting:** products with a quantity of 5 or less are highlighted in the inventory table.
- **Local database:** product data is stored in SQLite.
- **Legacy data import:** imports existing records from `data/products.txt` once, when that file is available and the database has not already completed the import.

## Tech Stack

- Java (JDK 17 or later recommended)
- Java Swing (desktop user interface)
- SQLite
- SQLite JDBC driver (`lib/sqlite-jdbc-3.54.1.0.jar`)

## Project Structure

```text
StockMate/
├── lib/
│   └── sqlite-jdbc-3.54.1.0.jar
├── src/
│   ├── Main.java
│   ├── TestProductManager.java
│   ├── controller/
│   │   └── ProductManager.java
│   ├── database/
│   │   ├── DatabaseConnection.java
│   │   └── ProductStorage.java
│   ├── model/
│   │   └── Product.java
│   └── view/
│       └── MainFrame.java
├── data/              # Created locally when the application runs
├── out/               # Compiled classes (generated)
└── .gitignore
```

## Requirements

- Windows, macOS, or Linux
- JDK 17 or later
- The SQLite JDBC JAR included in the `lib` directory

## Run on Windows (PowerShell)

Run these commands from the project root directory.

### 1. Compile the application

```powershell
javac -cp "lib\sqlite-jdbc-3.54.1.0.jar" -d out src\Main.java src\model\Product.java src\database\DatabaseConnection.java src\database\ProductStorage.java src\controller\ProductManager.java src\view\MainFrame.java
```

### 2. Launch StockMate

```powershell
java -cp "lib\sqlite-jdbc-3.54.1.0.jar;out" Main
```

The application initializes the SQLite database automatically. The database is created locally at `data/stockmate.db`.

## Data and Privacy

The local `data/` directory and compiled `out/` directory are excluded from Git through `.gitignore`. Your local database and inventory records are therefore not included in this repository by default.

## Current Project Status

StockMate is an actively developed learning and portfolio project. Features and documentation may change as development continues.

## Author

**Ibrahim Ahmed**

---

If you find a bug or have a suggestion, feel free to open an issue in the repository.
