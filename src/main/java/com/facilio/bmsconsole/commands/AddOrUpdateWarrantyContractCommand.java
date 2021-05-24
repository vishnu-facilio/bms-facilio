package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.modules.fields.SupplementRecord;
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
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateWarrantyContractCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		WarrantyContractContext warrantyContractContext = (WarrantyContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		warrantyContractContext.computeNextPaymentDate();
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);
		
		if (warrantyContractContext != null) {
			if (warrantyContractContext.getVendor() == null || warrantyContractContext.getVendor().getId() <= 0) {
				throw new IllegalArgumentException("Vendor cannot be empty");
			}
			
			if(warrantyContractContext.getFromDate() > 0 && warrantyContractContext.getEndDate() > 0)
				{
					if(warrantyContractContext.getEndDate() <= warrantyContractContext.getFromDate())
					{
						throw new IllegalArgumentException("Contract End Date should be greater than From Date");
					}
				}
			
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
//			if (warrantyContractContext.getVendor() == null) {
//				throw new Exception("Vendor cannot be empty");
//			}
			warrantyContractContext.setContractType(ContractType.WARRANTY);
			
			if (!isContractRevised && warrantyContractContext.getId() > 0) {
				//custom fields multi lookup handling
				List<SupplementRecord> supplements = new ArrayList<>();
				CommonCommandUtil.handleFormDataAndSupplement(fields, warrantyContractContext.getData(), supplements);
				ContractsAPI.updateRecord(warrantyContractContext, module, fields, true, (FacilioContext) context, supplements);
				
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
				deleteTermsBuilder.delete();
				updateLineItems(warrantyContractContext);
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				ContractsAPI.addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, warrantyContractContext);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
				
				
				
			} 
			else if (isContractRevised && warrantyContractContext.getId() > 0) {
				if(warrantyContractContext.getStatusEnum() == Status.APPROVED) {
					WarrantyContractContext revisedContract = warrantyContractContext.clone();
					//custom fields multi lookup handling
					List<SupplementRecord> supplements = new ArrayList<>();
					CommonCommandUtil.handleFormDataAndSupplement(fields, revisedContract.getData(), supplements);
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields, supplements);

					//custom fields multi lookup handling
					List<SupplementRecord> updateSupplements = new ArrayList<>();
					CommonCommandUtil.handleFormDataAndSupplement(fields, warrantyContractContext.getData(), updateSupplements);
					warrantyContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(warrantyContractContext, module, fields, true, (FacilioContext) context, updateSupplements);
					updateLineItems(revisedContract);
					ContractsAPI.updateAssetsAssociated(revisedContract.getId(), revisedContract.getAssociatedAssets());
					ContractsAPI.updateTermsAssociated(revisedContract.getId(), revisedContract.getTermsAssociated());
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
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
				
			}
			else {
				
				warrantyContractContext.setStatus(WarrantyContractContext.Status.WAITING_FOR_APPROVAL);
				warrantyContractContext.setRevisionNumber(0);

				//custom fields multi lookup handling
				List<SupplementRecord> supplements = new ArrayList<>();
				CommonCommandUtil.handleFormDataAndSupplement(fields, warrantyContractContext.getData(), supplements);
				ContractsAPI.addRecord(true,Collections.singletonList(warrantyContractContext), module, fields);
				warrantyContractContext.setParentId(warrantyContractContext.getLocalId());
				ContractsAPI.updateRecord(warrantyContractContext, module, fields, true, (FacilioContext) context);
				updateLineItems(warrantyContractContext);
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				ContractsAPI.addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, warrantyContractContext);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
				
	
			}
			
			
			context.put(FacilioConstants.ContextNames.RECORD_ID, warrantyContractContext.getId());
			
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
