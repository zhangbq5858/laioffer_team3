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
			
			JSONObject toAddress = status.getJSONObject("to_address");
			Address to_address = Address.parse(toAddress);
			
			//TODO 添加判断条件，如果order已经delivered，则不需要后续的判断current address
			
			//getting current address from database
			//TODO: in the future, will be get directly from robot itself
			Integer robotId = status.getInt("robot_id");
			//TODO get address by use robot geolocation to calculate the Address
//			Address current_address = DistanceUtils.g(currentAddress);
			Integer speed = dbConnection.getSpeed(robotId);
			
//			status.put("current_address", currentAddress);
//			status.put("estimate_time", EstimateTime.estimateTime(current_address, to_address, speed));
			
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
