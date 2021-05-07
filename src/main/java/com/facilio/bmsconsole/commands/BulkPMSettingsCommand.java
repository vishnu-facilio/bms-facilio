package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BulkPMSettingsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(BulkPMSettingsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenanceAPI.logIf(92L, "Entering BulkPMSettingsCommand");
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);
        List<WorkOrderContext> workOrderContexts = bulkWorkOrderContext.getWorkOrderContexts();
        Boolean isFromImport = (Boolean) context.getOrDefault(ImportAPI.ImportProcessConstants.IS_FROM_IMPORT, false);

        List<Integer> indices = new ArrayList<>();
        for (int j = 0; j < workOrderContexts.size(); j++) {
            WorkOrderContext workOrderContext = workOrderContexts.get(j);
            if (workOrderContext == null || workOrderContext.getPm() == null) {
                continue;
            }

            Map<String, List<TaskContext>> taskMap = bulkWorkOrderContext.getTaskMaps().get(j);
            if (isFromImport == null || (isFromImport != null && !isFromImport)) {
                if (taskMap == null || taskMap.isEmpty()) {
                    if (workOrderContext.getPm().isPreventOnNoTask()) {
                        indices.add(j);
                    } else {
                        LOGGER.log(Level.SEVERE, "No Task Generated In this workorder PM ID " + workOrderContext.getPm().getId());
                        CommonCommandUtil.emailAlert("No Task Generated In this workorder", "PM ID " + workOrderContext.getPm().getId());
                    }
                }
            }
        }
        bulkWorkOrderContext.removeElements(indices);
        PreventiveMaintenanceAPI.logIf(92L, "Leaving BulkPMSettingsCommand");
        return false;
    }
}
