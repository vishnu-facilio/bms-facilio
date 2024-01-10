package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderModuleSettingContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.*;
import com.facilio.bmsconsoleV3.util.V3WorkOrderModuleSettingAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Log4j
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

    /**
     * WorkOrderFeatureSettings
     */
    private List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettings;
    private Integer settingType;
    private Long allowedStateId;

    public String addOrUpdateWorkOrderFeatureSettings() throws Exception {
        long startTime = System.currentTimeMillis();
        FacilioChain facilioChain = FacilioChain.getTransactionChain();

        facilioChain.getContext().put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, workOrderFeatureSettings);
        if(settingType != null) {
            facilioChain.getContext().put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_TYPE, WorkOrderFeatureSettingType.valueOf(settingType));
        }

        facilioChain.addCommand(new ValidationForWorkOrderFeatureSettingCommand());
        facilioChain.addCommand(new DeleteWorkOrderFeatureSettingsCommand());
        facilioChain.addCommand(new AddOrUpdateWorkOrderFeatureSettingsCommand());
        facilioChain.addCommand(new FillTicketStatusInWorkOrderFeatureSettingsCommand());

        facilioChain.execute();
        List<Map<String, Object>> workOrderFeatureSettingsMap = (List<Map<String, Object>>) facilioChain.getContext().get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP);
        setData("message", "Settings added.");
        setData("workOrderFeatureSettings", workOrderFeatureSettingsMap);

        LOGGER.info("Time take for addOrUpdateWorkOrderFeatureSettings(): " + (System.currentTimeMillis() - startTime));
        return SUCCESS;
    }

    public String fetchWorkOrderFeatureSettings() throws Exception {
        long startTime = System.currentTimeMillis();
        FacilioChain facilioChain = FacilioChain.getTransactionChain();

        if(settingType != null) {
            facilioChain.getContext().put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_TYPE, WorkOrderFeatureSettingType.valueOf(settingType));
        }

        if(allowedStateId != null && allowedStateId > 0){
            facilioChain.getContext().put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_ALLOWED_STATE_ID, allowedStateId);
        }

        facilioChain.addCommand(new FetchWorkOrderFeatureSettingsSetUpCommand());
        facilioChain.addCommand(new FillTicketStatusInWorkOrderFeatureSettingsCommand());
        facilioChain.execute();


        List<Map<String, Object>> workOrderFeatureSettingsMap = (List<Map<String, Object>>) facilioChain.getContext().get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP);
        if(CollectionUtils.isNotEmpty(workOrderFeatureSettingsMap)) {
            setData("workOrderFeatureSettings", workOrderFeatureSettingsMap);
        }else{
            setData("result", "No Settings available.");
        }

        LOGGER.info("Time take for fetchWorkOrderFeatureSettings(): " + (System.currentTimeMillis() - startTime));
        return SUCCESS;
    }

    public List<V3WorkOrderFeatureSettingsContext> fetchWorkOrderFeatureSettingsList() throws Exception {
        List<V3WorkOrderFeatureSettingsContext> settingsContextList = new ArrayList<>();

        String status = fetchWorkOrderFeatureSettings();
        if(Objects.equals(status, SUCCESS)){
            List<Map<String, Object>> settingsListMap = (List<Map<String, Object>>) getData().get("workOrderFeatureSettings");
            if (CollectionUtils.isNotEmpty(settingsListMap)){
                settingsContextList = FieldUtil.getAsBeanListFromMapList(settingsListMap, V3WorkOrderFeatureSettingsContext.class);
            }
        }
        return  settingsContextList;
    }

    public String fetchWorkOrderFeatureSettingsTypes() throws Exception {
        long startTime = System.currentTimeMillis();
        FacilioChain facilioChain = FacilioChain.getTransactionChain();

        List<WorkOrderFeatureSettingType> workOrderFeatureSettingTypes = Arrays.asList(WorkOrderFeatureSettingType.values());
        List<Map<String, Object>> workOrderFeatureSettingTypesMap = workOrderFeatureSettingTypes.stream().map(settingType1 -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("settingType", settingType1.getVal());
            map.put("settingTypeENUM",settingType1.name());
            map.put("displayName",settingType1.getDisplayName());

           return map;
        }).collect(Collectors.toList());
        setData("workOrderFeatureSettingsTypes", workOrderFeatureSettingTypesMap);

        LOGGER.info("Time take for fetchWorkOrderFeatureSettingsTypes(): " + (System.currentTimeMillis() - startTime));
        return SUCCESS;
    }

}
