package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToolReservationValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedToolsContext> workOrderPlannedTools = recordMap.get(moduleName);
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        Map<Long, V3WorkOrderContext> workOrderMap = new HashMap<>();
        V3WorkOrderContext wo = new V3WorkOrderContext();
        if (CollectionUtils.isNotEmpty(workOrderPlannedTools) && MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("reserveValidation")) {
            Map<Long,Double> toolMap = new HashMap<>();
            for (WorkOrderPlannedToolsContext workOrderPlannedTool : workOrderPlannedTools) {
                workOrderPlannedTool.setReservationType(ReservationType.SOFT.getIndex());
                Long toolTypeId = workOrderPlannedTool.getToolType().getId();
                Long storeRoomId = workOrderPlannedTool.getStoreRoom() !=null ? workOrderPlannedTool.getStoreRoom().getId() :null;
                if(storeRoomId!=null) {
                    V3ToolContext tool = V3InventoryUtil.getToolWithStoreroomServingSites(toolTypeId,storeRoomId);
                    if (tool == null) {
                        workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                        workOrderPlannedTool.setDatum("errorMessage", "Tool not available in Storeroom");
                        workOrderPlannedTool.setDatum("availableQuantity", null);
                    } else {
                        // checking if storeroom servers the work order's site
                        if(workOrderPlannedTool.getWorkOrder()!=null){
                            Long workOrderId = workOrderPlannedTool.getWorkOrder().getId();
                            List<Long> servingSiteIds = tool.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
                            if(workOrderMap.get(workOrderId)==null){
                                wo = V3InventoryUtil.getWorkOrder(workOrderId);
                                workOrderMap.put(workOrderId,wo);
                            }else{
                                wo = workOrderMap.get(workOrderId);
                            }
                            if(wo.getSiteId()>0 && !servingSiteIds.contains(wo.getSiteId())){
                                workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                                workOrderPlannedTool.setDatum("errorMessage", "Storeroom does not serve the selected work order's site");
                            }
                        }
                        Long toolId = tool.getId();
                        Double availableQuantity = tool.getCurrentQuantity() != null ? tool.getCurrentQuantity() : 0.0;
                        if(toolMap.containsKey(toolId)){
                            availableQuantity = toolMap.get(toolId);
                        }
                        workOrderPlannedTool.setDatum("availableQuantity", availableQuantity);
                        // Quantity Validation
                        if (workOrderPlannedTool.getQuantity() == null || workOrderPlannedTool.getQuantity() == 0) {
                            workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                            workOrderPlannedTool.setDatum("errorMessage", "Reserve Quantity is empty");
                        }
                        else{
                            toolMap.put(toolId,availableQuantity - workOrderPlannedTool.getQuantity());
                            if (availableQuantity < workOrderPlannedTool.getQuantity() && workOrderPlannedTool.getReservationTypeEnum().equals(ReservationType.HARD)) {
                                workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                                workOrderPlannedTool.setDatum("errorMessage", "Hard Reserve quantity is more than available quantity");
                            } else if (availableQuantity < workOrderPlannedTool.getQuantity() && workOrderPlannedTool.getReservationTypeEnum().equals(ReservationType.SOFT)) {
                                workOrderPlannedTool.setDatum("errorType", "Reservable");
                                workOrderPlannedTool.setDatum("errorMessage", "Reserved quantity is greater than Available quantity");
                            }
                        }

                    }
                    //reservation type validation
                    if(workOrderPlannedTool.getReservationTypeEnum()==null || workOrderPlannedTool.getReservationType()<=0){
                        workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                        workOrderPlannedTool.setDatum("errorMessage", "Reservation Type is null");
                    }
                }
                else{
                    workOrderPlannedTool.setDatum("errorType", "Non-reservable");
                    workOrderPlannedTool.setDatum("errorMessage", "Storeroom is not defined");
                    workOrderPlannedTool.setDatum("availableQuantity", null);
                }
            }
        }

        return false;
    }
}
