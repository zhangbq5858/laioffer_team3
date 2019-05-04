package util;

import entity.Address;

public class EstimateTime {
	public static double estimateTime(Address fromAddress, Address toAddress, int speed) {
		//TODO
		//Optimize in the future
		double distance = DistanceUtils.getDistance(fromAddress, toAddress);
		return distance / speed;
	}
}
