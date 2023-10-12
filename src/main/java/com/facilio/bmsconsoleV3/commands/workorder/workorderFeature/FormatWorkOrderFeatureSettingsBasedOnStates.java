package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FormatWorkOrderFeatureSettingsBasedOnStates formats the WorkOrder Feature Settings as a map of states, with list of feature settings configured.
 * {
 *     "state_id_1": [
 *              {
 *                  "id": 1,
 *                  "isAllowed": true,
 *"                 "settingType": 1,
 *                  "settingTypeEnum": "PLANNING",
 *              },
 *              {
 *                  "id": 2,
 *                  "isAllowed": true,
 *                  "settingType": 2,
 *                  "settingTypeEnum": "EXECUTION",
 *              },
 *              {
 *                  "id": 3,
 *                  "isAllowed": true,
 *                  "settingType": 3,
 *                  "settingTypeEnum": "ACTUALS",
 *              },
 *     ],
 *     "state_id_2": [
 *              {
 *                  "id": 4,
 *                  "isAllowed": true,
 *                  "settingType": 2,
 *                  "settingTypeEnum": "EXECUTION",
 *              },
 *     ]
 * }
 */
@Log4j
public class FormatWorkOrderFeatureSettingsBasedOnStates extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("FormatWorkOrderFeatureSettingsBasedOnStates:");
        List<Map<String, Object>> workOrderFeatureSettingsMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP);

        if (CollectionUtils.isEmpty(workOrderFeatureSettingsMap)) {
            LOGGER.info("workOrderFeatureSettingsMap is empty.");
            return false;
        }

        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsList = FieldUtil.getAsBeanListFromMapList(workOrderFeatureSettingsMap, V3WorkOrderFeatureSettingsContext.class);
        Map<Long, List<V3WorkOrderFeatureSettingsContext>> workOrderStatesFeatureSettingsMap = new HashMap<>();

        for (V3WorkOrderFeatureSettingsContext settingsContext : workOrderFeatureSettingsList) {
            settingsContext.setIsAllowed(true);

            if (!workOrderStatesFeatureSettingsMap.containsKey(settingsContext.getAllowedTicketStatusId())) {
                workOrderStatesFeatureSettingsMap.put(settingsContext.getAllowedTicketStatusId(), new ArrayList<>());
            }
            ArrayList<V3WorkOrderFeatureSettingsContext> stateSettings = (ArrayList<V3WorkOrderFeatureSettingsContext>) workOrderStatesFeatureSettingsMap.get(settingsContext.getAllowedTicketStatusId());
            stateSettings.add(settingsContext);
            workOrderStatesFeatureSettingsMap.put(settingsContext.getAllowedTicketStatusId(), stateSettings);
        }
        context.put(FacilioConstants.ContextNames.WORK_ORDER_STATES_FEATURE_SETTINGS, workOrderStatesFeatureSettingsMap);
        return false;
    }
}
