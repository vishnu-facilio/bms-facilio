package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ContractsContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddOrUpdateLabourContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		LabourContractContext labourContractContext = (LabourContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		boolean isContractRevised = (boolean) context.get(FacilioConstants.ContextNames.IS_CONTRACT_REVISED);

		if (labourContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
			List<FacilioField> labourFields = modBean.getAllFields(labourModule.getName());
			
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(labourContractContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			if (labourContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			labourContractContext.setContractType(ContractType.LABOUR);
			
			if (!isContractRevised && labourContractContext.getId() > 0) {
				updateRecord(labourContractContext, module, fields);
				
				DeleteRecordBuilder<LabourContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<LabourContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("LABOUR_CONTRACT", "labourContractId", String.valueOf(labourContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
				updateLineItems(labourContractContext);
				//add labour if newly added here as lineItem
				addLabourRecords(labourContractContext.getLineItems(),labourModule,labourFields);
				addRecord(false,labourContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));

			}
			else if (isContractRevised && labourContractContext.getId() > 0) {
				if(labourContractContext.getStatusEnum() == Status.APPROVED) {
					LabourContractContext revisedContract = (LabourContractContext)labourContractContext.clone();
					addRecord(true,Collections.singletonList(revisedContract), module, fields);
					labourContractContext.setStatus(Status.REVISED);
					updateRecord(labourContractContext, module, fields);
					updateLineItems(revisedContract);
					addLabourRecords(revisedContract.getLineItems(),labourModule,labourFields);
					addRecord(false,revisedContract.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				}
				else {
					throw new IllegalArgumentException("Only Approved contracts can be revised");
				}
			}
			else {
				
				labourContractContext.setStatus(LabourContractContext.Status.WAITING_FOR_APPROVAL);
				labourContractContext.setRevisionNumber(0);
				addRecord(true,Collections.singletonList(labourContractContext), module, fields);
				labourContractContext.setParentId(labourContractContext.getLocalId());
				updateRecord(labourContractContext, module, fields);
				updateLineItems(labourContractContext);
				//add labour if newly added here as lineItem
				addLabourRecords(labourContractContext.getLineItems(),labourModule,labourFields);
				addRecord(false,labourContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));


			}
			
			context.put(FacilioConstants.ContextNames.RECORD, labourContractContext);
		}
		return false;
	}
	
		private void updateLineItems(LabourContractContext labourcontractContext) {
		for (LabourContractLineItemContext lineItemContext : labourcontractContext.getLineItems()) {
			lineItemContext.setLabourContractId(labourcontractContext.getId());
		}
	}
		
	private void addLabourRecords(List<LabourContractLineItemContext> lineItems,FacilioModule labourModule, List<FacilioField> labourFields) throws Exception {
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
		addRecord(false,newLocationRecords, locationModule, locationFields);
		if(!CollectionUtils.isEmpty(newLabourRecords)) {
			addRecord(true,newLabourRecords, labourModule, labourFields);
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
