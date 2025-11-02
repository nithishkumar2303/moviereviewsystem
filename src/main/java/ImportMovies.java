import java.util.Scanner;

import util.TMDBImporter;

public class ImportMovies {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TMDBImporter importer = new TMDBImporter();
        
        System.out.println("=".repeat(60));
        System.out.println("  TMDB MOVIE IMPORTER");
        System.out.println("=".repeat(60));
        
        System.out.println("\n1. Import Popular Hollywood Movies");
        System.out.println("2. Import Bollywood Movies");
        System.out.println("3. Import Both");
        System.out.print("\nChoose option: ");
        
        int choice = scanner.nextInt();
        
        System.out.print("How many pages to import (20 movies per page)? ");
        int pages = scanner.nextInt();
        
        switch (choice) {
            case 1:
                importer.importPopularMovies(pages);
                break;
            case 2:
                importer.importBollywoodMovies(pages);
                break;
            case 3:
                importer.importPopularMovies(pages);
                importer.importBollywoodMovies(pages);
                break;
            default:
                System.out.println("Invalid option!");
        }
        
        scanner.close();
        System.out.println("\n✅ Import completed!");
    }
}