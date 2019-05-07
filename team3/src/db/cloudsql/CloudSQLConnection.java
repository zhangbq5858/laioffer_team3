package db.cloudsql;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.mysql.cj.conf.StringPropertyDefinition;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

import entity.Address;
import entity.Order;
import entity.Address.AddressBuilder;
import jnr.ffi.Struct.int16_t;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudSQLConnection {

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
    
    public CloudSQLConnection() throws FileNotFoundException, IOException, SQLException {
    	DataSource pool = createConnectionPool(); 
    	this.conn = pool.getConnection();
    }

    public static DataSource createConnectionPool() throws FileNotFoundException, IOException {
        // [START cloud_sql_mysql_servlet_create]
        // The configuration object specifies behaviors for the connection pool.
        HikariConfig config = new HikariConfig();
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(JSON_PATH))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        // Configure which instance and what database user to connect with.
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
        config.setUsername(DB_USER); // e.g. "root", "postgres"
        config.setPassword(DB_PASS); // e.g. "my-password"

        // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated
        // connections.
        // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for
        // details.
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
        config.addDataSourceProperty("useSSL", "false");

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

    public JSONObject getOrderStatus(String orderId) throws SQLException {
        if (conn == null)
            return null;

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

                JSONObject fromAddress = getAddress(from_address_id);
                JSONObject toAddress = getAddress(to_address_id);

                obj.put("status", status);
                obj.put("robot_id", robot_id);
                obj.put("from_address", fromAddress);
                obj.put("to_address", toAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return obj;
    }

    public JSONObject getAddress(Integer addressId) throws SQLException {
        if (conn == null)
            return null;

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

    public Integer getAddressId(Integer robotId) throws SQLException {
        if (conn == null)
            return null;
        
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

    public int getSpeed(Integer robotId) throws SQLException, FileNotFoundException {
        if (conn == null)
            throw new FileNotFoundException("No connection to database");
        int speed = 0;
        try {
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
    public Integer createAddress(Address address) throws SQLException, FileNotFoundException {
    	if (conn == null)
    		throw new FileNotFoundException("No connection to database");
        
    	System.out.println("craete address");
        Integer addressId = null;

        try {
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
    public Map<Integer, Address> getAvailBranches() throws SQLException, FileNotFoundException {
    	Map<Integer, Address> map = new HashMap<>();
    	if (conn == null)
    		throw new FileNotFoundException("No connection to database");

        try {
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

    // TODO: implement this method to create a fake order
    public boolean createOrder(Order order) throws SQLException, FileNotFoundException {
    	if (conn == null)
    		throw new FileNotFoundException("No connection to database");

        try {
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

    // TODO: implement this method to update order by order_id  ?? only update price time robotid?
    public boolean updateOrder(Order o) throws SQLException, FileNotFoundException {
        return false;
    }
}
