// Database.java
package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:geonames.db";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, query TEXT)";
    private static final String CREATE_FAVORITES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS favorites (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, latitude REAL, longitude REAL, distance REAL)";
    private static final String INSERT_QUERY = "INSERT INTO history VALUES (NULL, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM history";

    private Connection connection;

    public Database() {
        try {
            // Establecer la conexión con la base de datos
            connection = DriverManager.getConnection(DB_URL);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try (Statement statement = connection.createStatement()) {
            // Crear la tabla si no existe
            statement.executeUpdate(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSearchQuery(String query) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            // Insertar la consulta de búsqueda en la base de datos
            statement.setString(1, query);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSearchHistory() {
        List<String> searchHistory = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_QUERY)) {

            while (rs.next()) {
                String searchQuery = rs.getString("search_query");
                searchHistory.add(searchQuery);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchHistory;
    }
    public void insertFavoritePlace(String name, double latitude, double longitude, double distance) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_FAVORITES_TABLE_QUERY)) {
            statement.setString(1, name);
            statement.setDouble(2, latitude);
            statement.setDouble(3, longitude);
            statement.setDouble(4, distance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
