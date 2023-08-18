package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardReadingWidgetFilterContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    private long reportId = -1;
    public long getReportId() {
        return reportId;
    }
    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    private String dataPointAlias;
    public String getDataPointAlias() { return this.dataPointAlias; }
    public void setDataPointAlias(String dataPointAlias) { this.dataPointAlias = dataPointAlias; }

    public Long triggerWidgetId;
    public Long getTriggerWidgetId() {
        return triggerWidgetId;
    }
    public void setTriggerWidgetId(Long triggerWidgetId) {
        this.triggerWidgetId = triggerWidgetId;
    }

    public Long targetWidgetId;
    public Long getTargetWidgetId() {
        return targetWidgetId;
    }
    public void setTargetWidgetId(Long targetWidgetId) {
        this.targetWidgetId = targetWidgetId;
    }

}
