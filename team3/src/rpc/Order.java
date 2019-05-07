package rpc;

import db.cloudsql.CloudSQLConnection;
import entity.Address;
import entity.Robot;
import org.json.JSONArray;
import org.json.JSONObject;
import util.DistanceUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/order", name = "Order")
public class Order extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. get from_address and to_address
        JSONObject jsonObject = RpcHelper.readJSONObject(request);
        JSONObject from_address_json = jsonObject.getJSONObject("from_address");
        Address from_address = Address.parse(from_address_json);
        JSONObject to_address_json = jsonObject.getJSONObject("to_address");
        Address to_address = Address.parse(to_address_json);

        // 2. create fake order
        entity.Order fake_order = new entity.Order();
        fake_order.fakeOrder(from_address, to_address);

        // 3. insert fake order to db
        // TODO: insert this order into order table @ database
        // CloudSQLConnection.insertFakeOrder(fake_order);
        System.out.println("inserting fake order to db...");

        // 4. get available robot from db
        // TODO: get available robots near the from_address @ backend strategy & database query
        Robot fakeRobot = new Robot("1", 5000);

        // 5. write back to frontend
        JSONArray robots_json = new JSONArray();
        robots_json.put(fakeRobot.toJSONObject());
        JSONObject order_json = new JSONObject();
        order_json.put("order_id", fake_order.getOrderId());
        order_json.put("distance", DistanceUtils.getStraightDistance(from_address, to_address));
        order_json.put("robots", robots_json);
        RpcHelper.writeJsonObject(response, order_json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
