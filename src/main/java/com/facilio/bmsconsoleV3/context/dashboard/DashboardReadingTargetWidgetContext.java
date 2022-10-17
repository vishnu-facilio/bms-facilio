package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class DashboardReadingTargetWidgetContext {

    public Long target_widget_id;
    public Long actionId;
    public String datapoint_link;
    public Criteria criteria;
    public String parentModuleName;
    public String moduleName;
    public Long criteriaId;
    public String dataPointMetaStr;
    public JSONObject toDataPointJson()
    {
        JSONObject result_json = new JSONObject();
        result_json.put("datapoint_link", datapoint_link);
        result_json.put("parentModuleName", parentModuleName);
        return result_json;
    }
}
