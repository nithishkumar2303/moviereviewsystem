package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import dao.GenreDAO;
import dao.MovieDAO;
import dao.UserDAO;
import model.Genre;
import model.Movie;
import model.User;

public class MovieDataGenerator {
    
    private static final Random random = new Random();
    
    // Movie title components for generation
    private static final String[] TITLE_PREFIXES = {
        "The", "A", "An", "Last", "First", "Final", "Ultimate", "Secret", 
        "Hidden", "Lost", "Forgotten", "Ancient", "Modern", "Future", "Past"
    };
    
    private static final String[] TITLE_SUBJECTS = {
        "King", "Queen", "Warrior", "Knight", "Dragon", "Phoenix", "Shadow",
        "Light", "Dark", "Storm", "Fire", "Ice", "Wind", "Earth", "Star",
        "Moon", "Sun", "Ocean", "Mountain", "Forest", "Desert", "City",
        "Love", "War", "Peace", "Journey", "Quest", "Adventure", "Mystery",
        "Secret", "Legend", "Hero", "Villain", "Angel", "Demon", "Spirit"
    };
    
    private static final String[] TITLE_SUFFIXES = {
        "Rising", "Falls", "Returns", "Begins", "Ends", "Chronicles",
        "Legacy", "Origins", "Destiny", "Prophecy", "Awakening", "Reborn",
        "Revolution", "Redemption", "Revenge", "Resurrection", "Revelation"
    };
    
    // Indian movie title components
    private static final String[] HINDI_TITLES = {
        "Pyaar", "Ishq", "Mohabbat", "Dil", "Dilwale", "Deewana", "Raja",
        "Rani", "Khiladi", "Hero", "Dosti", "Yaari", "Zindagi", "Kahani",
        "Sapna", "Armaan", "Khushi", "Gham", "Judaai", "Milan", "Sangam",
        "Bandhan", "Rishta", "Saathi", "Humsafar", "Raaz", "Bhoot", "Aatma"
    };
    
    private static final String[] DIRECTORS = {
        // Hollywood
        "Steven Spielberg", "Christopher Nolan", "Martin Scorsese", "Quentin Tarantino",
        "James Cameron", "Ridley Scott", "David Fincher", "Coen Brothers",
        "Wes Anderson", "Paul Thomas Anderson", "Denis Villeneuve", "Damien Chazelle",
        // Bollywood
        "Rajkumar Hirani", "Karan Johar", "Sanjay Leela Bhansali", "Anurag Kashyap",
        "Imtiaz Ali", "Rohit Shetty", "Zoya Akhtar", "Vishal Bhardwaj",
        // South Indian
        "S.S. Rajamouli", "Mani Ratnam", "Shankar", "Gautham Menon",
        "Trivikram Srinivas", "Sukumar", "Lokesh Kanagaraj", "Atlee"
    };
    
    private static final String[] DESCRIPTIONS = {
        "A thrilling adventure that will keep you on the edge of your seat",
        "An emotional journey of love, loss, and redemption",
        "A masterpiece of cinema that transcends genres",
        "A visual spectacle with stunning cinematography",
        "A thought-provoking drama that questions society",
        "An action-packed thriller with unexpected twists",
        "A heartwarming story of friendship and courage",
        "A dark psychological thriller that explores human nature",
        "A romantic tale that spans generations",
        "An epic saga of power, betrayal, and revenge"
    };
    
