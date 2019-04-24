package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdatePurchaseContractCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseContractContext purchaseContractContext = (PurchaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
		
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(purchaseContractContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			if (purchaseContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			purchaseContractContext.setContractType(ContractType.PURCHASE);
			if (purchaseContractContext.getId() > 0) {
				updateRecord(purchaseContractContext, module, fields);
				
				DeleteRecordBuilder<PurchaseContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("PURCHASE_CONTRACT", "purchaseContractId", String.valueOf(purchaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				
				purchaseContractContext.setStatus(PurchaseContractContext.Status.WAITING_FOR_APPROVAL);
				addRecord(true,Collections.singletonList(purchaseContractContext), module, fields);
			}
			
			updateLineItems(purchaseContractContext);
			addRecord(false,purchaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseContractContext);
		}
		return false;
	}
	
		private void updateLineItems(PurchaseContractContext purchasecontractContext) {
		for (PurchaseContractLineItemContext lineItemContext : purchasecontractContext.getLineItems()) {
			lineItemContext.setPurchaseContractId(purchasecontractContext.getId());
			updateLineItemCost(lineItemContext);
		}
	}
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	private void updateLineItemCost(PurchaseContractLineItemContext lineItemContext){
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
}