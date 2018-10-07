package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class WorkOrderAPI {
	
	private static final Logger LOGGER = Logger.getLogger(WorkOrderAPI.class.getName());
	
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
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
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
	
	
	public static long getSiteIdForWO(long woId) throws Exception {
		WorkOrderContext wo = getWorkOrder(woId);
		if (wo == null) {
			return -1;
		}
		return wo.getSiteId();
	}
	
	
	
	
	
	
 }
