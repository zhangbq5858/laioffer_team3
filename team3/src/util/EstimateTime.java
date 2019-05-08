package util;

import entity.Address;

public class EstimateTime {
	public static double estimateTime(Address fromAddress, Address toAddress, int speed) {
		//TODO
		//Optimize in the future
		double distance = DistanceUtils.getDistance(fromAddress, toAddress);
		return distance / speed;
	}
	
	// TODO estimate time for land_robot in unit s;
	public static double estimateTime(Address fromAddress, Address toAddress) {
		//TODO
		//Optimize in the future
		return 10;
	}
	
	// TODO estimate time for land_robot in unit s;
	public static double estimateTime(GeoLocation fromAddress, GeoLocation toAddress) {
		//TODO
		//Optimize in the future
		return 10;
	}
}
