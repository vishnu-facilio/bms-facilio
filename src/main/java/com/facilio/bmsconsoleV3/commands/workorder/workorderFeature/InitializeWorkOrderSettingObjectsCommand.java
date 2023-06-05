package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

/**
 * InitializeWorkOrderSettingObjectsCommand is just a helper class to initialize objects before-hand
 * that's required in the WorkOrderAction.featureSettings() chain.
 */
public class InitializeWorkOrderSettingObjectsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Add workorder settings object to the chain initially.
        WorkOrderSettings workOrderSettings = new WorkOrderSettings();
        context.put(FacilioConstants.ContextNames.WORK_ORDER_SETTINGS, workOrderSettings);

        return false;
    }
}
