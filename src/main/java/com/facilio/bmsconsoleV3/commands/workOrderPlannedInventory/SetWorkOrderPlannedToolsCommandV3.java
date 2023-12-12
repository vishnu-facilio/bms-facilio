package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;


import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedToolsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);

        if(CollectionUtils.isNotEmpty(workOrderPlannedTools)){
            for(WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools){
                workOrderPlannedTool.setReservationType(ReservationType.SOFT.getIndex());
                if(workOrderPlannedTool.getDescription()==null){
                    V3ToolTypesContext toolType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TOOL_TYPES,workOrderPlannedTool.getToolType().getId(),V3ToolTypesContext.class);
                    if(toolType.getDescription()!=null){
                        String description = toolType.getDescription();
                        workOrderPlannedTool.setDescription(description);
                    }
                }
                //not yet tested
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && workOrderPlannedTool.getRate()==null){
                    V3ToolContext tool = V3ToolsApi.getTool(workOrderPlannedTool.getToolType().getId(),workOrderPlannedTool.getStoreRoom().getId());
                    if (tool == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tool is not present in the given storeroom");
                    }
                    List<V3PurchasedToolContext> purchasedTools = V3InventoryUtil.getPurchasedToolsBasedOnCostType(tool,null,true);
                    if (CollectionUtils.isNotEmpty(purchasedTools)){
                        workOrderPlannedTool.setRate(purchasedTools.get(0).getRate());
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
                    Double totalCost = workOrderPlannedTool.getRate() * workOrderPlannedTool.getQuantity() * (workOrderPlannedTool.getDuration() / 3600);
                    workOrderPlannedTool.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
