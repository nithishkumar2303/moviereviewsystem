import java.util.Scanner;

import util.MovieDataGenerator;
import util.TMDBImporter;

public class DataGeneratorMain {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(60));
        System.out.println("MASSIVE MOVIE DATA GENERATOR");
        System.out.println("=".repeat(60));
        System.out.println("1. Generate 1,000 movies");
        System.out.println("2. Generate 10,000 movies");
        System.out.println("3. Generate 1,00,000 (1 Lakh) movies");
        System.out.println("4. Generate 10,00,000 (10 Lakh) movies");
        System.out.println("5. Import real movies from TMDB API");
        System.out.println("6. Custom amount");
        System.out.print("Choose option: ");
        
        int choice = scanner.nextInt();
        
        switch (choice) {
            case 1:
                MovieDataGenerator.generateMovies(1000);
                MovieDataGenerator.generateRatings(5000);
                break;
                
            case 2:
                MovieDataGenerator.generateMovies(10000);
                MovieDataGenerator.generateRatings(50000);
                break;
                
            case 3:
                System.out.println("This will generate 1 Lakh movies. Continue? (y/n): ");
                if (scanner.next().equalsIgnoreCase("y")) {
                    MovieDataGenerator.generateMovies(100000);
                    MovieDataGenerator.generateRatings(500000);
                }
                break;
                
            case 4:
                System.out.println("WARNING: This will generate 10 Lakh movies!");
                System.out.println("This may take several hours. Continue? (y/n): ");
                if (scanner.next().equalsIgnoreCase("y")) {
                    MovieDataGenerator.generateMovies(1000000);
                    MovieDataGenerator.generateRatings(5000000);
                }
                break;
                
            case 5:
                System.out.println("Note: You need a TMDB API key for this.");
                System.out.print("How many pages to import (20 movies per page)? ");
                int pages = scanner.nextInt();
                TMDBImporter importer = new TMDBImporter();
                importer.importPopularMovies(pages);
                importer.importBollywoodMovies(pages);
                break;
                
            case 6:
                System.out.print("Enter number of movies to generate: ");
                int count = scanner.nextInt();
                MovieDataGenerator.generateMovies(count);
                System.out.print("Enter number of ratings to generate: ");
                int ratingCount = scanner.nextInt();
                MovieDataGenerator.generateRatings(ratingCount);
                break;
                
            default:
                System.out.println("Invalid option!");
        }
        
        scanner.close();
        System.out.println("\nData generation complete!");
    }
}