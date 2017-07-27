package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.UserOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;

public class ViewFactory {
	
	private static Map<String, FacilioView> views = Collections.unmodifiableMap(initViews());
	public static FacilioView getView (String viewName) {
		return views.get(viewName);
	}
	
	private static Map<String, FacilioView> initViews() {
		
		Map<String, FacilioView> viewMap = new HashMap<>();
		viewMap.put("allopentickets", getAllOpenTickets());
		viewMap.put("myopentickets", getMyOpenTickets());
		viewMap.put("overduetickets", getAllOverdueTickets());
		viewMap.put("myoverduetickets", getMyOverdueTickets());
		viewMap.put("mytickets", getMyTickets());
		viewMap.put("mytasks", getMyTasks());
		
		return viewMap;
	}
	
	private static FacilioView getAllOpenTickets() {
		//All Open Tickets
		FacilioField statusField = new FacilioField();
		statusField.setName("statusCode");
		statusField.setColumnName("STATUS");
		statusField.setDataType(FieldType.NUMBER);
		
		Condition ticketOpen = new Condition();
		ticketOpen.setField(statusField);
		ticketOpen.setOperator(NumberOperators.EQUALS);
	//	ticketOpen.setValue(String.valueOf(TicketContext.Status.SUBMITTED.getStatusAsInt()));
		
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
		view.getCriteria().addAndCondition(getMyUserCondition());
		return view;
	}
	
	private static FacilioView getAllOverdueTickets() {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		
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
		view.getCriteria().addAndCondition(getMyUserCondition());
		return view;
	}
	
	private static FacilioView getMyTickets() {
		Map<Integer, Condition> conditions = new HashMap<>();
		conditions.put(1, getMyUserCondition());
		
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
		conditions.put(1, getMyUserCondition());
		
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern("(1)");
		
		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("mytasks");
		openTicketsView.setDisplayName("My Tasks");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}
		
	private static Condition getMyUserCondition() {
		FacilioField userField = new FacilioField();
		userField.setName("assignedToId");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		
		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(UserOperators.IS);
		myUserCondition.setValue(UserOperators.IS.getDynamicParameter());
		
		return myUserCondition;
	}
}
