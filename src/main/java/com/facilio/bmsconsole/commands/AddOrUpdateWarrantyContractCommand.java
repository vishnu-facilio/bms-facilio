package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateWarrantyContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		WarrantyContractContext warrantyContractContext = (WarrantyContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);
		
		if (warrantyContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule serviceModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE);
			
			List<FacilioField> serviceFields = modBean.getAllFields(serviceModule.getName());
			FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
			FacilioModule assetAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
			
//			if (CollectionUtils.isEmpty(warrantyContractContext.getLineItems())) {
//				throw new Exception("Line items cannot be empty");
//			}
			if (warrantyContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			warrantyContractContext.setContractType(ContractType.WARRANTY);
			
			if (!isContractRevised && warrantyContractContext.getId() > 0) {
				ContractsAPI.updateRecord(warrantyContractContext, module, fields);
				
				DeleteRecordBuilder<WarrantyContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<WarrantyContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("WARRANTY_CONTRACT", "warrantyContractId", String.valueOf(warrantyContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				
				DeleteRecordBuilder<ContractAssociatedAssetsContext> deleteAssetRelationBuilder = new DeleteRecordBuilder<ContractAssociatedAssetsContext>()
						.module(assetAssociatedModule)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(warrantyContractContext.getId()), NumberOperators.EQUALS));
				deleteAssetRelationBuilder.delete();
				DeleteRecordBuilder<ContractAssociatedTermsContext> deleteTermsBuilder = new DeleteRecordBuilder<ContractAssociatedTermsContext>()
						.module(termsModule)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(warrantyContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(warrantyContractContext);
				ContractsAPI.updateAssetsAssociated(warrantyContractContext);
				ContractsAPI.updateTermsAssociated(warrantyContractContext);
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				ContractsAPI.addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, warrantyContractContext);
				
				
			} 
			else if (isContractRevised && warrantyContractContext.getId() > 0) {
				if(warrantyContractContext.getStatusEnum() == Status.APPROVED) {
					WarrantyContractContext revisedContract = (WarrantyContractContext)warrantyContractContext.clone();
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields);
					warrantyContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(warrantyContractContext, module, fields);
					updateLineItems(revisedContract);
					ContractsAPI.updateAssetsAssociated(revisedContract);
					ContractsAPI.updateTermsAssociated(revisedContract);
					revisedContract.setStatus(Status.PENDING_FOR_REVISION);
					
					//add service if newly added here as lineItem
					//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
					//also add service vendor association
					ContractsAPI.addRecord(false,revisedContract.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
					context.put(FacilioConstants.ContextNames.REVISED_RECORD, revisedContract);
					context.put(FacilioConstants.ContextNames.RECORD, revisedContract);
					
					
				}
				else {
					throw new IllegalArgumentException("Only Approved contracts can be revised");
				}
			}
			else {
				
				warrantyContractContext.setStatus(WarrantyContractContext.Status.WAITING_FOR_APPROVAL);
				warrantyContractContext.setRevisionNumber(0);
				ContractsAPI.addRecord(true,Collections.singletonList(warrantyContractContext), module, fields);
				warrantyContractContext.setParentId(warrantyContractContext.getLocalId());
				ContractsAPI.updateRecord(warrantyContractContext, module, fields);
				updateLineItems(warrantyContractContext);
				ContractsAPI.updateAssetsAssociated(warrantyContractContext);
				ContractsAPI.updateTermsAssociated(warrantyContractContext);
				
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				ContractsAPI.addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, warrantyContractContext);
				
	
			}
			
			
			
		}
		return false;
	}
	
	private void updateLineItems(WarrantyContractContext warrantycontractContext) {
		if(CollectionUtils.isNotEmpty(warrantycontractContext.getLineItems())) {
			for (WarrantyContractLineItemContext lineItemContext : warrantycontractContext.getLineItems()) {
				lineItemContext.setWarrantyContractId(warrantycontractContext.getId());
			}
		}
	  }
		

	private void addServiceRecords(List<WarrantyContractLineItemContext> lineItems,FacilioModule serviceModule, List<FacilioField> serviceFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ServiceContext> newServiceRecords = new ArrayList<ServiceContext>();
		
		for(WarrantyContractLineItemContext lineItem :lineItems ) {
			if(lineItem.getService().getId() == -1) {
			 	lineItem.getService().setStatus(ServiceContext.ServiceStatus.ACTIVE);
				newServiceRecords.add(lineItem.getService());
			}
		}
		if(!CollectionUtils.isEmpty(newServiceRecords)) {
			ContractsAPI.addRecord(true,newServiceRecords, serviceModule, serviceFields);
		}
	}
		
}