    /**
     * Generate random movies and insert into database
     */
    public static void generateMovies(int count) {
        System.out.println("Starting generation of " + count + " movies...");
        
        MovieDAO movieDAO = new MovieDAO();
        GenreDAO genreDAO = new GenreDAO();
        
        List<Genre> genres = genreDAO.findAll();
        if (genres.isEmpty()) {
            System.out.println("No genres found! Please run the schema first.");
            return;
        }
        
        int batchSize = 1000;
        int generated = 0;
        
        String sql = "INSERT INTO movies (title, release_year, genre_id, director, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < count; i++) {
                    Movie movie = generateRandomMovie(genres);
                    
                    stmt.setString(1, movie.getTitle());
                    stmt.setInt(2, movie.getReleaseYear());
                    stmt.setInt(3, movie.getGenreId());
                    stmt.setString(4, movie.getDirector());
                    stmt.setString(5, movie.getDescription());
                    stmt.addBatch();
                    
                    generated++;
                    
                    if (generated % batchSize == 0) {
                        stmt.executeBatch();
                        conn.commit();
                        System.out.println("Generated " + generated + " movies...");
                    }
                }
                
                // Execute remaining batch
                if (generated % batchSize != 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
                
                System.out.println("Successfully generated " + generated + " movies!");
            }
        } catch (SQLException e) {
            System.err.println("Error generating movies: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate random ratings for existing movies
     */
    public static void generateRatings(int count) {
        System.out.println("Starting generation of " + count + " ratings...");
        
        MovieDAO movieDAO = new MovieDAO();
        UserDAO userDAO = new UserDAO();
        
        List<Movie> movies = movieDAO.findAll();
        if (movies.isEmpty()) {
            System.out.println("No movies found! Generate movies first.");
            return;
        }
        
        // Create sample users if not exist
        List<User> users = createSampleUsers(100);
        
        int batchSize = 1000;
        int generated = 0;
        
        String sql = "INSERT IGNORE INTO ratings (user_id, movie_id, score, review_text) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < count; i++) {
                    User randomUser = users.get(random.nextInt(users.size()));
                    Movie randomMovie = movies.get(random.nextInt(movies.size()));
                    int score = random.nextInt(5) + 1;
                    String review = generateRandomReview(score);
                    
                    stmt.setInt(1, randomUser.getUserId());
                    stmt.setInt(2, randomMovie.getMovieId());
                    stmt.setInt(3, score);
                    stmt.setString(4, review);
                    stmt.addBatch();
                    
                    generated++;
                    
                    if (generated % batchSize == 0) {
                        stmt.executeBatch();
                        conn.commit();
                        System.out.println("Generated " + generated + " ratings...");
                    }
                }
                
                // Execute remaining batch
                if (generated % batchSize != 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
                
                System.out.println("Successfully generated " + generated + " ratings!");
            }
        } catch (SQLException e) {
            System.err.println("Error generating ratings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Movie generateRandomMovie(List<Genre> genres) {
        Movie movie = new Movie();
        
        // Generate title based on random selection (70% English, 30% Hindi)
        if (random.nextDouble() < 0.7) {
            movie.setTitle(generateEnglishTitle());
        } else {
            movie.setTitle(generateHindiTitle());
        }
        
        // Random year between 1950 and 2024
        movie.setReleaseYear(1950 + random.nextInt(75));
        
        // Random genre
        Genre randomGenre = genres.get(random.nextInt(genres.size()));
        movie.setGenreId(randomGenre.getGenreId());
        
        // Random director
        movie.setDirector(DIRECTORS[random.nextInt(DIRECTORS.length)]);
        
        // Random description
        movie.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);
        
        return movie;
    }
    
    private static String generateEnglishTitle() {
        StringBuilder title = new StringBuilder();
        
        // 50% chance to add prefix
        if (random.nextBoolean()) {
            title.append(TITLE_PREFIXES[random.nextInt(TITLE_PREFIXES.length)]).append(" ");
        }
        
        // Add subject
        title.append(TITLE_SUBJECTS[random.nextInt(TITLE_SUBJECTS.length)]);
        
        // 30% chance to add suffix
        if (random.nextDouble() < 0.3) {
            title.append(" ").append(TITLE_SUFFIXES[random.nextInt(TITLE_SUFFIXES.length)]);
        }
        
        // 20% chance to add part number
        if (random.nextDouble() < 0.2) {
            title.append(" ").append(random.nextInt(5) + 1);
        }
        
        return title.toString();
    }
    
    private static String generateHindiTitle() {
        StringBuilder title = new StringBuilder();
        
        title.append(HINDI_TITLES[random.nextInt(HINDI_TITLES.length)]);
        
        // 50% chance to add another word
        if (random.nextBoolean()) {
            title.append(" ").append(HINDI_TITLES[random.nextInt(HINDI_TITLES.length)]);
        }
        
        // 30% chance to add "Ki" or "Ka" 
        if (random.nextDouble() < 0.3) {
            title.append(random.nextBoolean() ? " Ki " : " Ka ");
            title.append(HINDI_TITLES[random.nextInt(HINDI_TITLES.length)]);
        }
        
        return title.toString();
    }
    
    private static String generateRandomReview(int score) {
        String[] positiveReviews = {
            "Amazing movie! Must watch!",
            "Brilliant performance by the cast",
            "Exceeded my expectations",
            "A masterpiece!",
            "Loved every moment of it",
            "Outstanding direction and cinematography",
            "One of the best movies I've seen",
            "Highly recommended!",
            "A cinematic gem",
            "Perfect in every way"
        };
        
        String[] neutralReviews = {
            "Good movie, worth watching",
            "Decent entertainment",
            "Has its moments",
            "Not bad, could be better",
            "Average but enjoyable",
            "Good for a one-time watch",
            "Fairly entertaining",
            "Okay movie",
            "Nothing special but watchable",
            "Mixed feelings about this one"
        };
        
        String[] negativeReviews = {
            "Disappointing",
            "Not worth the time",
            "Could have been much better",
            "Waste of talent",
            "Poor execution",
            "Boring and predictable",
            "Expected more",
            "Not recommended",
            "Failed to impress",
            "Below average"
        };
        
        // 70% chance to leave a review
        if (random.nextDouble() < 0.3) {
            return null;
        }
        
        if (score >= 4) {
            return positiveReviews[random.nextInt(positiveReviews.length)];
        } else if (score == 3) {
            return neutralReviews[random.nextInt(neutralReviews.length)];
        } else {
            return negativeReviews[random.nextInt(negativeReviews.length)];
        }
    }
    
    private static List<User> createSampleUsers(int count) {
        UserDAO userDAO = new UserDAO();
        List<User> users = new ArrayList<>();
        
        String[] firstNames = {"John", "Jane", "Mike", "Sarah", "David", "Emma", 
                              "Raj", "Priya", "Amit", "Neha", "Rahul", "Anjali"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones",
                             "Kumar", "Sharma", "Patel", "Singh", "Gupta"};
        
        for (int i = 0; i < count; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String username = firstName.toLowerCase() + lastName.toLowerCase() + random.nextInt(1000);
            String email = username + "@example.com";
            
            User user = new User(username, email, PasswordUtil.hashPassword("password123"));
            
            if (userDAO.create(user)) {
                users.add(user);
            }
        }
        
        return users;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(60));
        System.out.println("MOVIE DATA GENERATOR");
        System.out.println("=".repeat(60));
        System.out.println("1. Generate Movies");
        System.out.println("2. Generate Ratings");
        System.out.println("3. Generate Both");
        System.out.print("Choose option: ");
        
        int choice = scanner.nextInt();
        
        switch (choice) {
            case 1:
                System.out.print("How many movies to generate? ");
                int movieCount = scanner.nextInt();
                generateMovies(movieCount);
                break;
                
            case 2:
                System.out.print("How many ratings to generate? ");
                int ratingCount = scanner.nextInt();
                generateRatings(ratingCount);
                break;
                
            case 3:
                System.out.print("How many movies to generate? ");
                int mCount = scanner.nextInt();
                System.out.print("How many ratings to generate? ");
                int rCount = scanner.nextInt();
                generateMovies(mCount);
                generateRatings(rCount);
                break;
                
            default:
                System.out.println("Invalid option!");
        }
        
        scanner.close();
    }
}