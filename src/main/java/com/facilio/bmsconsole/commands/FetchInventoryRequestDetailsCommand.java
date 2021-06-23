package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchInventoryRequestDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (inventoryRequestContext != null) {
			WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(inventoryRequestContext.getParentId());
			if(workOrder != null) {
				inventoryRequestContext.setWorkOrderLocalId(workOrder.getLocalId());
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			
			List<InventoryRequestLineItemContext> list = InventoryRequestAPI.getLineItemsForInventoryRequest(String.valueOf(inventoryRequestContext.getId()), null, null);
			inventoryRequestContext.setLineItems(list);
		}
		return false;
	}

}
