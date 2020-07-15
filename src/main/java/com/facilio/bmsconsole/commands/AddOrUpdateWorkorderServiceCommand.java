package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.RecordAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class AddOrUpdateWorkorderServiceCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderServiceModule = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);
		List<FacilioField> workorderServiceFields = modBean.getAllFields(FacilioConstants.ContextNames.WO_SERVICE);
		Map<String, FacilioField> serviceFieldsMap = FieldFactory.getAsMap(workorderServiceFields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) serviceFieldsMap.get("service"));
		List<WorkOrderServiceContext> workorderServices = (List<WorkOrderServiceContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkOrderServiceContext> workorderServicelist = new ArrayList<>();

		List<WorkOrderServiceContext> woServiceToBeAdded = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(workorderServices)) {
			long parentId = workorderServices.get(0).getParentId();
			WorkOrderContext workorder = (WorkOrderContext) RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER, parentId);

			for (WorkOrderServiceContext woService : workorderServices) {
				woService.setCost(getCostForService(woService.getVendor(), woService.getService().getId()));
				if (woService.getId() > 0) {
					SelectRecordsBuilder<WorkOrderServiceContext> selectBuilder = new SelectRecordsBuilder<WorkOrderServiceContext>()
							.select(workorderServiceFields).table(workorderServiceModule.getTableName())
							.moduleName(workorderServiceModule.getName()).beanClass(WorkOrderServiceContext.class)
							.andCondition(CriteriaAPI.getIdCondition(woService.getId(), workorderServiceModule))
							.fetchSupplements(lookUpfields);
					;
					List<WorkOrderServiceContext> woServiceContext = selectBuilder.get();
					woService = setWorkorderServiceObj(woServiceContext.get(0).getService(), parentId, workorder, woService);
					workorderServicelist.add(woService);
					updateWorkorderService(workorderServiceModule, workorderServiceFields, woService);
				}
				else
				{
					woService = setWorkorderServiceObj(woService.getService(), parentId, workorder, woService);
					woServiceToBeAdded.add(woService);
					workorderServicelist.add(woService);

				}
			}

			if (CollectionUtils.isNotEmpty(woServiceToBeAdded)) {
				addWorkorderService(workorderServiceModule, workorderServiceFields, woServiceToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderServices.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(workorderServices.get(0).getParentId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderServices);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 4);
			context.put(FacilioConstants.ContextNames.WO_SERVICE_LIST, workorderServices);
		}

		return false;
	}

	private void addWorkorderService(FacilioModule module, List<FacilioField> fields, List<WorkOrderServiceContext> woServices)
			throws Exception {
		InsertRecordBuilder<WorkOrderServiceContext> readingBuilder = new InsertRecordBuilder<WorkOrderServiceContext>()
				.module(module).fields(fields).addRecords(woServices);
		readingBuilder.save();
	}

	private void updateWorkorderService(FacilioModule module, List<FacilioField> fields, WorkOrderServiceContext service)
			throws Exception {

		UpdateRecordBuilder<WorkOrderServiceContext> updateBuilder = new UpdateRecordBuilder<WorkOrderServiceContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(service.getId(), module));
		updateBuilder.update(service);

	}

	private WorkOrderServiceContext setWorkorderServiceObj(ServiceContext service, long parentId, WorkOrderContext workorder, WorkOrderServiceContext workorderService) {
		WorkOrderServiceContext woService = new WorkOrderServiceContext();
		woService.setStartTime(workorderService.getStartTime());
		woService.setEndTime(workorderService.getEndTime());
		woService.setDuration(workorderService.getDuration());
		woService.setId(workorderService.getId());
		double duration = 0;
		if (woService.getDuration() <= 0) {
//			if (woService.getStartTime() <= 0) {
//				woService.setStartTime(workorder.getScheduledStart());
//			}
//			if (woService.getEndTime() <= 0) {
//				woService.setEndTime(workorder.getEstimatedEnd());
//			}
			if (woService.getStartTime() >= 0 && woService.getEndTime() >= 0) {
				duration = getEstimatedWorkDuration(woService.getStartTime(), woService.getEndTime());
			} else {
				if(workorder.getActualWorkDuration() > 0) {
					double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
					duration = Math.round(hours*100.0)/100.0;
				}
				else{
					duration = workorderService.getId() > 0 ? 0 : 1;
				}
			}
		} else {
			duration = woService.getDuration();
			if (woService.getStartTime() > 0) {
				long durationVal = (long) (woService.getDuration() * 60 * 60 * 1000);
				woService.setEndTime(woService.getStartTime() + durationVal);
			}
		}

		woService.setParentId(parentId);
		double costOccured = 0;
		if(service.getBuyingPrice() > 0) {
			if (service.getPaymentTypeEnum() == ServiceContext.PaymentType.SINGLE_PAYMENT) {
				costOccured = service.getBuyingPrice();
			}
			else {
				costOccured = service.getBuyingPrice() * duration;
			}
		}
		woService.setCost(costOccured);
		woService.setService(service);
		woService.setDuration(duration);
		return woService;
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

	public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
		double duration = -1;
		if (issueTime != -1 && returnTime != -1) {
			duration = returnTime - issueTime;
		}

		double hours = ((duration / (1000 * 60 * 60)));
		return Math.round(hours*100.0)/100.0;
	}


}
