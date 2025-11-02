package model;

import java.time.LocalDateTime;

public class Movie {
    private int movieId;
    private String title;
    private int releaseYear;
    private int genreId;
    private String genreName;
    private String director;
    private String description;
    private double averageRating;
    private int totalRatings;
    private LocalDateTime createdAt;

    public Movie() {}

    public Movie(String title, int releaseYear, int genreId, String director, String description) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genreId = genreId;
        this.director = director;
        this.description = description;
    }

    // Getters and Setters
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    
    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
    
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("%s (%d) - %s | Rating: %.1f (%d reviews)", 
            title, releaseYear, genreName, averageRating, totalRatings);
    }
}