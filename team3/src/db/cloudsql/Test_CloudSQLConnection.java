package db.cloudsql;

import entity.Address;
import entity.Order;
import entity.Order.OrderBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class Test_CloudSQLConnection {

	@Test
	public void testJSONObject_Success() throws FileNotFoundException, IOException, SQLException {};
	
	public static void main(String[] a) throws FileNotFoundException, IOException, SQLException {
		JSONObject obj = new JSONObject();
		JSONObject fromAddressJO = new JSONObject();
		JSONObject toAddressJO = new JSONObject();
		
		fromAddressJO.put("street", "sample Rd");
		fromAddressJO.put("city", "Seattle");
		fromAddressJO.put("state", "WA");
		fromAddressJO.put("zipcode", "99999");
		toAddressJO.put("street", "destination Rd");
		toAddressJO.put("city", "Seattle");
		toAddressJO.put("state", "WA");
		toAddressJO.put("zipcode", "88888");

		Address fromAddress = Address.parse(fromAddressJO);
		Address toAddress = Address.parse(toAddressJO);
		
		OrderBuilder orderBuilder = new OrderBuilder();
		orderBuilder.setFromAddress(fromAddress);
		orderBuilder.setToAddress(toAddress);
		Order order = orderBuilder.build();
		
		CloudSQLConnection connection = new CloudSQLConnection();
		connection.createOrder(order);
		
//		obj.put("from_address", fromAddress);
//		obj.put("to_address", toAddress);
//		obj.put("robot_id", "001");
//		obj.put("status", Order.STATUS_DELIVERED);
//		
//		//System.out.println(obj.toString());
//		
//		Assert.assertEquals(obj.get("status"), Order.STATUS_DELIVERED);
//		Assert.assertEquals(obj.get("robot_id"), "001");
//		Assert.assertEquals(obj.getJSONObject("from_address").get("street"), "sample Rd");
//		//System.out.println(obj.get("from_address"));
	}
	
//	@Test
//	public void testGetAddress_Success() {
//		
//	}
}
