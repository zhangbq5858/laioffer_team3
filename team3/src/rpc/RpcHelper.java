package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sBuilder = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			JSONObject jsonObject = new JSONObject();
			String key = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				System.out.println("rpc read data: " + line);
				if(count == 1) {
					key = line.split(";")[1].split("=")[1].split("\"")[1].trim();
				} else if (count == 3) {
					if(line.trim().charAt(0) == '{') {
						jsonObject.put(key, new JSONObject(line));
					} else {
						jsonObject.put(key, line.trim());
					}
				}
				count = (count + 1) % 4;
			}
			return jsonObject;
//			return new JSONObject(sBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}
}
