package com.facilio.weather.util;

public class WeatherServiceUtils {
    public final static Long ROUND_OFF_MIN = (long) (5 * 60 * 1000); //5 mins
    public static Long roundOfTime(Object time) {
        long ttime = (long) time;
        ttime /= ROUND_OFF_MIN;
        ttime *= ROUND_OFF_MIN;
        return ttime;
    }

    public static Long getTimeinMillis(Object time) {
        return time != null ? (Long) time * 1000 : null;
    }

    public static Double convert(Double coordinate) {
        return Double.valueOf(String.format("%.3f", coordinate));
    }

}
