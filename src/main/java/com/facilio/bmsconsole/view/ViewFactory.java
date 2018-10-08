package com.facilio.bmsconsole.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext.AssetState;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

public class ViewFactory {
	
	private static Map<String,Map<String,FacilioView>> views = Collections.unmodifiableMap(initializeViews());
	public static FacilioView getView (String moduleName, String viewName) {
		FacilioView view = getModuleViews(moduleName).get(viewName);
		if(view != null) {
			List<ViewField> columns = ColumnFactory.getColumns(moduleName, viewName);
			view.setFields(columns);
			view.setDefault(true);
		}
		return view;
	}
	
	public static Map<String,FacilioView> getModuleViews (String moduleName) {
		Map<String,FacilioView> moduleViews = new LinkedHashMap<>();
		if(views.containsKey(moduleName)) {
			moduleViews.putAll(views.get(moduleName));
		}
		return moduleViews;
	}
	
	private static Map<String, Map<String, FacilioView>> initializeViews() {
		Map<String, Map<String, FacilioView>> viewsMap = new HashMap<String, Map<String, FacilioView>>();
		Map<String,FacilioView> views = new LinkedHashMap<>();
		int order = 1;
		
		views.put("open", getOpenWorkorderRequests().setOrder(order++));
		views.put("all", getAllWorkRequests().setOrder(order++));
		views.put("rejected", getRejectedWorkorderRequests().setOrder(order++));
		views.put("myrequests", getMyWorkorderRequests().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("open", getAllOpenWorkOrders().setOrder(order++));
		views.put("overdue", getAllOverdueWorkOrders().setOrder(order++));
		views.put("duetoday", getAllDueTodayWorkOrders().setOrder(order++));
		views.put("planned", getOpenPlannedWorkOrders().setOrder(order++));
		views.put("unplanned", getOpenUnPlannedWorkOrders().setOrder(order++));
		views.put("unassigned", getUnassignedWorkorders().setOrder(order++));
		views.put("myopen", getMyOpenWorkOrders().setOrder(order++));
		views.put("myteamopen", getMyTeamOpenWorkOrders().setOrder(order++));
		views.put("myoverdue", getMyOverdueWorkOrders().setOrder(order++));
		views.put("myduetoday", getMyDueTodayWorkOrders().setOrder(order++));
		views.put("openfirealarms", getFireSafetyWOs().setOrder(order++));
		views.put("all", getAllWorkOrders().setOrder(order++));
		views.put("resolved", getAllResolvedWorkOrders().setOrder(order++));
		views.put("closed", getAllClosedWorkOrders().setOrder(order++));
		views.put("report", getReportView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORK_ORDER, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("my", getMyTasks().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TASK,views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAssetsView().setOrder(order++));
		views.put("energy", getAssets("Energy").setOrder(order++));
		views.put("hvac", getAssets("HVAC").setOrder(order++));
		views.put("active", getAssetsByState("Active").setOrder(order++));
		views.put("retired", getAssetsByState("Retired").setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ASSET, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("today", getEvents("Today").setOrder(order++));
		views.put("yesterday", getEvents("Yesterday").setOrder(order++));
		views.put("thisweek", getEvents("ThisWeek").setOrder(order++));
		views.put("lastweek", getEvents("LastWeek").setOrder(order++));
		viewsMap.put("event", views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("active", getSeverityAlarms("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledged", getUnacknowledgedAlarms().setOrder(order++));
		views.put("critical", getSeverityAlarms("critical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("major", getSeverityAlarms("major", "Major Alarms", "Major", true).setOrder(order++));
		views.put("minor", getSeverityAlarms("minor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("fire",  getTypeAlarms("fire", "Fire Alarms", AlarmType.FIRE).setOrder(order++));
		views.put("energy", getTypeAlarms("energy", "Energy Alarms", AlarmType.ENERGY).setOrder(order++));
		views.put("hvac", getTypeAlarms("hvac", "HVAC Alarms", AlarmType.HVAC).setOrder(order++));
		views.put("cleared", getSeverityAlarms("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		views.put("anomalies", getSourceTypeAlarms("anomalies", "Anomalies", SourceType.ANOMALY_ALARM).setOrder(order++));
		views.put("all", getAllAlarms().setOrder(order++));
		views.put("report", getReportView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ALARM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("report", getReportView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ENERGY_DATA_READING, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		FacilioView preventiveView = getStatusPreventiveWorkOrders("active", "Active", true).setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getTypePreventiveWorkOrders("preventive", "Preventive", "Preventive").setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getTypePreventiveWorkOrders("corrective", "Corrective", "Corrective").setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getTypePreventiveWorkOrders("rounds", "Rounds", "Rounds").setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getTypePreventiveWorkOrders("breakdown", "Breakdown", "Breakdown").setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getTypePreventiveWorkOrders("compliance", "Compliance", "Compliance").setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getAllPreventiveWorkOrders().setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		preventiveView = getStatusPreventiveWorkOrders("inactive", "Inactive", false).setOrder(order++);
		views.put(preventiveView.getName(),preventiveView);
		viewsMap.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE,views);
		
		return viewsMap;
	}
	
	private static FacilioView getEvents(String category) {
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(EventConstants.EventModuleFactory.getEventModule());
		
		Condition dateCondition = new Condition();
		dateCondition.setField(createdTime);
		if(category.equals("Today"))
		{
			dateCondition.setOperator(DateOperators.TODAY);
		}
		else if(category.equals("Yesterday"))
		{
			dateCondition.setOperator(DateOperators.YESTERDAY);
		}
		else if(category.equals("ThisWeek"))
		{
			dateCondition.setOperator(DateOperators.CURRENT_WEEK);
		}		
		else if(category.equals("LastWeek"))
		{
			dateCondition.setOperator(DateOperators.LAST_WEEK);
		}
		Criteria criteria = new Criteria();
		criteria.addAndCondition(dateCondition);
		
		FacilioView eventsView = new FacilioView();
		eventsView.setName("Created Time");
		eventsView.setDisplayName("Event Time");
		eventsView.setCriteria(criteria);
		
		return eventsView;
	}

	private static Criteria getAssetCategoryCriteria(String category) {
		FacilioField categoryType = new FacilioField();
		categoryType.setName("categoryType");
		categoryType.setColumnName("CATEGORY_TYPE");
		categoryType.setDataType(FieldType.NUMBER);
		categoryType.setModule(ModuleFactory.getAssetCategoryModule());
		
		Condition statusOpen = new Condition();
		statusOpen.setField(categoryType);
		statusOpen.setOperator(NumberOperators.EQUALS);
		if(category.equals("Energy"))
		{
			statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.ENERGY.getIntVal()));
		}
		else if(category.equals("HVAC"))
		{
			statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.HVAC.getIntVal()));
		}
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);
		
		return criteria;
	}
	
	private static Condition getAssetStateCondition(FacilioModule module, String state) {
		FacilioField statusField = new FacilioField();
		statusField.setName("state");
		statusField.setColumnName("STATE");
		statusField.setDataType(FieldType.NUMBER);
		statusField.setModule(module);
		
		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(NumberOperators.EQUALS);
		if(state.equals("Active"))
		{
			open.setValue(String.valueOf(AssetState.ACTIVE.getIntVal()));
		}
		else if(state.equals("Retired"))
		{
			open.setValue(String.valueOf(AssetState.RETIRED.getIntVal()));
		}
		
		return open;
	}
	
	private static Condition getAssetCategoryCondition(FacilioModule module, String category) {
		LookupField statusField = new LookupField();
		statusField.setName("category");
		statusField.setColumnName("CATEGORY");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getAssetCategoryModule());
		
		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getAssetCategoryCriteria(category));
		
		return open;
	}
	
	private static FacilioView getAssetsByState(String state) {
		
		FacilioView assetView = new FacilioView();
		if(state.equals("Active"))
		{
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetStateCondition(ModuleFactory.getAssetsModule(), state));
			
			assetView.setName("active");
			assetView.setDisplayName("Active Assets");
			assetView.setCriteria(criteria);
		}
		else if(state.equals("Retired"))
		{
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetStateCondition(ModuleFactory.getAssetsModule(), state));
			
			assetView.setName("retired");
			assetView.setDisplayName("Retired Assets");
			assetView.setCriteria(criteria);
		}
		
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());
		
		assetView.setSortFields(Arrays.asList(new SortField(localId, false)));
		
		return assetView;
	}
	
	private static FacilioView getAllAssetsView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Assets");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getAssets(String category) {
		
		FacilioView assetView = new FacilioView();
		if(category.equals("Energy"))
		{
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetCategoryCondition(ModuleFactory.getAssetsModule(), category));
			
			assetView.setName("energy");
			assetView.setDisplayName("Energy Assets");
			assetView.setCriteria(criteria);
		}
		else if(category.equals("HVAC"))
		{
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetCategoryCondition(ModuleFactory.getAssetsModule(), category));
			
			assetView.setName("hvac");
			assetView.setDisplayName("HVAC Assets");
			assetView.setCriteria(criteria);
		}
		
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());
		
		assetView.setSortFields(Arrays.asList(new SortField(localId, false)));
		
		return assetView;
	}
	
	private static Criteria getNotOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.NOT_EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.OPEN.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);
		
		return criteria;
	}
	
	public static Criteria getOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.OPEN.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);
		
		return criteria;
	}
	
	public static Condition getOpenStatusCondition(FacilioModule module) {
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
		
		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getOpenStatusCriteria());
		
		return open;
	}
	
	private static Criteria getCloseStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
		
		Condition statusClose = new Condition();
		statusClose.setField(statusTypeField);
		statusClose.setOperator(NumberOperators.EQUALS);
		statusClose.setValue(String.valueOf(TicketStatusContext.StatusType.CLOSED.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusClose);
		
		return criteria;
	}
	
	private static FacilioView getUnassignedWorkorders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		Criteria unassignedWOCriteria = getUnAssignedCriteria(workOrdersModule);
		unassignedWOCriteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned");
		unassignedWOView.setCriteria(unassignedWOCriteria);
		unassignedWOView.setSortFields(sortFields);
		
		return unassignedWOView;
	}
	
	private static Criteria getUnAssignedCriteria (FacilioModule module) {
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		
		userField.setModule(module);
		userField.setExtendedModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);
		
		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		
