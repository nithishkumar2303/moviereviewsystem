package model;

import java.time.LocalDateTime;

public class Rating {
    private int ratingId;
    private int userId;
    private int movieId;
    private int score;
    private String reviewText;
    private LocalDateTime timestamp;
    private String username;
    private String movieTitle;

    public Rating() {}

    public Rating(int userId, int movieId, int score, String reviewText) {
        this.userId = userId;
        this.movieId = movieId;
        this.score = score;
        this.reviewText = reviewText;
    }

    // Getters and Setters
    public int getRatingId() { return ratingId; }
    public void setRatingId(int ratingId) { this.ratingId = ratingId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    
    @Override
    public String toString() {
        return String.format("Rating: %d/5 by %s for %s - %s", 
            score, username, movieTitle, reviewText);
    }
}