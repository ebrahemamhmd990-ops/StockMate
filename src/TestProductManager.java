
import controller.ProductManager;
import model.Product;

public class TestProductManager {

    public static void main(String[] args) {

        ProductManager manager = new ProductManager();

        // Test valid product
        Product p1 = new Product(
            100, "Keyboard", "Accessories", 10, 500
        );

        System.out.println("Valid Product: " +
                           manager.addProduct(p1));

        // Test duplicate ID
        Product p2 = new Product(
            100, "Mouse", "Accessories", 20, 300
        );

        System.out.println("Duplicate ID: " +
                           manager.addProduct(p2));

        // Test negative quantity
        Product p3 = new Product(
            101, "Monitor", "Electronics", -5, 4000
        );

        System.out.println("Negative Quantity: " +
                           manager.addProduct(p3));

        // Test negative price
        Product p4 = new Product(
            102, "Printer", "Electronics", 5, -2000
        );

        System.out.println("Negative Price: " +
                           manager.addProduct(p4));

        // Test update
        System.out.println("Update Product: " +
            manager.updateProduct(
                100, "Gaming Keyboard", "Accessories", 15, 700
            )
        );

        // Test delete
        System.out.println("Delete Product: " +
                           manager.deleteProduct(100));
    }
}

