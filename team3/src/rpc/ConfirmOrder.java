package rpc;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.cloudsql.CloudSQLConnection;
import entity.Address;
import entity.Order;
import entity.Order.OrderBuilder;
import entity.Robot;

/**
 * Servlet implementation class Order
 */
@WebServlet("/confirmOrder")
public class ConfirmOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmOrder() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		CloudSQLConnection cloudSQLConnection = new CloudSQLConnection();
		try {
			
			
			
			JSONObject input = RpcHelper.readJSONObject(request);
			
			// select available robot and get robot_id
			JSONObject robotJsonObject = input.getJSONObject("robot");
			String type = robotJsonObject.getString("type");
			Integer robotId = null;
			while(true) {
				List<Integer> availableRobots = cloudSQLConnection.getAvailRobotIds(type);
				// if we have available robot, select the first one to work
				if(availableRobots.size() > 0) {
					robotId = availableRobots.get(0);
					robotJsonObject.put("robot_id" , robotId);
					break;
				}
				//if no available robot now sleep 10 minutes;
				Thread.sleep(10 * 60 * 1000); 
			}
			
			// update the information (robot_id, price) to order;
			String orderId = input.getString("order_id");
			cloudSQLConnection.confirmOrder(robotJsonObject, orderId);
			
			// monitor robot and assign order to it;
			
				//1. get from address and to address
			JSONObject status = cloudSQLConnection.getOrderStatus(orderId);
			Address fromAddress = Address.parse(status.getJSONObject("from_address"));
			Address toAddress =  Address.parse(status.getJSONObject("to_address"));
				//2.create order
			OrderBuilder orderBuilder = new OrderBuilder();
			orderBuilder.setFromAddress(fromAddress);
			orderBuilder.setToAddress(toAddress);
			Order order = orderBuilder.build();
				//3. create robot and began work
			Robot robot = new Robot(robotId, 5000);
			robot.addWork(order);
			robot.beganWork();
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	  		 cloudSQLConnection.close();
	  	}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		CloudSQLConnection cloudSQLConnection = new CloudSQLConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
	  		String orderId = input.getString("order_id");
	  		cloudSQLConnection.deleteOrder(orderId);
	  		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
	  	} finally {
	  		cloudSQLConnection.close();
	  	}	  		
	}

}
