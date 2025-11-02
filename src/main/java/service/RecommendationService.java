package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import dao.MovieDAO;
import dao.RatingDAO;
import model.Movie;
import model.Rating;

public class RecommendationService {
    private final MovieDAO movieDAO;
    private final RatingDAO ratingDAO;
    
    public RecommendationService() {
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
    }
    
    /**
     * Get movie recommendations for a user based on collaborative filtering
     */
    public List<Movie> getRecommendations(int userId, int limit) {
        // Get all ratings by this user
        List<Rating> userRatings = ratingDAO.findByUser(userId);
        
        if (userRatings.isEmpty()) {
            // If user hasn't rated anything, return top rated movies
            return movieDAO.getTopRatedMovies(limit);
        }
        
        // Find movies from user's favorite genres
        Map<Integer, Double> genreScores = calculateGenrePreferences(userRatings);
        
        // Get all movies
        List<Movie> allMovies = movieDAO.findAll();
        
        // Filter out movies already rated by user
        Set<Integer> ratedMovieIds = userRatings.stream()
            .map(Rating::getMovieId)
            .collect(Collectors.toSet());
        
        List<Movie> unratedMovies = allMovies.stream()
            .filter(m -> !ratedMovieIds.contains(m.getMovieId()))
            .collect(Collectors.toList());
        
        // Score each unrated movie based on genre preference and overall rating
        Map<Movie, Double> movieScores = new HashMap<>();
        for (Movie movie : unratedMovies) {
            double genreScore = genreScores.getOrDefault(movie.getGenreId(), 0.0);
            double ratingScore = movie.getAverageRating() / 5.0; // Normalize to 0-1
            double totalScore = (genreScore * 0.7) + (ratingScore * 0.3); // Weight genre preference more
            movieScores.put(movie, totalScore);
        }
        
        // Sort by score and return top N
        return movieScores.entrySet().stream()
            .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate user's genre preferences based on their ratings
     */
    private Map<Integer, Double> calculateGenrePreferences(List<Rating> userRatings) {
        Map<Integer, List<Integer>> genreRatings = new HashMap<>();
        
        for (Rating rating : userRatings) {
            Optional<Movie> movie = movieDAO.findById(rating.getMovieId());
            if (movie.isPresent()) {
                genreRatings.computeIfAbsent(movie.get().getGenreId(), k -> new ArrayList<>())
                    .add(rating.getScore());
            }
        }
        
        Map<Integer, Double> genreScores = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : genreRatings.entrySet()) {
            double avgScore = entry.getValue().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
            genreScores.put(entry.getKey(), avgScore / 5.0); // Normalize to 0-1
        }
        
        return genreScores;
    }
    
    /**
     * Get movies similar to a given movie
     */
    public List<Movie> getSimilarMovies(int movieId, int limit) {
        Optional<Movie> targetMovie = movieDAO.findById(movieId);
        if (targetMovie.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Find movies from the same genre
        List<Movie> genreMovies = movieDAO.findByGenre(targetMovie.get().getGenreId());
        
        // Remove the target movie and limit results
        return genreMovies.stream()
            .filter(m -> m.getMovieId() != movieId)
            .limit(limit)
            .collect(Collectors.toList());
    }
}