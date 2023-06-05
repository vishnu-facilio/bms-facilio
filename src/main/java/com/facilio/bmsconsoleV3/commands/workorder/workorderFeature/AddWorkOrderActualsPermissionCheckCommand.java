package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.V3WorkOrderFeatureSettingsContext;
import com.facilio.bmsconsoleV3.context.workorder.setup.WorkOrderFeatureSettingType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * AddWorkOrderActualsPermissionCheckCommand
 * Set WorkOrderSettings.INVENTORY_ACTUALS bool value based on,
 * - record lock status,
 * - Work Order Actuals Feature Setting
 */
public class AddWorkOrderActualsPermissionCheckCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3WorkOrderContext workOrderContext = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
        FacilioStatus currentModuleState = (FacilioStatus) context.get(FacilioConstants.ContextNames.CURRENT_MODULE_STATE);
        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsContexts = (List<V3WorkOrderFeatureSettingsContext>)
                context.getOrDefault(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, new ArrayList<>());

        Boolean isLockedState = currentModuleState.isRecordLocked();
        Boolean isWorkOrderActualsFeatureSettingEnabledForCurrentState = false;

        for (V3WorkOrderFeatureSettingsContext featureSettingsContext : workOrderFeatureSettingsContexts) {
            if (featureSettingsContext.getSettingType() == WorkOrderFeatureSettingType.ACTUALS.getVal()) {
                isWorkOrderActualsFeatureSettingEnabledForCurrentState = true;
                break;
            }
        }

        boolean canDoActionsOnActuals = !isLockedState && isWorkOrderActualsFeatureSettingEnabledForCurrentState;

        //overriding, for main app
        if (AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
            canDoActionsOnActuals = true;
        }

        WorkOrderSettings workOrderSettings = (WorkOrderSettings) context.get(FacilioConstants.ContextNames.WORK_ORDER_SETTINGS);

        WorkOrderFeatureSetting inventoryActualsFeatureSetting = new WorkOrderFeatureSetting();
        inventoryActualsFeatureSetting.setAllowed(canDoActionsOnActuals);

        workOrderSettings.setInventoryActuals(inventoryActualsFeatureSetting);

        return false;
    }
}
