package com.facilio.iam.accounts.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.util.FacilioUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class DCUtil {

    private static final String DC_FILE = "conf/datacenter.yml";
    
    private static Map<Integer, Map<String,String>> dataCenters = new HashMap<>();
    
    public static void init() throws IOException {
    		Map<String, Object> configs = null;
        try {
        		configs = FacilioUtil.loadYaml(DC_FILE);
        } catch (IOException e) {
            LOGGER.error("Error occurred while parsing "+ DC_FILE, e);
            throw e;
        }
        dataCenters = (Map<Integer, Map<String,String>>) configs.get("datacenters");
	}
    
    public static Map<String,String> getDc(int id) {
    		return dataCenters.get(id);
    }
    
    public static String getMainAppDomain(int id) {
    		return getDc(id).get("mainapp");
    }
    
    public static String getOccupantPortalDomain(int id) {
		return getDc(id).get("occupantportal");
    }
    public static String getTenantPortalDomain(int id) {
		return getDc(id).get("tenantportal");
    }
    public static String getClientPortalDomain(int id) {
		return getDc(id).get("clientportal");
    }
    public static String getVendorPortalDomain(int id) {
		return getDc(id).get("vendorportal");
    }

}
