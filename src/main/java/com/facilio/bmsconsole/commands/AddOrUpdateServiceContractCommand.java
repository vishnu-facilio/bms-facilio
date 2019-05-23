package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ServiceContractContext;
import com.facilio.bmsconsole.context.ServiceContractLineItemContext;
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

public class AddOrUpdateServiceContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ServiceContractContext serviceContractContext = (ServiceContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (serviceContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule serviceModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE);
			List<FacilioField> serviceFields = modBean.getAllFields(serviceModule.getName());
			
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_CONTRACTS_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(serviceContractContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			if (serviceContractContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			serviceContractContext.setContractType(ContractType.LABOUR);
			
			if (serviceContractContext.getId() > 0) {
				updateRecord(serviceContractContext, module, fields);
				
				DeleteRecordBuilder<ServiceContractLineItemContext> deleteBuilder = new DeleteRecordBuilder<ServiceContractLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("SERVICE_CONTRACT", "serviceContractId", String.valueOf(serviceContractContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				
				serviceContractContext.setStatus(ServiceContractContext.Status.WAITING_FOR_APPROVAL);
				addRecord(true,Collections.singletonList(serviceContractContext), module, fields);
			}
			
			updateLineItems(serviceContractContext);
			//add service if newly added here as lineItem
			addServiceRecords(serviceContractContext.getLineItems(),serviceModule,serviceFields);
			addRecord(false,serviceContractContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, serviceContractContext);
		}
		return false;
	}
	
		private void updateLineItems(ServiceContractContext servicecontractContext) {
		for (ServiceContractLineItemContext lineItemContext : servicecontractContext.getLineItems()) {
			lineItemContext.setServiceContractId(servicecontractContext.getId());
			lineItemContext.setCost(lineItemContext.calculateCost());
		}
	}
		
	private void addServiceRecords(List<ServiceContractLineItemContext> lineItems,FacilioModule serviceModule, List<FacilioField> serviceFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule locationModule = modBean.getModule("location");
		List<FacilioField> locationFields = modBean.getAllFields(locationModule.getName());
		List<ServiceContext> newServiceRecords = new ArrayList<ServiceContext>();
		
		for(ServiceContractLineItemContext lineItem :lineItems ) {
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
