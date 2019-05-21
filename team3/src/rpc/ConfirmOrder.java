package rpc;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import database.DBConnection;
import database.DBConnectionFactory;
import db.cloudsql.CloudSQLConnection;
import entity.Address;
import entity.Order;
import entity.Order.OrderBuilder;
import entity.Robot;
import entity.orderManager;

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
        DBConnection dbConnection = new DBConnectionFactory().getConnection();
        try {


            JSONObject input = RpcHelper.readJSONObject(request);

            // select available robot and get robot_id
            JSONObject robotJsonObject = input.getJSONArray("robot").getJSONObject(0);
            String type = robotJsonObject.getString("type");
            /*Integer robotId = null;
            while (true) {
                List<Integer> availableRobots = dbConnection.getAvailRobotIds(type);
                // if we have available robot, select the first one to work
                if (availableRobots.size() > 0) {
                    robotId = availableRobots.get(0);
                    robotJsonObject.put("robot_id", robotId);
                    break;
                }
                //if no available robot now sleep 10 minutes;
                Thread.sleep(10 * 60 * 1000);
            }

            // update the information (robot_id, price) to order;
             */
            String orderId = input.getString("order_id");
            dbConnection.confirmOrder(input);

            // monitor robot and assign order to it;

            //1. get from address and to address
            JSONObject status = dbConnection.getOrderStatus(orderId);
            Address fromAddress = Address.parse(status.getJSONObject("from_address"));
            Address toAddress = Address.parse(status.getJSONObject("to_address"));
            
            //2.create order
            String time = robotJsonObject.getString("appointment_time");
    		//2007-10-27
    		String[] strings = time.split(" ");
    		String[] yearAndMonthAndDay = strings[0].split("-");
    		String[] hourAndMinute = strings[1].split(":");
    		
    		Calendar appointmentTime = Calendar.getInstance();
    		appointmentTime.setTime(new Date(Integer.parseInt(yearAndMonthAndDay[0]), Integer.parseInt(yearAndMonthAndDay[1]),
    				Integer.parseInt(yearAndMonthAndDay[2]), Integer.parseInt(hourAndMinute[0]), Integer.parseInt(hourAndMinute[1])));
            OrderBuilder orderBuilder = new OrderBuilder().setOrderId(orderId).setFromAddress(fromAddress)
            		.setToAddress(toAddress).setToAppointmentTime(appointmentTime);
            Order order = orderBuilder.build();
            
            orderManager.AddTask(order, type);
            //3. create robot and began work
           /* Robot robot = new Robot(robotId, 5000);
            robot.addWork(order);
            robot.beganWork();
            */
            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();
        }
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DBConnection dbConnection = new DBConnectionFactory().getConnection();
        try {
            JSONObject input = RpcHelper.readJSONObject(request);
            String orderId = input.getString("order_id");
            dbConnection.deleteOrder(orderId);
            RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();
        }
    }

}
