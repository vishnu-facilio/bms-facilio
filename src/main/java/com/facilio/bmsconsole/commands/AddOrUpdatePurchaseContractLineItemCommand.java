package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdatePurchaseContractLineItemCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<PurchaseContractLineItemContext> lineItemContexts = (List<PurchaseContractLineItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(lineItemContexts)) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			for(PurchaseContractLineItemContext lineItemContext : lineItemContexts) {
				if (lineItemContext.getPurchaseContractId() == -1) {
					throw new Exception("Purchase Contract cannot be null");
				}
				if (lineItemContext.getId() > 0) {
					updateRecord(lineItemContext, module, fields);
				} else {
					addRecord(lineItemContext, module, fields);
				}
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
