package rpc;

import db.cloudsql.CloudSQLConnection;
import entity.Address;
import entity.Order;
import entity.Robot;
//import org.apache.commons.io.IOUtils;
import entity.Order.OrderBuilder;
import util.DistanceUtils;
import util.EstimateTime;
import util.PriceUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.DBConnection;
import database.DBConnectionFactory;

import java.io.IOException;
import java.sql.SQLException;

//@WebServlet(urlPatterns = "/createOrder", name = "Order")
@WebServlet("/createOrder")
public class CreateOrder extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. get from_address and to_address
//        String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
//        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject jsonObject = RpcHelper.readJSONObject(request);
        JSONObject from_address_json = null;
        Address from_address = null;
        JSONObject to_address_json = null;
        Address to_address = null;
        try {
            from_address_json = jsonObject.getJSONObject("from_address");
            from_address = Address.parse(from_address_json);
            to_address_json = jsonObject.getJSONObject("to_address");
            to_address = Address.parse(to_address_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2. create fake order
        OrderBuilder orderBuilder = new OrderBuilder();
		orderBuilder.setFromAddress(from_address);
		orderBuilder.setToAddress(to_address);
		Order order = orderBuilder.build();

		DBConnection dbConnection = new DBConnectionFactory().getConnection();
        try {
        	
        	
            // 3. insert fake order to db
            // TODO: insert this order into order table @ database
            // CloudSQLConnection.insertFakeOrder(fake_order);
            System.out.println("inserting fake order to db...");
            dbConnection.createOrder(order);

            // 4. check whether have available robot from db and write back to frontend
            JSONArray robots_json = new JSONArray();
        	
            // if we have available landrobot in branch:
            if(dbConnection.getAvailRobotIds(Robot.LAND_ROBOT).size() > 0) {
            	JSONObject object = new JSONObject();
            	double time = EstimateTime.estimateTime(from_address, to_address);
            	object.put("type", Robot.LAND_ROBOT);
            	object.put("time", time);
            	object.put("price", PriceUtils.price(time, Robot.LAND_ROBOT));
            	robots_json.put(object);
            }
            
            if(dbConnection.getAvailRobotIds(Robot.UAV).size() > 0) {
            	JSONObject object = new JSONObject();
            	double time = EstimateTime.estimateTime(from_address, to_address);
            	object.put("type", Robot.UAV);
            	object.put("time", time);
            	object.put("price", PriceUtils.price(time, Robot.UAV));
            	robots_json.put(object);
            }
            

            JSONObject order_json = new JSONObject();
            order_json.put("order_id", order.getOrderId());
            order_json.put("distance", DistanceUtils.getStraightDistance(from_address, to_address));
            order_json.put("robots", robots_json);
            order_json.put("price", 10);

            RpcHelper.writeJsonObject(response, order_json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	dbConnection.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }
}
