package com.napier.devops;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SearchGUI {
    private App app;

    public SearchGUI(App app) {
        this.app = app;
    }

    public void createAndShowGUI() {
        // Create frame
        JFrame frame = new JFrame("Country Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Set up the panel with a more flexible layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // Space between components

        // Top panel for label, search field, and search type dropdown
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel label = new JLabel("Enter Search Term:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        // Add a dropdown for selecting search field type
        String[] searchOptions = {"Search by All Fields", "Code", "Name", "Continent", "Region", "Population"};
        JComboBox<String> searchFieldCombo = new JComboBox<>(searchOptions);

        // Create a text area for results with improved font and scroll
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));  // Set a readable font
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));  // Fixed height for scroll area

        // Add components to the top panel
        topPanel.add(label);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(searchFieldCombo); // Add dropdown to panel

        // Add topPanel and resultArea to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add a clear button below the search button
        JPanel bottomPanel = new JPanel();
        JButton clearButton = new JButton("Clear");
        bottomPanel.add(clearButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Disable the search button initially and enable it when text is entered
        searchButton.setEnabled(false);

        // Update the button's enabled state based on the search field text
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchButton.setEnabled(!searchField.getText().trim().isEmpty());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchButton.setEnabled(!searchField.getText().trim().isEmpty());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchButton.setEnabled(!searchField.getText().trim().isEmpty());
            }
        });

        // Action listener for search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                String searchType = (String) searchFieldCombo.getSelectedItem();
                resultArea.setText("Searching...\n"); // Provide immediate feedback
                try {
                    // Call the updated method based on selected search type
                    String results = app.searchCountryByFields(searchTerm, searchType, "./output/report1.txt");
                    resultArea.setText(results); // Display the results in the GUI
                } catch (IOException ex) {
                    resultArea.setText("Error executing search: " + ex.getMessage());
                }
            }
        });

        // Clear button functionality to reset the search field and result area
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                resultArea.setText("");
                searchButton.setEnabled(false); // Disable search until the user types something
            }
        });

        // Add the panel to the frame
        frame.add(panel);
        frame.setLocationRelativeTo(null);  // Center the window
        frame.setVisible(true);
    }
}
