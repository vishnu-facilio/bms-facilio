package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PMSettingsCommandV3 extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);

        if(CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext wo = wos.get(0);

            if (wo == null || wo.getPm() == null) {
                return false;
            }

            if (wo.getPm().isPreventOnNoTask() && wo.getDeviationTaskUniqueId() == null) {
                Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
                if (taskMap == null || taskMap.isEmpty()) {
                    LOGGER.log(Level.SEVERE, "Skipping because of no task " + "PM ID " + wo.getPm().getId());
                    return true;
                }
            } else {
                Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
                if (taskMap == null || taskMap.isEmpty()) {
                    LOGGER.log(Level.SEVERE, "No Task Generated In this workorder", "PM ID " + wo.getPm().getId());
                    CommonCommandUtil.emailAlert("No Task Generated In this workorder", "PM ID " + wo.getPm().getId());
                }
            }
        }
        return false;
    }
}
