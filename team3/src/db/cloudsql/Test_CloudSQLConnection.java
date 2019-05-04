package db.cloudsql;

import entity.Order;

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
	public void testJSONObject_Success() {
		JSONObject obj = new JSONObject();
		JSONObject fromAddress = new JSONObject();
		JSONObject toAddress = new JSONObject();
		
		fromAddress.put("street", "sample Rd");
		fromAddress.put("zipcode", "99999");
		toAddress.put("street", "destination Rd");
		toAddress.put("zipcode", "88888");
		obj.put("from_address", fromAddress);
		obj.put("to_address", toAddress);
		obj.put("robot_id", "001");
		obj.put("status", Order.STATUS_DELIVERED);
		
		//System.out.println(obj.toString());
		
		Assert.assertEquals(obj.get("status"), Order.STATUS_DELIVERED);
		Assert.assertEquals(obj.get("robot_id"), "001");
		Assert.assertEquals(obj.getJSONObject("from_address").get("street"), "sample Rd");
		//System.out.println(obj.get("from_address"));
	}
	
	@Test
	public void testGetAddress_Success() {
		
	}
}
