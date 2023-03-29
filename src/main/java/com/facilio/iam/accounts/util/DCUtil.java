package com.facilio.iam.accounts.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.util.FacilioUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class DCUtil {

    private static final String DC_FILE = FacilioUtil.normalizePath("conf/datacenter.yml");
    
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
    public static String getRegion(int id) {
        return getDc(id).get("region");
    }

    public static Integer getCurrentDC() {
    		String appDomain = FacilioProperties.getMainAppDomain();
    		for (Integer dc : dataCenters.keySet()) {
    			String mainAppDomain = getMainAppDomain(dc);
    			if (mainAppDomain.equals(appDomain)) {
    				return dc;
    			}
    		}
    		if (FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise()) {
    			return 1;
    		}
    		return null;
    }
}
