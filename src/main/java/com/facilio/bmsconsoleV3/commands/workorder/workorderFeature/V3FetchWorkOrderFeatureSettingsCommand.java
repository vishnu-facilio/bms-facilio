package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.bmsconsoleV3.actions.WorkOrderModuleSettingAction;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.WorkOrderFeatureSettingType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

/**
 * V3FetchWorkOrderFeatureSettingsCommand
 * - fetches WorkOrder Feature Settings for the record's module state
 * - translates settings as a MAP
 */
public class V3FetchWorkOrderFeatureSettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3WorkOrderContext workOrderContext = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
        FacilioStatus currentModuleState = (FacilioStatus) context.get(FacilioConstants.ContextNames.CURRENT_MODULE_STATE);
        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsContexts;

        WorkOrderModuleSettingAction workOrderModuleSettingAction = new WorkOrderModuleSettingAction();
        workOrderModuleSettingAction.setAllowedStateId(currentModuleState.getId());

        workOrderFeatureSettingsContexts = workOrderModuleSettingAction.fetchWorkOrderFeatureSettingsList();

        //HashMap<String, Boolean> featureSettingValueMap = getWorkOrderFeatureSettingValues();

        //for (V3WorkOrderFeatureSettingsContext workOrderFeatureSettingsContext: workOrderFeatureSettingsContexts){
        //    featureSettingValueMap.put(workOrderFeatureSettingsContext.getSettingTypeEnum().name(),
        //            true);
        //}

        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, workOrderFeatureSettingsContexts);
        //context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_VALUES_MAP, featureSettingValueMap);

        return false;
    }

    /**
     * getWorkOrderFeatureSettingValues() creates a map `featureSettingValueMap` with all values in WorkOrderFeatureSettingType
     * @return featureSettingValueMap
     */
    private HashMap<String, Boolean> getWorkOrderFeatureSettingValues(){
        HashMap<String, Boolean> featureSettingValueMap = new HashMap<>();
        for(WorkOrderFeatureSettingType settingType: WorkOrderFeatureSettingType.values()){
            featureSettingValueMap.put(settingType.name(), false);
        }
        return featureSettingValueMap;
    }
}
