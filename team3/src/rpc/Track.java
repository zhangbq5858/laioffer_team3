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

import db.cloudsql.CloudSQLConnection;
import entity.*;
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
		String orderId = request.getParameter("order_id");
		
		
		try {
			CloudSQLConnection cloudSQLConnection = new CloudSQLConnection();
			final Connection conn = cloudSQLConnection.getConnection();
			JSONObject status = cloudSQLConnection.getOrderStatus(orderId);
			
			JSONObject toAddress = status.getJSONObject("to_address");
			Address to_address = Address.parse(toAddress);
			
			//getting current address from database
			//TODO: in the future, will be get directly from robot itself
			Integer robotId = status.getInt("robot_id");
			Integer currentAddressId = cloudSQLConnection.getAddressId(robotId);
			JSONObject currentAddress = cloudSQLConnection.getAddress(currentAddressId);
			Address current_address = Address.parse(currentAddress);
			Integer speed = cloudSQLConnection.getSpeed(robotId);
			
			status.put("current_address", currentAddress);
			status.put("estimate_time", EstimateTime.estimateTime(current_address, to_address, speed));
			
			RpcHelper.writeJsonObject(response, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
