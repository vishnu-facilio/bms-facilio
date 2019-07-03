package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdatePurchaseOrderLineItemCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<PurchaseOrderLineItemContext> lineItems = (List<PurchaseOrderLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(lineItems)) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
		
			for(PurchaseOrderLineItemContext lineItemContext : lineItems) {
				if (lineItemContext.getPoId() == -1) {
					throw new Exception("Purchase Order cannot be null");
				}
				updateLineItemCost(lineItemContext);
				
				if (lineItemContext.getId() > 0) {
					RecordAPI.updateRecord(lineItemContext, module, fields);
				} else {
					RecordAPI.addRecord(false, Collections.singletonList(lineItemContext), module, fields);
				}
			}
		}
		return false;
	}

	
	private void updateLineItemCost(PurchaseOrderLineItemContext lineItemContext) throws Exception {
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
	


}

