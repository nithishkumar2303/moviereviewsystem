package ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import dao.GenreDAO;
import dao.MovieDAO;
import dao.RatingDAO;
import dao.UserDAO;
import model.Genre;
import model.Movie;
import model.Rating;
import model.User;
import service.RecommendationService;
import service.StatisticsService;
import util.PasswordUtil;

public class ConsoleUI {
    private final Scanner scanner;
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;
    private final RatingDAO ratingDAO;
    private final GenreDAO genreDAO;
    private final RecommendationService recommendationService;
    private final StatisticsService statisticsService;
    private User currentUser;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.userDAO = new UserDAO();
        this.movieDAO = new MovieDAO();
        this.ratingDAO = new RatingDAO();
        this.genreDAO = new GenreDAO();
        this.recommendationService = new RecommendationService();
        this.statisticsService = new StatisticsService();
    }
    
    public void start() {
        printWelcome();
        
        while (true) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu();
            }
        }
    }
    
    private void printWelcome() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("     🎬 WELCOME TO MOVIE RECOMMENDATION & REVIEW SYSTEM 🎬");
        System.out.println("=".repeat(80));
    }
    
    private void showGuestMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU (Not Logged In)");
        System.out.println("-".repeat(50));
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Browse Movies");
        System.out.println("4. Search Movies");
        System.out.println("5. View Statistics");
        System.out.println("6. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Choose an option: ");
        
        int choice = readInt();
        
        switch (choice) {
            case 1: login(); break;
            case 2: register(); break;
            case 3: browseMovies(); break;
            case 4: searchMovies(); break;
            case 5: viewStatistics(); break;
            case 6: exit(); break;
            default: System.out.println("❌ Invalid option!");
        }
    }
    
    private void showUserMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("USER MENU - Welcome, " + currentUser.getUsername() + "!");
        System.out.println("-".repeat(50));
        System.out.println("1. Browse Movies");
        System.out.println("2. Search Movies");
        System.out.println("3. Rate/Review a Movie");
        System.out.println("4. View My Ratings");
        System.out.println("5. Get Recommendations");
        System.out.println("6. View Statistics");
        System.out.println("7. Logout");
        System.out.println("8. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Choose an option: ");
        
        int choice = readInt();
        
        switch (choice) {
            case 1: browseMovies(); break;
            case 2: searchMovies(); break;
            case 3: rateMovie(); break;
            case 4: viewMyRatings(); break;
            case 5: getRecommendations(); break;
            case 6: viewStatistics(); break;
            case 7: logout(); break;
            case 8: exit(); break;
            default: System.out.println("❌ Invalid option!");
        }
    }
    
    private void login() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("LOGIN");
        System.out.println("-".repeat(50));
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        Optional<User> user = userDAO.findByUsername(username);
        
        if (user.isPresent() && PasswordUtil.checkPassword(password, user.get().getPasswordHash())) {
            currentUser = user.get();
            System.out.println("✅ Login successful! Welcome back, " + username + "!");
        } else {
            System.out.println("❌ Invalid username or password!");
        }
    }
    
    private void register() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("REGISTER NEW ACCOUNT");
        System.out.println("-".repeat(50));
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        if (userDAO.findByUsername(username).isPresent()) {
            System.out.println("❌ Username already exists!");
            return;
        }
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        if (userDAO.emailExists(email)) {
            System.out.println("❌ Email already registered!");
            return;
        }
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (password.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters!");
            return;
        }
        
        User newUser = new User(username, email, PasswordUtil.hashPassword(password));
        
        if (userDAO.create(newUser)) {
            System.out.println("✅ Registration successful! You can now login.");
        } else {
            System.out.println("❌ Registration failed. Please try again.");
        }
    }
    
    private void browseMovies() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("BROWSE MOVIES");
        System.out.println("-".repeat(50));
        System.out.println("1. View All Movies");
        System.out.println("2. Browse by Genre");
        System.out.println("3. Top Rated Movies");
        System.out.println("4. Back");
        System.out.print("Choose an option: ");
        
        int choice = readInt();
        
        switch (choice) {
            case 1: viewAllMovies(); break;
            case 2: browseByGenre(); break;
            case 3: viewTopRatedMovies(); break;
            case 4: return;
            default: System.out.println("❌ Invalid option!");
        }
    }
    
    private void viewAllMovies() {
        List<Movie> movies = movieDAO.findAll();
        displayMovies("ALL MOVIES", movies);
    }
    
    private void browseByGenre() {
        List<Genre> genres = genreDAO.findAll();
        
        System.out.println("\n" + "-".repeat(50));
        System.out.println("AVAILABLE GENRES:");
        for (int i = 0; i < genres.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, genres.get(i).getName());
        }
        System.out.print("Select genre (number): ");
        
        int choice = readInt();
        if (choice < 1 || choice > genres.size()) {
            System.out.println("❌ Invalid genre!");
            return;
        }
        
        Genre selectedGenre = genres.get(choice - 1);
        List<Movie> movies = movieDAO.findByGenre(selectedGenre.getGenreId());
        displayMovies("MOVIES IN " + selectedGenre.getName().toUpperCase(), movies);
    }
    
    private void viewTopRatedMovies() {
        List<Movie> movies = movieDAO.getTopRatedMovies(10);
        displayMovies("TOP RATED MOVIES", movies);
    }
    
    private void searchMovies() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("SEARCH MOVIES");
        System.out.println("-".repeat(50));
        System.out.print("Enter movie title (or part of it): ");
        
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("❌ Search term cannot be empty!");
            return;
        }
        
        List<Movie> movies = movieDAO.searchByTitle(searchTerm);
        
        if (movies.isEmpty()) {
            System.out.println("❌ No movies found matching: " + searchTerm);
        } else {
            displayMovies("SEARCH RESULTS FOR: " + searchTerm, movies);
        }
    }
    
    private void displayMovies(String title, List<Movie> movies) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(title);
        System.out.println("=".repeat(80));
        
        if (movies.isEmpty()) {
            System.out.println("No movies found.");
            return;
        }
        
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            System.out.printf("\n%d. %s (%d)\n", i + 1, movie.getTitle(), movie.getReleaseYear());
            System.out.printf("   Director: %s | Genre: %s\n", movie.getDirector(), movie.getGenreName());
            System.out.printf("   Rating: %.1f ⭐ (%d reviews)\n", 
                movie.getAverageRating(), movie.getTotalRatings());
            if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
                System.out.printf("   Description: %s\n", movie.getDescription());
            }
        }
        
        if (currentUser != null) {
            System.out.println("\n" + "-".repeat(80));
            System.out.print("Enter movie number to view details/rate (0 to go back): ");
            int choice = readInt();
            
            if (choice > 0 && choice <= movies.size()) {
                viewMovieDetails(movies.get(choice - 1));
            }
        }
    }
    
    private void viewMovieDetails(Movie movie) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MOVIE DETAILS");
        System.out.println("=".repeat(80));
        System.out.println("Title: " + movie.getTitle());
        System.out.println("Year: " + movie.getReleaseYear());
        System.out.println("Director: " + movie.getDirector());
        System.out.println("Genre: " + movie.getGenreName());
        System.out.println("Average Rating: " + String.format("%.1f", movie.getAverageRating()) + " ⭐");
        System.out.println("Total Ratings: " + movie.getTotalRatings());
        
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            System.out.println("Description: " + movie.getDescription());
        }
        
        // Show recent reviews
        List<Rating> reviews = ratingDAO.findByMovie(movie.getMovieId());
        if (!reviews.isEmpty()) {
            System.out.println("\n" + "-".repeat(40));
            System.out.println("RECENT REVIEWS:");
            int count = 0;
            for (Rating rating : reviews) {
                if (rating.getReviewText() != null && !rating.getReviewText().isEmpty()) {
                    System.out.printf("\n%s (Rating: %d/5)\n", rating.getUsername(), rating.getScore());
                    System.out.println("\"" + rating.getReviewText() + "\"");
                    count++;
                    if (count >= 3) break; // Show only top 3 reviews
                }
            }
        }
        
        if (currentUser != null) {
            System.out.println("\n" + "-".repeat(40));
            System.out.println("1. Rate/Review this movie");
            System.out.println("2. View similar movies");
            System.out.println("3. Back");
            System.out.print("Choose an option: ");
            
            int choice = readInt();
            switch (choice) {
                case 1: rateSpecificMovie(movie); break;
                case 2: viewSimilarMovies(movie); break;
                case 3: return;
            }
        }
    }
    
    private void viewSimilarMovies(Movie movie) {
        List<Movie> similarMovies = recommendationService.getSimilarMovies(movie.getMovieId(), 5);
        if (similarMovies.isEmpty()) {
            System.out.println("No similar movies found.");
        } else {
            displayMovies("MOVIES SIMILAR TO: " + movie.getTitle(), similarMovies);
        }
    }
    
    private void rateMovie() {
        System.out.print("Enter movie title to rate: ");
        String title = scanner.nextLine().trim();
        
        List<Movie> movies = movieDAO.searchByTitle(title);
        
        if (movies.isEmpty()) {
            System.out.println("❌ No movies found!");
            return;
        }
        
        if (movies.size() == 1) {
            rateSpecificMovie(movies.get(0));
        } else {
            System.out.println("\nMultiple movies found:");
            for (int i = 0; i < movies.size(); i++) {
                System.out.printf("%d. %s (%d)\n", i + 1, 
                    movies.get(i).getTitle(), movies.get(i).getReleaseYear());
            }
            System.out.print("Select movie number: ");
            int choice = readInt();
            
            if (choice > 0 && choice <= movies.size()) {
                rateSpecificMovie(movies.get(choice - 1));
            } else {
                System.out.println("❌ Invalid selection!");
            }
        }
    }
    
    private void rateSpecificMovie(Movie movie) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("RATE: " + movie.getTitle());
        System.out.println("-".repeat(50));
        
        // Check if user already rated this movie
        Optional<Rating> existingRating = ratingDAO.findByUserAndMovie(
            currentUser.getUserId(), movie.getMovieId());
        
        if (existingRating.isPresent()) {
            System.out.println("You already rated this movie: " + 
                existingRating.get().getScore() + "/5");
            System.out.print("Do you want to update your rating? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.equals("y")) {
                return;
            }
        }
        
        System.out.print("Your rating (1-5): ");
        int score = readInt();
        
        if (score < 1 || score > 5) {
            System.out.println("❌ Rating must be between 1 and 5!");
            return;
        }
        
        System.out.print("Write a review (optional, press Enter to skip): ");
        String reviewText = scanner.nextLine().trim();
        
        Rating rating = new Rating(currentUser.getUserId(), movie.getMovieId(), score, reviewText);
        
        boolean success;
        if (existingRating.isPresent()) {
            success = ratingDAO.update(rating);
        } else {
            success = ratingDAO.create(rating);
        }
        
        if (success) {
            System.out.println("✅ Rating saved successfully!");
        } else {
            System.out.println("❌ Failed to save rating!");
        }
    }
    
    private void viewMyRatings() {
        List<Rating> myRatings = ratingDAO.findByUser(currentUser.getUserId());
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MY RATINGS");
        System.out.println("=".repeat(80));
        
        if (myRatings.isEmpty()) {
            System.out.println("You haven't rated any movies yet!");
            return;
        }
        
        for (Rating rating : myRatings) {
            System.out.printf("\n%s - Rating: %d/5\n", rating.getMovieTitle(), rating.getScore());
            if (rating.getReviewText() != null && !rating.getReviewText().isEmpty()) {
                System.out.println("Review: " + rating.getReviewText());
            }
            System.out.println("Date: " + rating.getTimestamp());
        }
    }
    
    private void getRecommendations() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERSONALIZED RECOMMENDATIONS FOR YOU");
        System.out.println("=".repeat(80));
        
        List<Movie> recommendations = recommendationService.getRecommendations(
            currentUser.getUserId(), 10);
        
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available. Try rating some movies first!");
        } else {
            displayMovies("RECOMMENDED FOR YOU", recommendations);
        }
    }
    
    private void viewStatistics() {
        statisticsService.printDashboard();
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void logout() {
        System.out.println("👋 Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }
    
    private void exit() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Thank you for using Movie Recommendation System!");
        System.out.println("Goodbye! 👋");
        System.out.println("=".repeat(50));
        System.exit(0);
    }
    
    private int readInt() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}