package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlannedToolsUnSavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String toolTypes = (String) context.get(FacilioConstants.ContextNames.TOOL_TYPES);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(toolTypes!=null && workOrderId!=null){
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            List<Long> toolTypeIdsArray = Arrays.asList(toolTypes.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

            List<WorkOrderPlannedToolsContext> workOrderPlannedTools = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(toolTypeIdsArray)){
                List<V3ToolTypesContext> toolTypeRecords = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.TOOL_TYPES,toolTypeIdsArray,V3ToolTypesContext.class);
                for(V3ToolTypesContext toolTypeRecord : toolTypeRecords){
                    WorkOrderPlannedToolsContext workOrderPlannedTool = new WorkOrderPlannedToolsContext();

                    V3ToolTypesContext toolType = new V3ToolTypesContext();
                    toolType.setId(toolTypeRecord.getId());
                    toolType.setName(toolTypeRecord.getName());

                    workOrderPlannedTool.setToolType(toolType);

                    if(toolTypeRecord.getDescription()!=null){
                        workOrderPlannedTool.setDescription(toolTypeRecord.getDescription());
                    }
                    workOrderPlannedTool.setWorkOrder(workOrder);
                    workOrderPlannedTool.setQuantity(1.0);
                    workOrderPlannedTools.add(workOrderPlannedTool);
                }
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WO_PLANNED_TOOLS,workOrderPlannedTools);
            context.put(FacilioConstants.ContextNames.WO_PLANNED_TOOLS,workOrderPlannedTools);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WO_PLANNED_TOOLS);
        }
        return false;
    }
}
