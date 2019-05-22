package database;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;



import entity.Address;
import entity.Order;
import entity.Robot;
import jnr.ffi.Struct.int16_t;


public interface DBConnection {

    public Connection getConnection();

    public JSONObject getOrderStatus(String orderId);

    public JSONObject getAddress(Integer addressId);

    public Integer getAddressId(Integer robotId);
    public int getSpeed(Integer robotId) ;
    // TODO: implement this method to create an address if not exists in database
    public Integer createAddress(Address address);

    // TODO: implement this method to get available (key: branch_id, value: branch_address) pairs
    public JSONObject getBranchAddress(int branch_id);
    
    public Map<Integer, Address> getAvailBranches();
    
    // implement this method to get all available robot with input type among all branches
    public List<Integer> getAvailRobotIds(String robotType);
    // implement this method to get all available robot with input type in branch with branch_id
    public List<Integer> getAvailRobotIds(String robotType, Integer branch_id);

    // TODO: implement this method to create a fake order
    public boolean createOrder(Order order);
    
    // TODO: implement this method to complement robot information(type, branch_id, geolocation) with input robot. 
    public boolean complementRobot(Robot robot);
    
    public boolean deleteOrder(String orderId);
    
    public boolean confirmOrder(JSONObject input);
    
    public boolean updateOrder(String orderId, Integer robotId);
    
    public void close();

	public JSONObject getRobotInformation(Integer robot_id);
}
