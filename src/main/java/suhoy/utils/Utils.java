package suhoy.utils;

import java.util.Random;

/**
 *
 * @author suh1995
 */
public class Utils {

    public static int getRand(int min, int max) {
        Random r = new Random();
        return (r.nextInt(max - min + 1) + min);
    }

    public static long getPacing(long min, long max) {
        if (min>max) {
            return 0;
        } else {
            return (long) (getRand((int) min, (int) max));
        }
    }
}
