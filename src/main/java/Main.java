import ui.ConsoleUI;
import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // Register shutdown hook to close database connections
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down application...");
            DatabaseConnection.closeDataSource();
        }));
        
        // Test database connection before starting UI
        System.out.println("Testing database connection...");
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'movie_review_system' exists");
            System.err.println("3. Username and password are correct");
            System.err.println("4. MySQL is accessible on localhost:3306");
            return;
        }
        
        System.out.println("✅ Database connection successful!");
        
        // Start the application
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}