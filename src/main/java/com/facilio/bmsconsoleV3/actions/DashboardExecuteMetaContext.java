package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Log4j
public class DashboardExecuteMetaContext {

    public Long trigger_widget_id;
    public Long dashboardId;
    public Long dashboardTabId;
    public JSONObject trigger_meta;
    public JSONObject placeHolders= new JSONObject();
    public JSONObject placeHoldersMeta= new JSONObject();
    public JSONObject groupByMeta= new JSONObject();
    public void setPlaceHoldersValues(){
        placeHolders.put("CURRENT_USER", AccountUtil.getCurrentUser().getId());
        placeHolders.put("CURRENT_DASHBOARD", dashboardId);
    }




    public JSONObject global_filter_widget_map = new JSONObject();
    public JSONObject reading_filter_widget_map = new JSONObject();
    public JSONObject global_timeline_filter_widget_map = new JSONObject();
    public Map<Long, Map<String, String>> timeline_widget_field_map = new HashMap<>();
    public List<Long> rule_applied_widget_ids = new ArrayList<>();
    public JSONObject target_widget_json = new JSONObject();

    public JSONArray main_result_array = new JSONArray();
    public Map<String, Object> placeholder_vs_value_map= new HashMap<>();
    public Map<String, Object> groupby_placeholder_vs_value_map= new HashMap<>();
}
