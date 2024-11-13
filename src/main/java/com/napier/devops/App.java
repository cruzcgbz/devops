package com.napier.devops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class App {

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;
    private static final String OUTPUT_DIR = "/tmp/output";  // Fixed path in container
    public App() {
        // Create output directory in constructor
        createOutputDirectory();
    }
    private void createOutputDirectory() {
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (created) {
                System.out.println("Created output directory: " + OUTPUT_DIR);
            } else {
                System.err.println("Failed to create output directory: " + OUTPUT_DIR);
            }
        }
    }

    /**
     * Connects to the MySQL database.
     *
     * @param conString the connection string (e.g., "localhost:33060" for local, "db:3306" for Docker)
     * @param delay     the delay in milliseconds before trying to connect
     */
    public void connect(String conString, int delay) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait before connecting to allow for potential delays
                Thread.sleep(delay);
                // Connect to the database
                con = DriverManager.getConnection("jdbc:mysql://" + conString + "/world?allowPublicKeyRetrieval=true&useSSL=false", "root", "example");
                System.out.println("Successfully connected to the database");
                break;
            } catch (SQLException sqle) {
                System.err.println("Failed to connect to database on attempt " + (i + 1));
                System.err.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.err.println("Thread interrupted unexpectedly");
            }
        }

        if (con == null) {
            System.err.println("Unable to establish database connection after " + retries + " attempts.");
            System.exit(-1);
        }
    }

    /**
     * Disconnects from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from the database");
            } catch (SQLException e) {
                System.err.println("Error closing connection to the database");
            }
        }
    }

    /**
     * Retrieves country information based on a search term and writes the results to a file.
     *
     * @param searchTerm the search term for country name
     * @throws IOException if there is an issue with file operations
     */
    public String searchCountryByName(String searchTerm, String outputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT code, name, continent, region, population FROM country WHERE name LIKE ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rset = stmt.executeQuery();

            // Check if any results were returned
            if (!rset.isBeforeFirst()) {
                sb.append("No results found for: ").append(searchTerm);
            } else {
                // Add headers for the table
                sb.append(String.format("%-10s %-30s %-20s %-20s %-15s\n", "Code", "Name", "Continent", "Region", "Population"));
                sb.append("--------------------------------------------------------------\n");

                // Iterate through results and display in tabular format
                while (rset.next()) {
                    String code = rset.getString("code");
                    String name = rset.getString("name");
                    String continent = rset.getString("continent");
                    String region = rset.getString("region");
                    int population = rset.getInt("population");

                    sb.append(String.format("%-10s %-30s %-20s %-20s %-15d\n", code, name, continent, region, population));
                }
            }

            // Write to file (optional, if you still need to save the output)
            writeToFile(sb.toString(), outputFile);

        } catch (SQLException e) {
            System.err.println("Failed to execute search query: " + e.getMessage());
        }

        // Return the results as a String to display in the GUI
        return sb.toString();
    }





    /**
     * Writes text content to a specified file.
     *
     * @param content  the content to write
     * @param filePath the file path for saving the content
     * @throws IOException if an I/O error occurs
     */
    private void writeToFile(String content, String filePath) throws IOException {
        File file = new File(OUTPUT_DIR, new File(filePath).getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + file.getPath(), e);
        }
    }




    public void report1(String searchTerm) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM country WHERE name LIKE '%" + searchTerm + "%'";
            ResultSet rset = stmt.executeQuery(sql);

            // Check if any results were returned
            if (!rset.isBeforeFirst()) { // This checks if ResultSet is empty
                sb.append("No results found for: ").append(searchTerm);
            } else {
                while (rset.next()) {
                    String name = rset.getString("name");
                    Integer population = rset.getInt("population");
                    sb.append(name).append("\t").append(population).append("\n");
                }
            }

            new File("./output/").mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./output/report1.txt")));
            writer.write(sb.toString());
            writer.close();
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get details");
        }
    }
    public String searchCountryByFields(String searchTerm, String searchType, String outputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        String sql = "";

        // Define the SQL query based on the selected search type
        switch (searchType) {
            case "Code":
                sql = "SELECT code, name, continent, region, population FROM country WHERE code LIKE ?";
                break;
            case "Name":
                sql = "SELECT code, name, continent, region, population FROM country WHERE name LIKE ?";
                break;
            case "Continent":
                sql = "SELECT code, name, continent, region, population FROM country WHERE continent LIKE ?";
                break;
            case "Region":
                sql = "SELECT code, name, continent, region, population FROM country WHERE region LIKE ?";
                break;
            case "Population":
                sql = "SELECT code, name, continent, region, population FROM country WHERE population LIKE ?";
                break;
            case "Search by All Fields":
                sql = "SELECT code, name, continent, region, population FROM country WHERE code LIKE ? OR name LIKE ? OR continent LIKE ? OR region LIKE ? OR population LIKE ?";
                break;
            default:
                sql = "SELECT code, name, continent, region, population FROM country WHERE name LIKE ?";
                break;
        }

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            // Set the parameters for the query based on the search type
            if (searchType.equals("Search by All Fields")) {
                // Search across all fields
                stmt.setString(1, "%" + searchTerm + "%");
                stmt.setString(2, "%" + searchTerm + "%");
                stmt.setString(3, "%" + searchTerm + "%");
                stmt.setString(4, "%" + searchTerm + "%");
                stmt.setString(5, "%" + searchTerm + "%");
            } else {
                // Search by specific field (only one parameter is needed)
                stmt.setString(1, "%" + searchTerm + "%");
            }

            ResultSet rset = stmt.executeQuery();

            // Check if any results were returned
            if (!rset.isBeforeFirst()) {
                sb.append("No results found for: ").append(searchTerm);
            } else {
                // Add headers for the table
                sb.append(String.format("%-10s %-30s %-20s %-20s %-15s\n", "Code", "Name", "Continent", "Region", "Population"));
                sb.append("--------------------------------------------------------------\n");

                // Iterate through results and display in tabular format
                while (rset.next()) {
                    String code = rset.getString("code");
                    String name = rset.getString("name");
                    String continent = rset.getString("continent");
                    String region = rset.getString("region");
                    int population = rset.getInt("population");

                    sb.append(String.format("%-10s %-30s %-20s %-20s %-15d\n", code, name, continent, region, population));
                }
            }

            // Write to file (optional)
            writeToFile(sb.toString(), outputFile);

        } catch (SQLException e) {
            System.err.println("Failed to execute search query: " + e.getMessage());
        }

        // Return the results as a String to display in the GUI
        return sb.toString();
    }





}
