package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdatePurchaseContractCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseContractContext purchaseContractContext = (PurchaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);

		if (purchaseContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
		
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS_LINE_ITEMS);
			FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
			
//			if (CollectionUtils.isEmpty(purchaseContractContext.getLineItems())) {
//				throw new Exception("Line items cannot be empty");
//			}
			if (purchaseContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			purchaseContractContext.setContractType(ContractType.PURCHASE);
			if (!isContractRevised && purchaseContractContext.getId() > 0) {
				ContractsAPI.updateRecord(purchaseContractContext, module, fields);
				
				DeleteRecordBuilder<PurchaseContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("PURCHASE_CONTRACT", "purchaseContractId", String.valueOf(purchaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				DeleteRecordBuilder<ContractAssociatedTermsContext> deleteTermsBuilder = new DeleteRecordBuilder<ContractAssociatedTermsContext>()
						.module(termsModule)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(purchaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(purchaseContractContext);
				ContractsAPI.updateTermsAssociated(purchaseContractContext);
				
				ContractsAPI.addRecord(false,purchaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, purchaseContractContext);
				

			}
			else if (isContractRevised && purchaseContractContext.getId() > 0) {
				if(purchaseContractContext.getStatusEnum() == Status.APPROVED) {
					PurchaseContractContext revisedContract =  purchaseContractContext.clone();
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields);
					purchaseContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(purchaseContractContext, module, fields);
					updateLineItems(revisedContract);
					ContractsAPI.updateTermsAssociated(revisedContract);
					revisedContract.setStatus(Status.PENDING_FOR_REVISION);
					ContractsAPI.addRecord(false,revisedContract.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
					context.put(FacilioConstants.ContextNames.REVISED_RECORD, revisedContract);
					context.put(FacilioConstants.ContextNames.RECORD, revisedContract);
					
					
				}
				else {
					throw new IllegalArgumentException("Only Approved contracts can be revised");
				}

			}
			else {
				purchaseContractContext.setStatus(PurchaseContractContext.Status.WAITING_FOR_APPROVAL);
				purchaseContractContext.setRevisionNumber(0);
				ContractsAPI.addRecord(true,Collections.singletonList(purchaseContractContext), module, fields);
				purchaseContractContext.setParentId(purchaseContractContext.getLocalId());
				ContractsAPI.updateRecord(purchaseContractContext, module, fields);
				updateLineItems(purchaseContractContext);
				ContractsAPI.updateTermsAssociated(purchaseContractContext);
				
				ContractsAPI.addRecord(false,purchaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, purchaseContractContext);
				
			}

			
		}
		return false;
	}
	
		private void updateLineItems(PurchaseContractContext purchasecontractContext) {
		if(CollectionUtils.isNotEmpty(purchasecontractContext.getLineItems())) {
			for (PurchaseContractLineItemContext lineItemContext : purchasecontractContext.getLineItems()) {
				lineItemContext.setPurchaseContractId(purchasecontractContext.getId());
				updateLineItemCost(lineItemContext);
			}
		}
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