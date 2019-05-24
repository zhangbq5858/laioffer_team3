package util;

import entity.Address;
import entity.GeoLocation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static entity.Robot.LAND_ROBOT;
import static entity.Robot.UAV;

public class EstimateTime {
	
	private static final double UAVSPEED = 10.8; // 10.8m /s
    // TODO: please use this to estimate time!!!!!!!!!!!!!!!
    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    /**
     * @param fromAddress
     * @param toAddress
     * @param robotType
     * @return time (unit: minutes)
     */
    public static double estimateTime(Address fromAddress, Address toAddress, String robotType) {
        double time = -1;
        switch (robotType) {
            case LAND_ROBOT:
                time = DistanceUtils.getRouteDistanceAndTime(fromAddress, toAddress)[1];
                break;
            case UAV:
                time = estimateTime(fromAddress, toAddress, UAVSPEED);
                break;
            default:
                System.err.println("Wrong type!!!!!!!!!!!!!!!!");
        }
        return toMin(time);
    }

    public static double estimateTime(Address fromAddress, Address toAddress, double speed) {
        //TODO
        //Optimize in the future
        double distance = DistanceUtils.getDistance(fromAddress, toAddress);
        return distance / speed;
    }


    public static int toMin(double time) {
        return (int) time / 60;
        
    }

    public static String prettify(double time) {
        int min = toMin(time);
        int hour = min / 60;
        return hour + " hour(s) and " + min % 60 + " min(s)";
    }

    public static void main(String[] args) {
        Address fromAddress = new Address.AddressBuilder().setStreet("Scott St Duboce Ave").setCity("San Francisco").build();
        Address toAddress = new Address.AddressBuilder().setStreet("Museum Way Roosevelt Way").setCity("San Francisco").build();

        System.out.println("Route Time:");
        double time1 = estimateTime(fromAddress, toAddress, LAND_ROBOT);
        System.out.println(time1 + " min");
        System.out.println(prettify(time1));

        System.out.println("\nDirect Time:");
        double time2 = estimateTime(fromAddress, toAddress, UAV);
        System.out.println(time2 + " min");
        System.out.println(prettify(time2));
    

//        Route Time:
//        Response Code: 200
//        275992.0
//        4599 min
//        76 hour(s) and 39 min(s)
//
//        Direct Time:
//        Response Code: 200
//        129978.4
//        2166 min
//        36 hour(s) and 6 min(s)
    }
}
