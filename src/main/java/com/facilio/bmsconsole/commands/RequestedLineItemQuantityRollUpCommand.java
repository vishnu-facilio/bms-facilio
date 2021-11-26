package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class RequestedLineItemQuantityRollUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
        List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        Integer costType = (Integer)context.get(FacilioConstants.ContextNames.WORKORDER_COST_TYPE);
        for(Long id : recordIds) {
        	if(costType == 1) {
        		WorkorderItemContext woItem = WorkOrderAPI.getWorkOrderItem(id);
        		if(woItem.getRequestedLineItem() != null && woItem.getRequestedLineItem().getId() > 0) {
	        		V3InventoryRequestLineItemContext woLineItem = V3InventoryRequestAPI.getLineItem(woItem.getRequestedLineItem().getId());
	        		V3InventoryRequestAPI.updateRequestUsedQuantity(woItem.getRequestedLineItem(), woLineItem.getUsedQuantity() - woItem.getQuantity());
        		}
        	}
//        	else if(costType == 2) {
//        		WorkorderToolsContext woTool = WorkOrderAPI.getWorkOrderTool(id);
//        		InventoryRequestAPI.updateRequestUsedQuantity(woTool.getRequestedLineItem(), 0);
//        	}
        }
		
		return false;
	}
	
	

}