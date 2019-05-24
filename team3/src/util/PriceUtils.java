package util;

import entity.Robot;
import jnr.ffi.Struct.int16_t;

public class PriceUtils {
    public static double price(double time, String landRobot, String size, double weight) {
    	double packageFee = 0;
    	double timeFee = 0;
    	double weightFee = 1;
    	if(size.equals("compact")) {
    		packageFee = 3;
    	} else if (size.equals("premium")) {
    		packageFee = 5;
    	} else if(size.equals("cube")) {
    		packageFee = 7;
    	} else if(size.equals("super_value")) {
    		packageFee = 8;
    	}
    	if(weight > 20) {
    		weightFee = (weight - 20) / 50 + 1;
    	}
        if (landRobot.equals(Robot.LAND_ROBOT)) {
            timeFee = 0.15 * time;
        } else {
            timeFee = 0.75 * time;
        }
        return timeFee * weightFee + packageFee;
    }
}
