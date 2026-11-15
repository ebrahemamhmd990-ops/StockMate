
package controller;

import database.ProductStorage;
import java.util.ArrayList;
import model.Product;

public class ProductManager {

    private ArrayList<Product> products;

    // Constructor
    public ProductManager() {
        products = ProductStorage.loadProducts();
    }

    // Add Product
    public boolean addProduct(Product product) {

        if (product == null) {
            return false;
        }

        if (product.getId() <= 0 ||
            product.getName() == null ||
            product.getName().trim().isEmpty() ||
            product.getCategory() == null ||
            product.getCategory().trim().isEmpty() ||
            product.getQuantity() < 0 ||
            product.getPrice() < 0 ||
            Double.isNaN(product.getPrice()) ||
            Double.isInfinite(product.getPrice())) {

            return false;
        }

        if (searchProduct(product.getId()) != null) {
            return false;
        }

        products.add(product);
        save();

        return true;
    }

    // View All Products
    public ArrayList<Product> getAllProducts() {
        return products;
    }

    // Search Product by ID
    public Product searchProduct(int id) {

        for (Product product : products) {

            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    // Search Products by Name or Category
    public ArrayList<Product> searchProducts(String keyword) {

        ArrayList<Product> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String searchText = keyword.trim().toLowerCase();

        for (Product product : products) {

            boolean nameMatches =
                product.getName().toLowerCase().contains(searchText);

            boolean categoryMatches =
                product.getCategory().toLowerCase().contains(searchText);

            if (nameMatches || categoryMatches) {
                results.add(product);
            }
        }

        return results;
    }

    // Update Product
    public boolean updateProduct(int id, String name,
                                 String category,
                                 int quantity, double price) {

        if (id <= 0 ||
            name == null || name.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            quantity < 0 ||
            price < 0 ||
            Double.isNaN(price) ||
            Double.isInfinite(price)) {

            return false;
        }

        Product product = searchProduct(id);

        if (product != null) {

            Product updatedProduct = new Product(
                id, name, category, quantity, price
            );

            int index = products.indexOf(product);

            products.set(index, updatedProduct);

            save();

            return true;
        }

        return false;
    }

    // Delete Product
    public boolean deleteProduct(int id) {

        Product product = searchProduct(id);

        if (product != null) {

            products.remove(product);

            save();

            return true;
        }

        return false;
    }

    // Save Changes
    private void save() {
        ProductStorage.saveProducts(products);
    }
}
