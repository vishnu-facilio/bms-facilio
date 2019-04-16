package com.facilio.util;

import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class FacilioUtil {

    private static final Logger LOGGER = LogManager.getLogger(FacilioUtil.class.getName());

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static long getActualLastRecordedTime(FacilioModule module) {
    	if(module.getDataInterval() <= 0) {
    		return -1l;
    	}
		try {
			ZonedDateTime zdt = DateTimeUtil.getDateTime(DateTimeUtil.getCurrenTime());
			zdt = zdt.truncatedTo(module.getDateIntervalUnit());
			return DateTimeUtil.getMillis(zdt, true);
		}
		catch(Exception e) {
			return -1l;
		}
	}

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

	private static JSONParser parser = new JSONParser();
    public static  JSONObject parseJson (String jsonStr) throws ParseException {
    	if (StringUtils.isEmpty(jsonStr)) {
    		return null;
		}
		JSONParser parser = new JSONParser();
		return  (JSONObject) parser.parse(jsonStr);
	}
	public static  JSONArray parseJsonArray (String str) throws ParseException {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		JSONParser parser = new JSONParser();
    	return (JSONArray) parser.parse(str);
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
	
	public static void addNotificationLogger (NotificationType type, String to, JSONObject info) throws Exception {
		Map<String, Object> props = new HashMap<>();
		props.put("type", type.getValue());
		props.put("to", to);
		props.put("threadName", Thread.currentThread().getName());
		props.put("createdTime", System.currentTimeMillis());
		if (info != null) {
			props.put("info", info.toJSONString());
		}
		
		new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNotificationLoggerModule().getTableName())
				.fields(FieldFactory.getNotificationLoggerFields())
				.insert(props)
				;
	}
	
	public static enum NotificationType {
		EMAIL,
		SMS
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static NotificationType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
