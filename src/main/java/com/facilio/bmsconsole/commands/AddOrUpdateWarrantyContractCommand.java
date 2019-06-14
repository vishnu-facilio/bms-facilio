package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;

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
			
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(warrantyContractContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			if (warrantyContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			warrantyContractContext.setContractType(ContractType.WARRANTY);
			
			if (!isContractRevised && warrantyContractContext.getId() > 0) {
				updateRecord(warrantyContractContext, module, fields);
				
				DeleteRecordBuilder<WarrantyContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<WarrantyContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("WARRANTY_CONTRACT", "warrantyContractId", String.valueOf(warrantyContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				
				GenericDeleteRecordBuilder deleteAssetRelationBuilder = new GenericDeleteRecordBuilder()
						.table(FacilioConstants.ContextNames.CONTRACT_ASSET_RELATION)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(warrantyContractContext.getId()), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
						;
				deleteAssetRelationBuilder.delete();
				updateLineItems(warrantyContractContext);
				updateAssetsAssociated(warrantyContractContext);
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				
			} 
			else if (isContractRevised && warrantyContractContext.getId() > 0) {
				if(warrantyContractContext.getStatusEnum() == Status.APPROVED) {
					WarrantyContractContext revisedContract = (WarrantyContractContext)warrantyContractContext.clone();
					addRecord(true,Collections.singletonList(revisedContract), module, fields);
					warrantyContractContext.setStatus(Status.REVISED);
					updateRecord(warrantyContractContext, module, fields);
					updateLineItems(revisedContract);
					updateAssetsAssociated(revisedContract);
					//add service if newly added here as lineItem
					//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
					//also add service vendor association
					addRecord(false,revisedContract.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
					context.put(FacilioConstants.ContextNames.REVISED_RECORD, revisedContract);
					
				}
				else {
					throw new IllegalArgumentException("Only Approved contracts can be revised");
				}
			}
			else {
				
				warrantyContractContext.setStatus(WarrantyContractContext.Status.WAITING_FOR_APPROVAL);
				warrantyContractContext.setRevisionNumber(0);
				addRecord(true,Collections.singletonList(warrantyContractContext), module, fields);
				warrantyContractContext.setParentId(warrantyContractContext.getLocalId());
				updateRecord(warrantyContractContext, module, fields);
				updateLineItems(warrantyContractContext);
				updateAssetsAssociated(warrantyContractContext);
				//add service if newly added here as lineItem
				//addServiceRecords(warrantyContractContext.getLineItems(),serviceModule,serviceFields);
				//also add service vendor association
				addRecord(false,warrantyContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
	
			}
			
			
			
			context.put(FacilioConstants.ContextNames.RECORD, warrantyContractContext);
		}
		return false;
	}
	
	private void updateLineItems(WarrantyContractContext warrantycontractContext) {
		for (WarrantyContractLineItemContext lineItemContext : warrantycontractContext.getLineItems()) {
			lineItemContext.setWarrantyContractId(warrantycontractContext.getId());
		}
	  }
		
	private void updateAssetsAssociated(WarrantyContractContext warrantycontractContext) throws Exception {
		List<Map<String, Object>> values = new ArrayList<Map<String,Object>>();
		if(CollectionUtils.isNotEmpty(warrantycontractContext.getAssetIds())) {
			for (Long assetId : warrantycontractContext.getAssetIds()) {
				Map<String, Object> val = new HashMap<String, Object>();
				val.put("contractId", warrantycontractContext.getId());
				val.put("assetId", assetId);
				val.put("orgId", AccountUtil.getCurrentOrg().getId());
				values.add(val);
			}
			FacilioModule associateAssetModule = ModuleFactory.getContractAssociatedAssetsModule();
			List<FacilioField> fields = FieldFactory.getContractAssociatedAssetModuleFields();
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
		                    .table(associateAssetModule.getTableName())
		                    .fields(fields);
		    insertRecordBuilder.addRecords(values);
		    insertRecordBuilder.save();
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
			addRecord(true,newServiceRecords, serviceModule, serviceFields);
		}
	}
	private void addRecord(boolean isLocalIdNeeded,List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
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
	
}
