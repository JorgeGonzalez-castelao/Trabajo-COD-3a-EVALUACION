// ApplicationController.java
package controllers;

import database.Database;
import models.Place;
import services.GeoNamesService;

import java.util.ArrayList;
import java.util.List;

public class ApplicationController {

    private GeoNamesService geoNamesService;
    private List<Place> favorites;
    private List<String> searchHistory;
    private Database database;

    public ApplicationController(String username) {
        geoNamesService = new GeoNamesService(username);
        favorites = new ArrayList<>();
        searchHistory = new ArrayList<>();
        database = new Database();
        database.createTable();
    }

    public List<Place> searchPlaces(String query, double myLat, double myLng) {
        List<Place> places = geoNamesService.searchPlaces(query, myLat, myLng);
        searchHistory.add(query);
        return places;
    }

    public void addToFavorites(Place place) {
        favorites.add(place);
        database.insertFavoritePlace(place.getName(), place.getLatitude(), place.getLongitude(), place.getDistance());
    }

    public void removeFromFavorites(Place place) {
        favorites.remove(place);
    }

    public List<Place> getFavorites() {
        return favorites;
    }

    public List<String> getSearchHistory() {
        return database.getSearchHistory();
    }

    public void addToSearchHistory(String query) {
        searchHistory.add(query);
        database.insertSearchQuery(query);
    }
}

