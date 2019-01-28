package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketStatusContext.StatusType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class WorkOrderAPI {
	
	private static final Logger LOGGER = Logger.getLogger(WorkOrderAPI.class.getName());
	
	public static List<Map<String, Object>> getTasks(Long workorderID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.ID, workorderID);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
		context.put("isAsMap", true);
		chain.execute(context);
		
		return (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
	}
	public static WorkOrderContext getWorkOrder(long ticketId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getIdCondition(ticketId, module))
														;
		
		List<WorkOrderContext> workOrders = builder.get();
		if(workOrders != null && !workOrders.isEmpty()) {
			return workOrders.get(0);
		}
		return null;
	}
	public static List<WorkOrderContext> getWorkOrderFromPMId(long pmid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("PM_ID", "pmId", pmid+"", NumberOperators.EQUALS))
														;
		
		return builder.get();
	}
	
	public static List<WorkOrderContext> getWorkOrders(long categoryId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", categoryId+"", NumberOperators.EQUALS))
														;
		
		List<WorkOrderContext> workOrders = builder.get();
		return workOrders;
	}
	
	public static List<WorkOrderContext> getWorkOrders(long categoryId,Long startTime,Long endTime,Long spaceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", categoryId+"", NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
														;
		if(spaceId != null && spaceId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resource", spaceId+"", BuildingOperator.BUILDING_IS));
		}
		List<WorkOrderContext> workOrders = builder.get();
		LOGGER.log(Level.SEVERE, "builder1 - "+builder);
		return workOrders;
	}

	public static List<WorkOrderContext> getOpenWorkOrderForUser(Long ouid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		TicketStatusContext status = TicketAPI.getStatus("Closed");
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.table("WorkOrders")
														.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("STATUS_ID", "status", status.getId()+"", NumberOperators.NOT_EQUALS))
														.andCondition(CriteriaAPI.getCondition("ASSIGNED_TO_ID", "assignedTo", ouid+"", NumberOperators.EQUALS));
		
		List<WorkOrderContext> workOrders = builder.get();

		return workOrders;
	}
	
	public static Map<Long, Object> getLookupFieldPrimary(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioField mainField = modBean.getPrimaryField(moduleName);

		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(mainField);
		selectFields.add(FieldFactory.getIdField(module));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		FacilioModule prevModule = module;
		while (prevModule.getExtendModule() != null) {
			builder.innerJoin(prevModule.getExtendModule().getTableName())
				.on(prevModule.getTableName()+".ID = " + prevModule.getExtendModule().getTableName()+ ".ID");
			prevModule = prevModule.getExtendModule();
		}

		List<Map<String,Object>> asProps = builder.get();
		Map lookupMap = new HashMap<>();
		for (Map<String, Object> map : asProps) {
			lookupMap.put((Long) map.get("id"), map.get(mainField.getName()));
		}
		return lookupMap;
	}


	public static List<WorkOrderContext> getOverdueWorkOrders(List<WorkOrderContext> workOrders) throws Exception {
		List<WorkOrderContext> overdueWorkorders =null;
		
		for(WorkOrderContext workOrder:workOrders) {
			
			long estimatedEnd = workOrder.getEstimatedEnd();
			if(estimatedEnd != -1) {
				if(estimatedEnd - DateTimeUtil.getCurrenTime() < 0) {
					
					overdueWorkorders = overdueWorkorders == null ? new ArrayList<>() : overdueWorkorders;
					
					overdueWorkorders.add(workOrder);
				}
			}
		}
		
		return overdueWorkorders;
	}
	
	
	public static List<WorkOrderContext> getDueTodayWorkOrders(List<WorkOrderContext> workOrders) throws Exception {
		
		List<WorkOrderContext> dueTodayWorkorders =null;
		
		for(WorkOrderContext workOrder:workOrders) {
			
			long estimatedEnd = workOrder.getEstimatedEnd();
			if(estimatedEnd != -1) {
				
				DateRange dateRange = DateOperators.TODAY.getRange(null);
				
				if (dateRange.getStartTime() < estimatedEnd && dateRange.getEndTime() > estimatedEnd) {
					
					dueTodayWorkorders = dueTodayWorkorders == null ? new ArrayList<>() : dueTodayWorkorders;
					
					dueTodayWorkorders.add(workOrder);
				}
			}
		}
		
		return dueTodayWorkorders;
	}
	
	
public static List<WorkOrderContext> getWorkOrderStatusPercentage(Long startTime,Long endTime) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		  .module(workOrderModule)
																		  .beanClass(WorkOrderContext.class)
																		  .select(workorderFields)
																		  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																		  .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																		  
																		  ;



		List<WorkOrderContext> woStatusPercentage = selectRecordsBuilder.get();

       return woStatusPercentage;
	}


public static List<Map<String,Object>> getAvgCompletionTimeByCategory(Long startTime,Long endTime) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);


		FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		List<FacilioField> ticketFields = modBean.getAllFields(workOrderModule.getName());


		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);
		Map<String, FacilioField> ticketFieldMap = FieldFactory.getAsMap(ticketFields);


		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField avgField = new FacilioField();
		avgField.setName("avg_resolution_time");
		avgField.setColumnName("avg(ACTUAL_WORK_DURATION/(1000*60))");//to render client in mins
		fields.add(avgField);

		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("count(*)");
		fields.add(countField);

		FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
		fields.add(siteIdField);

		FacilioField categoryField = workorderFieldMap.get("category");
		fields.add(categoryField);

		FacilioField statusField = ticketFieldMap.get("status");
		//fields.add(statusField);


		//fetching the workorders with closed status



		GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
				  													.table(workOrderModule.getTableName())
				  													.select(fields)
				  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
					  												.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
					  												.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
					  											    .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
					  												.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																	.groupBy(siteIdField.getCompleteColumnName() +  ", "  + categoryField.getCompleteColumnName())
																  ;



       List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();


       return avgResolutionTime;

	}



	public static long getSiteIdForWO(long woId) throws Exception {
		WorkOrderContext wo = getWorkOrder(woId);
		if (wo == null) {
			return -1;
		}
		return wo.getSiteId();
	}
	
	
	
	
	
	
 }
