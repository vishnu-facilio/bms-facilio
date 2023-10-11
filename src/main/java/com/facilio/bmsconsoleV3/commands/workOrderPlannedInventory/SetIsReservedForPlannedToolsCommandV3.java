package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;


public class SetIsReservedForPlannedToolsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if(CollectionUtils.isNotEmpty(workOrderPlannedTools) && MapUtils.isNotEmpty(bodyParams)){
            if(bodyParams.containsKey("reserve")){
                for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools){
                    workOrderPlannedTool.setIsReserved(true);
                }
            }
            if(bodyParams.containsKey("unreserve")){
                for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools){
                    workOrderPlannedTool.setIsReserved(false);
                }
            }
        }
        return false;
    }

}
