package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext.AssetState;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;

public class ViewFactory {
	
	private static Map<String, FacilioView> views = Collections.unmodifiableMap(initViews());
	public static FacilioView getView (String viewName) {
		return views.get(viewName);
	}
	
	private static Map<String, FacilioView> initViews() {
		
		Map<String, FacilioView> viewMap = new HashMap<>();
		viewMap.put("workorderrequest-open", getOpenWorkorderRequests());
		viewMap.put("workorderrequest-rejected", getRejectedWorkorderRequests());
		
		viewMap.put("workorder-open", getAllOpenWorkOrders());
		viewMap.put("workorder-myopen", getMyOpenWorkOrders());
		viewMap.put("workorder-unassigned", getUnassignedWorkorders());
		viewMap.put("workorder-closed", getAllClosedWorkOrders());
		viewMap.put("workorder-openfirealarms", getFireSafetyWOs());
		
		viewMap.put("workorder-overdue", getAllOverdueWorkOrders());
		viewMap.put("workorder-myoverdue", getMyOverdueWorkOrders());
		viewMap.put("workorder-my", getMyWorkOrders());
		viewMap.put("task-my", getMyTasks());
		
		viewMap.put("asset-energy", getAssets("Energy"));
		viewMap.put("asset-hvac", getAssets("HVAC"));
		viewMap.put("asset-active", getAssetsByState("Active"));
		viewMap.put("asset-retired", getAssetsByState("Retired"));
		
		
		//Add module name in field objects
		
		//viewMap.put("workorder-all", getAllWorkorders());
		
		return viewMap;
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
		statusField.setName("name");
		statusField.setColumnName("NAME");
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
		
		return assetView;
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
	
	private static Criteria getOpenStatusCriteria() {
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
	
	private static Condition getOpenStatusCondition(FacilioModule module) {
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
		
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getWorkOrdersModule());
		userField.setExtendedModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);
		
		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		
		Criteria unassignedWOCriteria = new Criteria();
		unassignedWOCriteria.addAndCondition(userFieldCondition);
		
		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned Work Orders");
		unassignedWOView.setCriteria(unassignedWOCriteria);
		
		return unassignedWOView;
	}
	
	private static FacilioView getOpenWorkorderRequests() {
		
		FacilioField field = new FacilioField();
		field.setName("status");
		field.setColumnName("STATUS");
		field.setDataType(FieldType.NUMBER);
		field.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(WorkOrderRequestContext.RequestStatus.OPEN.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		
		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("open");
		allRequestsView.setDisplayName("Open Requests");
		allRequestsView.setCriteria(criteria);
		return allRequestsView;
	}
	
	private static FacilioView getRejectedWorkorderRequests() {
		
		FacilioField field = new FacilioField();
		field.setName("status");
		field.setColumnName("STATUS");
		field.setDataType(FieldType.NUMBER);
		field.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(WorkOrderRequestContext.RequestStatus.REJECTED.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		
		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("rejected");
		allRequestsView.setDisplayName("Rejected Requests");
		allRequestsView.setCriteria(criteria);
		return allRequestsView;
	}
	
	private static FacilioView getAllOpenWorkOrders() {
		//All Open Tickets
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("open");
		openTicketsView.setDisplayName("Open Work Orders");
		openTicketsView.setCriteria(criteria);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllClosedWorkOrders() {
		//All Open Tickets
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getWorkOrdersModule());
		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
		
		Condition ticketClose = new Condition();
		ticketClose.setField(statusField);
		ticketClose.setOperator(LookupOperator.LOOKUP);
		ticketClose.setCriteriaValue(getCloseStatusCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("closed");
		openTicketsView.setDisplayName("Closed Work Orders");
		openTicketsView.setCriteria(criteria);
		
		return openTicketsView;
	}
	
	private static FacilioView getMyOpenWorkOrders() {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myopen");
		openTicketsView.setDisplayName("My Open Work Orders");
		openTicketsView.setCriteria(criteria);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllOverdueWorkOrders() {
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
		
		FacilioView overdueView = new FacilioView();
		overdueView.setName("overdue");
		overdueView.setDisplayName("Overdue Work Orders");
		overdueView.setCriteria(criteria);
		return overdueView;
	}
	
	private static FacilioView getMyOverdueWorkOrders() {
		
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
		criteria.addAndCondition(getMyUserCondition(ModuleFactory.getWorkOrdersModule()));
		
		FacilioView view = new FacilioView();
		view.setName("myoverduetickets");
		view.setDisplayName("My Overdue Tickets");
		view.setCriteria(criteria);
		return view;
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
		categoryField.setModule(ModuleFactory.getWorkOrdersModule());
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
		sourceField.setModule(ModuleFactory.getWorkOrdersModule());
		sourceField.setExtendedModule(ModuleFactory.getTicketsModule());
		
		Condition alarmSourceCondition = new Condition();
		alarmSourceCondition.setField(sourceField);
		alarmSourceCondition.setOperator(NumberOperators.EQUALS);
		alarmSourceCondition.setValue(String.valueOf(TicketContext.SourceType.ALARM.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafetyCategory);
		criteria.addAndCondition(getOpenStatusCondition(ModuleFactory.getWorkOrdersModule()));
		criteria.addAndCondition(alarmSourceCondition);
		
		FacilioView fireSafetyWOView = new FacilioView();
		fireSafetyWOView.setName("openfirealarms");
		fireSafetyWOView.setDisplayName("Fire Alarm Work Orders");
		fireSafetyWOView.setCriteria(criteria);
		
		return fireSafetyWOView;
	}
}
