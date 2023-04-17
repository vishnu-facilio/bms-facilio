package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadPlannedToolsForActualsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = recordMap.get(moduleName);
        Map<String, Object> queryParams = Constants.getQueryParams(context);

        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("actuals") && (CollectionUtils.isNotEmpty(workOrderPlannedTools))) {
            boolean forActuals = FacilioUtil.parseBoolean((Constants.getQueryParam(context, "actuals")));
            if(forActuals){
                List<WorkOrderPlannedToolsContext> validatedWorkOrderPlannedTools = new ArrayList<>();
                for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools) {
                    V3ToolTypesContext toolType = workOrderPlannedTool.getToolType();
                    V3StoreRoomContext storeRoom = workOrderPlannedTool.getStoreRoom();
                    if(toolType != null && storeRoom != null && toolType.getId() > 0 && storeRoom.getId() > 0) {
                        V3ToolContext tool = V3ToolsApi.getTool(toolType.getId(), storeRoom.getId());
                        if (!FacilioUtil.isEmptyOrNull(tool)) {
                            validatedWorkOrderPlannedTools.add(workOrderPlannedTool);
                        }
                    }
                }
                Map<String, List> updatedRecordMap = new HashMap<>();
                updatedRecordMap.put(moduleName, validatedWorkOrderPlannedTools);
                context.put(Constants.RECORD_MAP, updatedRecordMap);
            }
        }
        return false;
    }
}