//		LookupField groupField = (LookupField) FieldFactory.getField("assignmentGroup", "ASSIGNMENT_GROUP_ID", workOrdersModule, FieldType.LOOKUP);
//		groupField.setExtendedModule(ModuleFactory.getTicketsModule());
//		groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
//		
//		Condition groupFieldCondition = new Condition();
//		groupFieldCondition.setField(groupField);
//		groupFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(userFieldCondition);
		// criteria.addAndCondition(groupFieldCondition);
		return criteria;
	}
	
	private static FacilioView getMyWorkorderRequests() {
		
		Criteria criteria = new Criteria();
		FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
		criteria.addAndCondition(getMyRequestCondition(workOrderRequestsModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrderRequestsModule);
		
		FacilioView myRequestsView = new FacilioView();
		myRequestsView.setName("myrequests");
		myRequestsView.setDisplayName("My Work Requests");
		myRequestsView.setCriteria(criteria);
		myRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return myRequestsView;
	}
	
	private static Condition getMyRequestCondition(FacilioModule module) {
		LookupField userField = new LookupField();
		userField.setName("requester");
		userField.setColumnName("REQUESTER_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(module);
		userField.setSpecialType(FacilioConstants.ContextNames.REQUESTER);
		
		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);
		
		return myUserCondition;
	}
	
	private static FacilioView getOpenWorkorderRequests() {
		
		Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.OPEN);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("open");
		allRequestsView.setDisplayName("Work Requests");
		allRequestsView.setCriteria(criteria);
		allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		
		return allRequestsView;
	}
	
	private static FacilioView getRejectedWorkorderRequests() {
		
		Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.REJECTED);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("rejected");
		allRequestsView.setDisplayName("Rejected Work Requests");
		allRequestsView.setCriteria(criteria);
		allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allRequestsView;
	}
	
	public static Criteria getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus status) {
		
		FacilioField field = new FacilioField();
		field.setName("status");
		field.setColumnName("STATUS");
		field.setDataType(FieldType.NUMBER);
		FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
		field.setModule(workOrderRequestsModule);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(status.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		
		return criteria;
	}
	
	private static FacilioView getAllOpenWorkOrders() {
		//All Open Tickets
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("open");
		openTicketsView.setDisplayName("Open");
		openTicketsView.setCriteria(criteria);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllClosedWorkOrders() {
		
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("closed");
		openTicketsView.setDisplayName("Closed Workorders");
		openTicketsView.setCriteria(getClosedTicketsCriteria(workOrdersModule));
		openTicketsView.setSortFields(sortFields);
		
		return openTicketsView;
	}
	
	private static Criteria getClosedTicketsCriteria (FacilioModule module) {
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		
		statusField.setModule(module);
		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
		
		Condition ticketClose = new Condition();
		ticketClose.setField(statusField);
		ticketClose.setOperator(LookupOperator.LOOKUP);
		ticketClose.setCriteriaValue(getCloseStatusCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}
	
	private static FacilioView getAllResolvedWorkOrders() {
		
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView resolvedTicketsView = new FacilioView();
		resolvedTicketsView.setName("resolved");
		resolvedTicketsView.setDisplayName("Resolved");
		resolvedTicketsView.setCriteria(getTicketStatusCriteria("Resolved", workOrdersModule));
		resolvedTicketsView.setSortFields(sortFields);
		
		return resolvedTicketsView;
	}
	
	private static Criteria getTicketStatusCriteria (String status, FacilioModule module) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
		
		Condition statusClose = new Condition();
		statusClose.setField(statusTypeField);
		statusClose.setOperator(StringOperators.IS);
		statusClose.setValue(status);
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addAndCondition(statusClose);
		
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
		
		Condition ticketClose = new Condition();
		ticketClose.setField(statusField);
		ticketClose.setOperator(LookupOperator.LOOKUP);
		ticketClose.setCriteriaValue(statusCriteria);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}
	
	private static FacilioView getMyOpenWorkOrders() {
		
		Criteria criteria = new Criteria();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		criteria.addAndCondition(getMyUserCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myopen");
		openTicketsView.setDisplayName("My Work Orders");
		openTicketsView.setCriteria(criteria);
		openTicketsView.setSortFields(sortFields);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllDueTodayWorkOrders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		Criteria criteria = getDueTodayCriteria(workOrdersModule);
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView overdueView = new FacilioView();
		overdueView.setName("duetoday");
		overdueView.setDisplayName("Due Today");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);
		
		return overdueView;
	}
	
	private static Criteria getDueTodayCriteria(FacilioModule workOrdersModule) {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		
		dueField.setModule(workOrdersModule);
		dueField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TODAY);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);
		
		return criteria;
	}

	private static FacilioView getMyTeamOpenWorkOrders() {
	
		Criteria criteria = new Criteria();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		criteria.addAndCondition(getMyTeamCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
	
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myteamopen");
		openTicketsView.setDisplayName("My Team Work Orders");
		openTicketsView.setCriteria(criteria);
		openTicketsView.setSortFields(sortFields);
	
		return openTicketsView;
	}
	
	private static FacilioView getOpenPlannedWorkOrders() {
		FacilioField sourceType = new FacilioField();
		sourceType.setName("sourceType");
		sourceType.setColumnName("SOURCE_TYPE");
		sourceType.setDataType(FieldType.NUMBER);
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		sourceType.setModule(workOrdersModule);
		sourceType.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition sourceTypeCondition = new Condition();
		sourceTypeCondition.setField(sourceType);
		sourceTypeCondition.setOperator(NumberOperators.EQUALS);
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceTypeCondition);
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView overdueView = new FacilioView();
		overdueView.setName("planned");
		overdueView.setDisplayName("Planned");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);

		return overdueView;
	}
	
	private static FacilioView getOpenUnPlannedWorkOrders() {
		FacilioField sourceType = new FacilioField();
		sourceType.setName("sourceType");
		sourceType.setColumnName("SOURCE_TYPE");
		sourceType.setDataType(FieldType.NUMBER);
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		sourceType.setModule(workOrdersModule);
		sourceType.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition sourceTypeCondition = new Condition();
		sourceTypeCondition.setField(sourceType);
		sourceTypeCondition.setOperator(NumberOperators.NOT_EQUALS);
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceTypeCondition);
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView overdueView = new FacilioView();
		overdueView.setName("unplanned");
		overdueView.setDisplayName("Un Planned");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);
		
		return overdueView;
	}
	
	public static Criteria getAllOverdueWorkOrdersCriteria() {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModule(ModuleFactory.getWorkOrdersModule());
		dueField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TILL_YESTERDAY);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		
		return criteria;
	}
	
	private static FacilioView getAllOverdueWorkOrders(){
	
		FacilioView overdueView = new FacilioView();
		overdueView.setName("overdue");
		overdueView.setDisplayName("Overdue");
		overdueView.setCriteria(getAllOverdueWorkOrdersCriteria());
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());
		
		SortField sortField = new SortField(createdTime, false);
		overdueView.setSortFields(Arrays.asList(sortField));
		
		return overdueView;
	}
	
	private static FacilioView getMyDueTodayWorkOrders() {
		
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModule(ModuleFactory.getWorkOrdersModule());
		dueField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TODAY);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getWorkOrdersModule()));
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView view = new FacilioView();
		view.setName("myduetoday");
		view.setDisplayName("My Due Today");
		view.setCriteria(criteria);
		view.setSortFields(sortFields);
		
		return view;
	}
	
	private static FacilioView getMyOverdueWorkOrders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		Criteria criteria = getOverdueCriteria(workOrdersModule);
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		criteria.addAndCondition(getMyUserCondition(workOrdersModule));
		
		FacilioView view = new FacilioView();
		view.setName("myoverdue");
		view.setDisplayName("My Overdue");
		view.setCriteria(criteria);
		view.setSortFields(sortFields);
		
		return view;
	}
	
	private static Criteria getOverdueCriteria(FacilioModule module) {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		
		dueField.setModule(module);
		dueField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TILL_YESTERDAY);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);
		
		return criteria;
	}
	
	private static FacilioView getMyWorkOrders() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioView myWorkOrdersView = new FacilioView();
		myWorkOrdersView.setName("my");
		myWorkOrdersView.setDisplayName("My Work Orders");
		myWorkOrdersView.setCriteria(criteria);
		return myWorkOrdersView;
	}
	
	private static FacilioView getMyTasks() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getTasksModule()));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("mytasks");
		openTicketsView.setDisplayName("My Tasks");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
		
	public static Condition getMyUserCondition(FacilioModule module) {
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(module);
		userField.setExtendedModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);
		
		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);
		
		return myUserCondition;
	}
	
	private static Condition getMyTeamCondition(FacilioModule module) {
		LookupField groupField = new LookupField();
		groupField.setName("assignmentGroup");
		groupField.setColumnName("ASSIGNMENT_GROUP_ID");
		groupField.setDataType(FieldType.LOOKUP);
		groupField.setModule(module);
		groupField.setExtendedModule(ModuleFactory.getTicketsModule());
		groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
		
		/*String groupIds = "";
		try {
			List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
			if (myGroups != null) {
				for (int i=0; i< myGroups.size(); i++) {
					if (i != 0) {
						groupIds += ",";
					}
					groupIds += myGroups.get(i).getId();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (groupIds.equals("")) {
			groupIds = "-100";
		}*/
		
		Condition myTeamCondition = new Condition();
		myTeamCondition.setField(groupField);
		myTeamCondition.setOperator(PickListOperators.IS);
		myTeamCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP);
		
		return myTeamCondition;
	}
	
	private static Criteria getFireSafetyCategoryCriteria() {
		FacilioField categoryNameField = new FacilioField();
		categoryNameField.setName("name");
		categoryNameField.setColumnName("NAME");
		categoryNameField.setDataType(FieldType.STRING);
		categoryNameField.setModule(ModuleFactory.getTicketCategoryModule());
		
		Condition fireSafety = new Condition();
		fireSafety.setField(categoryNameField);
		fireSafety.setOperator(StringOperators.IS);
		fireSafety.setValue("Fire Safety");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafety);
		
		return criteria;
	}
	
	private static FacilioView getFireSafetyWOs() {
		LookupField categoryField = new LookupField();
		categoryField.setName("category");
		categoryField.setColumnName("CATEGORY_ID");
		categoryField.setDataType(FieldType.LOOKUP);
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		categoryField.setModule(workOrdersModule);
		categoryField.setExtendedModule(ModuleFactory.getTicketsModule());
		categoryField.setLookupModule(ModuleFactory.getTicketCategoryModule());
		
		Condition fireSafetyCategory = new Condition();
		fireSafetyCategory.setField(categoryField);
		fireSafetyCategory.setOperator(LookupOperator.LOOKUP);
		fireSafetyCategory.setCriteriaValue(getFireSafetyCategoryCriteria());
		
		FacilioField sourceField = new FacilioField();
		sourceField.setName("sourceType");
		sourceField.setColumnName("SOURCE_TYPE");
		sourceField.setDataType(FieldType.NUMBER);
		sourceField.setModule(workOrdersModule);
		sourceField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition alarmSourceCondition = new Condition();
		alarmSourceCondition.setField(sourceField);
		alarmSourceCondition.setOperator(NumberOperators.EQUALS);
		alarmSourceCondition.setValue(String.valueOf(TicketContext.SourceType.ALARM.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafetyCategory);
		criteria.addAndCondition(getOpenStatusCondition(workOrdersModule));
		criteria.addAndCondition(alarmSourceCondition);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView fireSafetyWOView = new FacilioView();
		fireSafetyWOView.setName("openfirealarms");
		fireSafetyWOView.setDisplayName("Fire Alarm Workorders");
		fireSafetyWOView.setCriteria(criteria);
		fireSafetyWOView.setSortFields(sortFields);
		
		return fireSafetyWOView;
	}
	
	private static FacilioView getSeverityAlarms(String name, String displayName, String severity, boolean equals) {
		
		Condition alarmCondition = getAlarmSeverityCondition(severity, equals);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);
		criteria.andCriteria(getCommonAlarmCriteria());
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());
		
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
		
		return view;
	}
	
	public static Condition getAlarmSeverityCondition(String severity, boolean equals) {
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());
		
		Condition alarmCondition = new Condition();
		alarmCondition.setField(severityField);
		alarmCondition.setOperator(LookupOperator.LOOKUP);
		alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));
		
		return alarmCondition;
	}
	
	private static Criteria getSeverityAlarmCriteria(String severity, boolean equals) {
		FacilioField severityField = new FacilioField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.STRING);
		severityField.setModule(ModuleFactory.getAlarmSeverityModule());
		
		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		if(equals)
		{
			activeAlarm.setOperator(StringOperators.IS);
		}
		else
		{
			activeAlarm.setOperator(StringOperators.ISN_T);
		}
		activeAlarm.setValue(severity);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(activeAlarm);
		
		return criteria;
	}
	
	private static FacilioView getSourceTypeAlarms(String name, String displayName, TicketContext.SourceType sourceType) {
		
		FacilioModule module = ModuleFactory.getAlarmsModule();
		
		Criteria criteria = getSourceTypeCriteria(sourceType, module, true);
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(module);
		
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
		
		return view;
	}
	
	private static Criteria getCommonAlarmCriteria() {
		return getSourceTypeCriteria(SourceType.ANOMALY_ALARM,  ModuleFactory.getAlarmsModule(), false);
	}
	
	private static Criteria getSourceTypeCriteria(TicketContext.SourceType sourceType, FacilioModule module, boolean isEqual) {
		
		FacilioField sourceField = new FacilioField();
		sourceField.setName("sourceType");
		sourceField.setColumnName("SOURCE_TYPE");
		sourceField.setDataType(FieldType.NUMBER);
		sourceField.setModule(module);
		sourceField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition sourceCondition = new Condition();
		sourceCondition.setField(sourceField);
		sourceCondition.setOperator(isEqual ? NumberOperators.EQUALS : NumberOperators.NOT_EQUALS);
		sourceCondition.setValue(String.valueOf(sourceType.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceCondition);
		
		return criteria;
	}
	
	private static FacilioView getMyAlarms() {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getAlarmsModule()));
		
		FacilioView myAlarms = new FacilioView();
		myAlarms.setName("myalarms");
		myAlarms.setDisplayName("My Alarms");
		myAlarms.setCriteria(criteria);
		
		return myAlarms;
	}
	
	private static FacilioView getTypeAlarms(String name, String displayName, AlarmType type) {
		Condition condition = new Condition();
		condition.setColumnName("Alarms.ALARM_TYPE");
		condition.setFieldName("alarmType");
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(type.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		criteria.andCriteria(getCommonAlarmCriteria());
		
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());
		
		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));
		
		criteria.addAndCondition(activeAlarm);
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());
		
		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName(name);
		typeAlarms.setDisplayName(displayName);
		typeAlarms.setCriteria(criteria);
		typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
		
		return typeAlarms;
	}
	
	private static FacilioView getUnacknowledgedAlarms() {
		Condition falseCondition = new Condition();
		falseCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
		falseCondition.setFieldName("isAcknowledged");
		falseCondition.setOperator(BooleanOperators.IS);
		falseCondition.setValue(String.valueOf(false));
		
		Condition emptyCondition = new Condition();
		emptyCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
		emptyCondition.setFieldName("isAcknowledged");
		emptyCondition.setOperator(CommonOperators.IS_EMPTY);
		
		Criteria criteria = new Criteria();
		criteria.addOrCondition(emptyCondition);
		criteria.addOrCondition(falseCondition);
		criteria.andCriteria(getCommonAlarmCriteria());
		
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());
		
		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));
		
		criteria.addAndCondition(activeAlarm);
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());
		
		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledged");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
		
		return typeAlarms;
	}
	
	private static FacilioView getUnassignedAlarms() {
		
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getAlarmsModule());
		userField.setExtendedModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);
		
		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		
		Criteria unassignedWOCriteria = new Criteria();
		unassignedWOCriteria.addAndCondition(userFieldCondition);
		
		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned Alarms");
		unassignedWOView.setCriteria(unassignedWOCriteria);
		
		return unassignedWOView;
	}
	
	private static FacilioView getAllAlarms() {
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Alarms");
		allView.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
		
		return allView;
	}
	
	private static FacilioView getAllWorkOrders() {
		
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Workorders");
		allView.setSortFields(sortFields);
		
		return allView;
	}
	
	private static FacilioView getAllWorkRequests() {
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Requests");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		
		return allView;
	}
	
	private static FacilioView getAllPreventiveWorkOrders() {
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		
		return allView;
	}
	
	private static FacilioView getTypePreventiveWorkOrders(String name, String displayName, String type) {
		
		List<FacilioField> templateFields = FieldFactory.getWorkOrderTemplateFields();
		Map<String,FacilioField> fieldProps = FieldFactory.getAsMap(templateFields);
		LookupField typeIdField = (LookupField) fieldProps.get("typeId");
		
		FacilioModule typeModule = ModuleFactory.getTicketTypeModule();
		FacilioField nameField = FieldFactory.getField("name", "NAME", typeModule, FieldType.STRING);
		Condition nameCondition = CriteriaAPI.getCondition(nameField, type,StringOperators.IS);
		Criteria crit = new Criteria();
		crit.addAndCondition(nameCondition);
		
		Condition typeCondition = CriteriaAPI.getCondition(typeIdField, crit,LookupOperator.LOOKUP);
		Condition statusCondition = getPreventiveStatusCondition(true);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(typeCondition);
		criteria.addAndCondition(statusCondition);
		
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		
		return view;
	}
	
	private static FacilioView getStatusPreventiveWorkOrders(String name, String displayName, boolean status) {
		
		Condition statusCondition = getPreventiveStatusCondition(status);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusCondition);
		
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		
		return view;
	}
	
	public static Condition getPreventiveStatusCondition(boolean status) {
		List<FacilioField> preventiveFields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String,FacilioField> fieldProps = FieldFactory.getAsMap(preventiveFields);
		FacilioField statusField = (FacilioField) fieldProps.get("status");
		
		Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status),BooleanOperators.IS);
		
		return statusCondition;
	}
	
	// View for reports. Not shown in ui list
	private static FacilioView getReportView() {
		
		FacilioView reportView = new FacilioView();
		reportView.setName("report");
		reportView.setHidden(true);
		
		return reportView;
	}
	
	public static Criteria getCriteriaForView(String viewName, FacilioModule module) {
		
		// For sub views, parent criteria can be taken from views. It is not included here. eg: for closed view, closed criteria is not applied for its subviews
		
		Criteria criteria = new Criteria();
		switch(viewName) {
			case "my": 
				Condition myUser = getMyUserCondition(module);
				criteria.addAndCondition(myUser);
			break;
			
			case "myTeam": 
				Condition myTeam = getMyTeamCondition(module);
				criteria.addAndCondition(myTeam);
			break;
			
			case "overdue":
				criteria = getOverdueCriteria(module);
			break;
			
			case "duetoday":
				criteria = getDueTodayCriteria(module);
			break;
				
			case "unassigned": 
				criteria = getUnAssignedCriteria(module);
			break;
				
			case "rejected":
				criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.REJECTED);
			break;
				
			case "open":
				if (module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER_REQUEST)) {
					criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.OPEN);
				}
			break;
		}
		return !criteria.isEmpty() ? criteria : null; 
	}
	
	private static final Map<String, List<Map<String, Object>>> subViews = Collections.unmodifiableMap(initSubViews());

	private static Map<String,List<Map<String, Object>>> initSubViews() {
		Map<String,List<Map<String, Object>>> subViewsMap = new HashMap<String, List<Map<String, Object>>>();
		
		FacilioModule workorderModule = ModuleFactory.getWorkOrdersModule();
		
		// For sub views, parent criteria can be taken from views. It is not included here. eg: for closed view, closed criteria is not applied for its subviews
		
		// My Team SubViews
		List<Map<String, Object>> subViews = new ArrayList<>();
//		Map<String, Criteria> subViews = new LinkedHashMap<String, Criteria>();
		Map<String, Object> subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Open");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "unassigned");
		subviewDetail.put("displayName", "Unassigned");
		subviewDetail.put("criteria", getCriteriaForView("unassigned", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));
		
		subViewsMap.put("workorder-myteamopen", subViews);
		
		// My SubViews
		subViews = new ArrayList<>();
		
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "duetoday");
		subviewDetail.put("displayName", "Due Today");
		subviewDetail.put("criteria", getCriteriaForView("duetoday", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));
		
		subViewsMap.put("workorder-myopen", subViews);
		
		// Resolved
		subViews = new ArrayList<>();
		
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Resolved");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Resolved");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));
		
		subViewsMap.put("workorder-resolved", subViews);
		
		// Closed
		subViews = new ArrayList<>();
		
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Closed");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Closed");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));
		
		subViewsMap.put("workorder-closed", subViews);
		
		return subViewsMap;
	}
	
	public static List<Map<String, Object>> getSubViewsCriteria(String moduleName, String viewName) {
		String name = moduleName + "-" + viewName;
		if (!subViews.containsKey(name)) {
			return null;
		}
		return new ArrayList(subViews.get(name));
	}
	
	private static Map<String, Object> getAllSubViewDetail(FacilioModule module) {
		Map<String, Object> subviewDetail = new HashMap<>(); 
		subviewDetail.put("name", "all");
		subviewDetail.put("displayName", "All");
		subviewDetail.put("criteria", null);
		return subviewDetail;
	}

}
