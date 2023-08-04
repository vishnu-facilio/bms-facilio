package com.facilio.bmsconsole.monitoring;

import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MonitoringMXBeanImp implements MonitoringMXBean {

    public static final Logger LOGGER = LogManager.getLogger(MonitoringMXBeanImp.class.getName());
    long[] orgIds = new long[]{1102, 17, 6};

    public MonitoringMXBeanImp() {
    }


    // Logic used: There should be no pre-open work orders after their created time. It is an error if there are.
    public Map<String, Long> getPMWorkOrderMonitor() {
        Map<String, Long> queryMap = new HashMap<>();
        try {
            long currentTime = DateTimeUtil.getCurrenTime();
            for (long orgId : orgIds) {
                String keyName = MonitoringAPI.getKeyName(orgId);

                long preOpenWoCount = MonitoringAPI.getPreOpenWoCount(currentTime, orgId);
                queryMap.put(keyName, preOpenWoCount);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during getPMWorkOrderMonitor Implementation" + e);
        }
        return queryMap;
    }

    // logic: same as logic used for monitoring PM workorder.
    public Map<String, Long> getInspectionMonitor() {
        Map<String, Long> queryMap = new HashMap<>();
        try {
            for (long orgId : orgIds) {
                long preOpenInspectionCount = MonitoringAPI.getPreOpenInspectionCount(DateTimeUtil.getCurrenTime(), orgId);
                String keyName = MonitoringAPI.getKeyName(orgId);
                queryMap.put(keyName, preOpenInspectionCount);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during getInspectionMonitor implementation " + e);
        }
        return queryMap;
    }

    public Map<String, Long> getWorkFlowMonitor() {
        Map<String, Long> queryMap = new HashMap<>();
        try {
            for (long orgId : orgIds) {
                long failedWorkflowCount = MonitoringAPI.getFailedWorkflowCount(DateTimeUtil.getCurrenTime(), orgId);
                String keyName = MonitoringAPI.getKeyName(orgId);
                queryMap.put(keyName, failedWorkflowCount);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during getInspectionMonitor implementation " + e);
        }
        return queryMap;
    }

    @Override
    public Map<String, Long> getKpiMonitor() {
        return null;
    }
}
