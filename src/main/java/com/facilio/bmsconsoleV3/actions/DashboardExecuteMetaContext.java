package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Setter
@Getter
@Log4j
public class DashboardExecuteMetaContext {

    public Long trigger_widget_id;
    public Long dashboardId;
    public JSONObject trigger_meta;
    public JSONObject placeHolders= new JSONObject();

    public void setPlaceHoldersValues(){
        placeHolders.put("CURRENT_USER", AccountUtil.getCurrentUser().getId());
        placeHolders.put("CURRENT_DASHBOARD", dashboardId);
    }
}
