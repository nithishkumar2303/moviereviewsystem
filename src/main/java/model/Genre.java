package model;

public class Genre {
    private int genreId;
    private String name;
    private int totalRatings;

    public Genre() {}

    public Genre(int genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }

    // Getters and Setters
    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    
    @Override
    public String toString() {
        return name;
    }
}