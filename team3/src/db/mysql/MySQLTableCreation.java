package db.mysql;

import java.sql.Connection;
import java.sql.Statement;

import database.DBConnectionFactory;
import entity.Order;
import entity.Robot;

public class MySQLTableCreation {
	public static void main(String[] args) {
		
		try  {
			
			// Step 1 Connect to MySQL.
			Connection conn = new DBConnectionFactory().getConnection().getConnection();
			
			// Step 2 drop old tables.
			Statement statement = conn.createStatement();
			
			
			String sql = "DROP TRIGGER IF EXISTS tr_Robots";
			statement.executeUpdate(sql);
			
			
			sql = "DROP TABLE IF EXISTS robottracks";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS ordertracks";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS historys";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS orders";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS robots";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS branches";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS addresses";
			statement.executeUpdate(sql);

			// Step 3 Create new tables
			
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "email VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE addresses("
					+ "address_id INT NOT NULL AUTO_INCREMENT,"
					+ "street VARCHAR(255),"
					+ "city VARCHAR(255),"
					+ "state VARCHAR(255),"
					+ "zipcode VARCHAR(255),"
					+ "PRIMARY KEY (address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE branches("
					+ "branch_id INT NOT NULL AUTO_INCREMENT,"
					+ "address_id INT NOT NULL,"
					+ "PRIMARY KEY (branch_id),"
					+ "FOREIGN KEY (address_id) REFERENCES addresses(address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE robots("
					+ "robot_id INT NOT NULL AUTO_INCREMENT,"
					+ "branch_id INT NOT NULL,"
					+ "type VARCHAR(255) NOT NULL,"
					+ "max_load INT,"
					+ "speed INT,"
					+ "endurance INT,"
					+ "status VARCHAR(255) NOT NULL DEFAULT '" + Robot.IN_BRANCH + "',"
					+ "current_order_id VARCHAR(255),"
					+ "current_lat DOUBLE,"
					+ "current_lng DOUBLE,"
					+ "PRIMARY KEY (robot_id),"
					+ "FOREIGN KEY (branch_id) REFERENCES branches(branch_id)"	
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE orders("
					+ "order_id VARCHAR(255) NOT NULL,"
					+ "from_address_id INT NOT NULL,"
					+ "to_address_id INT NOT NULL,"
					+ "robot_id INT,"
					+ "created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "appointment_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "temporary BOOLEAN DEFAULT TRUE,"
					+ "status VARCHAR(255) DEFAULT \"" + Order.STATUS_INITIAL + "\" ,"
					+ "price FLOAT,"
					+ "receiver_email VARCHAR(255),"
					+ "sender_email VARCHAR(255),"
					+ "expect_arrive_time DATETIME DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (order_id),"
//					+ "FOREIGN KEY (robot_id) REFERENCES robots(robot_id),"
					+ "FOREIGN KEY (from_address_id) REFERENCES addresses(address_id),"
					+ "FOREIGN KEY (to_address_id) REFERENCES addresses(address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
//			sql = "ALTER TABLE robots "
//					+ "ADD CONSTRAINT FK_RobotOrder"
//					+ "ADD FOREIGN KEY (current_order_id) REFERENCES orders(order_id)";
			
			sql = "CREATE TABLE historys("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "order_id VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (user_id, order_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (order_id) REFERENCES orders(order_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			//Step 4: create trigger on robot status to update order status automatically.
			
			sql = "CREATE TRIGGER  tr_Robot BEFORE UPDATE ON robots \n" + 
					"FOR EACH ROW\n" + 
					"BEGIN	\n" + 
					"	IF NEW.current_order_id IS NOT NULL THEN\n" + 
					"		IF NEW.status = \"" + Robot.PICKED + "\" THEN\n" + 
					"			UPDATE orders SET status = \"" + Order.STATUS_ONWAY + "\", updated_time = CURRENT_TIMESTAMP WHERE order_id = NEW.current_order_id;\n" + 
					"		END IF; \n" + 
					"		IF New.status = '" + Robot.RECEIVED + "' THEN\n" + 
					"			UPDATE orders SET status = '" + Order.STATUS_DELIVERED + "', updated_time = CURRENT_TIMESTAMP WHERE order_id = NEW.current_order_id;\n" + 
					"		END IF; \n" + 
					"	END IF;\n" + 
					"END";
			statement.executeUpdate(sql);


			// Step 5: insert fake user 1111/3229c1097c00d497a0fd282d586be050
			sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith', 'laioffer_team3@gamil.com')";
			statement.executeUpdate(sql);

			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
