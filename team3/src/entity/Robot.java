package entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import javax.sql.DataSource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.json.JSONObject;

import com.google.api.client.util.Lists;
import db.cloudsql.CloudSQLConnection;
import entity.Order.OrderBuilder;
import util.DistanceUtils;
import util.EstimateTime;
import util.GeoLocation;

// robot 启动流程： addWord(order) -> beganWork(new Date() set time to began work, default immediately) 


public class Robot {
//	public static final String ADDINGWORK = "addingWORK";
	public static final String PICKING = "picking";
	public static final String SENDING = "sending";
	public static final String PICKED = "picked";
	public static final String RECEIVED = "received";
	public static final String GOING_BACK = "going back";
	public static final String IN_BRANCH = "in branch";
	
	public static final String LAND_ROBOT = "land_robot";
	public static final String UAV = "UAV";
	
	
	//TODO 所有时间单位暂时为ms 便与测试，之后下需要改为分钟
	private int robotId;
	private int interval_report_time; 
	private Queue<Order> orders;
	private String status;
	private Order currentOrder;
	private int speed;
	private GeoLocation currenGeoLocation;
	private Address branchAddress;
	private String type;
	
	private Connection conn;
	
	
	//TODO private Address currentAddress;
	
	public static void main(String[] inputs) throws FileNotFoundException, IOException, InterruptedException, SQLException {
		JSONObject fromAddressJO = new JSONObject();
		JSONObject toAddressJO = new JSONObject();
		
		fromAddressJO.put("street", "2451DelmarDrE");
		fromAddressJO.put("city", "Seattle");
		fromAddressJO.put("state", "WA");
		fromAddressJO.put("zipcode", "98102");
		

		toAddressJO.put("street", "124715thAveE");
		toAddressJO.put("city", "Seattle");
		toAddressJO.put("state", "WA");
		toAddressJO.put("zipcode", "98112");

		Address fromAddress = Address.parse(fromAddressJO);
		Address toAddress = Address.parse(toAddressJO);
		
		OrderBuilder orderBuilder = new OrderBuilder();
		orderBuilder.setFromAddress(fromAddress);
		orderBuilder.setToAddress(toAddress);
		Order order = orderBuilder.build();
		
		CloudSQLConnection connection = new CloudSQLConnection();
		connection.createOrder(order);
		
		List<Integer> availableRobots = connection.getAvailRobotIds(LAND_ROBOT);
		if(availableRobots.size() > 0) {
			Robot robot = new Robot(availableRobots.get(0), 5000);
			robot.addWork(order);
			robot.beganWork();
		}
	}
	
	//initialize robot with robot_id and interval_report_time.
	public Robot(int robotId, int interval_report_time) throws FileNotFoundException, IOException, SQLException {
		CloudSQLConnection connection = new CloudSQLConnection();
	    this.conn = connection.getConnection();
		this.robotId = robotId;
		this.interval_report_time = interval_report_time;
		orders = new LinkedList<>();
		status = IN_BRANCH;
		connection.complementRobot(this);
		System.out.println("finish creating robot with id: " + getRobotId());
	}
	
	// unit: s
	public void setIntervalReportTime(int time) {
		this.interval_report_time = time;
	}
	
	public void setBranchAddress(Address address) {
		this.branchAddress = address;
	}
	
	public Address getBranchAddress() {
		return this.branchAddress;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Order getCurrentOrder() {
		return this.currentOrder;
	}
	
	public GeoLocation getCurrentGeoLocation() {
		return this.currenGeoLocation;
	}
	
	public void setCurrentGeoLocation(GeoLocation geoLocation) {
		this.currenGeoLocation = geoLocation;
	}
	
	private void setStatus(String status) {
		this.status = status;
	}
	
	private void setCurrentOrder(Order order) {
		this.currentOrder = order;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getRobotId() {
		return this.robotId;
	}
	
	public boolean addWork(Order order) {
		this.orders.offer(order);
		return true;
	}
	
	public boolean cancelAllWorks() {
		this.orders.clear();
		return true;
	}
	
	
	
	public void beganWork() throws InterruptedException, FileNotFoundException, IOException, SQLException {
		beganWork(new Date());
	}
	
	public void beganWork(Date beginDate) throws InterruptedException, FileNotFoundException, IOException, SQLException {
		
		Timer timer = new Timer(true);
		TimerTask reporTask = new TimerTask() {
			@Override
			public void run() {
				report();
			};
		};
		timer.schedule(reporTask, beginDate, interval_report_time);
		//deal with task queue
		while(!orders.isEmpty()) {
			Order currentOrder = orders.poll();
			setCurrentOrder(currentOrder);
			pickPackage();
			sendPackage();
		}
		//set 10 minutes to go back
		goBack();
		timer.cancel();
	}
	// TODO monitor geolocation changing
	private void pickPackage() throws InterruptedException {
		System.out.println("prepare to pick package");
		int timeInSecond = (int)EstimateTime.estimateTime(getBranchAddress(), getCurrentOrder().getFromAddress());
		setStatus(PICKING);
		Thread.sleep(timeInSecond * 1000);
		remainderSender();
		setStatus(PICKED);
		report();
	}
	
	// TODO monitor geolocation changing
	private void sendPackage() throws InterruptedException {
		System.out.println("prepare to send package");
		int timeInSecond = (int)EstimateTime.estimateTime(getCurrentOrder().getFromAddress(), getCurrentOrder().getToAddress());
		setStatus(SENDING);
		Thread.sleep(timeInSecond * 1000);
		remainderReceiver();
		setStatus(RECEIVED);
		report();
	}
	
	private void goBack() throws InterruptedException {
		System.out.println("prepare to go back to branch");
		int timeInSecond = (int)EstimateTime.estimateTime(getCurrentOrder().getToAddress(), getBranchAddress());
		setCurrentOrder(null);
		setStatus(GOING_BACK);
		Thread.sleep(timeInSecond * 1000);
		setStatus(IN_BRANCH);
		report();
	}

	private void report() {
		try {
			// Step 1 Connect to MySQL.
			if (conn == null) {
				return;
			}
			// read user data from table users;
			String sql = "UPDATE robots SET status = ?, current_order_id = ?, "
					+ "current_lat = ?, current_lng = ? "
					+ " WHERE robot_id = ? ";
//			System.out.println("robot update report with command ");
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, getStatus());
			if(getCurrentOrder() == null) {
				stmt.setString(2, "null");
			} else {
				stmt.setString(2, getCurrentOrder().getOrderId().toString());
			}
			stmt.setDouble(3, getCurrentGeoLocation().getLat());
			stmt.setDouble(4,  getCurrentGeoLocation().getLng());
			stmt.setInt(5, getRobotId());
			stmt.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void remainderReceiver() {
		// TODO send email to receiver
		
	}

	private void remainderSender() {
		// TODO send email to sender
		
	}

	
	public int getSpeed() {
		return this.speed;
	}
	

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("robotId", robotId);
		jsonObject.put("interval_report_time", interval_report_time);
		jsonObject.put("orders", orders);
		jsonObject.put("status", status);
		jsonObject.put("currentOrder", currentOrder);
		jsonObject.put("speed", speed);

		return jsonObject;
	}

}
