package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.util.V3InventoryAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class addPurchasedToolCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3ToolContext> toolsList =  (List<V3ToolContext>) context.get(FacilioConstants.ContextNames.TOOLS);
        if(CollectionUtils.isEmpty(toolsList)){
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            toolsList = recordMap.get(moduleName);
        }
        if(CollectionUtils.isEmpty(toolsList)){
            return false;
        }
        List<Long> toolIds = new ArrayList<>();
        List<Long> toolTypeIds = toolsList.stream().map(a -> a.getToolType().getId()).collect(Collectors.toList());
        List<V3ToolTypesContext> toolTypes = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.TOOL_TYPES, toolTypeIds, V3ToolTypesContext.class);
        Map<Long, V3ToolTypesContext> toolTypesMap = toolTypes.stream().collect(Collectors.toMap(V3ToolTypesContext::getId, Function.identity()));
        List<V3PurchasedToolContext> purchasedToolsToAdd = new ArrayList<>();
        for (V3ToolContext tool: toolsList) {
            toolIds.add(tool.getId());
            List<V3PurchasedToolContext> purchasedTools = tool.getPurchasedTools();
            if(CollectionUtils.isEmpty(purchasedTools)){
                continue;
            }
            V3ToolTypesContext toolType = toolTypesMap.get(tool.getToolType().getId());
            if(toolType == null){
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tool type not found");
            }
            tool.setToolType(toolType);
            for (V3PurchasedToolContext purchasedTool:purchasedTools) {
                purchasedTool.setToolType(toolType);
                purchasedTool.setTool(tool);
                double quantity = purchasedTool.getQuantity();
                if(toolType.isRotating()){
                    if (quantity > 0) {
                        throw new IllegalArgumentException(
                                "Quantity cannot be set when individual Tracking is enabled");
                    }
                    purchasedTool.setQuantity(quantity);
                    purchasedTool.setCurrentQuantity(quantity);
                } else {
                    if (quantity <= 0) {
                        throw new IllegalArgumentException("Quantity cannot be null");
                    }
                    purchasedTool.setCurrentQuantity(quantity);
                }
                purchasedTool.setCostDate(System.currentTimeMillis());
                purchasedToolsToAdd.add(purchasedTool);
            }
            tool.setLastPurchasedDate(System.currentTimeMillis());
            tool.setPurchasedTools(null);
        }
        List<V3PurchasedToolContext> purchasedTools = V3InventoryAPI.addPurchasedTool(purchasedToolsToAdd);
        context.put(FacilioConstants.ContextNames.PURCHASED_TOOL,purchasedTools);
        context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
        context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypeIds);
        context.put(FacilioConstants.ContextNames.STORE_ROOM, toolsList.get(0).getStoreRoom().getId());

        return false;
    }
}
