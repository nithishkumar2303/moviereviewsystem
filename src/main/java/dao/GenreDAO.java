package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Genre;
import util.DatabaseConnection;

public class GenreDAO {
    
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY name";
        List<Genre> genres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                genres.add(mapResultSetToGenre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }
    
    public Optional<Genre> findById(int genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToGenre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Genre> getTrendingGenres(int days, int limit) {
        String sql = "SELECT g.genre_id, g.name, COUNT(r.rating_id) as total_ratings " +
                    "FROM genres g " +
                    "JOIN movies m ON g.genre_id = m.genre_id " +
                    "JOIN ratings r ON m.movie_id = r.movie_id " +
                    "WHERE r.timestamp >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                    "GROUP BY g.genre_id " +
                    "ORDER BY total_ratings DESC " +
                    "LIMIT ?";
        
        List<Genre> genres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, days);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setGenreId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));
                genre.setTotalRatings(rs.getInt("total_ratings"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }
    
    private Genre mapResultSetToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}