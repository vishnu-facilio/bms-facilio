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
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class UpdateWorkOrderCommand extends FacilioCommand {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateWorkOrderCommand.class.getName());
	private static final List<EventType> TYPES = Arrays.asList(EventType.EDIT, EventType.ASSIGN_TICKET, EventType.CLOSE_WORK_ORDER,  EventType.SOLVE_WORK_ORDER, EventType.HOLD_WORK_ORDER, EventType.STATE_TRANSITION);

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER); //WorkOrders to be updated in bulk
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); //All IDs (bulk and individual) of WOs to be updated
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		if(workOrder != null && recordIds != null && !recordIds.isEmpty() && oldWos != null && !oldWos.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			EventType activityType = (EventType)context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			Map<Long, List<UpdateChangeSet>> changeSets = new HashMap<>();
			
			validateSyncTime(context, workOrder, oldWos);
			
			int rowsUpdated = 0;
			Map<Long, WorkOrderContext> oldWoMap = oldWos.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
			if (!oldWoMap.isEmpty()) {
				rowsUpdated += updateWorkOrders(workOrder, module, oldWoMap.values().stream().collect(Collectors.toList()), changeSets, context, recordIds);
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
				if (workOrder.getOfflineModifiedTime() != -1) {
					workOrders.forEach(wo -> wo.setOfflineModifiedTime(workOrder.getOfflineModifiedTime()));
				}
				context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrders);
			}
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);

			addActivity(workOrder, oldWos, changeSets, recordIds, modBean, moduleName, context);
		}
		

		return false;
	}
	
	private int updateWorkOrders (WorkOrderContext workOrder, FacilioModule module, List<WorkOrderContext> oldWos, Map<Long, List<UpdateChangeSet>> changeSets, Context context, List<Long> recordIds) throws Exception {

		updateWODetails(workOrder);
		
		boolean isTenantChanged = false;
		boolean isScopeFieldsChanged = false;
		
		TenantContext tenant = null;
		for (WorkOrderContext oldWo : oldWos) {
			if (workOrder.getSiteId() > 0 && oldWo.getSiteId() != workOrder.getSiteId()) {
				log.error("Old Site - " + oldWo.getSiteId() +", New Site - " + workOrder.getSiteId());
				throw new IllegalArgumentException("Site cannot be changed for work order");
			}
			if (workOrder.getTenant() != null && workOrder.getTenant().getId() > 0 && (oldWo.getTenant() == null || workOrder.getTenant().getId() !=  oldWo.getTenant().getId())) {
				isTenantChanged = true;
				if (tenant == null) {
					tenant = TenantsAPI.getTenant(workOrder.getTenant().getId());
				}
				if (tenant.getSiteId() != oldWo.getSiteId()) {
					throw new IllegalArgumentException("The selected tenant belongs to another Site.");
				}
			}
			if (!isScopeFieldsChanged && workOrder.getResource() != null && workOrder.getResource().getId() > 0 && (oldWo.getResource() == null || workOrder.getResource().getId() !=  oldWo.getResource().getId())) {
				isScopeFieldsChanged  = true;
			}
			else if (!isScopeFieldsChanged && workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() > 0 && (oldWo.getAssignedTo() == null || workOrder.getAssignedTo().getId() !=  oldWo.getAssignedTo().getId())) {
				isScopeFieldsChanged  = true;
			}
			else if (!isScopeFieldsChanged && workOrder.getAssignmentGroup() != null &&  workOrder.getAssignmentGroup().getId() > 0 && (oldWo.getAssignmentGroup() == null || workOrder.getAssignmentGroup().getId() !=  oldWo.getAssignmentGroup().getId())) {
				isScopeFieldsChanged  = true;
			}
		}

		if (workOrder.getParentWO() != null && workOrder.getParentWO().getId() > 0) {
			validateStatusOfParentAndChild(workOrder, oldWos);
		}
		
		if(workOrder.getStatus() != null) {
			validateCloseStatus(workOrder, oldWos);
		}
		
		if (isScopeFieldsChanged) {
			TicketAPI.validateSiteSpecificData(workOrder, oldWos);			
		}
		
		if (isTenantChanged) {
			TicketAPI.associateTenant(workOrder);
		}

		
		int rowsUpdated = 0;
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
				.module(module)
				.fields(fields)
				.withChangeSet(oldWos)
				.skipModuleCriteria() //Just in case to avoid any error. Please remove this when moving WO to V3
				; //No where condition because Old records are specified
		
		List<SupplementRecord> supplements = new ArrayList<>();
		CommonCommandUtil.handleFormDataAndSupplement(fields, workOrder.getData(), supplements);
		if(!supplements.isEmpty()) {
			updateBuilder.updateSupplements(supplements);
		}
		
		rowsUpdated = updateBuilder.update(workOrder);
		if (updateBuilder.getChangeSet() != null) {
			changeSets.putAll(updateBuilder.getChangeSet());
		}
		
		return rowsUpdated;
	}

	private void validateStatusOfParentAndChild(WorkOrderContext workOrder, List<WorkOrderContext> oldWos) throws Exception {
		WorkOrderContext parentWO = WorkOrderAPI.getWorkOrder(workOrder.getParentWO().getId());
		FacilioStatus status = TicketAPI.getStatus(parentWO.getStatus().getId());
		if (status.getType() == StatusType.CLOSED) {
			for (WorkOrderContext oldWo : oldWos) {
				long statusId = oldWo.getStatus().getId();
				FacilioStatus statusObj = TicketAPI.getStatus(statusId);
				if (statusObj.getType() != StatusType.CLOSED) {
					throw new IllegalArgumentException("Cannot add open WO as a child to closed parent");
				}
			}
		}
	}

	private void validateSyncTime(Context context, WorkOrderContext workOrder, List<WorkOrderContext> oldWos) throws Exception {
		Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
		// For syncing, only one workorder will be there
		WorkOrderContext oldWoForSync = oldWos.get(0);
		if (lastSyncTime != null && oldWoForSync.getModifiedTime() > lastSyncTime ) {
			throw new RuntimeException("The workorder was modified after the last sync");
		}
		if (workOrder.getSyncTime() != -1 && oldWoForSync.getModifiedTime() > workOrder.getSyncTime()) {
			throw new RuntimeException("The workorder was modified after the last sync");
		}
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
		
		TenantContext tenant = TenantsAPI.getTenant(workOrder.getTenant().getId());
		
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
	
	private void validateCloseStatus (WorkOrderContext workOrder, List<WorkOrderContext> oldWos) throws Exception {
		FacilioStatus statusObj = workOrder.getStatus();
		if (statusObj.getType() == StatusType.CLOSED) {
			for(WorkOrderContext oldWo: oldWos) {
				if (!validateTaskStatus(statusObj, oldWo)) {
					throw new IllegalArgumentException("Please close all tasks before closing the workorder");
				}
				if (oldWo.isUserSignatureRequired() && workOrder.getSignature() == null) {
					throw new IllegalArgumentException("Please enter the signature before closing the workorder");
				}
				if (!validateChildWOStatus(oldWo)) {
					throw new IllegalArgumentException("Please close all child WO before closing the workorder");
				}
			}
		}
	}

	private boolean validateChildWOStatus(WorkOrderContext oldWo) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.beanClass(WorkOrderContext.class)
				.module(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER))
				.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
				.innerJoin("TicketStatus")
					.on("TicketStatus.ID = Tickets.STATUS_ID")
				.andCondition(CriteriaAPI.getCondition("PARENT_WOID", "parentWO", String.valueOf(oldWo), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "statusType", String.valueOf(2), NumberOperators.NOT_EQUALS))
				;
		List<WorkOrderContext> workOrderContexts = builder.get();
		return CollectionUtils.isEmpty(workOrderContexts);
	}

	private boolean validateTaskStatus(FacilioStatus statusObj , WorkOrderContext oldWo) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = ModuleFactory.getTasksModule();
		List<FacilioField> fields =  modBean.getAllFields("task");
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(FieldFactory.getField("closedTasks", "COUNT(Tasks.ID)", FieldType.NUMBER)))
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(oldWo.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("statusNew"),"2", NumberOperators.EQUALS));
		
		List<Map<String, Object>> task = builder.get();
		if (task != null && !task.isEmpty()) {
			long count = ((Number) task.get(0).get("closedTasks")).longValue();
			if (count < oldWo.getNoOfTasks()) {
				return false;
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
		if (wo.getDueDate() > 0) {
			wo.setEstimatedEnd(wo.getDueDate());
		}
	}
	
	private void addActivity(WorkOrderContext workOrder, List<WorkOrderContext> oldWos, Map<Long, List<UpdateChangeSet>> changeSets, List<Long> recordIds, ModuleBean modBean, String moduleName, Context context) throws Exception {
		if (workOrder.getSiteId() != -1) {
			JSONObject info = new JSONObject();
			JSONObject woupdate = new JSONObject();
		    List<Object> wolist = new ArrayList<Object>();
			info.put("field", "site");
			info.put("displayName", "Site");
			info.put("newValue", workOrder.getSiteId());
			wolist.add(info);
		    woupdate.put("woupdate", wolist);
			CommonCommandUtil.addActivityToContext(recordIds.get(0), workOrder.getCurrentTime(), WorkOrderActivityType.UPDATE, woupdate, (FacilioContext) context);
		}
		if ( (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) || (workOrder.getAssignmentGroup() != null && workOrder.getAssignmentGroup().getId() != -1) ) {
			for (WorkOrderContext oldWo: oldWos) {
				addAssignmentActivity(workOrder, oldWo.getId(), oldWo, context);
			}
		} else if (workOrder.getVendor() != null && workOrder.getVendor().getId() != -1) {
			for (WorkOrderContext oldWo : oldWos) {
				addVendorAssignmentActivity(workOrder, oldWo.getId(), oldWo, context);
			}
		}
		else if (!changeSets.isEmpty() && workOrder.getApprovalStateEnum() == null && workOrder.getStatus() == null) {

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
				else if(field.getName().contains("vendor") && workOrder.getVendor().getId() > 0) {
					VendorContext vendor = InventoryApi.getVendor(workOrder.getVendor().getId());
					info.put("newValue", vendor.getName());
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
				CommonCommandUtil.addActivityToContext(recordIds.get(0),  workOrder.getCurrentTime(), WorkOrderActivityType.PREREQUISITE_APPROVE, woupdate,(FacilioContext) context);
			} else {
				CommonCommandUtil.addActivityToContext(recordIds.get(0),  workOrder.getCurrentTime(), WorkOrderActivityType.UPDATE, woupdate,(FacilioContext) context);
			}
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
		CommonCommandUtil.addActivityToContext(parentId,  workOrder.getCurrentTime(), WorkOrderActivityType.ASSIGN, newinfo, (FacilioContext) context);
	}
	
	private void addVendorAssignmentActivity(WorkOrderContext workOrder, long parentId, WorkOrderContext oldWo, Context context) throws Exception{
		JSONObject info = new JSONObject();
		if (workOrder.getVendor() != null && workOrder.getVendor().getId() > 0) {
			VendorContext vendor = InventoryApi.getVendor(workOrder.getVendor().getId());
			info.put("vendor", vendor.getName());
			JSONObject newinfo = new JSONObject();
	        newinfo.put("vendor", info);
			CommonCommandUtil.addActivityToContext(parentId,  workOrder.getCurrentTime(), WorkOrderActivityType.VENDOR_ASSIGNED, newinfo, (FacilioContext) context);
		}
	}
	
}
