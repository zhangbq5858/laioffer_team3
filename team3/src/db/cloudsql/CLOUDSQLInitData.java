package db.cloudsql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import entity.Robot;

public class CLOUDSQLInitData {
	

	
	public static void initData() throws FileNotFoundException, IOException {
		CloudSQLConnection cloudSQLConnection =  new CloudSQLConnection();
		try  {
			
			Connection conn = cloudSQLConnection.getConnection();
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
			sql = "ALTER TABLE robots AUTO_INCREMENT = 0";
			statement.executeUpdate(sql);
			
			sql = "DELETE FROM branches";
			statement.executeUpdate(sql);
			sql = "ALTER TABLE branches AUTO_INCREMENT = 0";
			statement.executeUpdate(sql);
			
			sql = "DELETE FROM addresses";
			statement.executeUpdate(sql);
			sql = "ALTER TABLE addresses AUTO_INCREMENT = 0";
			statement.executeUpdate(sql);
			
			// Step 5: insert fake data
			sql = "INSERT INTO addresses VALUES(null, '429CastroSt', 'SanFrancisco', 'CA', '94114')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO addresses VALUES(null, '735CollegeAve', 'Kentfield', 'CA', '94904')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO addresses VALUES(null, '1254DavisSt', 'SanLeandro', 'CA', '94577')";
			statement.executeUpdate(sql);
			sql = "INSERT INTO branches VALUES(null, 1)";
			statement.executeUpdate(sql);
			sql = "INSERT INTO branches VALUES(null, 2)";
			statement.executeUpdate(sql);
			sql = "INSERT INTO branches VALUES(null, 3)";
			statement.executeUpdate(sql);
			insertRobotData(conn);
			
			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cloudSQLConnection.close();
		}
	}
	
	private static void insertRobotData(Connection conn) throws SQLException {
		// 200 land_robot	
		int lrNumber = 200;
		String sql = "INSERT INTO robots(branch_id, type) VALUES(?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(2, Robot.LAND_ROBOT);
		for(int i = 0; i < lrNumber; i++) {
			int branch_id = (int)(Math.random() * 3) + 1;
			ps.setInt(1, branch_id);
			ps.executeUpdate();
		}
		//100 UAV
		int uNumber =  100;
		sql = "INSERT INTO robots(branch_id, type, speed) VALUES(?, ?, ?)";
		ps = conn.prepareStatement(sql);
		ps.setString(2, Robot.UAV);
		ps.setInt(3, 40);
		for(int i = lrNumber; i < lrNumber + uNumber; i++) {
			int branch_id = (int)(Math.random() * 3) + 1;
			ps.setInt(1, branch_id);
			ps.executeUpdate();
		}
	}
	
	public static void main(String[] a) throws FileNotFoundException, IOException {
		initData();
	}
}
