package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
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
		//viewMap.put("allrequests", getAllWorkorderRequests());
		viewMap.put("allopentickets", getAllOpenTickets());
		viewMap.put("myopentickets", getMyOpenTickets());
		viewMap.put("overduetickets", getAllOverdueTickets());
		viewMap.put("myoverduetickets", getMyOverdueTickets());
		viewMap.put("mytickets", getMyTickets());
		viewMap.put("mytasks", getMyTasks());
		//viewMap.put("all", getAllWorkorders());
		
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
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, statusOpen);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
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
	
	private static FacilioView getAllWorkorderRequests() {
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
		ticketRequested.setCriteriaValue(getRequestedStatusCriteria());
		
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
		ticketField.setModuleTableName("WorkOrderRequests");
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
		
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, ticketOpen);
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("allopentickets");
		openTicketsView.setDisplayName("All Open Tickets");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
	
	private static FacilioView getMyOpenTickets() {
		FacilioView view = getAllOpenTickets();
		view.setName("myopentickets");
		view.setDisplayName("My Open Tickets");
		view.getCriteria().addAndCondition(getMyUserCondition("Tickets"));
		return view;
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
}
