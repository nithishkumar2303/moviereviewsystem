package service;

import java.util.List;

import dao.GenreDAO;
import dao.MovieDAO;
import dao.RatingDAO;
import dao.UserDAO;
import model.Genre;
import model.Movie;
import model.Rating;
import model.User;

public class StatisticsService {
    private final MovieDAO movieDAO;
    private final RatingDAO ratingDAO;
    private final GenreDAO genreDAO;
    private final UserDAO userDAO;
    
    public StatisticsService() {
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        this.genreDAO = new GenreDAO();
        this.userDAO = new UserDAO();
    }
    
    public List<Movie> getTopRatedMovies(int limit) {
        return movieDAO.getTopRatedMovies(limit);
    }
    
    public List<Movie> getMostReviewedMovies(int limit) {
        return movieDAO.getMostReviewedMovies(limit);
    }
    
    public List<Genre> getTrendingGenres(int days, int limit) {
        return genreDAO.getTrendingGenres(days, limit);
    }
    
    public List<Rating> getLatestReviews(int limit) {
        return ratingDAO.getLatestReviews(limit);
    }
    
    public List<User> getTopReviewers(int limit) {
        return userDAO.getTopReviewers(limit);
    }
    
    public void printDashboard() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                         MOVIE STATS DASHBOARD");
        System.out.println("=".repeat(80));
        
        // Top Rated Movies
        System.out.println("\n📊 TOP RATED MOVIES:");
        System.out.println("-".repeat(40));
        List<Movie> topMovies = getTopRatedMovies(5);
        for (int i = 0; i < topMovies.size(); i++) {
            Movie m = topMovies.get(i);
            System.out.printf("%d. %s (%.1f ⭐) - %d reviews\n", 
                i + 1, m.getTitle(), m.getAverageRating(), m.getTotalRatings());
        }
        
        // Trending Genres
        System.out.println("\n🔥 TRENDING GENRES (Last 30 days):");
        System.out.println("-".repeat(40));
        List<Genre> trendingGenres = getTrendingGenres(30, 5);
        for (int i = 0; i < trendingGenres.size(); i++) {
            Genre g = trendingGenres.get(i);
            System.out.printf("%d. %s - %d ratings\n", 
                i + 1, g.getName(), g.getTotalRatings());
        }
        
        // Most Reviewed Movies
        System.out.println("\n💬 MOST REVIEWED MOVIES:");
        System.out.println("-".repeat(40));
        List<Movie> mostReviewed = getMostReviewedMovies(5);
        for (int i = 0; i < mostReviewed.size(); i++) {
            Movie m = mostReviewed.get(i);
            System.out.printf("%d. %s - %d reviews\n", 
                i + 1, m.getTitle(), m.getTotalRatings());
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
}