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
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.RentalLeaseContractContext;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateRentalLeaseContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		RentalLeaseContractContext rentalLeaseContractContext = (RentalLeaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);
		
		if (rentalLeaseContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS_LINE_ITEMS);
			FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
			FacilioModule assetAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			
//			if (CollectionUtils.isEmpty(rentalLeaseContractContext.getLineItems())) {
//				throw new Exception("Line items cannot be empty");
//			}
			if (rentalLeaseContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			rentalLeaseContractContext.setContractType(ContractType.RENTAL_LEASE);
			
			if (!isContractRevised && rentalLeaseContractContext.getId() > 0) {
				ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields);
				
				DeleteRecordBuilder<RentalLeaseContractLineItemsContext> deleteBuilder = new DeleteRecordBuilder<RentalLeaseContractLineItemsContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("RENTAL_LEASE_CONTRACT", "rentalLeaseContractId", String.valueOf(rentalLeaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				DeleteRecordBuilder<ContractAssociatedAssetsContext> deleteAssetRelationBuilder = new DeleteRecordBuilder<ContractAssociatedAssetsContext>()
						.module(assetAssociatedModule)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(rentalLeaseContractContext.getId()), NumberOperators.EQUALS));
				deleteAssetRelationBuilder.delete();
				DeleteRecordBuilder<ContractAssociatedTermsContext> deleteTermsBuilder = new DeleteRecordBuilder<ContractAssociatedTermsContext>()
						.module(termsModule)
						.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(rentalLeaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(rentalLeaseContractContext);
				ContractsAPI.updateAssetsAssociated(rentalLeaseContractContext);
				ContractsAPI.updateTermsAssociated(rentalLeaseContractContext);
				ContractsAPI.addRecord(false,rentalLeaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContractContext);

			
			} 
			else if (isContractRevised && rentalLeaseContractContext.getId() > 0) {
				if(rentalLeaseContractContext.getStatusEnum() == Status.APPROVED) {
					RentalLeaseContractContext revisedContract = (RentalLeaseContractContext)rentalLeaseContractContext.clone();
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields);
					rentalLeaseContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields);
					updateLineItems(revisedContract);
					ContractsAPI.updateAssetsAssociated(revisedContract);
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
				rentalLeaseContractContext.setStatus(WarrantyContractContext.Status.WAITING_FOR_APPROVAL);
				rentalLeaseContractContext.setRevisionNumber(0);
				ContractsAPI.addRecord(true,Collections.singletonList(rentalLeaseContractContext), module, fields);
				rentalLeaseContractContext.setParentId(rentalLeaseContractContext.getLocalId());
				ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields);
				updateLineItems(rentalLeaseContractContext);
				ContractsAPI.updateAssetsAssociated(rentalLeaseContractContext);
				ContractsAPI.updateTermsAssociated(rentalLeaseContractContext);
				ContractsAPI.addRecord(false,rentalLeaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContractContext);

			
			}
		}
		return false;
	}
	
	private void updateLineItems(RentalLeaseContractContext rentalLeaseContractContext) throws Exception{
		if(CollectionUtils.isNotEmpty(rentalLeaseContractContext.getLineItems())) {
			for (RentalLeaseContractLineItemsContext lineItemContext : rentalLeaseContractContext.getLineItems()) {
				if(lineItemContext.getInventoryType() == InventoryType.ITEM.getValue()) {
					ItemTypesContext itemType = ItemsApi.getItemTypes(lineItemContext.getItemType().getId());
					if(!itemType.isRotating()) {
						throw new IllegalArgumentException("Only Rotating Items/tools can be rented/leased");
					}
				}
				else if(lineItemContext.getInventoryType() == InventoryType.TOOL.getValue()) {
					ToolTypesContext toolType = ToolsApi.getToolTypes(lineItemContext.getToolType().getId());
					if(!toolType.isRotating()) {
						throw new IllegalArgumentException("Only Rotating Items/tools can be rented/leased");
					}
				}
				lineItemContext.setRentalLeaseContractId(rentalLeaseContractContext.getId());
			}
		}
	  }
		
	
	
	
	
}
