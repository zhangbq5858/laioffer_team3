package util;

import entity.Address;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DistanceUtils {
    // TODO: get api from the following url
    // https://developers.google.com/maps/documentation/distance-matrix/get-api-key
    private final static String API_KEY = "YOUR_API";

    private final static String OUTPUT_FORMAT = "/json";

    private final static String URL = "https://maps.googleapis.com/maps/api";

    private final static String DISTANCEMATRIX = "/distancematrix";

    private final static String GEOCODE = "/geocode";

    /**
     * @param fromAddress
     * @param toAddress
     * @return double: route dist (unit: meter)
     */
    public static double getDistance(Address fromAddress, Address toAddress) {
        double dist = 0;

        String query = String.format("origins=%s,%s,%s&destinations=%s,%s,%s&key=%s",
                fromAddress.getStreet(), fromAddress.getCity(), fromAddress.getState(),
                toAddress.getStreet(), toAddress.getCity(), toAddress.getState(), API_KEY);

        // https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR&key=YOUR_API_KEY
        String url = URL + DISTANCEMATRIX + OUTPUT_FORMAT + "?" + query;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                return -1; // indicate fail
            }

            // read every 8k data
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            JSONObject object = new JSONObject(response.toString());

            if (!object.isNull("rows")) {
                JSONArray rows = object.getJSONArray("rows");
                JSONObject row = rows.getJSONObject(0);

                if (!row.isNull("elements")) {
                    JSONArray elements = row.getJSONArray("elements");
                    JSONObject element = elements.getJSONObject(0);

                    if (!element.isNull("distance")) {
                        JSONObject distance = element.getJSONObject("distance");
                        dist = distance.getDouble("value");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return dist;
    }

    /**
     * ‘haversine’ formula
     * <p>
     * ref:
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param fromAddress
     * @param toAddress
     * @return double: straight distance (unit: meter)
     */
    public static double getStraightDistance(Address fromAddress, Address toAddress) {
        GeoLocation fromGeoLoc = getGeocode(fromAddress);
        GeoLocation toGeoLoc = getGeocode(toAddress);

        double R = 6371e3; // metres
        double phi1 = Math.toRadians(fromGeoLoc.lat); // from_lat in radian
        double phi2 = Math.toRadians(toGeoLoc.lat); // to_lat in radian
        double delta_phi = Math.toRadians(toGeoLoc.lat - fromGeoLoc.lat); // diff_lat in radian
        double delta_lambda = Math.toRadians(toGeoLoc.lng - fromGeoLoc.lng); // diff_lng in radian

        double a = Math.sin(delta_phi / 2) * Math.sin(delta_phi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(delta_lambda / 2) * Math.sin(delta_lambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;

        return d;
    }

    private static GeoLocation getGeocode(Address address) {
        GeoLocation geoLoc = new GeoLocation();

        String query = String.format("address=%s,%s,%s&key=%s",
                address.getStreet(), address.getCity(), address.getState(), API_KEY);

        // https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
        String url = URL + GEOCODE + OUTPUT_FORMAT + "?" + query;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                return null; // indicate fail
            }

            // read every 8k data
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            JSONObject object = new JSONObject(response.toString());

            if (!object.isNull("results")) {
                JSONArray results = object.getJSONArray("results");
                JSONObject result = results.getJSONObject(0);

                if (!result.isNull("geometry")) {
                    JSONObject geometry = result.getJSONObject("geometry");


                    if (!geometry.isNull("location")) {
                        JSONObject location = geometry.getJSONObject("location");

                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
//                        dist = distance.getDouble("value");
                        geoLoc.lat = lat;
                        geoLoc.lng = lng;
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return geoLoc;
    }

    private static class GeoLocation {
        private double lat; // latitude
        private double lng; // longitude

        @Override
        public String toString() {
            return "lat: " + lat + ", lng: " + lng;
        }

        //
//        public double getLat() {
//            return lat;
//        }
//
//        public void setLat(double lat) {
//            this.lat = lat;
//        }
//
//        public double getLng() {
//            return lng;
//        }
//
//        public void setLng(double lng) {
//            this.lng = lng;
//        }
    }

    public static void main(String[] args) {
        Address fromAddress = new Address.AddressBuilder().setCity("Vancouver").build();
        Address toAddress = new Address.AddressBuilder().setCity("San+Francisco").build();

        System.out.println("Route Distance:");
        System.out.println(getDistance(fromAddress, toAddress));

//        Address address = new Address.AddressBuilder().
//                setStreet("1600+Amphitheatre+Parkway").
//                setCity("Mountain+View").
//                setState("CA").build();
//
//        System.out.println(getGeocode(address));
        System.out.println("\nGeo Locations:");
        System.out.println(getGeocode(fromAddress));
        System.out.println(getGeocode(toAddress));

        System.out.println("\nStraight Distance:");
        System.out.println(getStraightDistance(fromAddress, toAddress));

        // Route Distance:
        // Response Code: 200
        // 1528863.0
        //
        // Geo Locations:
        // Response Code: 200
        // lat: 49.2827291, lng: -123.1207375
        // Response Code: 200
        // lat: 37.7854864, lng: -122.4402223
        //
        // Straight Distance:
        // Response Code: 200
        // Response Code: 200
        // 1279596.824474498
    }
}
