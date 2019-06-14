package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.context.WorkOrderServiceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateWorkorderServiceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderServiceModule = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);
		List<FacilioField> workorderServiceFields = modBean.getAllFields(FacilioConstants.ContextNames.WO_SERVICE);
		Map<String, FacilioField> serviceFieldsMap = FieldFactory.getAsMap(workorderServiceFields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) serviceFieldsMap.get("service"));
		List<WorkOrderServiceContext> workorderServices = (List<WorkOrderServiceContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(workorderServices)) {
			for (WorkOrderServiceContext woService : workorderServices) {
				woService.setCost(getCostForService(woService.getVendor(), woService.getService().getId()));
				if (woService.getId() > 0) {
					updateWorkorderService(workorderServiceModule, workorderServiceFields, woService);
				}
				else
				{
					addWorkorderService(workorderServiceModule, workorderServiceFields, woService);
				}
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderServices.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workorderServices.get(0).getParentId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderServices);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 3);
			context.put(FacilioConstants.ContextNames.WO_LABOUR_LIST, workorderServices);
		}

		return false;
	}

	private void addWorkorderService(FacilioModule module, List<FacilioField> fields, WorkOrderServiceContext woServices)
			throws Exception {
		InsertRecordBuilder<WorkOrderServiceContext> readingBuilder = new InsertRecordBuilder<WorkOrderServiceContext>()
				.module(module).fields(fields).addRecord(woServices);
		readingBuilder.save();
	}

	private void updateWorkorderService(FacilioModule module, List<FacilioField> fields, WorkOrderServiceContext labour)
			throws Exception {

		UpdateRecordBuilder<WorkOrderServiceContext> updateBuilder = new UpdateRecordBuilder<WorkOrderServiceContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(labour.getId(), module));
		updateBuilder.update(labour);

	}
	
	private double getCostForService(long vendorId, long serviceId) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
		SelectRecordsBuilder<WarrantyContractContext> selectBuilder = new SelectRecordsBuilder<WarrantyContractContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(WarrantyContractContext.class).
				andCondition(CriteriaAPI.getCondition("VENDOR", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(ContractsContext.Status.APPROVED.getValue()), NumberOperators.EQUALS))
				
				;
		List<WarrantyContractContext> activeServiceContracts = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(activeServiceContracts)) {
			FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
			List<FacilioField> lineItemFields = modBean.getAllFields(FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS);
			SelectRecordsBuilder<WarrantyContractLineItemContext> lineItemsBuilder = new SelectRecordsBuilder<WarrantyContractLineItemContext>()
					.select(lineItemFields).table(lineItemModule.getTableName()).moduleName(lineItemModule.getName())
					.beanClass(WarrantyContractLineItemContext.class).
					andCondition(CriteriaAPI.getCondition("SERVICE", "service", String.valueOf(serviceId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("SERVICE_CONTRACT", "serviceContract", String.valueOf(activeServiceContracts.get(0).getId()), NumberOperators.EQUALS))
					
					;
			List<WarrantyContractLineItemContext> serviceContractLineItems = lineItemsBuilder.get();
			if(CollectionUtils.isNotEmpty(serviceContractLineItems)) {
				return serviceContractLineItems.get(0).getCost();
			}
			
		}
		return 0;
	}
}
