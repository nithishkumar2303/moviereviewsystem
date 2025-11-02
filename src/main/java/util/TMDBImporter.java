package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MovieDAO;
import dao.GenreDAO;
import model.Movie;
import model.Genre;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TMDBImporter {
    
    // Get your free API key from https://www.themoviedb.org/settings/api
    private static final String API_KEY = "e150e0de716250e2ccf65dbebdb37770";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    
    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;
    private final ObjectMapper objectMapper;
    private final Map<Integer, Integer> genreMapping;
    
    public TMDBImporter() {
        this.movieDAO = new MovieDAO();
        this.genreDAO = new GenreDAO();
        this.objectMapper = new ObjectMapper();
        this.genreMapping = new HashMap<>();
        initializeGenreMapping();
    }
    
    private void initializeGenreMapping() {
        // Map TMDB genre IDs to your database genre IDs
        genreMapping.put(28, 1);  // Action
        genreMapping.put(35, 2);  // Comedy
        genreMapping.put(18, 3);  // Drama
        genreMapping.put(27, 4);  // Horror
        genreMapping.put(878, 5); // Sci-Fi
        genreMapping.put(10749, 6); // Romance
        genreMapping.put(53, 7);  // Thriller
        // Add more mappings as needed
    }
    
    public void importPopularMovies(int pages) {
        System.out.println("Importing popular movies from TMDB...");
        
        for (int page = 1; page <= pages; page++) {
            try {
                String urlString = BASE_URL + "/movie/popular?api_key=" + API_KEY + "&page=" + page;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonNode root = objectMapper.readTree(response.toString());
                JsonNode results = root.get("results");
                
                for (JsonNode movieNode : results) {
                    Movie movie = parseMovie(movieNode);
                    if (movie != null) {
                        movieDAO.create(movie);
                        System.out.println("Imported: " + movie.getTitle());
                    }
                }
                
                System.out.println("Imported page " + page + " of " + pages);
                Thread.sleep(250); // Rate limiting
                
            } catch (Exception e) {
                System.err.println("Error importing movies: " + e.getMessage());
            }
        }
    }
    
    public void importBollywoodMovies(int pages) {
        System.out.println("Importing Bollywood movies from TMDB...");
        
        for (int page = 1; page <= pages; page++) {
            try {
                // Search for Indian movies
                String urlString = BASE_URL + "/discover/movie?api_key=" + API_KEY + 
                                  "&with_original_language=hi&page=" + page;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonNode root = objectMapper.readTree(response.toString());
                JsonNode results = root.get("results");
                
                for (JsonNode movieNode : results) {
                    Movie movie = parseMovie(movieNode);
                    if (movie != null) {
                        movieDAO.create(movie);
                        System.out.println("Imported: " + movie.getTitle());
                    }
                }
                
                System.out.println("Imported Bollywood page " + page + " of " + pages);
                Thread.sleep(250); // Rate limiting
                
            } catch (Exception e) {
                System.err.println("Error importing Bollywood movies: " + e.getMessage());
            }
        }
    }
    
    private Movie parseMovie(JsonNode movieNode) {
        try {
            Movie movie = new Movie();
            movie.setTitle(movieNode.get("title").asText());
            
            String releaseDate = movieNode.get("release_date").asText();
            if (!releaseDate.isEmpty()) {
                movie.setReleaseYear(Integer.parseInt(releaseDate.substring(0, 4)));
            }
            
            // Get first genre
            JsonNode genreIds = movieNode.get("genre_ids");
            if (genreIds != null && genreIds.size() > 0) {
                int tmdbGenreId = genreIds.get(0).asInt();
                Integer ourGenreId = genreMapping.get(tmdbGenreId);
                if (ourGenreId != null) {
                    movie.setGenreId(ourGenreId);
                } else {
                    movie.setGenreId(3); // Default to Drama
                }
            } else {
                movie.setGenreId(3); // Default to Drama
            }
            
            movie.setDescription(movieNode.get("overview").asText());
            movie.setDirector("Unknown"); // TMDB doesn't provide director in list API
            
            return movie;
        } catch (Exception e) {
            System.err.println("Error parsing movie: " + e.getMessage());
            return null;
        }
    }
}