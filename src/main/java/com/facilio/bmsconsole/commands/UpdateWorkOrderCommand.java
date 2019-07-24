package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateWorkOrderCommand extends FacilioCommand {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateWorkOrderCommand.class.getName());
	private static final List<EventType> TYPES = Arrays.asList(EventType.EDIT, EventType.ASSIGN_TICKET, EventType.CLOSE_WORK_ORDER,  EventType.SOLVE_WORK_ORDER, EventType.HOLD_WORK_ORDER, EventType.STATE_TRANSITION);
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER); //WorkOrders to be updated in bulk
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); //All IDs (bulk and individual) of WOs to be updated
		List<ReadingContext> readings = new ArrayList<>();
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		if(workOrder != null && recordIds != null && !recordIds.isEmpty() && oldWos != null && !oldWos.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			EventType activityType = (EventType)context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			Map<Long, List<UpdateChangeSet>> changeSets = new HashMap<>();
			
			int rowsUpdated = 0;
			Map<Long, WorkOrderContext> oldWoMap = oldWos.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
			if (!oldWoMap.isEmpty()) {
				rowsUpdated += updateWorkOrders(workOrder, module, oldWoMap.values().stream().collect(Collectors.toList()), readings, activityType, changeSets, context, recordIds);
			}
			
			if(((List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST) != null && CollectionUtils.containsAny(TYPES, (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST))) 
					|| TYPES.contains(activityType) || workOrder.getPriority() != null) {
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
			
			if (!changeSets.isEmpty() && workOrder.getApprovalStateEnum() == null && workOrder.getStatus() == null && workOrder.getAssignedTo() == null && workOrder.getAssignmentGroup() == null) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);

				Iterator it = recordIds.iterator();
				List<UpdateChangeSet> changeSetList = null;
				while (it.hasNext()) {
					Object record = it.next();
					 changeSetList = changeSets == null ? null : changeSets.get(record);
				}
                JSONObject woupdate = new JSONObject();
                List<Object> wolist = new ArrayList<Object>();
                boolean updatingPrereqApproved = false;
				for (UpdateChangeSet changeset : changeSetList) {
				    long fieldid = changeset.getFieldId();
					Object oldValue = changeset.getOldValue();
					Object newValue = changeset.getNewValue();
					FacilioField field = modBean.getField(fieldid, moduleName); 
					
					JSONObject info = new JSONObject();
					info.put("field", field.getName());
					info.put("displayName", field.getDisplayName());
					if (field.getName().contains("resource")) {
						ResourceContext resource = ResourceAPI.getResource((long) newValue);
						info.put("newValue", resource.getName());
					}
					else {
						if (field.getName().contains("preRequisiteApproved")) {
							updatingPrereqApproved = true;
						}
						info.put("newValue", newValue);
					}
					info.put("oldValue", oldValue);
	                wolist.add(info);

				}	
                woupdate.put("woupdate", wolist);
				if (updatingPrereqApproved) {
					CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, WorkOrderActivityType.PREREQUISITE_APPROVE, woupdate,(FacilioContext) context);
				} else {
					CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, WorkOrderActivityType.UPDATE, woupdate,(FacilioContext) context);
				}
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
	
	private int updateWorkOrders (WorkOrderContext workOrder, FacilioModule module, List<WorkOrderContext> oldWos, List<ReadingContext> readings, EventType activityType, Map<Long, List<UpdateChangeSet>> changeSets, Context context, List<Long> recordIds) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
		// For syncing, only one workorder will be there
		WorkOrderContext oldWoForSync = oldWos.get(0);
		if (lastSyncTime != null && oldWoForSync.getModifiedTime() > lastSyncTime ) {
			throw new RuntimeException("The workorder was modified after the last sync");
		}
		if (workOrder.getSyncTime() != -1 && oldWoForSync.getModifiedTime() > workOrder.getSyncTime()) {
			throw new RuntimeException("The workorder was modified after the last sync");
		}
		
		List<WorkOrderContext> newWos = new ArrayList<WorkOrderContext>();
		updateWODetails(workOrder);
		
		if (workOrder.getApprovalStateEnum() == ApprovalState.APPROVED || ((workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) || (workOrder.getAssignmentGroup() != null && workOrder.getAssignmentGroup().getId() != -1)) ) {
			updateStatus(workOrder, oldWos, newWos, readings, activityType, context, recordIds);
		}
		else if(workOrder.getStatus() != null) {
			validateCloseStatus(workOrder, oldWos, newWos, readings, activityType, context, recordIds);
		}
		else if (workOrder.getSiteId() != -1 && AccountUtil.getCurrentSiteId() == -1) {
			transferToAnotherSite(workOrder);
		}
		else if (workOrder.getTenant()!= null && workOrder.getTenant().getId() != -1) {
			transferToAnotherTenant(workOrder, oldWos);
		}
		// TODO verify
		if (workOrder.getDueDate() > 0) {
			workOrder.setEstimatedEnd(workOrder.getDueDate());
		}
		
		if (workOrder.getSiteId() == -1) {
			TicketAPI.validateSiteSpecificData(workOrder, oldWos);
		}
		if (workOrder.getSiteId() != -1) {
			JSONObject info = new JSONObject();
			JSONObject woupdate = new JSONObject();
		    List<Object> wolist = new ArrayList<Object>();
			info.put("field", "site");
			info.put("displayName", "Site");
			info.put("newValue", workOrder.getSiteId());
			wolist.add(info);
		    woupdate.put("woupdate", wolist);
			CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, WorkOrderActivityType.UPDATE, woupdate, (FacilioContext) context);
		}
		if (workOrder.getApprovalStateEnum()!=null && workOrder.getApprovalStateEnum().toString().equals("REJECTED")) {
			JSONObject info = new JSONObject();
			info.put("approvalStatus", workOrder.getApprovalStateEnum().toString());
		CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, WorkOrderActivityType.REJECTED, info, (FacilioContext) context);
		}
		
		if (workOrder.getApprovalStateEnum()!=null && workOrder.getApprovalStateEnum().toString().equals("APPROVED")) {
			JSONObject info = new JSONObject();
			info.put("approvalStatus", workOrder.getApprovalStateEnum().toString());
		CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, WorkOrderActivityType.APPROVED, info, (FacilioContext) context);
		}
		
		if (newWos.isEmpty()) {
			return bulkUpdate(module, fields, oldWos, workOrder, changeSets, context);
		}
		else {
			return individualUpdate(module, fields, oldWos, newWos, changeSets, context);
		}
	}
	
	private int bulkUpdate(FacilioModule module, List<FacilioField> fields, List<WorkOrderContext> oldWos, WorkOrderContext wo, Map<Long, List<UpdateChangeSet>> changeSets, Context context) throws Exception {
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
	
	private int individualUpdate (FacilioModule module, List<FacilioField> fields, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, Map<Long, List<UpdateChangeSet>> changeSets, Context context) throws Exception {
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
	
	private Map<String, FacilioStatus> statusMap = null;
	private Map<String, FacilioStatus> getAllTicketStatus() throws Exception {
		if (statusMap == null) {
			List<FacilioStatus> allStatus = TicketAPI.getAllStatus(false);
			statusMap = allStatus.stream().collect(Collectors.toMap(FacilioStatus::getStatus, Function.identity()));
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
		workOrder.setTenant(new TenantContext());
		workOrder.getTenant().setId(-99);
	}
	
	public static <T extends TicketContext> void transferToAnotherTenant(WorkOrderContext workOrder, List<T> oldTickets) throws Exception {
		
		TenantContext tenant = TenantsAPI.fetchTenant(workOrder.getTenant().getId());
		
		for(T oldWo: oldTickets) {
			long siteId = oldWo.getSiteId();
			if (tenant.getSiteId() != siteId) {
				  throw new IllegalArgumentException("The selected tenant belongs to another Site.");
			} 
	
		  }
		
		//Creating multiple New WOs is unnecessary here 
		workOrder.setAssignedTo(new User());
		workOrder.getAssignedTo().setId(-1);
		workOrder.setAssignmentGroup(new Group());
		workOrder.getAssignmentGroup().setId(-1);
		workOrder.setResource(new ResourceContext());
		workOrder.getResource().setId(-1);
		workOrder.setSiteId(tenant.getSiteId());
	}
	
	private void validateCloseStatus (WorkOrderContext workOrder, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, List<ReadingContext> userReadings, EventType activityType, Context context, List<Long> recordIds) throws Exception {
		FacilioStatus statusObj = workOrder.getStatus();
		
		for(WorkOrderContext oldWo: oldWos) {
			
			if (!validateWorkorderStatus(statusObj, oldWo)) {
				throw new IllegalArgumentException("Please close all tasks before closing/resolving the workorder");
			}
			if (oldWo.isUserSignatureRequired() && "Closed".equalsIgnoreCase(statusObj.getStatus()) && workOrder.getSignature() == null) {
				throw new IllegalArgumentException("Please enter the signature before closing the workorder");
			}
			
			WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
			newWo.setId(oldWo.getId());
			newWos.add(newWo);
			
//			TicketAPI.updateTicketStatus(activityType, newWo, oldWo, newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()));
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
	
	private WorkOrderContext cloneWO (WorkOrderContext wo, long oldId, List<WorkOrderContext> newWos) {
		WorkOrderContext newWo =FieldUtil.cloneBean(wo, WorkOrderContext.class);
		newWo.setId(oldId);
		newWos.add(newWo);
		return newWo;
	}
	
	private void updateStatus (WorkOrderContext workOrder, List<WorkOrderContext> oldWos, List<WorkOrderContext> newWos, List<ReadingContext> userReadings, EventType activityType, Context context, List<Long> recordIds) throws Exception {
		Map<String, FacilioStatus> allStatus = getAllTicketStatus();
		FacilioStatus preOpen = allStatus.get("preopen");
		FacilioStatus submittedStatus = allStatus.get("Submitted");
		FacilioStatus assignedStatus = allStatus.get("Assigned");
		FacilioStatus onHoldStatus = allStatus.get("On Hold");
		FacilioStatus wipStatus = allStatus.get("Work in Progress");
		long submittedId = submittedStatus.getId();
		long preOpenId = preOpen == null ? -1 : preOpen.getId();
		for (WorkOrderContext oldWo: oldWos) {
			WorkOrderContext newWo = null;
			if (workOrder.getApprovalStateEnum() == ApprovalState.APPROVED && oldWo.getStatus() != null && oldWo.getStatus().getId() == preOpenId) {
				newWo = cloneWO(workOrder, oldWo.getId(), newWos);
				newWo.setStatus(submittedStatus); //For updating
				oldWo.setStatus(submittedStatus); //For further check
			}
			
			List<Long> parentId = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if ( ((workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) || (workOrder.getAssignmentGroup() != null && workOrder.getAssignmentGroup().getId() != -1)) && oldWo.getStatus() != null) {
				newWo = newWo == null ? cloneWO(workOrder, oldWo.getId(), newWos) : newWo;
				if ( oldWo.getStatus() != null && oldWo.getStatus().getId() == submittedId) {
					newWo.setStatus(assignedStatus);
				}
				else {
					if (workOrder.getAssignedTo() != null && oldWo.getAssignedTo() != null) {
						if  (workOrder.getAssignedTo().getOuid() == -1) {
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
				addAssignmentActivity(workOrder, parentId.get(0), oldWo, context);
				
			}
		}
	}
	
	private boolean validateWorkorderStatus(FacilioStatus statusObj , WorkOrderContext oldWo) throws Exception {
		if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = ModuleFactory.getTasksModule();
			List<FacilioField> fields =  modBean.getAllFields("task");
			Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(FieldFactory.getField("closedTasks", "COUNT(Tasks.ID)", FieldType.NUMBER)))
					.table(module.getTableName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(oldWo.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("statusNew"),"2", NumberOperators.EQUALS));
			
			List<Map<String, Object>> task = builder.get();
			if (task != null && !task.isEmpty()) {
				long count = ((Number) task.get(0).get("closedTasks")).longValue();
				if (count < oldWo.getNoOfTasks()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void updateWODetails (WorkOrderContext wo) throws Exception {
		TicketAPI.updateTicketAssignedBy(wo);
		wo.setModifiedTime(wo.getCurrentTime());
		if(wo.getStatus() != null &&  wo.getStatus().getId() > 0) {
			FacilioStatus statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), wo.getStatus().getId());
			wo.setStatus(statusObj);
		}
	}
	
	private void addAssignmentActivity(WorkOrderContext workOrder, long parentId, WorkOrderContext oldWo, Context context) {
		JSONObject info = new JSONObject();
		info.put("assignedBy", workOrder.getAssignedBy().getOuid());
		if (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) {
			info.put("assignedTo", workOrder.getAssignedTo().getOuid());
			if (oldWo != null && oldWo.getAssignedTo() != null && oldWo.getAssignedTo().getId() != -1) {
				info.put("prevAssignedTo", oldWo.getAssignedTo().getOuid());
			}
		}
		else {
			info.put("assignmentGroup", workOrder.getAssignmentGroup().getId());
			if (oldWo != null && oldWo.getAssignmentGroup() != null && oldWo.getAssignmentGroup().getId() != -1) {
				info.put("prevAssignmentGroup", oldWo.getAssignmentGroup().getId());
			}
		}
		JSONObject newinfo = new JSONObject();
        newinfo.put("assigned", info);
		CommonCommandUtil.addActivityToContext(parentId, -1, WorkOrderActivityType.ASSIGN, newinfo, (FacilioContext) context);
	}
	
}
