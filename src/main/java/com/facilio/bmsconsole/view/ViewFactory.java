package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketStatusContext;
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
import com.facilio.constants.FacilioConstants;

public class ViewFactory {
	
	private static Map<String, FacilioView> views = Collections.unmodifiableMap(initViews());
	public static FacilioView getView (String viewName) {
		return views.get(viewName);
	}
	
	private static Map<String, FacilioView> initViews() {
		
		Map<String, FacilioView> viewMap = new HashMap<>();
		viewMap.put("workorderrequest-open", getAllWorkorderOpenRequests());
		viewMap.put("workorderrequest-rejected", getAllWorkorderOpenRequests());
		
		viewMap.put("workorder-open", getAllOpenTickets());
		viewMap.put("workorder-myopen", getMyOpenTickets());
		viewMap.put("workorder-unassigned", getUnassignedWorkorders());
		viewMap.put("workorder-closed", getAllClosedTickets());
		viewMap.put("workorder-openfirealarms", getFireSafetyWOs());
		
		viewMap.put("workorder-overduetickets", getAllOverdueTickets());
		viewMap.put("workorder-myoverduetickets", getMyOverdueTickets());
		viewMap.put("workorder-mytickets", getMyTickets());
		viewMap.put("workorder-mytasks", getMyTasks());
		
		//viewMap.put("workorder-all", getAllWorkorders());
		
		return viewMap;
	}
	
