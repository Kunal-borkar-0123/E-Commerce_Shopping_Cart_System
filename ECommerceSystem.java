import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ECommerceSystem {
    private final FileHandler fileHandler; // Add fileHandler declaration
    private final Authentication<User> authentication; // Add authentication declaration
    private final Inventory inventory; // Add inventory declaration
    private final OrderManager orderManager;
    private final ProductManager productManager;
    private final UserManager userManager;
    private final ProductCatalog productCatalog;
    private static ArrayList<Order> ordersList;
    static User currentUser;
    private final Scanner scanner;

    public ECommerceSystem() {
        this.fileHandler = new FileHandler("users.txt");
        this.authentication = new Authentication<>(fileHandler);
        this.inventory = new Inventory();
        this.orderManager = new OrderManager();
        this.productManager = new ProductManager();
        this.userManager = new UserManager();
        this.productCatalog = new ProductCatalog();
        ordersList = new ArrayList<>();
        currentUser = null; // No user is logged in initially
        this.scanner = new Scanner(System.in);
        loadInitialProducts();
        loadOrders();
    }

    public static void main(String[] args) {
        ECommerceSystem ecommerceSystem = new ECommerceSystem();
        ecommerceSystem.run();
        ecommerceSystem.saveOrders(ordersList); // Provide the actual list of orders to save
    }

    private void displayMainMenu() {
        System.out.println("ECommerce System Main Menu");
        System.out.println("1. View Products");
        System.out.println("2. Add to Cart");
        System.out.println("3. View Cart");
        System.out.println("4. Remove from Cart");
        System.out.println("5. Place Order");
        System.out.println("6. Cancel Order");
        System.out.println("7. View Order History");
        System.out.println("8. View Profile");
        System.out.println("9. Update Profile");
        System.out.println("10. Apply Discount");
        System.out.println("11. View Product Catalog");
        System.out.println("12. Manage Products");
        System.out.println("13. View Product Statistics");
        System.out.println("14. Manage Users (Admin only)");
        System.out.println("15. Update Order Status (Admin only)");
        System.out.println("16. Manage Inventory (Admin only)");
        System.out.println("17. Logout");
        System.out.println("18. Exit the system");
        System.out.print("Enter your choice: ");
    }

    public void run() {
        displayWelcomeMessage();

        while (true) {
            displayAuthenticationMenu(); // Display the authentication menu

            int authChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (authChoice) {
                case 1: // For Log in
                    authenticateUser();
                    break;
                case 2: // For Create Account
                    createUserAccount();
                    break;
                case 3: // For Exit
                    exit();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (currentUser != null) {
                // If user successfully authenticated or created, proceed to the main menu
                displayMainMenu();
                int mainMenuChoice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (mainMenuChoice) {
                    case 1: // For View Products
                        viewProducts();
                        break;
                    case 2: // For Add to Cart
                        addItemToCart();
                        break;
                    case 3: // For View Cart
                        viewCart();
                        break;
                    case 4: // For Remove from Cart (customer only)
                        if (currentUser instanceof Customer) {
                            removeFromCart();
                        } else {
                            System.out.println("Invalid choice. Please try again.");
                        }
                        break;
                    case 5: // For Place Order
                        placeOrder();
                        break;
                    case 6: // For Cancel Order
                        cancelOrder();
                        break;
                    case 7: // For View Order History
                        viewOrderHistory();
                        break;
                    case 8: // For View Profile
                        viewProfile();
                        break;
                    case 9: // For Update Profile
                        updateProfile();
                        break;
                    case 10: // For Apply Discount
                        applyDiscount();
                        break;
                    case 11: // For View Product Catalog
                        viewProductCatalog();
                        break;
                    case 12: // For Manage Products (Admin only)
                        if (currentUser instanceof Admin) {
                            manageProducts();
                        } else {
                            System.out.println("You do not have permission to access this feature.");
                        }
                        break;
                    case 13: // For View Product Statistics (Admin)
                        if (currentUser instanceof Admin) {
                            productCatalog.viewProductStatistics();
                        } else {
                            System.out.println("You do not have permission to access this feature.");
                        }
                        break;
                    case 14: // For Manage Users (Admin only)
                        if (currentUser instanceof Admin) {
                            userManager.manageUsers();
                        } else {
                            System.out.println("You do not have permission to access this feature.");
                        }
                        break;
                    case 15: // For Update Order Status (Admin only)
                        updateOrderStatus();
                        break;
                    case 16: // For Manage Inventory (Admin only)
                        manageInventory();
                        break;
                    case 17: // For Logout
                        logout();
                        break;
                    case 18: // For Exit
                        exit();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private void manageProducts() {
        // Example: Managing products
        while (true) {
            System.out.println("Product Management Menu");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Update Product");
            System.out.println("4. Search Product");
            System.out.println("5. View All Products");
            System.out.println("6. Back to the Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1: // For Add Product
                    addProduct();
                    break;
                case 2: // For Remove Product
                    removeProduct();
                    break;
                case 3: // For Update Product
                    updateProduct();
                    break;
                case 4: // For Search Product
                    searchProduct();
                    break;
                case 5: // For View All Products
                    viewAllProducts();
                    break;
                case 6: // For Back to the Main Menu
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    manageProducts();
            }
        }
    }

    private void addProduct() {
        System.out.println("Enter Product details to add:");

        System.out.println("Enter product ID: ");
        int productId = scanner.nextInt();

        System.out.print("Enter product name: ");
        String name = scanner.nextLine();

        System.out.print("Enter product description: ");
        String description = scanner.nextLine();

        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter product category: ");
        String category = scanner.nextLine().toUpperCase();

        System.out.print("Enter product stock quantity: ");
        int stockQuantity = scanner.nextInt();

        Product newProduct = new Product(productId, name,
                description, price, Category.valueOf(category), stockQuantity);
        productManager.addProduct(newProduct);

        System.out.println("Product added successfully.");
    }


    private void removeProduct() {
        System.out.println("Enter Product details to remove:");

        System.out.print("Enter the Product ID to remove: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        productManager.removeProduct(productId);
    }

    private void updateProduct() {
        System.out.println("Enter Product details to update:");

        System.out.print("Enter the Product ID to update: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Product existingProduct = productManager.searchProduct(productId);

        if (existingProduct != null) {
            System.out.print("Enter updated product name (press Enter to keep current name): ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                existingProduct.setName(newName);
            }

            System.out.print("Enter updated product description (press Enter to keep current description): ");
            String newDescription = scanner.nextLine();
            if (!newDescription.isEmpty()) {
                existingProduct.setDescription(newDescription);
            }

            System.out.print("Enter updated product price (press Enter to keep current price): ");
            String newPriceString = scanner.nextLine();
            if (!newPriceString.isEmpty()) {
                double newPrice = Double.parseDouble(newPriceString);
                existingProduct.setPrice(newPrice);
            }

            System.out.print("Enter updated product category (press Enter to keep current category): ");
            String newCategoryString = scanner.nextLine();
            if (!newCategoryString.isEmpty()) {
                existingProduct.setCategory(Category.valueOf(newCategoryString.toUpperCase()));
            }

            System.out.print("Enter updated product stock quantity (press Enter to keep current stock quantity): ");
            String newStockQuantityString = scanner.nextLine();
            if (!newStockQuantityString.isEmpty()) {
                int newStockQuantity = Integer.parseInt(newStockQuantityString);
                existingProduct.setStockQuantity(newStockQuantity);
            }

            productManager.updateProduct(existingProduct);
            System.out.println("Product updated successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void searchProduct() {
        System.out.println("Enter Product details to search:");

        System.out.print("Enter the Product ID to search: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Product foundProduct = productManager.searchProduct(productId);

        if (foundProduct != null) {
            System.out.println("Product found: " + foundProduct);
        } else {
            System.out.println("Product not found.");
        }
    }

    private void viewAllProducts() {
        System.out.println("Viewing all products:");

        List<Product> allProducts = productManager.getAllProducts();

        if (allProducts.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("All Products:");
            for (Product product : allProducts) {
                System.out.println(product);
            }
        }
    }

    private void viewProductCatalog() {
        productCatalog.viewProductCatalog();
    }

    private void applyDiscount() {
        System.out.println("Enter discount percentage:");
        double discountPercentage = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        currentUser.applyDiscount(discountPercentage);
    }

    private void loadInitialProducts() {
        // Example: Adding some initial products to the catalog
        Product product1 = new Product(1, "Laptop", "Powerful laptop", 1200.0, Category.ELECTRONICS, 10);
        Product product2 = new Product(2, "Book", "Bestseller book", 20.0, Category.BOOKS, 50);
        Product product3 = new Product(3, "Phone", "Latest smartphone", 800.0, Category.ELECTRONICS, 15);
        Product product4 = new Product(4, "Tablet", "High resolution tablet", 400.0, Category.ELECTRONICS, 20);
        Product product5 = new Product(5, "Headphones", "Noise cancelling headphones", 150.0, Category.ELECTRONICS, 30);
        Product product6 = new Product(6, "Keyboard", "Wireless keyboard", 60.0, Category.ELECTRONICS, 25);
        Product product7 = new Product(7, "Mouse", "Wireless mouse", 40.0, Category.ELECTRONICS, 25);
        Product product8 = new Product(8, "Monitor", "4k monitor", 300.0, Category.ELECTRONICS, 10);
        Product product9 = new Product(9, "Speakers", "Bluetooth speakers", 100.0, Category.ELECTRONICS, 20);
        Product product10 = new Product(10, "Smart Watch", "Fitness smart watch", 200.0, Category.ELECTRONICS, 15);

        productCatalog.addProduct(product1);
        productCatalog.addProduct(product2);
        productCatalog.addProduct(product3);
        productCatalog.addProduct(product4);
        productCatalog.addProduct(product5);
        productCatalog.addProduct(product6);
        productCatalog.addProduct(product7);
        productCatalog.addProduct(product8);
        productCatalog.addProduct(product9);
        productCatalog.addProduct(product10);
    }

    private void manageInventory() {
        while (true) {
            System.out.println("Inventory Management Menu");
            System.out.println("1. Add Product to Inventory");
            System.out.println("2. Remove Product from Inventory");
            System.out.println("3. Update Stock Level");
            System.out.println("4. Display Inventory");
            System.out.println("5. Display Suppliers");
            System.out.println("6. Exit Inventory Management");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1: // For Add Product to Inventory
                    addProductToInventory();
                    break;
                case 2: // For Remove Product from Inventory
                    removeProductFromInventory();
                    break;
                case 3: // For Update Stock Level
                    updateStockLevel();
                    break;
                case 4: // For Display Inventory
                    inventory.displayInventory();
                    break;
                case 5: // For Display Suppliers
                    inventory.displaySuppliers();
                    break;
                case 6: // For Exit Inventory Management
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    manageInventory();
            }
        }
    }

    private void removeProductFromInventory() {
        // You can add validation and error handling here
        System.out.print("Enter the Product ID to remove from inventory: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Product product = getProductById(productId);
        if (product != null) {
            inventory.removeProductFromInventory(product);
        } else {
            System.out.println("Product not found.");
        }
    }

    private void addProductToInventory() {
        // You can add validation and error handling here
        Product product = Product.createProductFromConsoleInput(scanner);
        assert product != null;
        System.out.print("Enter the initial stock level for " + product.getName() + ": ");
        int initialStock = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the supplier for " + product.getName() + ": ");
        String supplier = scanner.nextLine();

        inventory.addProductToInventory(product, initialStock, supplier);
    }

    private void updateStockLevel() {
        // You can add validation and error handling here
        System.out.print("Enter the Product ID to update stock level: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Product product = getProductById(productId);
        if (product != null) {
            System.out.print("Enter the quantity to add or subtract from the stock level: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            inventory.updateStockLevel(product, quantity);
        } else {
            System.out.println("Product not found.");
        }
    }

    private Product getProductById(int productId) {
        return productCatalog.getProduct(productId);
    }


    private void displayWelcomeMessage() {
        System.out.println("Welcome to the E-Commerce System!");
    }

    private void displayAuthenticationMenu() {
        System.out.println("1. Log in to your account");
        System.out.println("2. Create Account (New User)");
        System.out.println("3. Exit the system");
        System.out.print("Enter your choice: ");
    }

    private void authenticateUser() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User user = authentication.login(username, password);

        if (user != null) {
            currentUser = user;
            System.out.println("Authentication successful. Welcome, " + currentUser.getName() + "!");
        } else {
            System.out.println("Authentication failed. Please check your credentials.");
        }
    }

    private void viewProducts() {
        productCatalog.viewProducts();
    }

    private void addItemToCart() {
        System.out.print("Enter Item ID: ");
        String itemId = scanner.nextLine();

        System.out.print("Enter Item Name: ");
        String itemName = scanner.nextLine();

        System.out.print("Enter Item Price: ");
        double itemPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter Item Description: ");
        String itemDescription = scanner.nextLine();

        // Creating an instance of the Item class
        Item newItem = new Item(itemId, itemName, itemPrice, itemDescription);

        System.out.println("Item added to cart: " + newItem);
    }

    private void viewCart() {
        // Viewing the user's shopping cart
        currentUser.viewCart();
    }

    private void removeFromCart() {
        if (currentUser instanceof Customer) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the product ID to remove from the cart: ");
            int productId = scanner.nextInt();
            ((Customer) currentUser).getShoppingCart().removeFromCart(productId);
        } else {
            System.out.println("Invalid user type for this operation.");
        }
    }


    private void placeOrder() {
        // Example: Placing an order
        List<Product> cartItems = currentUser.viewCart();
        double totalAmount = currentUser.calculateTotal();

        // Display order summary
        System.out.println("Order Summary:");
        currentUser.viewCart();
        System.out.println("Total Amount: $" + totalAmount);

        // Process payment
        boolean paymentSuccess = PaymentProcessor.processPayment(totalAmount, scanner);

        if (paymentSuccess) {
            // If payment is successful, proceed with order placement
            currentUser.placeOrder(cartItems);
        } else {
            System.out.println("Order placement failed due to payment issues.");
        }
    }

    private void cancelOrder() {
        System.out.print("Enter the Order ID to cancel: ");
        String orderId = scanner.nextLine();

        orderManager.cancelOrder(orderId);
    }

    private void updateOrderStatus() {
        System.out.print("Enter the Order ID to update status: ");
        String orderId = scanner.nextLine();

        // Assuming you have an instance of OrderManager called orderManager
        OrderStatus newStatus = promptForOrderStatus(); // Implement this method to get the new status

        orderManager.updateOrderStatus(orderId, newStatus);
    }

    private OrderStatus promptForOrderStatus() {
        System.out.println("Select the new order status:");
        System.out.println("1. PENDING");
        System.out.println("2. PROCESSING");
        System.out.println("3. SHIPPED");
        System.out.println("4. DELIVERED");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        return switch (choice) {
            case 1 -> OrderStatus.PENDING;
            case 2 -> OrderStatus.PROCESSING;
            case 3 -> OrderStatus.SHIPPED;
            case 4 -> OrderStatus.DELIVERED;
            case 5 -> OrderStatus.CANCELED;
            default -> {
                System.out.println("Invalid choice. Setting order status to PENDING.");
                yield OrderStatus.PENDING;
            }
        };
    }


    private void viewOrderHistory() {
        List<Order> orderHistory = currentUser.viewOrderHistory();
        for (Order order : orderHistory) {
            System.out.println(order);
        }
    }

    private void viewProfile() {
        currentUser.viewProfile();
    }

    private void updateProfile() {
        currentUser.updateProfile();
    }

    private void createUserAccount() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User newUser = authentication.register(username, password);

        if (newUser != null) {
            currentUser = newUser;
            System.out.println("Account created successfully. Welcome, " + currentUser.getName() + "!");
        }
    }

    private void logout() {
        authentication.logout();
        currentUser = null;
    }

    private void exit() {
        System.out.println("Exiting the ECommerce System. Goodbye!");
        scanner.close();
        System.exit(0);
    }

    private void loadOrders() {
        ordersList = fileHandler.readOrders();
        // Do something with the orders, such as updating your system's order records
    }

    // Example: Writing orders to file
    private void saveOrders(ArrayList<Order> ordersList) {
        StringBuilder content = new StringBuilder();
        for (Order order : ordersList) {
            String orderString = order.toFileString();
            content.append(orderString).append("\n");
        }

        try {
            fileHandler.writeFile(content.toString());
        } catch (IOException e) {
            System.out.println("Error writing orders to the file: " + e.getMessage());
        }
    }
}
