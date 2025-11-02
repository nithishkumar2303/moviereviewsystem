package util;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static HikariDataSource dataSource;
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            
            // Fixed JDBC URL with proper MySQL 8+ settings
            config.setJdbcUrl("jdbc:mysql://localhost:3306/movie_review_system?" +
                    "useSSL=false&" +
                    "serverTimezone=UTC&" +
                    "allowPublicKeyRetrieval=true&" +  // FIX for Public Key Retrieval error
                    "useUnicode=true&" +
                    "characterEncoding=UTF-8");
            
            config.setUsername("root");
            config.setPassword("nithish23"); // Change this to your actual MySQL password
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            // Additional recommended settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("✅ Database connection pool initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database connection pool!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is not initialized or has been closed");
        }
        return dataSource.getConnection();
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }
    
    // Test connection method
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}