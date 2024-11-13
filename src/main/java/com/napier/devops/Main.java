package com.napier.devops;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Create new Application instance
        App a = new App();

        if (args.length < 1) {
            // Local connection
            a.connect("localhost:33060", 0);
        } else {
            // Docker connection parameters passed from Dockerfile
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        // Launch the GUI for searching
        SwingUtilities.invokeLater(() -> {
            SearchGUI searchGUI = new SearchGUI(a);
            searchGUI.createAndShowGUI();
        });

        // Ensure the database connection is closed when GUI exits
        Runtime.getRuntime().addShutdownHook(new Thread(a::disconnect));
    }
}
