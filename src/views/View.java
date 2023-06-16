// View.java
package views;

import controllers.ApplicationController;
import models.Place;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class View extends JFrame {
    private ApplicationController controller;

    private JTextField searchTextField;
    private JTextArea resultTextArea;
    private JTable favoritesTable;
    private JTable historyTable;

    public View(ApplicationController controller) {
        this.controller = controller;

        setTitle("GeoNames Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        resultTextArea = new JTextArea();
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        add(resultScrollPane, BorderLayout.CENTER);

        //createFavoritesTable();
        createHistoryTable();
    }


    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Search:");
        searchPanel.add(searchLabel);

        searchTextField = new JTextField(20);
        searchPanel.add(searchTextField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchName = searchTextField.getText();
                if (!searchName.isEmpty()) {
                    List<Place> places = controller.searchPlaces(searchName, 0, 0);
                    displayPlaces(places);

                    // Añadir la búsqueda al historial
                    controller.addToSearchHistory(searchName);
                }
            }
        });
        searchPanel.add(searchButton);

        return searchPanel;
    }

    /**
     * Aplica la funcionalidad de agregar a favoritos mediante un Jbutton
     * @param places Recibe Arraylist de tipo place
     *
     */
    private void displayPlaces(List<Place> places) {
        resultTextArea.setText("");
        if (places.isEmpty()) {
            resultTextArea.append("No places found.");
        } else {
            for (Place place : places) {
                resultTextArea.append("Name: " + place.getName() + "\n");
                resultTextArea.append("Latitude: " + place.getLatitude() + "\n");
                resultTextArea.append("Longitude: " + place.getLongitude() + "\n");
                resultTextArea.append("Distance: " + place.getDistance() + "\n");
                resultTextArea.append("\n");
                JButton addToFavoritesButton = new JButton("Agregar a favoritos");
                addToFavoritesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        controller.addToFavorites(place);
                    }
                });
                resultTextArea.append(" ");
                resultTextArea.add(addToFavoritesButton);

            }
        }
    }

    /**
     * metodo donde se guarda la informacion de Favoritos
     */
    private void createFavoritesTable() {
        List<Place> favorites = controller.getFavorites();
        Object[][] data = new Object[favorites.size()][4];
        for (int i = 0; i < favorites.size(); i++) {
            Place place = favorites.get(i);
            data[i][0] = place.getName();
            data[i][1] = place.getLatitude();
            data[i][2] = place.getLongitude();
            data[i][3] = place.getDistance();
        }
        String[] columnNames = {"Name", "Latitude", "Longitude", "Distance"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        favoritesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(favoritesTable);
        JPanel favoritesPanel = new JPanel();
        favoritesPanel.setBorder(BorderFactory.createTitledBorder("Favorites"));
        favoritesPanel.setLayout(new BorderLayout());
        favoritesPanel.add(scrollPane, BorderLayout.CENTER);

        add(favoritesPanel, BorderLayout.WEST);
    }
    /**
     * metodo donde se guarda la informacion de Historial
     */
    private void createHistoryTable() {
        List<String> searchHistory = controller.getSearchHistory();
        Object[][] data = new Object[searchHistory.size()][1];
        for (int i = 0; i < searchHistory.size(); i++) {
            data[i][0] = searchHistory.get(i);
        }
        String[] columnNames = {"Search History"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        historyTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        JPanel historyPanel = new JPanel();
        historyPanel.setBorder(BorderFactory.createTitledBorder("Search History"));
        historyPanel.setLayout(new BorderLayout());
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        add(historyPanel, BorderLayout.EAST);
    }
}
