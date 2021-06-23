package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
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
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateRentalLeaseContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		RentalLeaseContractContext rentalLeaseContractContext = (RentalLeaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		rentalLeaseContractContext.computeNextPaymentDate();
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);
		
		if (rentalLeaseContractContext != null) {
			if (rentalLeaseContractContext.getVendor() == null || rentalLeaseContractContext.getVendor().getId() <= 0){
				throw new IllegalArgumentException("Vendor cannot be empty");
			}
			
			if(rentalLeaseContractContext.getFromDate() > 0 && rentalLeaseContractContext.getEndDate() > 0)
				{
					if(rentalLeaseContractContext.getEndDate() <= rentalLeaseContractContext.getFromDate())
					{
						throw new IllegalArgumentException("Contract End Date should be greater than From Date");
					}
				}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS_LINE_ITEMS);
			
//			if (CollectionUtils.isEmpty(rentalLeaseContractContext.getLineItems())) {
//				throw new Exception("Line items cannot be empty");
//			}
			
			rentalLeaseContractContext.setContractType(ContractType.RENTAL_LEASE);
			
			if (!isContractRevised && rentalLeaseContractContext.getId() > 0) {
				ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields,true, (FacilioContext) context);
				
				DeleteRecordBuilder<RentalLeaseContractLineItemsContext> deleteBuilder = new DeleteRecordBuilder<RentalLeaseContractLineItemsContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("RENTAL_LEASE_CONTRACT", "rentalLeaseContractId", String.valueOf(rentalLeaseContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(rentalLeaseContractContext);
				ContractsAPI.addRecord(false,rentalLeaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContractContext);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
							
			} 
			else if (isContractRevised && rentalLeaseContractContext.getId() > 0) {
				if(rentalLeaseContractContext.getStatusEnum() == Status.APPROVED) {
					RentalLeaseContractContext revisedContract = rentalLeaseContractContext.clone();
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields);
					rentalLeaseContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields, true, (FacilioContext) context);
					updateLineItems(revisedContract);
					ContractsAPI.updateAssetsAssociated(revisedContract.getId(), revisedContract.getAssociatedAssets());
					ContractsAPI.updateTermsAssociated(revisedContract.getId(), revisedContract.getTermsAssociated());
					revisedContract.setStatus(Status.PENDING_FOR_REVISION);
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
				rentalLeaseContractContext.setStatus(WarrantyContractContext.Status.WAITING_FOR_APPROVAL);
				rentalLeaseContractContext.setRevisionNumber(0);
				ContractsAPI.addRecord(true,Collections.singletonList(rentalLeaseContractContext), module, fields);
				rentalLeaseContractContext.setParentId(rentalLeaseContractContext.getLocalId());
				ContractsAPI.updateRecord(rentalLeaseContractContext, module, fields, true, (FacilioContext) context);
				updateLineItems(rentalLeaseContractContext);
				ContractsAPI.addRecord(false,rentalLeaseContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContractContext);

				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
				
			}
			
			context.put(FacilioConstants.ContextNames.RECORD_ID, rentalLeaseContractContext.getId());
			
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
