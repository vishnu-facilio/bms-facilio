package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.context.LocationContext;
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

public class AddOrUpdateLabourContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		LabourContractContext labourContractContext = (LabourContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		labourContractContext.computeNextPaymentDate();
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);
		
		if (labourContractContext != null) {
			if (labourContractContext.getVendor() == null || labourContractContext.getVendor().getId() <= 0) {
				throw new IllegalArgumentException("Vendor cannot be empty");
			}
			
			if(labourContractContext.getFromDate() > 0 && labourContractContext.getEndDate() > 0)
				{
					if(labourContractContext.getEndDate() <= labourContractContext.getFromDate())
					{
						throw new IllegalArgumentException("Contract End Date should be greater than From Date");
					}
				}
				
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
			List<FacilioField> labourFields = modBean.getAllFields(labourModule.getName());
			
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS_LINE_ITEMS);
			
//			if (CollectionUtils.isEmpty(labourContractContext.getLineItems())) {
//				throw new Exception("Line items cannot be empty");
//			}
			
			labourContractContext.setContractType(ContractType.LABOUR);
			
			if (!isContractRevised && labourContractContext.getId() > 0) {
				ContractsAPI.updateRecord(labourContractContext, module, fields, true, (FacilioContext) context);
				
				DeleteRecordBuilder<LabourContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<LabourContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("LABOUR_CONTRACT", "labourContractId", String.valueOf(labourContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(labourContractContext);
				//add labour if newly added here as lineItem
				addLabourRecords(labourContractContext.getLineItems(),labourModule,labourFields);
				ContractsAPI.addRecord(false,labourContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, labourContractContext);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
				
				

			}
			else if (isContractRevised && labourContractContext.getId() > 0) {
				if(labourContractContext.getStatusEnum() == Status.APPROVED) {
					LabourContractContext revisedContract = labourContractContext.clone();
					ContractsAPI.addRecord(true,Collections.singletonList(revisedContract), module, fields);
					labourContractContext.setStatus(Status.REVISED);
					ContractsAPI.updateRecord(labourContractContext, module, fields, true, (FacilioContext) context);
					updateLineItems(revisedContract);
					ContractsAPI.updateTermsAssociated(revisedContract.getId(), revisedContract.getTermsAssociated());
					revisedContract.setStatus(Status.PENDING_FOR_REVISION);
					addLabourRecords(revisedContract.getLineItems(),labourModule,labourFields);
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
				
				labourContractContext.setStatus(LabourContractContext.Status.WAITING_FOR_APPROVAL);
				labourContractContext.setRevisionNumber(0);
				ContractsAPI.addRecord(true,Collections.singletonList(labourContractContext), module, fields);
				labourContractContext.setParentId(labourContractContext.getLocalId());
				ContractsAPI.updateRecord(labourContractContext, module, fields, true, (FacilioContext) context);
				updateLineItems(labourContractContext);
				//add labour if newly added here as lineItem
				addLabourRecords(labourContractContext.getLineItems(),labourModule,labourFields);
				ContractsAPI.addRecord(false,labourContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				context.put(FacilioConstants.ContextNames.RECORD, labourContractContext);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
				


			}
			context.put(FacilioConstants.ContextNames.RECORD_ID, labourContractContext.getId());	
		}
		return false;
	}
	
	private void updateLineItems(LabourContractContext labourcontractContext) {
		if(CollectionUtils.isNotEmpty(labourcontractContext.getLineItems())) {
			for (LabourContractLineItemContext lineItemContext : labourcontractContext.getLineItems()) {
				lineItemContext.setLabourContractId(labourcontractContext.getId());
			}
		}
	}
		
	private void addLabourRecords(List<LabourContractLineItemContext> lineItems,FacilioModule labourModule, List<FacilioField> labourFields) throws Exception {
		if(CollectionUtils.isNotEmpty(lineItems)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule locationModule = modBean.getModule("location");
			List<FacilioField> locationFields = modBean.getAllFields(locationModule.getName());
			List<LabourContext> newLabourRecords = new ArrayList<LabourContext>();
			List<LocationContext> newLocationRecords = new ArrayList<LocationContext>();
			
			for(LabourContractLineItemContext lineItem :lineItems ) {
				if(lineItem.getLabour().getId() == -1) {
				   //add empty location object
					LocationContext location = new LocationContext();
					location.setName(lineItem.getLabour().getName()+"_Location");
					location.setLat(1.1);
					location.setLng(1.1);
					newLocationRecords.add(location);
					lineItem.getLabour().setLocation(location);
					lineItem.getLabour().setAvailability(true);
					newLabourRecords.add(lineItem.getLabour());
				}
			}
			ContractsAPI.addRecord(false,newLocationRecords, locationModule, locationFields);
			if(!CollectionUtils.isEmpty(newLabourRecords)) {
				ContractsAPI.addRecord(true,newLabourRecords, labourModule, labourFields);
			}
		}
	}
	
	}
