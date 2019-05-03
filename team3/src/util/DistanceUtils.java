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
    // get api from the following url
    // https://developers.google.com/maps/documentation/distance-matrix/get-api-key
    private final static String API_KEY = "YOUR_API";

    private final static String OUTPUT_FORMAT = "json";

    // https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR&key=YOUR_API_KEY
    private final static String URL = "https://maps.googleapis.com/maps/api/distancematrix/";

    /**
     *
     * @param fromAddress
     * @param toAddress
     * @return dist, which is in the unit of meter
     */
    public static double getDistance(Address fromAddress, Address toAddress) {
        double dist = 0;

        String query = String.format("origins=%s,%s,%s&destinations=%s,%s,%s&key=%s",
                fromAddress.getStreet(), fromAddress.getCity(), fromAddress.getState(),
                toAddress.getStreet(), toAddress.getCity(), toAddress.getState(), API_KEY);

        String url = URL + OUTPUT_FORMAT + "?" + query;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                return -1; // indicate fail
//                return new ArrayList <>();
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


    public static void main(String[] args) {
        Address fromAddress = new Address.AddressBuilder().setCity("Vancouver").build();
        Address toAddress = new Address.AddressBuilder().setCity("San+Francisco").build();

        System.out.println(getDistance(fromAddress, toAddress));
    }
}
