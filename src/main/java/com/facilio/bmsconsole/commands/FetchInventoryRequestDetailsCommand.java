package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchInventoryRequestDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (inventoryRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			
			List<InventoryRequestLineItemContext> list = InventoryRequestAPI.getLineItemsForInventoryRequest(String.valueOf(inventoryRequestContext.getId()), null, null);
			inventoryRequestContext.setLineItems(list);
		}
		return false;
	}

}
