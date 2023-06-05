package com.facilio.bmsconsoleV3.signup.workOrder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.actions.WorkOrderModuleSettingAction;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.WorkOrderFeatureSettingType;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddWorkOrderFeatureSettingsDefaultValues
 * addData() method adds the WorkOrder Feature Setting default values, of the Setting Types [PLANNING, EXECUTION, ACTUALS],
 * for various status of workorder.
 * TODO: Add/Remove Status of WorkOrder from statusNameList, to add/remove from status from each setting.
 */
@Log4j
public class AddWorkOrderFeatureSettingsDefaultValues extends SignUpData {
    @Override
    public void addData() throws Exception {
        LOGGER.info("AddWorkOrderFeatureSettingsDefaultValues");
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule workOrderModule = moduleBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioStatus> ticketStatusList = TicketAPI.getAllStatus(workOrderModule, false);
        Map<String, FacilioStatus> ticketStatusMap = new HashMap<>();
        for (FacilioStatus status : ticketStatusList) {
            ticketStatusMap.put(status.getStatus(), status);
        }

        // Add default Feature Setting values for WorkOrder Planning Setting
        List<String> statusNameList = new ArrayList<>();
        statusNameList.add("Processing");
        statusNameList.add("Requested");
        statusNameList.add("Submitted");
        statusNameList.add("Assigned");
        statusNameList.add("Work in Progress");
        statusNameList.add("On Hold");
        List<V3WorkOrderFeatureSettingsContext> executionSettingList = getDefaultFeatureSettingValuesForSetting(WorkOrderFeatureSettingType.PLANNING, ticketStatusMap, statusNameList);
        List<V3WorkOrderFeatureSettingsContext> featureSettingsContextsList = new ArrayList<>(executionSettingList);

        // Add default Feature Setting values for Task Execution Setting
        statusNameList = new ArrayList<>();
        statusNameList.add("Assigned");
        statusNameList.add("Work in Progress");
        List<V3WorkOrderFeatureSettingsContext> planningSettingList = getDefaultFeatureSettingValuesForSetting(WorkOrderFeatureSettingType.EXECUTION, ticketStatusMap, statusNameList);
        featureSettingsContextsList.addAll(planningSettingList);

        // Add default Feature Setting values for WorkOrder Actuals Setting
        statusNameList = new ArrayList<>();
        statusNameList.add("Assigned");
        statusNameList.add("Work in Progress");
        statusNameList.add("Resolved");
        List<V3WorkOrderFeatureSettingsContext> actualsSettingList = getDefaultFeatureSettingValuesForSetting(WorkOrderFeatureSettingType.ACTUALS, ticketStatusMap, statusNameList);
        featureSettingsContextsList.addAll(actualsSettingList);

        LOGGER.info("Adding " + featureSettingsContextsList.size() + " workorder feature setting.");

        addWorkOrderFeatureSettings(featureSettingsContextsList);
    }

    /**
     * Helper function to get the list of V3WorkOrderFeatureSettingsContext based on the @param statusNameList.
     * @param settingType
     * @param ticketStatusMap
     * @param statusNameList
     * @return
     */
    private List<V3WorkOrderFeatureSettingsContext> getDefaultFeatureSettingValuesForSetting(WorkOrderFeatureSettingType settingType, Map<String, FacilioStatus> ticketStatusMap, List<String> statusNameList) {
        List<V3WorkOrderFeatureSettingsContext> featureSettingsContexts = new ArrayList<>();

        for (String statusName : statusNameList) {
            if (ticketStatusMap.get(statusName) != null) {
                FacilioStatus status = ticketStatusMap.get(statusName);
                V3WorkOrderFeatureSettingsContext settingsContext = new V3WorkOrderFeatureSettingsContext();
                settingsContext.setSettingType(settingType.getVal());
                settingsContext.setAllowedTicketStatusId(status.getId());
                settingsContext.setAllowedTicketStatus(status);
                settingsContext.setIsAllowed(true);
                featureSettingsContexts.add(settingsContext);
            }
        }
        return featureSettingsContexts;
    }

    /**
     * Helper function to add the feature settings @param featureSettingsContexts into DB.
     * @param featureSettingsContexts
     * @throws Exception
     */
    private void addWorkOrderFeatureSettings(List<V3WorkOrderFeatureSettingsContext> featureSettingsContexts) throws Exception {
        WorkOrderModuleSettingAction settingAction = new WorkOrderModuleSettingAction();
        settingAction.setWorkOrderFeatureSettings(featureSettingsContexts);
        settingAction.addOrUpdateWorkOrderFeatureSettings();
    }
}

