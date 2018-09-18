package com.facilio.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FacilioUtil {

    private static final String PROPERTY_FILE = "conf/facilio.properties";
    private static final Properties PROPERTIES = new Properties();
    private static Logger log = LogManager.getLogger(FacilioUtil.class.getName());

    static {
        URL resource = FacilioUtil.class.getClassLoader().getResource(PROPERTY_FILE);
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                PROPERTIES.load(stream);
            } catch (IOException e) {
                log.info("Exception occurred ", e);
            }
        }
    }

    public static String getProperty(String name) {
        String value = PROPERTIES.getProperty(name);
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }
    
    public static boolean isNumeric(String str)
    {
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
}
