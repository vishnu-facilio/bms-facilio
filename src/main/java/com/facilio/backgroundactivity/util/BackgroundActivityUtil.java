package com.facilio.backgroundactivity.util;

import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class BackgroundActivityUtil {
    private static final String BACKGROUND_ACTIVITY_CONFIG = FacilioUtil.normalizePath("conf/backgroundactivity.yml");

    private static Map<String, Map<Integer,String>> activityVsStatus = new HashMap<>();
    private static Map<String, Map<Integer,String>> activityVsStatusColorCode = new HashMap<>();
    private static List<String> availableActivities = new ArrayList<>();

    public static void init() throws IOException {
        Map<String, Object> configs;
        try {
            configs = FacilioUtil.loadYaml(BACKGROUND_ACTIVITY_CONFIG);
        } catch (IOException e) {
            LOGGER.error("Error occurred while parsing "+ BACKGROUND_ACTIVITY_CONFIG, e);
            throw e;
        }


        Map<String,Object> activityConfigs = (Map<String, Object>) configs.get("activity");

        if(MapUtils.isNotEmpty(activityConfigs)) {
            for (Map.Entry<String, Object> entry : activityConfigs.entrySet()) {
                String activity = entry.getKey();
                availableActivities.add(activity);
                Map<String,Map<Integer,Map<String,String>>> statusMap = (Map<String, Map<Integer,Map<String,String>>>) entry.getValue();
                if(statusMap.containsKey("status")) {
                    Map<Integer,Map<String,String>> map = statusMap.get("status");
                    String initialStatus = map.get(0).get("name");
                    String initialStatusColor = map.get(0).get("color");
                    Map<Integer,String> valueStatusMap = new HashMap<>();
                    Map<Integer,String> valueStatusColorCodeMap = new HashMap<>();
                    valueStatusMap.put(0,initialStatus);
                    valueStatusColorCodeMap.put(0,map.get(0).get("color"));
                    for(int i = 1; i <= 100; i++) {
                        if(map.containsKey(i)) {
                            initialStatus = map.get(i).get("name");
                            initialStatusColor = map.get(i).get("color");
                            valueStatusMap.put(i, initialStatus);
                            valueStatusColorCodeMap.put(i,initialStatusColor);
                        } else {
                            valueStatusMap.put(i, initialStatus);
                            valueStatusColorCodeMap.put(i,initialStatusColor);
                        }
                    }
                    valueStatusMap.put(-2,map.get(-2).get("name"));
                    valueStatusColorCodeMap.put(-2,map.get(-2).get("color"));
                    activityVsStatus.put(activity, valueStatusMap);
                    activityVsStatusColorCode.put(activity, valueStatusColorCodeMap);
                }
            }
        }
    }
    public static boolean isActivityAvailable(String activity) {
        return availableActivities.contains(activity);
    }

    public static String getStatus(String activity, Integer percentage) {
        if(activityVsStatus.containsKey(activity)) {
            Map<Integer,String> map = activityVsStatus.get(activity);
            if(map.containsKey(percentage)) {
                return map.get(percentage);
            }
        }
        return null;
    }

    public static String getStatusColorCode(String activity, Integer percentage) {
        if(activityVsStatusColorCode.containsKey(activity)) {
            Map<Integer,String> map = activityVsStatusColorCode.get(activity);
            if(map.containsKey(percentage)) {
                return map.get(percentage);
            }
        }
        return null;
    }
}
