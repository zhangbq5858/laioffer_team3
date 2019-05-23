package rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


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
		try (BufferedReader reader = request.getReader()) {
			String key = null;
			String line = reader.readLine();
			if(line == null) {
				return null;
			}
			
			if(line.charAt(0) == '{') {  // request from postman json
				StringBuilder sBuilder = new StringBuilder();
//				System.out.println("rpc input is json");
				while(line != null) {
//					System.out.println(line);
					sBuilder.append(line);
					line = reader.readLine();
				}
				return new JSONObject(sBuilder.toString());
			} else { //request from front end formdata
				int count = 0;
				System.out.println("rpc input is formdata");
				JSONObject jsonObject = new JSONObject();
				while(line != null) {
					System.out.println(line);
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
					line = reader.readLine();
				}
				return jsonObject;
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}