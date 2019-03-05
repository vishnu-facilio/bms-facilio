package com.facilio.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FacilioUtil {

    private static final Logger LOGGER = LogManager.getLogger(FacilioUtil.class.getName());

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");


    public static boolean isNumeric(String str) {
    	if(str == null) {
    		return false;
    	}
    	return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal. eg  : (-10.23)
    }
    
    public static Double decimalClientFormat(Double d) {
    	
    	if(d != null) {
    		d = Double.parseDouble(DECIMAL_FORMAT.format(d));
    	}
    	return d;
    }
    public static JSONArray getSingleTonJsonArray(Object o) {
    	JSONArray jsonArray = new JSONArray();
    	jsonArray.add(o);
    	return jsonArray;
    }
    
    public static Map<String, Object> getAsMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();

	    Iterator<String> keysItr = object.keySet().iterator();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        if(value instanceof JSONArray) {
	            value = getAsList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = getAsMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}

	public static List<Object> getAsList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.size(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = getAsList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = getAsMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	
	public static int parseInt (Object val) {
		if (val instanceof Integer) {
			return (int) val;
		}
		return new Double(val.toString()).intValue();
	}
	public static long parseLong (Object val) {
		if (val instanceof Long) {
			return (long) val;
		}
		return new Double(val.toString()).longValue();
	}
	public static double parseDouble (Object val) {
		if (val instanceof Double) {
			return (double) val;
		}
		return new Double(val.toString());
	}
	
	public static final double RADIUS_OF_EARTH = 6378.137; // Radius of earth in KM
	public static double calculateHaversineDistance (double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
	    double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
	    double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    			Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
	    			Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double d = RADIUS_OF_EARTH * c;
	    return d * 1000; // meters
	}
}
