package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;


import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedToolsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(workOrderPlannedTools)){
            for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools){
                if(workOrderPlannedTool.getDescription()==null){
                    V3ToolTypesContext toolType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TOOL_TYPES,workOrderPlannedTool.getToolType().getId(),V3ToolTypesContext.class);
                    if(toolType.getDescription()!=null){
                        String description = toolType.getDescription();
                        workOrderPlannedTool.setDescription(description);
                    }
                }
                //number fields validation
                if(workOrderPlannedTool.getQuantity()!=null && workOrderPlannedTool.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(workOrderPlannedTool.getRate()!=null && workOrderPlannedTool.getRate()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid rate");
                }
                if(workOrderPlannedTool.getDuration()!=null && workOrderPlannedTool.getDuration()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid duration");
                }
                if(workOrderPlannedTool.getRate()!=null && workOrderPlannedTool.getQuantity()!=null && workOrderPlannedTool.getDuration()!=null){
                    //total cost computation
                    Double totalCost = workOrderPlannedTool.getRate() * workOrderPlannedTool.getQuantity() * workOrderPlannedTool.getDuration();
                    workOrderPlannedTool.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
