package com.promeets.android.util;


public class TimeUtil {
    /**
     * Takes a time in milliseconds and returns an hours, minutes and
     * seconds representation.  Fractional ms times are rounded down.
     * Leading zeros and all-zero slots are removed.  A table of input
     * and output examples follows.
     *
     * <p>Recall that 1 second = 1000 milliseconds.
     *
     * @param ms Time in seconds.
     * @return String-based representation of time in hours, minutes
     * and second format.
     */
    public static String ms2String(long ms) {
        long totalSecs = ms / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString;
        else return ":" + secsString;
    }

}
