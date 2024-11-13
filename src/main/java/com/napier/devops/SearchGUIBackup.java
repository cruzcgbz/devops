package com.napier.devops;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SearchGUIBackup {
    private App app;

    public SearchGUIBackup(App app) {
        this.app = app;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Country Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter Country Name:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        // Adding components to the panel
        JPanel topPanel = new JPanel();
        topPanel.add(label);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                try {
                    // Call modified report1 with search term
                    app.report1(searchTerm);
                    resultArea.setText("Search complete. Check output file.");
                } catch (IOException ex) {
                    resultArea.setText("Error executing search: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        App app = new App();
        app.connect("localhost:33060", 0);

        // Run the GUI
        SwingUtilities.invokeLater(() -> {
            SearchGUIBackup searchGUI = new SearchGUIBackup(app);
            searchGUI.createAndShowGUI();
        });
    }
}
