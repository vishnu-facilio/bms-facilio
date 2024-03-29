package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdatePurchaseRequestLineItemCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<PurchaseRequestLineItemContext> lineItemContext = (List<PurchaseRequestLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if (CollectionUtils.isNotEmpty(lineItemContext)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			for(PurchaseRequestLineItemContext lineItem : lineItemContext) {
				if (lineItem.getPrid() == -1) {
					throw new Exception("Purchase Request cannot be null");
				}
				updateLineItemCost(lineItem);
				
				if (lineItem.getId() > 0) {
					RecordAPI.updateRecord(lineItem, module, fields);
				} else {
					RecordAPI.addRecord(false,Collections.singletonList(lineItem), module, fields);
				}
			}
		}
		return false;
	}

	
	private void updateLineItemCost(PurchaseRequestLineItemContext lineItemContext) throws Exception {
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
	

}
