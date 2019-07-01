package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class AddOrUpdateInventoryRequestLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<InventoryRequestLineItemContext> lineItems = (List<InventoryRequestLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(lineItems)) {
			for(InventoryRequestLineItemContext lineItemContext : lineItems) {
				if (lineItemContext.getInventoryRequestId() == -1) {
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
