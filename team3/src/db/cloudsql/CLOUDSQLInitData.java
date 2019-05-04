package db.cloudsql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

public class CLOUDSQLInitData {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		DataSource pool = CloudSQLConnection.createConnectionPool();
		Connection conn;
		try  {
			conn = pool.getConnection();
			// Step 1 Connect to MySQL.
			if (conn == null) {
				return;
			}
			Statement statement = conn.createStatement();
			String sql;
			
			
			// Step 2 delete old datas in tables.
		
			sql = "DELETE FROM orders";
			statement.executeUpdate(sql);
			sql = "DELETE FROM robots";
			statement.executeUpdate(sql);
			sql = "DELETE FROM branches";
			statement.executeUpdate(sql);
			sql = "DELETE FROM addresses";
			statement.executeUpdate(sql);
			
			// Step 5: insert fake data
			sql = "INSERT INTO addresses VALUES('1','12504 SE', 'Seattle', 'WA', '98109')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO branches VALUES('1', '1')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO robots(robot_id, branch_id, type) VALUES('1', '1', 'land_robot')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO orders(order_id, from_address_id, to_address_id) VALUES('1', '1', '1')";
			statement.executeUpdate(sql);

			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
