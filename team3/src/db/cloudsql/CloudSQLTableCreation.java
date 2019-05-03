package db.cloudsql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import com.kenai.constantine.platform.WaitFlags;

import jnr.ffi.Struct.int16_t;

public class CloudSQLTableCreation {		
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		DataSource pool = CloudSQLConnection.createConnectionPool();
		Connection conn;
		try  {
			conn = pool.getConnection();
			// Step 1 Connect to MySQL.
			if (conn == null) {
				return;
			}
			
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
			
//			sql = "IF (OBJECT_ID('dbo.FK_RobotOrder', 'F') IS NOT NULL)\n" + 
//					"BEGIN\n" + 
//					"    ALTER TABLE dbo.robots DROP CONSTRAINT FK_RobotOrder\n" + 
//					"END";
//			statement.executeUpdate(sql);
			
			//TODO add double constraints between robot and order?
			
			
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
					+ "address_id VARCHAR(255) NOT NULL,"
					+ "street VARCHAR(255),"
					+ "city VARCHAR(255),"
					+ "state VARCHAR(255),"
					+ "zipcode VARCHAR(255),"
					+ "PRIMARY KEY (address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE branches("
					+ "branch_id VARCHAR(255) NOT NULL,"
					+ "address_id VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (branch_id),"
					+ "FOREIGN KEY (address_id) REFERENCES addresses(address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE robots("
					+ "robot_id VARCHAR(255) NOT NULL,"
					+ "branch_id VARCHAR(255) NOT NULL,"
					+ "type VARCHAR(255) NOT NULL,"
					+ "max_load INT,"
					+ "speed INT,"
					+ "endurance INT,"
					+ "status VARCHAR(255) NOT NULL DEFAULT 'in branch',"
					+ "current_address_id VARCHAR(255),"
					+ "current_order_id VARCHAR(255),"
					+ "PRIMARY KEY (robot_id),"
					+ "FOREIGN KEY (branch_id) REFERENCES branches(branch_id),"	
					+ "FOREIGN KEY (current_address_id) REFERENCES addresses(address_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE orders("
					+ "order_id VARCHAR(255) NOT NULL,"
					+ "from_address_id VARCHAR(255) NOT NULL,"
					+ "to_address_id VARCHAR(255) NOT NULL,"
					+ "robot_id VARCHAR(255),"
					+ "created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "status VARCHAR(255) DEFAULT \"package waiting to be picked\" ,"
					+ "price FLOAT,"
					+ "receiver_email VARCHAR(255),"
					+ "sender_email VARCHAR(255),"
					+ "current_address_id VARCHAR(255),"
					+ "expect_arrive_time DATETIME DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (order_id),"
//					+ "FOREIGN KEY (robot_id) REFERENCES robots(robot_id),"
					+ "FOREIGN KEY (from_address_id) REFERENCES addresses(address_id),"
					+ "FOREIGN KEY (to_address_id) REFERENCES addresses(address_id),"
					+ "FOREIGN KEY (current_address_id) REFERENCES addresses(address_id)"
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
					"		IF NEW.status = \"picked\" THEN\n" + 
					"			UPDATE orders SET status = \"package is on the way\", updated_time = CURRENT_TIMESTAMP WHERE order_id = NEW.current_order_id;\n" + 
					"		END IF; \n" + 
					"		IF New.status = 'received' THEN\n" + 
					"			UPDATE orders SET status = 'package is received', updated_time = CURRENT_TIMESTAMP WHERE order_id = NEW.current_order_id;\n" + 
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


