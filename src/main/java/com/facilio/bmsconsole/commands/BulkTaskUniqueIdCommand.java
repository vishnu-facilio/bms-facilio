package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;

public class BulkTaskUniqueIdCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(BulkTaskUniqueIdCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenanceAPI.logIf(92L, "Entering BulkTaskUniqueIdCommand");
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

        if (bulkWorkOrderContext.getTaskMaps() == null || bulkWorkOrderContext.getTaskMaps().isEmpty()) {
            return false;
        }

        List<Map<String, List<TaskContext>>> taskMaps = bulkWorkOrderContext.getTaskMaps();
        if (taskMaps == null || taskMaps.isEmpty()) {
            return false;
        }

       for (int i = 0; i < taskMaps.size(); i++) {
           int maxUniqueId = 0;
           if (taskMaps.get(i) == null) {
               continue;
           }
           for (List<TaskContext> values: taskMaps.get(i).values()) {
              for (int j = 0; j < values.size(); j++) {
                  values.get(j).setUniqueId(++maxUniqueId);
              }
           }

           Map<String, Boolean> countMap = new HashMap<>();
       }
        PreventiveMaintenanceAPI.logIf(92L, "done BulkTaskUniqueIdCommand");
       return false;
    }
}
