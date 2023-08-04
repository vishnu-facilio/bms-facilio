package com.facilio.bmsconsole.monitoring;

import java.util.Map;

public interface MonitoringMXBean {
    Map<String, Long> getPMWorkOrderMonitor();

    Map<String, Long> getKpiMonitor();

    Map<String, Long> getInspectionMonitor();

    Map<String, Long> getWorkFlowMonitor();

}
