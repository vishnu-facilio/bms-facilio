package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderModuleSettingContext;
import com.facilio.bmsconsoleV3.util.V3WorkOrderModuleSettingAPI;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderModuleSettingAction extends V3Action {

    private V3WorkOrderModuleSettingContext workOrderModuleSetting;

    public String addOrUpdate() throws Exception {
        V3WorkOrderModuleSettingAPI.addOrUpdateSetting(workOrderModuleSetting);
        return SUCCESS;
    }

    public String fetch() throws Exception {

        setData("workOrderModuleSetting",V3WorkOrderModuleSettingAPI.fetchWorkOrderModuleSettings());
        return SUCCESS;
    }

}
