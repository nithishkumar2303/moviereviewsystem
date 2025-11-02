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

import model.Movie;
import util.DatabaseConnection;

public class MovieDAO {
    
    public List<Movie> findAll() {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "COALESCE(AVG(r.score), 0) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "LEFT JOIN ratings r ON m.movie_id = r.movie_id " +
                    "GROUP BY m.movie_id " +
                    "ORDER BY m.title";
        
        return executeMovieQuery(sql);
    }
    
    public List<Movie> findByGenre(int genreId) {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "COALESCE(AVG(r.score), 0) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "LEFT JOIN ratings r ON m.movie_id = r.movie_id " +
                    "WHERE m.genre_id = ? " +
                    "GROUP BY m.movie_id " +
                    "ORDER BY avg_rating DESC";
        
        List<Movie> movies = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    public List<Movie> searchByTitle(String title) {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "COALESCE(AVG(r.score), 0) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "LEFT JOIN ratings r ON m.movie_id = r.movie_id " +
                    "WHERE LOWER(m.title) LIKE LOWER(?) " +
                    "GROUP BY m.movie_id " +
                    "ORDER BY m.title";
        
        List<Movie> movies = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    public Optional<Movie> findById(int movieId) {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "COALESCE(AVG(r.score), 0) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "LEFT JOIN ratings r ON m.movie_id = r.movie_id " +
                    "WHERE m.movie_id = ? " +
                    "GROUP BY m.movie_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Movie> getTopRatedMovies(int limit) {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "AVG(r.score) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "INNER JOIN ratings r ON m.movie_id = r.movie_id " +
                    "GROUP BY m.movie_id " +
                    "HAVING total_ratings >= 1 " +
                    "ORDER BY avg_rating DESC, total_ratings DESC " +
                    "LIMIT ?";
        
        List<Movie> movies = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    public List<Movie> getMostReviewedMovies(int limit) {
        String sql = "SELECT m.*, g.name as genre_name, " +
                    "COALESCE(AVG(r.score), 0) as avg_rating, " +
                    "COUNT(r.rating_id) as total_ratings " +
                    "FROM movies m " +
                    "LEFT JOIN genres g ON m.genre_id = g.genre_id " +
                    "INNER JOIN ratings r ON m.movie_id = r.movie_id " +
                    "WHERE r.review_text IS NOT NULL AND r.review_text != '' " +
                    "GROUP BY m.movie_id " +
                    "ORDER BY total_ratings DESC " +
                    "LIMIT ?";
        
        List<Movie> movies = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    public boolean create(Movie movie) {
        String sql = "INSERT INTO movies (title, release_year, genre_id, director, description) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getReleaseYear());
            stmt.setInt(3, movie.getGenreId());
            stmt.setString(4, movie.getDirector());
            stmt.setString(5, movie.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    movie.setMovieId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private List<Movie> executeMovieQuery(String sql) {
        List<Movie> movies = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setMovieId(rs.getInt("movie_id"));
        movie.setTitle(rs.getString("title"));
        movie.setReleaseYear(rs.getInt("release_year"));
        movie.setGenreId(rs.getInt("genre_id"));
        movie.setDirector(rs.getString("director"));
        movie.setDescription(rs.getString("description"));
        
        // These columns might not always be present
        try {
            movie.setGenreName(rs.getString("genre_name"));
            movie.setAverageRating(rs.getDouble("avg_rating"));
            movie.setTotalRatings(rs.getInt("total_ratings"));
        } catch (SQLException e) {
            // Columns not present in this query
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            movie.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return movie;
    }
}