package rpc;

import java.io.IOException;
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

/**
 * Servlet implementation class Order
 */
@WebServlet("/confirmOrder")
public class Order extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Order() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataSource pool = CloudSQLConnection.createConnectionPool();
		
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String orderId = input.getString("order_id");
			
			JSONArray robot = input.getJSONArray("robot");
			String robot_id = robot.getString("robot_id");
			Robot robot = new Robot(robot_id, 5000);
			Order order = new Order();
			//order.set
			robot.addWork(order);
			String type = robot.getString("type");
			// unit?
			String time = robot.getString("time");
			Integer price = input.getInt("price");
			
			final Connection conn = pool.getConnection();
			CloudSQLConnection cloudSQLConnection = new CloudSQLConnection(conn);
			
			cloudSQLConnection.acceptOrder(orderId, robot.type, price);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	  		 connection.close();
	  	}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataSource pool = CloudSQLConnection.createConnectionPool();
		
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
	  		String orderId = input.getString("order_id");
	  		
	  		connection.deleteOrder(orderId);
	  		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
	  	} finally {
	  		connection.close();
	  	}	  		
	}

}
