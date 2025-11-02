package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Rating;
import util.DatabaseConnection;

public class RatingDAO {
    
    public Optional<Rating> findByUserAndMovie(int userId, int movieId) {
        String sql = "SELECT r.*, u.username, m.title as movie_title " +
                    "FROM ratings r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN movies m ON r.movie_id = m.movie_id " +
                    "WHERE r.user_id = ? AND r.movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Rating> findByMovie(int movieId) {
        String sql = "SELECT r.*, u.username, m.title as movie_title " +
                    "FROM ratings r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN movies m ON r.movie_id = m.movie_id " +
                    "WHERE r.movie_id = ? " +
                    "ORDER BY r.timestamp DESC";
        
        return executeRatingQuery(sql, movieId);
    }
    
    public List<Rating> findByUser(int userId) {
        String sql = "SELECT r.*, u.username, m.title as movie_title " +
                    "FROM ratings r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN movies m ON r.movie_id = m.movie_id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.timestamp DESC";
        
        return executeRatingQuery(sql, userId);
    }
    
    public List<Rating> getLatestReviews(int limit) {
        String sql = "SELECT r.*, u.username, m.title as movie_title " +
                    "FROM ratings r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "JOIN movies m ON r.movie_id = m.movie_id " +
                    "WHERE r.review_text IS NOT NULL AND r.review_text != '' " +
                    "ORDER BY r.timestamp DESC " +
                    "LIMIT ?";
        
        return executeRatingQuery(sql, limit);
    }
    
    public boolean create(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, movie_id, score, review_text) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, rating.getUserId());
            stmt.setInt(2, rating.getMovieId());
            stmt.setInt(3, rating.getScore());
            stmt.setString(4, rating.getReviewText());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    rating.setRatingId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(Rating rating) {
        String sql = "UPDATE ratings SET score = ?, review_text = ?, timestamp = CURRENT_TIMESTAMP " +
                    "WHERE user_id = ? AND movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rating.getScore());
            stmt.setString(2, rating.getReviewText());
            stmt.setInt(3, rating.getUserId());
            stmt.setInt(4, rating.getMovieId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getAverageRating(int movieId) {
        String sql = "SELECT AVG(score) as avg_rating FROM ratings WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private List<Rating> executeRatingQuery(String sql, int param) {
        List<Rating> ratings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, param);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }
    
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setRatingId(rs.getInt("rating_id"));
        rating.setUserId(rs.getInt("user_id"));
        rating.setMovieId(rs.getInt("movie_id"));
        rating.setScore(rs.getInt("score"));
        rating.setReviewText(rs.getString("review_text"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            rating.setTimestamp(timestamp.toLocalDateTime());
        }
        
        // These columns might not always be present
        try {
            rating.setUsername(rs.getString("username"));
            rating.setMovieTitle(rs.getString("movie_title"));
        } catch (SQLException e) {
            // Columns not present in this query
        }
        
        return rating;
    }
}