package rpc;

import db.cloudsql.CloudSQLConnection;
import db.mysql.MySQLConnection;
import entity.Address;
import entity.Management;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

//@WebServlet(urlPatterns = "/createOrder", name = "Order")
@WebServlet("/createOrder")
public class CreateOrder extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. get from_address and to_address
//        String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
//        JSONObject jsonObject = new JSONObject(jsonString);
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jsonObject = RpcHelper.readJSONObject(request);
        JSONObject from_address_json = null;
        Address from_address = null;
        JSONObject to_address_json = null;
        Address to_address = null;
        System.out.println("createOrder get data: " + jsonObject);
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
/*
            // if we have available landrobot in branch:
            if (dbConnection.getAvailRobotIds(Robot.LAND_ROBOT).size() > 0) {
                JSONObject object = new JSONObject();
                double time = EstimateTime.estimateTime(from_address, to_address, Robot.LAND_ROBOT);
                object.put("type", Robot.LAND_ROBOT);
                object.put("time", time);
                object.put("price", PriceUtils.price(time, Robot.LAND_ROBOT));
                robots_json.put(object);
            }

            if (dbConnection.getAvailRobotIds(Robot.UAV).size() > 0) {
                JSONObject object = new JSONObject();
                double time = EstimateTime.estimateTime(from_address, to_address, Robot.UAV);
                object.put("type", Robot.UAV);
                object.put("time", time);
                object.put("price", PriceUtils.price(time, Robot.UAV));
                robots_json.put(object);
            }
*/
            if (true) {
	            JSONObject object = new JSONObject();
	            Calendar appointmentTime = Management.getInstance().getNextAvailableTime(Robot.LAND_ROBOT);
	            double time = shortestTime(from_address, to_address, Robot.LAND_ROBOT);
	            if(time <= 4 * 60) {
		            object.put("type", Robot.LAND_ROBOT);
		            object.put("time", time);
		            object.put("price", PriceUtils.price(time, Robot.LAND_ROBOT, jsonObject.getString("size"), 
		            		Double.parseDouble(jsonObject.getString("weight"))));
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            object.put("appointment_time", sdf.format(appointmentTime.getTime()));
		            robots_json.put(object);
	            }
            }
            
            if (true) {
	            JSONObject object = new JSONObject();
	            Calendar appointmentTime = Management.getInstance().getNextAvailableTime(Robot.UAV);
	            double time =  shortestTime(from_address, to_address, Robot.UAV);
	            if(time <= 4 * 60) {
		            object.put("type", Robot.UAV);
		            object.put("time", time);
		            object.put("price", PriceUtils.price(time, Robot.UAV, jsonObject.getString("size"), 
		            		Double.parseDouble(jsonObject.getString("weight"))));
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            object.put("appointment_time", sdf.format(appointmentTime.getTime()));
		            robots_json.put(object);
	            }
            }

            JSONObject order_json = new JSONObject();
            order_json.put("order_id", order.getOrderId());
            order_json.put("distance", DistanceUtils.getStraightDistance(from_address, to_address));
            order_json.put("robots", robots_json);
           
            RpcHelper.writeJsonObject(response, order_json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();
        }
    }
    
    private double shortestTime(Address fromAddress, Address toAddress, String type) {
    	double time = 5 * 60;
    	DBConnection connection = new MySQLConnection();
    	// check the whole three branch
    	for(int i = 1; i <= 3; i++) {
    		double tmpTime = 0;
    		Address branchAddress = Address.parse(connection.getBranchAddress(i));
    		tmpTime += EstimateTime.estimateTime(branchAddress, fromAddress, type);
    		tmpTime += EstimateTime.estimateTime(fromAddress, toAddress, type);
    		tmpTime += EstimateTime.estimateTime(toAddress, branchAddress, type);
    		System.out.println("createOrder calculate shortest time from branch " + i + " is " + tmpTime + " mins.");
    		time = Math.min(time, tmpTime);
    	}
    	return time;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