	private static Criteria getNotRequestedStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModuleTableName("TicketStatus");
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.NOT_EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.OPEN.getIntVal()));
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, statusOpen);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		return criteria;
	}
	
	private static Criteria getRequestedStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModuleTableName("TicketStatus");
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.OPEN.getIntVal()));
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, statusOpen);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		return criteria;
	}
	
	private static Criteria getOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModuleTableName("TicketStatus");
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.OPEN.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);
		
		return criteria;
	}
	
	private static Criteria getCloseStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModuleTableName("TicketStatus");
		
		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(TicketStatusContext.StatusType.CLOSED.getIntVal()));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);
		
		return criteria;
	}
	
	private static FacilioView getAllWorkorders() {
		FacilioModule module = new FacilioModule();
		module.setName("ticketstatus");
		module.setTableName("TicketStatus");
		module.setDisplayName("Ticket Status");
		
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModuleTableName("Tickets");
		statusField.setLookupModule(module);
		
		Condition ticketRequested = new Condition();
		ticketRequested.setField(statusField);
		ticketRequested.setOperator(LookupOperator.LOOKUP);
		ticketRequested.setCriteriaValue(getNotRequestedStatusCriteria());
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, ticketRequested);
		
		Criteria ticketCriteria = new Criteria();
		ticketCriteria.setConditions(conditions);
		ticketCriteria.setPattern("(1)");
		
		FacilioModule ticketModule = new FacilioModule();
		ticketModule.setName("ticket");
		ticketModule.setTableName("Tickets");
		ticketModule.setDisplayName("Tickets");
		
		LookupField ticketField = new LookupField();
		ticketField.setName("ticket");
		ticketField.setColumnName("TICKET_ID");
		ticketField.setDataType(FieldType.LOOKUP);
		ticketField.setModuleTableName("WorkOrders");
		ticketField.setLookupModule(ticketModule);
		
		Condition ticketCondition = new Condition();
		ticketCondition.setField(ticketField);
		ticketCondition.setOperator(LookupOperator.LOOKUP);
		ticketCondition.setCriteriaValue(ticketCriteria);
		
		Map<Integer, Condition> ticketConditions = new HashMap<>();
		ticketConditions.put(1, ticketCondition);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(ticketConditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("allrequests");
		openTicketsView.setDisplayName("Work Order Requests");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
	
	private static FacilioView getUnassignedWorkorders() {
		
		FacilioField userField = new FacilioField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModuleTableName("Tickets");
		
		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(userFieldCondition);
		
		FacilioModule module = new FacilioModule();
		module.setName("tickets");
		module.setTableName("Tickets");
		module.setDisplayName("Tickets");
		
		LookupField field = new LookupField();
		field.setName("ticket");
		field.setColumnName("TICKET_ID");
		field.setDataType(FieldType.LOOKUP);
		field.setModuleTableName("WorkOrders");
		field.setLookupModule(module);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(criteria);
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(condition);
		
		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned Workorders");
		unassignedWOView.setCriteria(criteria2);
		
		return unassignedWOView;
	}
	
	private static FacilioView getAllWorkorderOpenRequests() {
		
		FacilioField field = new FacilioField();
		field.setName("status");
		field.setColumnName("STATUS");
		field.setDataType(FieldType.NUMBER);
		field.setModuleTableName("WorkOrderRequests");
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(DateOperators.IS);
		condition.setValue("1");
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, condition);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("allrequests");
		allRequestsView.setDisplayName("Workorder Requests");
		allRequestsView.setCriteria(criteria);
		return allRequestsView;
	}
	
	private static FacilioView getAllOpenTickets() {
		//All Open Tickets
		FacilioModule module = new FacilioModule();
		module.setName("ticketstatus");
		module.setTableName("TicketStatus");
		module.setDisplayName("Ticket Status");
		
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModuleTableName("Tickets");
		statusField.setLookupModule(module);
		
		Condition ticketOpen = new Condition();
		ticketOpen.setField(statusField);
		ticketOpen.setOperator(LookupOperator.LOOKUP);
		ticketOpen.setCriteriaValue(getOpenStatusCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketOpen);
		
		FacilioModule module2 = new FacilioModule();
		module2.setName("ticket");
		module2.setTableName("Tickets");
		module2.setDisplayName("Tickets");
		
		LookupField field = new LookupField();
		field.setName("ticket");
		field.setColumnName("TICKET_ID");
		field.setDataType(FieldType.LOOKUP);
		field.setModuleTableName("WorkOrders");
		field.setLookupModule(module2);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(criteria);
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(condition);
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("allopen");
		openTicketsView.setDisplayName("All Open");
		openTicketsView.setCriteria(criteria2);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllClosedTickets() {
		//All Open Tickets
		FacilioModule module = new FacilioModule();
		module.setName("ticketstatus");
		module.setTableName("TicketStatus");
		module.setDisplayName("Ticket Status");
		
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModuleTableName("Tickets");
		statusField.setLookupModule(module);
		
		Condition ticketOpen = new Condition();
		ticketOpen.setField(statusField);
		ticketOpen.setOperator(LookupOperator.LOOKUP);
		ticketOpen.setCriteriaValue(getCloseStatusCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketOpen);
		
		FacilioModule module2 = new FacilioModule();
		module2.setName("ticket");
		module2.setTableName("Tickets");
		module2.setDisplayName("Tickets");
		
		LookupField field = new LookupField();
		field.setName("ticket");
		field.setColumnName("TICKET_ID");
		field.setDataType(FieldType.LOOKUP);
		field.setModuleTableName("WorkOrders");
		field.setLookupModule(module2);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(criteria);
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(condition);
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("allopen");
		openTicketsView.setDisplayName("All Open");
		openTicketsView.setCriteria(criteria2);
		
		return openTicketsView;
	}
	
	private static FacilioView getMyOpenTickets() {
		
		FacilioModule module = new FacilioModule();
		module.setName("ticketstatus");
		module.setTableName("TicketStatus");
		module.setDisplayName("Ticket Status");
		
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModuleTableName("Tickets");
		statusField.setLookupModule(module);
		
		Condition ticketOpen = new Condition();
		ticketOpen.setField(statusField);
		ticketOpen.setOperator(LookupOperator.LOOKUP);
		ticketOpen.setCriteriaValue(getOpenStatusCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketOpen);
		criteria.addAndCondition(getMyUserCondition("Tickets"));
		
		FacilioModule module2 = new FacilioModule();
		module2.setName("ticket");
		module2.setTableName("Tickets");
		module2.setDisplayName("Tickets");
		
		LookupField field = new LookupField();
		field.setName("ticket");
		field.setColumnName("TICKET_ID");
		field.setDataType(FieldType.LOOKUP);
		field.setModuleTableName("WorkOrders");
		field.setLookupModule(module2);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(criteria);
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(condition);
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("allopen");
		openTicketsView.setDisplayName("All Open");
		openTicketsView.setCriteria(criteria2);
		
		return openTicketsView;
	}
	
	private static FacilioView getAllOverdueTickets() {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModuleTableName("Tickets");
		
		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TILL_YESTERDAY);
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, overdue);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("overduetickets");
		openTicketsView.setDisplayName("Overdue Tickets");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
	
	private static FacilioView getMyOverdueTickets() {
		FacilioView view = getAllOverdueTickets();
		view.setName("myoverduetickets");
		view.setDisplayName("My Overdue Tickets");
		view.getCriteria().addAndCondition(getMyUserCondition("Tickets"));
		return view;
	}
	
	private static FacilioView getMyTickets() {
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, getMyUserCondition("Tickets"));
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("mytickets");
		openTicketsView.setDisplayName("My Tickets");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
	
	private static FacilioView getMyTasks() {
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, getMyUserCondition("Tasks"));
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("mytasks");
		openTicketsView.setDisplayName("My Tasks");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
		
	public static Condition getMyUserCondition(String moduleTableName) {
		FacilioField userField = new FacilioField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModuleTableName(moduleTableName);
		
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
		categoryNameField.setModuleTableName("TicketCategory");
		
		Condition fireSafety = new Condition();
		fireSafety.setField(categoryNameField);
		fireSafety.setOperator(StringOperators.IS);
		fireSafety.setValue("Fire Safety");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafety);
		
		return criteria;
	}
	
	private static FacilioView getFireSafetyWOs() {
		//All Open Tickets
		FacilioModule categoryModule = new FacilioModule();
		categoryModule.setName(FacilioConstants.ContextNames.TICKET_CATEGORY);
		categoryModule.setTableName("TicketCategory");
		categoryModule.setDisplayName("Ticket Category");
		
		LookupField categoryField = new LookupField();
		categoryField.setName("category");
		categoryField.setColumnName("CATEGORY_ID");
		categoryField.setDataType(FieldType.LOOKUP);
		categoryField.setModuleTableName("Tickets");
		categoryField.setLookupModule(categoryModule);
		
		Condition fireSafetyTickets = new Condition();
		fireSafetyTickets.setField(categoryField);
		fireSafetyTickets.setOperator(LookupOperator.LOOKUP);
		fireSafetyTickets.setCriteriaValue(getFireSafetyCategoryCriteria());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafetyTickets);
		
		FacilioModule ticketModule = new FacilioModule();
		ticketModule.setName(FacilioConstants.ContextNames.TICKET);
		ticketModule.setTableName("Tickets");
		ticketModule.setDisplayName("Tickets");
		
		LookupField field = new LookupField();
		field.setName("ticket");
		field.setColumnName("TICKET_ID");
		field.setDataType(FieldType.LOOKUP);
		field.setModuleTableName("WorkOrders");
		field.setLookupModule(ticketModule);
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(criteria);
		
		Criteria fireSafetyWOcriteria = new Criteria();
		fireSafetyWOcriteria.addAndCondition(condition);
		
		FacilioView fireSafetyWOView = new FacilioView();
		fireSafetyWOView.setName("openfirealarms");
		fireSafetyWOView.setDisplayName("Fire Alarm Workorders");
		fireSafetyWOView.setCriteria(fireSafetyWOcriteria);
		
		return fireSafetyWOView;
	}
}
