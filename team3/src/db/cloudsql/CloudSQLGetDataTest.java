package db.cloudsql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

public class CloudSQLGetDataTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		DataSource pool = CloudSQLConnection.createConnectionPool();
		try (Connection conn = pool.getConnection()) {
			// Step 1 Connect to MySQL.
			if (conn == null) {
				return;
			}
			// read user data from table users;
			Statement statement = conn.createStatement();
			
			String sql = "SELECT * FROM users";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
		    while (rs.next()) {
		    	System.out.println("user_id : " + rs.getString("user_id"));
		    	System.out.println("password : " + rs.getString("password"));
		    	System.out.println("email : " + rs.getString("email"));
		    	System.out.println("first_name : " + rs.getString("first_name"));
		    	System.out.println("last_name : " + rs.getString("last_name"));
		    }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
