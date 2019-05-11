package db.cloudsql;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.mysql.cj.conf.StringPropertyDefinition;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import database.DBConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

import entity.Address;
import entity.Order;
import entity.Robot;
import entity.Address.AddressBuilder;
import util.DistanceUtils;
import util.GeoLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudSQLConnection implements DBConnection{

    private Connection conn;
//	  private static final Logger LOGGER = Logger.getLogger(IndexServlet.class.getName());

    // Saving credentials in environment variables is convenient, but not secure -
    // consider a more
    // secure solution such as https://cloud.google.com/kms/ to help keep secrets
    // safe.

    // export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service/account/key.json
//	export CLOUD_SQL_CONNECTION_NAME='<MY-PROJECT>:<INSTANCE-REGION>:<MY-DATABASE>'
    private static final String CLOUD_SQL_CONNECTION_NAME = "dark-quasar-236002:us-central1:laiproject";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String DB_NAME = "team3";
    private static final String JSON_PATH = "/Users/boqunzhang/Downloads/laioffer/project/Around-27f2aa43fec3.json";

    public Connection getConnection() {
    	return this.conn;
    }
    
    public CloudSQLConnection() {
//    	try {
    		//http://localhost:8080/download/Around-27f2aa43fec3.json
        	String envValue = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
//            System.out.print("system env: " + envValue);
//            File file = new File(envValue); 
//            BufferedReader br = new BufferedReader(new FileReader(file)); 
//            
//            String st; 
//            while ((st = br.readLine()) != null) {
//              System.out.println(st); 
//            } 
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	    try {
	    	DataSource pool = createConnectionPool(); 
	    	this.conn = pool.getConnection();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
    }

    public static DataSource createConnectionPool() throws FileNotFoundException, IOException, ClassNotFoundException {
        // [START cloud_sql_mysql_servlet_create]
        // The configuration object specifies behaviors for the connection pool.
        HikariConfig config = new HikariConfig();
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(JSON_PATH))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        
//         Configure which instance and what database user to connect with.

        config.setJdbcUrl(String.format("jdbc:mysql://google/%s", DB_NAME));
        config.setUsername(DB_USER); // e.g. "root", "postgres"
        config.setPassword(DB_PASS); // e.g. "my-password"

        // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated
        // connections.
        
       // jdbc:mysql://google/<DATABASE_NAME>?cloudSqlInstance=<INSTANCE_CONNECTION_NAME>&
        //socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&user=<MYSQL_USER_NAME>
        //&password=<MYSQL_USER_PASSWORD>
        
        
        // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for
        // details.
        
//        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
//        config.addDataSourceProperty("url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        
        //The CloudSQL driver class name is: "com.mysql.jdbc.GoogleDriver". You are using: "com.google.cloud.sql.jdbc.Driver".
        
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
//        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.jdbc.Driver");
        config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
        config.addDataSourceProperty("useSSL", "false");
//        Class.forName("com.google.cloud.sql.jdbc.Driver");

        // ... Specify additional connection properties here.
        // [START_EXCLUDE]

        // [START cloud_sql_mysql_servlet_limit]
        // maximumPoolSize limits the total number of concurrent connections this pool
        // will keep. Ideal
        // values for this setting are highly variable on app design, infrastructure,
        // and database.
        config.setMaximumPoolSize(5);
        // minimumIdle is the minimum number of idle connections Hikari maintains in the
        // pool.
        // Additional connections will be established to meet this value unless the pool
        // is full.
        config.setMinimumIdle(5);
        // [END cloud_sql_mysql_servlet_limit]

        // [START cloud_sql_mysql_servlet_timeout]
        // setConnectionTimeout is the maximum number of milliseconds to wait for a
        // connection checkout.
        // Any attempt to retrieve a connection from this pool that exceeds the set
        // limit will throw an
        // SQLException.
        config.setConnectionTimeout(10000); // 10 seconds
        // idleTimeout is the maximum amount of time a connection can sit in the pool.
        // Connections that
        // sit idle for this many milliseconds are retried if minimumIdle is exceeded.
        config.setIdleTimeout(10000); // 10 minutes
        // [END cloud_sql_mysql_servlet_timeout]

        // [START cloud_sql_mysql_servlet_backoff]
        // Hikari automatically delays between failed connection attempts, eventually
        // reaching a
        // maximum delay of `connectionTimeout / 2` between attempts.
        // [END cloud_sql_mysql_servlet_backoff]

        // [START cloud_sql_mysql_servlet_lifetime]
        // maxLifetime is the maximum possible lifetime of a connection in the pool.
        // Connections that
        // live longer than this many milliseconds will be closed and reestablished
        // between uses. This
        // value should be several minutes shorter than the database's timeout value to
        // avoid unexpected
        // terminations.
        config.setMaxLifetime(1800000); // 30 minutes
        // [END cloud_sql_mysql_servlet_lifetime]

        // [END_EXCLUDE]

        // Initialize the connection pool using the configuration object.
        DataSource pool = new HikariDataSource(config);
        // [END cloud_sql_mysql_servlet_create]
        return pool;
    }

    public JSONObject getOrderStatus(String orderId){

        JSONObject obj = new JSONObject();

        try {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                Integer from_address_id = rs.getInt("from_address_id");
                Integer to_address_id = rs.getInt("to_address_id");
                Integer robot_id = rs.getInt("robot_id");
                String expectTime = rs.getDate("expect_arrive_time").toString();

                JSONObject toAddress = getAddress(to_address_id);

                obj.put("order_status", status);
                obj.put("robot_id", robot_id);               
                obj.put("to_address", toAddress);
                obj.put("expect_arrive_time", expectTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return obj;
    }

    public JSONObject getAddress(Integer addressId){
        JSONObject obj = new JSONObject();

        try {
            String sql = "SELECT * FROM addresses WHERE address_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, addressId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                obj.put("street", rs.getString("street"));
                obj.put("city", rs.getString("city"));
                obj.put("state", rs.getString("state"));
                obj.put("zipcode", rs.getString("zipcode"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return obj;
    }

    public Integer getAddressId(Integer robotId){

        Integer addressId = null;

        try {
            String sql = "SELECT * FROM robots WHERE robot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robotId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                addressId = rs.getInt("current_address_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return addressId;
    }
    
    public JSONObject getCurrentGeoLocation(Integer robotId){

        JSONObject currentGeo = new JSONObject();

        try {
            String sql = "SELECT * FROM robots WHERE robot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robotId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String lat = rs.getString("current_lat");
                String lon = rs.getString("current_lng");
                currentGeo.put("Lat", lat);
                currentGeo.put("Lon", lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return currentGeo;
    }

    public int getSpeed(Integer robotId){

        int speed = 0;
        try {
            if (conn == null)
                throw new FileNotFoundException("No connection to database");
            String sql = "SELECT * FROM robots WHERE robot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robotId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                speed = rs.getInt("speed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return speed;
    }

    // TODO: implement this method to create an address if not exists in database
    public Integer createAddress(Address address){
        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
        	System.out.println("craete address");
            Integer addressId = null;
            String sql = "INSERT IGNORE INTO addresses(street, city, state, zipcode) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
//            stmt.setNull(1,Types.INTEGER);
            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setString(3, address.getState());
            stmt.setString(4, address.getZipcode());
//            System.out.println("final command is " + stmt.toString());
            stmt.execute();
            
            sql = "SELECT LAST_INSERT_ID();";
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                addressId = rs.getInt("LAST_INSERT_ID()");
            }
            System.out.println("the newest address_id is: " + addressId);
            
            return addressId;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: implement this method to get available (key: branch_id, value: branch_address) pairs
    public JSONObject getBranchAddress(int branch_id){
        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
            String sql = "SELECT address_id FROM branches WHERE branch_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, branch_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                 return  getAddress(rs.getInt("address_id"));
            }
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 

        return null;
    }
    
    public Map<Integer, Address> getAvailBranches(){
    	Map<Integer, Address> map = new HashMap<>();
        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
            String sql = "SELECT * FROM branches";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                 Integer branchId = rs.getInt("branch_id");
                 map.put(branchId, Address.parse(getAddress(rs.getInt("address_id"))));
            }
            return map;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 

        return map;
    }
    
    // implement this method to get all available robot with input type among all branches
    public List<Integer> getAvailRobotIds(String robotType){
    	Integer avaRobotsInAllBranch = null;
    	return getAvailRobotIds(robotType, avaRobotsInAllBranch);
    }
    // implement this method to get all available robot with input type in branch with branch_id
    public List<Integer> getAvailRobotIds(String robotType, Integer branch_id){
    	List<Integer> robotIds = new ArrayList<Integer>();

        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
            String sql = "SELECT robot_id FROM robots WHERE status = ? AND type = ? ";
            if(branch_id != null) {
            	sql += " AND branch_id = ? ";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            if(branch_id != null) {
            	stmt.setInt(3, branch_id);
            }
            stmt.setString(1, Robot.IN_BRANCH);
            stmt.setString(2, robotType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                 robotIds.add(rs.getInt("robot_id"));
            }
            return robotIds;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

    // TODO: implement this method to create a fake order
    public boolean createOrder(Order order){

        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
        	// create from_address and to_address first;
            int fromAddressId = createAddress(order.getFromAddress());
            int toAddressId = createAddress(order.getToAddress());
            
            String sql = "INSERT INTO orders(order_id, from_address_id, to_address_id, receiver_email, "
            		+ "sender_email) VALUES(?, ?, ?, ?, ?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, order.getOrderId().toString());
            stmt.setInt(2, fromAddressId);
            stmt.setInt(3, toAddressId);
            stmt.setString(4, order.getReceiverEmail());
            stmt.setString(5, order.getSenderEmail());
            stmt.execute();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }

    
    // TODO: implement this method to complement robot information(type, branch_id, geolocation) with input robot. 
    public boolean complementRobot(Robot robot) {

        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
        	// create from_address and to_address first;
            int robot_id = robot.getRobotId();
            String sql = "SELECT * from robots where robot_id = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robot_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	System.out.println("complementing robot...");
            	robot.setType(rs.getString("type"));
            	if(robot.getType().equals(Robot.UAV)) {
            		robot.setSpeed(rs.getInt("speed"));
            	}
            	robot.setBranchAddress(Address.parse(getBranchAddress(rs.getInt("branch_id"))));
            	GeoLocation location = DistanceUtils.getGeocode(robot.getBranchAddress());
                robot.setCurrentGeoLocation(location);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }
    
    public boolean deleteOrder(String orderId) {
        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
        	// create from_address and to_address first;
            String sql = "DELEE FROM orders where order_id = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            return stmt.execute();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }
    
    public boolean confirmOrder(JSONObject robotObject, String orderId) {

        try {
        	// create from_address and to_address first;
            String sql = "UPDATE orders SET robot_id = ?, price = ? WHERE order_id = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robotObject.getInt("robot_id"));
            stmt.setDouble(2, robotObject.getDouble("price"));
            stmt.setString(3, orderId);
            return stmt.execute();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return false;
    }
    
    public void close() {
	    if (conn != null) {
		     try {
		      this.conn.close();
		     } catch (Exception e) {
		      e.printStackTrace();
		     }
		    }
    }

	@Override
	public JSONObject getRobotInformation(Integer robot_id) {
        try {
        	if (conn == null)
        		throw new FileNotFoundException("No connection to database");
        	// create from_address and to_address first;
            String sql = "SELECT * from robots where robot_id = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, robot_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	JSONObject robot = new JSONObject();
            	robot.put("robot_id", rs.getInt("robot_id"));
            	robot.put("branch_id", rs.getInt("branch_id"));
            	robot.put("type", rs.getString("type"));
            	//robot.put("max_load", rs.getInt("max_load"));
            	robot.put("speed", rs.getInt("speed"));
            	robot.put("endurance", rs.getInt("endurance"));
            	robot.put("status", rs.getString("status"));
            	robot.put("current_order_id", rs.getString("current_order_id"));
            	robot.put("current_lat", rs.getDouble("current_lat"));
            	robot.put("current_lng", rs.getDouble("current_lng"));
                return robot;
            }
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
	}
}
