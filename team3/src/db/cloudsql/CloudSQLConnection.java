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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

import entity.Address;
import entity.Order;
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
    private static final String JSON_PATH = "/Users/qiaozhu/Downloads/laioffer_team3-database/Around-27f2aa43fec3.json";

    public CloudSQLConnection(Connection conn) {
        this.conn = conn;
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
                String from_address_id = rs.getString("from_address_id");
                String to_address_id = rs.getString("to_address_id");
                String robot_id = rs.getString("robot_id");

                JSONObject fromAddress = getAddress(from_address_id);
                JSONObject toAddress = getAddress(to_address_id);

                obj.put("status", status);
                obj.put("robot_id", robot_id);
                obj.put("from_address", fromAddress);
                obj.put("to_address", toAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return obj;
    }

    public JSONObject getAddress(String addressId) throws SQLException {
        if (conn == null)
            return null;

        JSONObject obj = new JSONObject();

        try {
            String sql = "SELECT * FROM addresses WHERE address_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, addressId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                obj.put("street", rs.getString("street"));
                obj.put("city", rs.getString("city"));
                obj.put("state", rs.getString("state"));
                obj.put("zipcode", rs.getString("zipcode"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return obj;
    }

    public String getAddressId(String robotId) throws SQLException {
        if (conn == null)
            return null;

        String addressId = "";

        try {
            String sql = "SELECT * FROM robots WHERE robot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, robotId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                addressId = rs.getString("current_address_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return addressId;
    }

    public int getSpeed(String robotId) throws SQLException, FileNotFoundException {
        if (conn == null)
            throw new FileNotFoundException("No connection to database");

        int speed = 0;

        try {
            String sql = "SELECT * FROM robots WHERE robot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, robotId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                speed = rs.getInt("speed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return speed;
    }

    // TODO: implement this method to create an address if not exists in database
    public boolean createAddress() throws SQLException, FileNotFoundException {
        return false;
    }

    // TODO: implement this method to get available (key: branch_id, value: branch_address) pairs
    public Map<Integer, Address> getAvailBranches() throws SQLException, FileNotFoundException {
        return new HashMap<>();
    }

    // TODO: implement this method to create a fake order
    public boolean createFakeOrder(Order o) throws SQLException, FileNotFoundException {
        return false;
    }

    // TODO: implement this method to update order by order_id
    public boolean updateOrder(Order o) throws SQLException, FileNotFoundException {
        return false;
    }
}
