package rpc;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import database.DBConnection;
import database.DBConnectionFactory;
import db.cloudsql.CloudSQLConnection;
import entity.*;
import util.DistanceUtils;
import util.EstimateTime;

/**
 * Servlet implementation class Track
 */
@WebServlet("/track")
public class Track extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Track() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		
		
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String orderId = input.getString("order_id");
			DBConnection dbConnection = new DBConnectionFactory().getConnection();
			JSONObject status = dbConnection.getOrderStatus(orderId);
			
			//delivered, put to_address as the current location
			if (status.getString("order_status").equals(Order.STATUS_DELIVERED)) {
				JSONObject toAddress = status.getJSONObject("to_address");
				Address to_address = Address.parse(toAddress);
				GeoLocation currentGeoLocation = DistanceUtils.getGeocode(to_address);
				
				JSONObject current_geo_location = new JSONObject();
				current_geo_location.put("Lat", currentGeoLocation.getLat());
				current_geo_location.put("Lon", currentGeoLocation.getLng());
				status.put("current_geoLocation", current_geo_location);
			}
			//not delivered, fetch current geoLocation from robot table
			else {
				Integer robotId = status.getInt("robot_id");
				//TODO get address by use robot geolocation to calculate the Address
				JSONObject current_geo_location = dbConnection.getCurrentGeoLocation(robotId);
				status.put("current_geoLocation", current_geo_location);
			}
			
			status.remove("robot_id");
			status.remove("to_address");
			status.put("order_id", orderId);
			RpcHelper.writeJsonObject(response, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
