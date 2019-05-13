package util;

import entity.Robot;

public class PriceUtils {
    public static double price(double time, String landRobot) {
        if (landRobot.equals(Robot.LAND_ROBOT)) {
            return 3 * (int)(time);
        } else {
            return 10 * (int)(time);
        }
    }
}
