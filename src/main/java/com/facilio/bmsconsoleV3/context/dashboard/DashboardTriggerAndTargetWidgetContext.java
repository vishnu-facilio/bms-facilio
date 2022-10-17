package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DashboardTriggerAndTargetWidgetContext {

    public Long id=-1l;
    public Long actionId;
    public Long target_widget_id;
    public Long datapoint_id;
    public Long criteriaId;
    public Criteria criteria;
    public String moduleName;
    public DashboardWidgetContext.WidgetType widgetType;
    public JSONObject targetWidgetMeta;
    public List<DashboardReadingTargetWidgetContext> dataPointList = new ArrayList<>();
    public JSONObject dataPointMeta;
    public String getDataPointMetaStr( JSONObject dataPointMeta) {
        return dataPointMeta != null ? dataPointMeta.toJSONString() : null;
    }

    public void setDataPointMetaStr(String dataPointMetaStr) throws ParseException{
        if(dataPointMetaStr != null) {
            this.dataPointMeta = FacilioUtil.parseJson(dataPointMetaStr);
        }
    }

}
