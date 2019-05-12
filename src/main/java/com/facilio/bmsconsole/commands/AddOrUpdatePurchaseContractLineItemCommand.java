package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdatePurchaseContractLineItemCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseContractLineItemContext lineItemContext = (PurchaseContractLineItemContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (lineItemContext != null) {
			if (lineItemContext.getPurchaseContractId() == -1) {
				throw new Exception("Purchase Contract cannot be null");
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

	private void updateRecord(PurchaseContractLineItemContext lineItemContext, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		updateLineItemCost(lineItemContext);
		UpdateRecordBuilder<PurchaseContractLineItemContext> updateBuilder = new UpdateRecordBuilder<PurchaseContractLineItemContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(lineItemContext.getId(), module));
		updateBuilder.update(lineItemContext);
	}

	private void addRecord(PurchaseContractLineItemContext lineItemContext, FacilioModule module, List<FacilioField> fields) throws Exception {
		updateLineItemCost(lineItemContext);
		InsertRecordBuilder<PurchaseContractLineItemContext> insertBuilder = new InsertRecordBuilder<PurchaseContractLineItemContext>()
				.module(module)
				.fields(fields);
		insertBuilder.addRecord(lineItemContext);
		insertBuilder.save();		
	}
	
	private void updateLineItemCost(PurchaseContractLineItemContext lineItemContext) throws Exception {
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
	
}
