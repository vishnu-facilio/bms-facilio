package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateInventoryRequestLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		InventoryRequestLineItemContext lineItemContext = (InventoryRequestLineItemContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (lineItemContext != null) {
			if (lineItemContext.getInventoryRequestId() == -1) {
				throw new Exception("Inventory Request cannot be null");
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			if (lineItemContext.getId() > 0) {
				updateRecord(lineItemContext, module, fields);
			} else {
				addRecord(lineItemContext, module, fields);
			}
		}
		return false;
	}

	private void updateRecord(InventoryRequestLineItemContext lineItemContext, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<InventoryRequestLineItemContext> updateBuilder = new UpdateRecordBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemContext.getId(), module));
		updateBuilder.update(lineItemContext);
	}

	private void addRecord(InventoryRequestLineItemContext lineItemContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder<InventoryRequestLineItemContext> insertBuilder = new InsertRecordBuilder<InventoryRequestLineItemContext>()
				.module(module)
				.fields(fields);
		insertBuilder.addRecord(lineItemContext);
		insertBuilder.save();		
	}
	

}
