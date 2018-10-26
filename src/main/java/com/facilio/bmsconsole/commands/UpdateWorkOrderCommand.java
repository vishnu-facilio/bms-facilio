package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class UpdateWorkOrderCommand implements Command {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateWorkOrderCommand.class.getName());
	private static final List<ActivityType> TYPES = Arrays.asList(ActivityType.EDIT, ActivityType.ASSIGN_TICKET, ActivityType.CLOSE_WORK_ORDER,  ActivityType.SOLVE_WORK_ORDER, ActivityType.HOLD_WORK_ORDER);
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER); //WorkOrders to be updated in bulk
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); //All IDs (bulk and individual) of WOs to be updated
		List<ReadingContext> readings = new ArrayList<>();
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		List<WorkOrderContext> newWos = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST); //WorkOrders to be updated individually. But should be present in oldWOs
		if(workOrder != null && recordIds != null && !recordIds.isEmpty() && oldWos != null && !oldWos.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			ActivityType activityType = (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			Map<Long, List<UpdateChangeSet>> changeSets = new HashMap<>();
			
			int rowsUpdated = 0;
			Map<Long, WorkOrderContext> oldWoMap = oldWos.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
			if (newWos != null && !newWos.isEmpty()) {
				for (WorkOrderContext wo : newWos) {
					rowsUpdated += updateWorkOrders(wo, module, Collections.singletonList(oldWoMap.remove(wo.getId())), readings, activityType, changeSets, (FacilioContext) context);
				}
			}
			
			if (!oldWoMap.isEmpty()) {
				rowsUpdated += updateWorkOrders(workOrder, module, oldWoMap.values().stream().collect(Collectors.toList()), readings, activityType, changeSets, (FacilioContext) context);
			}
			
			if(TYPES.contains(activityType) || workOrder.getPriority() != null) {
				SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
						.module(module)
						.beanClass(WorkOrderContext.class)
						.select(modBean.getAllFields(moduleName))
						.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
						.orderBy("ID");

				List<WorkOrderContext> workOrders = builder.get();
				context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrders);
			}
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			
			if (!changeSets.isEmpty()) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
			}
		}
		
		if (!readings.isEmpty()) {
			Map<String, List<ReadingContext>> readingMap = new HashMap<>();
			readingMap.put("userworkhoursreading", readings);
			context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
		}
		return false;
	}
	
	private int updateWorkOrders (WorkOrderContext workOrder, FacilioModule module, List<WorkOrderContext> oldWos, List<ReadingContext> readings, ActivityType activityType, Map<Long, List<UpdateChangeSet>> changeSets, FacilioContext context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
		if (lastSyncTime != null && oldWos.get(0).getModifiedTime() > lastSyncTime ) {
			throw new RuntimeException("The workorder was modified after the last sync");
		}
		
		List<WorkOrderContext> newWos = new ArrayList<WorkOrderContext>();
		updateWODetails(workOrder);
		
		if (workOrder.getAssignedTo() != null || workOrder.getAssignmentGroup() != null) {
			updateStatus(workOrder, oldWos, newWos, readings, activityType);
		}
		else if(workOrder.getStatus() != null) {
			validateCloseStatus(workOrder, oldWos, newWos, readings, activityType, context);
		}
		else if (workOrder.getSiteId() != -1 && AccountUtil.getCurrentSiteId() == -1) {
			transferToAnotherSite(workOrder);
		}
		
		if (workOrder.getSiteId() == -1) {
			TicketAPI.validateSiteSpecificData(workOrder, oldWos);
		}
		
		if (newWos.isEmpty()) {
			return bulkUpdate(module, fields, oldWos, workOrder, changeSets, context);
		}
		else {
			return individualUpdate(module, fields, oldWos, newWos, changeSets, context);
		}
	}
	
	private int bulkUpdate(FacilioModule module, List<FacilioField> fields, List<WorkOrderContext> oldWos, WorkOrderContext wo, Map<Long, List<UpdateChangeSet>> changeSets, FacilioContext context) throws Exception {
		UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
				.module(module)
				.fields(fields)
				.withChangeSet(oldWos)
				; //No where condition because Old records are specified
		
		int rowsUpdated = updateBuilder.update(wo);
		if (updateBuilder.getChangeSet() != null) {
			changeSets.putAll(updateBuilder.getChangeSet());
		}
		return rowsUpdated;
	}
	
	private int individualUpdate (FacilioModule module, List<FacilioField> fields, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, Map<Long, List<UpdateChangeSet>> changeSets, FacilioContext context) throws Exception {
		int rowsUpdated = 0;
		Map<Long, WorkOrderContext> oldWoMap = oldWos.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
		for (WorkOrderContext wo : newWos) {
			UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
					.module(module)
					.fields(fields)
					.withChangeSet(Collections.singletonList(oldWoMap.get(wo.getId())))
					; //No where condition because Old records are specified
			
			rowsUpdated += updateBuilder.update(wo);
			if (updateBuilder.getChangeSet() != null) {
				changeSets.putAll(updateBuilder.getChangeSet());
			}
		}
		return rowsUpdated;
	}
	
	private Map<String, TicketStatusContext> statusMap = null;
	private Map<String, TicketStatusContext> getAllTicketStatus() throws Exception {
		if (statusMap == null) {
			List<TicketStatusContext> allStatus = TicketAPI.getAllStatus();
			statusMap = allStatus.stream().collect(Collectors.toMap(TicketStatusContext::getStatus, Function.identity()));
		}
		return statusMap; 
	}
	
	private void transferToAnotherSite (WorkOrderContext workOrder) throws Exception {
		List<Long> mySites = CommonCommandUtil.getMySiteIds();
		if (mySites != null && !mySites.isEmpty()) {
			boolean found = false;
			for (long siteId: mySites) {
				if (siteId == workOrder.getSiteId()) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new IllegalArgumentException("The site is not accessible.");
			}
		}
		
		//Creating multiple New WOs is unnecessary here 
		workOrder.setAssignedTo(new User());
		workOrder.getAssignedTo().setId(-1);
		workOrder.setAssignmentGroup(new Group());
		workOrder.getAssignmentGroup().setId(-1);
		workOrder.setResource(new ResourceContext());
		workOrder.getResource().setId(-1);
	}
	
	private void validateCloseStatus (WorkOrderContext workOrder, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, List<ReadingContext> userReadings, ActivityType activityType, FacilioContext context) throws Exception {
		TicketStatusContext statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), workOrder.getStatus().getId());
		
		for(WorkOrderContext oldWo: oldWos) {
			
			if (!validateWorkorderStatus(statusObj, oldWo)) {
				throw new RuntimeException("Please close all tasks before closing/resolving the workorder");
			}
			
			WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
			newWo.setId(oldWo.getId());
			newWos.add(newWo);
			
			TicketAPI.updateTicketStatus(activityType, newWo, oldWo, newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()));
			try {
				List<List<Long>> actualTimings = (List<List<Long>>) context.get(FacilioConstants.ContextNames.ACTUAL_TIMINGS);
				if (actualTimings != null && !actualTimings.isEmpty() && (newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()))) {
					userReadings.addAll(ShiftAPI.handleWorkHoursReading(activityType, oldWo.getAssignedTo().getOuid(), oldWo.getId(), oldWo.getStatus(), newWo.getStatus(), true));
					ShiftAPI.markAutoEntriesAsInvalid(oldWo.getAssignedTo().getOuid(), oldWo.getId());
					userReadings.addAll(ShiftAPI.addActualWorkHoursReading(oldWo.getAssignedTo().getOuid(), oldWo.getId(), activityType, actualTimings));
				} else if (oldWo.getAssignedTo() != null) {
					userReadings.addAll(ShiftAPI.handleWorkHoursReading(activityType, oldWo.getAssignedTo().getOuid(), oldWo.getId(), oldWo.getStatus(), newWo.getStatus()));
				}
			}
			catch(Exception e) {
				log.info("Exception occurred while handling work hours", e);
				CommonCommandUtil.emailException(ShiftAPI.class.getName(), "Exception occurred while handling work hours", e);
			}
		}
	}
	
	private void updateStatus (WorkOrderContext workOrder, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, List<ReadingContext> userReadings, ActivityType activityType) throws Exception {
		Map<String, TicketStatusContext> allStatus = getAllTicketStatus(); 
		TicketStatusContext submittedStatus = allStatus.get("Submitted");
		TicketStatusContext assignedStatus = allStatus.get("Assigned");  
		TicketStatusContext onHoldStatus = allStatus.get("On Hold");
		TicketStatusContext wipStatus = allStatus.get("Work in Progress");
		long submittedId = submittedStatus.getId();
		for (WorkOrderContext oldWo: oldWos) {
			WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
			newWos.add(newWo);
			newWo.setId(oldWo.getId());
			if (oldWo.getStatus().getId() == submittedId) {
				newWo.setStatus(assignedStatus);
			} else {
				if (workOrder.getAssignedTo() != null && oldWo.getAssignedTo() != null) {
					if (workOrder.getAssignedTo().getOuid() == -1) {
						userReadings.addAll(ShiftAPI.addUserWorkHoursReading(oldWo.getAssignedTo().getOuid(), oldWo.getId(), activityType, "Close", System.currentTimeMillis()));
						newWo.setStatus(submittedStatus);
					} else if (oldWo.getAssignedTo().getOuid() != workOrder.getAssignedTo().getOuid()) {
						try {
							if (oldWo.getStatus().getId() == wipStatus.getId()) {
								newWo.setStatus(onHoldStatus);
								userReadings.addAll(ShiftAPI.addUserWorkHoursReading(oldWo.getAssignedTo().getOuid(), oldWo.getId(), activityType, "Pause", System.currentTimeMillis()));
							} else {
								newWo.setStatus(oldWo.getStatus());
							}
						} catch (Exception e) {
							log.info("Exception occurred while handling work hours", e);
							CommonCommandUtil.emailException(ShiftAPI.class.getName(), "Exception occurred while handling work hours", e);
						}
					}
				}
			}
		}
	}
	
	private boolean validateWorkorderStatus(TicketStatusContext statusObj , WorkOrderContext oldWo) throws Exception {
		if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = ModuleFactory.getTasksModule();
			List<FacilioField> fields =  modBean.getAllFields("task");
			Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(FieldFactory.getField("closedTasks", "COUNT(Tasks.ID)", FieldType.NUMBER)))
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(oldWo.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("statusNew"),"2", NumberOperators.EQUALS));
			
			List<Map<String, Object>> task = builder.get();
			if (task != null && !task.isEmpty()) {
				long count = (long) task.get(0).get("closedTasks");
				if (count < oldWo.getNoOfTasks()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void updateWODetails (WorkOrderContext wo) {
		TicketAPI.updateTicketAssignedBy(wo);
		wo.setModifiedTime(System.currentTimeMillis());
	}
	
}
