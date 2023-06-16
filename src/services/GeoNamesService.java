package services;

import models.Place;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GeoNamesService {
    private String username;

    public GeoNamesService(String username) {
        this.username = username;
    }

    public List<Place> searchPlaces(String query, double myLat, double myLng) {
        List<Place> places = new ArrayList<>();

        try {
            String urlString = "http://api.geonames.org/searchJSON?q=" + query + "&maxRows=10&username=" + username;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String output;
            StringBuilder responseBuilder = new StringBuilder();
            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            JSONArray geonames = response.getJSONArray("geonames");
            for (int i = 0; i < geonames.length(); i++) {
                JSONObject placeJson = geonames.getJSONObject(i);
                int geonameId = placeJson.getInt("geonameId");
                String name = placeJson.getString("name");
                double lat = placeJson.getDouble("lat");
                double lng = placeJson.getDouble("lng");

                // Calcular la distancia desde el punto de origen a este lugar
                double distance = calculateDistanceDifference(myLat, myLng, lat, lng);

                Place place = new Place(geonameId, name, lat, lng, distance);
                places.add(place);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return places;
    }

    public Place getPlaceDetails(int geonameId) {
        try {
            String urlString = "http://api.geonames.org/getJSON?geonameId=" + geonameId + "&username=" + username;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String output;
            StringBuilder responseBuilder = new StringBuilder();
            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            JSONObject response = new JSONObject(responseBuilder.toString());
            String name = response.getString("name");
            double lat = response.getDouble("lat");
            double lng = response.getDouble("lng");
            double distance = response.getDouble("distance");
            Place place = new Place(geonameId, name, lat, lng, distance);

            conn.disconnect();

            return place;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Radio de la Tierra en kilómetros
        double earthRadius = 6371.0;

        // Convertir las coordenadas de grados a radianes
        double lat1Rad = Math.toRadians(lat1);
        double lng1Rad = Math.toRadians(lng1);
        double lat2Rad = Math.toRadians(lat2);
        double lng2Rad = Math.toRadians(lng2);

        // Diferencias de latitud y longitud entre los dos puntos
        double latDiff = lat2Rad - lat1Rad;
        double lngDiff = lng2Rad - lng1Rad;

        // Calcular la distancia utilizando la fórmula de Haversine
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance;
    }
    public double calculateDistanceDifference(double myLat, double myLng, double placeLat, double placeLng) {
        double myDistance = calculateDistance(myLat, myLng, placeLat, placeLng);
        double placeDistance = calculateDistance(placeLat, placeLng, placeLat, placeLng);
        double difference = myDistance - placeDistance;

        return difference;
    }

}
