package com.facilio.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class FacilioUtil {

    private static final String PROPERTY_FILE = "conf/facilio.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        URL resource = FacilioUtil.class.getClassLoader().getResource(PROPERTY_FILE);
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                PROPERTIES.load(stream);
            } catch (IOException e) {
                e.printStackTrace();
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
}
