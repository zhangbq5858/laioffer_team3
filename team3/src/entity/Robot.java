package entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import javax.sql.DataSource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
import com.google.api.client.util.Lists;
import db.cloudsql.CloudSQLConnection;
import util.EstimateTime;

// robot 启动流程： addWord(order) -> beganWork(new Date() set time to began work, default immediately) 


public class Robot {
//	public static final String ADDINGWORK = "addingWORK";
	public static final String PICKING = "picking";
	public static final String SENDING = "sending";
	public static final String PICKED = "picked";
	public static final String RECEIVED = "received";
	public static final String GOING_BACK = "going back";
	public static final String IN_BRANCH = "in branch";
	
	//TODO 所有时间单位暂时为ms 便与测试，之后下需要改为分钟
	private int robotId;
	private int interval_report_time; 
	private Queue<Order> orders;
	private String status;
	private Order currentOrder;
	private int speed;
	//TODO private Address currentAddress;
	/*
	public static void main(String[] inputs) throws FileNotFoundException, IOException, InterruptedException, SQLException {
	    Order order = new Order();
	    order.setOrderId("1");
	    order.setPickPackageTime(10000);
	    order.setSendPackageTime(20000);
		Robot robot = new Robot("1", 5000);
		robot.addWork(order);
		robot.beganWork();
	}
	
	public Robot(String robotId, int interval_report_time) {
		this.robotId = robotId;
		this.interval_report_time = interval_report_time;
		orders = new LinkedList<>();
		status = IN_BRANCH;
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
		DataSource pool = CloudSQLConnection.createConnectionPool();
		final Connection conn = pool.getConnection();
		Timer timer = new Timer(true);
		TimerTask reporTask = new TimerTask() {
			@Override
			public void run() {
				// TODO correct insert command
				report(conn);
			};
		};
		timer.schedule(reporTask, beginDate, interval_report_time);
		
		while(!orders.isEmpty()) {
			Order currentOrder = orders.poll();
			setCurrentOrder(currentOrder);
			setStatus(PICKING);
			Thread.sleep(EstimateTime.estimateTime(getAddress(), getAddress(), speed)());
			remainderSender();
			setStatus(PICKED);
			report(conn);
			setStatus(SENDING);
			Thread.sleep(currentOrder.getSendPackageTime());
			remainderReceiver();
			setStatus(RECEIVED);
			report(conn);
		}
		//set 10 minutes to go back
		setCurrentOrder(null);
		setStatus(GOING_BACK);
		Thread.sleep(10000);
		setStatus(IN_BRANCH);
		report(conn);
		timer.cancel();
	}

	private void report(Connection conn) {
		try {
			// Step 1 Connect to MySQL.
			if (conn == null) {
				return;
			}
			// read user data from table users;
			Statement statement = conn.createStatement();
			String sql = "UPDATE robots SET status = '" + status + "', current_order_id = "
					+ (currentOrder == null ? "null" :  "'" + currentOrder.getOrderId() + "'")
					+ " WHERE robot_id = '" + robotId + "';";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setStatus(String status) {
		this.status = status;
	}
	
	private void setCurrentOrder(Order order) {
		this.currentOrder = order;
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
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public String getRobotId() {
		return this.robotId;
	}
	
	public Address getAddress() {
		//TODO: using coordinates to get address
		return new Address.AddressBuilder().build();
	}
	*/

}
