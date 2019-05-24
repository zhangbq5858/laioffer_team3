package entity;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Multiset.Entry;

import database.DBConnection;
import database.DBConnectionFactory;
import jnr.ffi.Struct.int16_t;

public class Management implements Runnable{
	public static final Integer ROBOTREPORTINTERVAL = 10 * 1000;
	private static Management instance;
	private TreeMap<Calendar, Order> allLandTasks;
	private TreeMap<Calendar, Order> allUAVTasks;
	private DBConnection dbConnection;
	private Integer totalLandRobotNumber;
	private Integer totalUAVNumber;
	// robot 执行一个任务分配周期
	private Integer robotWindowLength = 2; //hour
	
	private Management() {
		 allLandTasks = new TreeMap<>();
		 allUAVTasks= new TreeMap<>();
		 dbConnection = new DBConnectionFactory().getConnection();
		 totalLandRobotNumber = dbConnection.getAvailRobotIds(Robot.LAND_ROBOT).size();
		 totalUAVNumber = dbConnection.getAvailRobotIds(Robot.UAV).size();
		 Thread managementThread = new Thread(this);
		 managementThread.start();
	}
	
	public static Management getInstance() {
		if(instance == null) {
			instance = new Management();
		}
		return instance;
	}
	
	public void AddTask(Order order, String type) {
		if(type.equals(Robot.LAND_ROBOT)) {
			allLandTasks.put(order.getAppointmentTime(), order);
		} else if(type.equalsIgnoreCase(Robot.UAV)) {
			allUAVTasks.put(order.getAppointmentTime(), order);
		}
	}
	
	public Calendar getNextAvailableTime(String type) {
		TreeMap<Calendar, Order> map = type.equals(Robot.LAND_ROBOT) ? allLandTasks : allUAVTasks;
		int allRobotNumbers = type.equals(Robot.LAND_ROBOT) ? totalLandRobotNumber : totalUAVNumber;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.HOUR_OF_DAY, robotWindowLength);
		// 需要找到第一个window周期，使得window周期内order任务数小于robot数量
		int orderCount = 0;
//		System.out.println("get next available time: current tasks : " + map.size());
		for(Map.Entry<Calendar, Order> entry : map.entrySet()) {
			if(entry.getKey().compareTo(end) > 0) {
				if(orderCount < allRobotNumbers) {
					return start;
				}
				start.add(Calendar.HOUR_OF_DAY, robotWindowLength);
				end.add(Calendar.HOUR_OF_DAY, robotWindowLength);
				orderCount = 0;
			} else {
				orderCount++;
			}
		}
		// 需要新开一个周期
		if(orderCount >= allRobotNumbers) {
			start.add(Calendar.HOUR_OF_DAY, robotWindowLength);
		}
		return start;
	}
	
	class assignWork implements Runnable {
		private String type;
		
		public assignWork(String type) {
			this.type = type;
		}

		@Override
		public void run() {
			TreeMap<Calendar, Order> map = type.equals(Robot.LAND_ROBOT) ? allLandTasks : allUAVTasks;
			while(true) {
				if(map.size() == 0) { //没有任务时，休眠一分钟后检查
					System.out.println("Task Manager have no " + type + " order now");
					try {
						Thread.sleep(1 * 60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(map.size() > 0) {
					Map.Entry<Calendar, Order> entry = map.firstEntry();
					Calendar currentTime = Calendar.getInstance();
					if(currentTime.compareTo(entry.getValue().getAppointmentTime()) >= 0) {
						List<Integer> availableRobots = dbConnection.getAvailRobotIds(type);
						if(availableRobots.size() == 0) {
							System.out.println("Sorry, we have none " + type + " available now.");
							try {
								Thread.sleep(5 * 60 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							Integer robotId = availableRobots.get(0);
							//创建模拟机器人 并分配任务
							Robot robot = new Robot(robotId, ROBOTREPORTINTERVAL);
				            robot.addWork(entry.getValue());
				            // 更新order订单信息 提供robotid
				            dbConnection.updateOrder(entry.getValue().getOrderId(), robotId);
				            //删除map中已执行任务
				            map.remove(entry.getKey());
				            //机器人执行任务
				            Thread t = new Thread(robot);
				            t.start();
						}
					} else {
						//有任务但是最早的任务要在未来才需要执行 休眠五分钟
						System.out.println("No order need to be finished right now. But currently we have total " + type + " orders: " + map.size());
						try {
							Thread.sleep(5 * 60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		
	}

	@Override
	public void run() {
		Thread uav_tasks = new Thread(new assignWork(Robot.UAV));
		Thread land_tasks = new Thread(new assignWork(Robot.LAND_ROBOT));
		uav_tasks.start();
		land_tasks.start();
		
	}
	
}
