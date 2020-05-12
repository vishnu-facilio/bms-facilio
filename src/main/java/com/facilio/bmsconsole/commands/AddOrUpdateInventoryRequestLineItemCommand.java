package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateInventoryRequestLineItemCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<InventoryRequestLineItemContext> lineItems = (List<InventoryRequestLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(lineItems)) {
			for(InventoryRequestLineItemContext lineItemContext : lineItems) {
				if (lineItemContext.getInventoryRequestId() ==  null) {
					throw new Exception("Inventory Request cannot be null");
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				
				if (lineItemContext.getId() > 0) {
					RecordAPI.updateRecord(lineItemContext, module, fields);
				} else {
					RecordAPI.addRecord(false, Collections.singletonList(lineItemContext), module, fields);
				}
			}
		}
		return false;
	}

	
}
