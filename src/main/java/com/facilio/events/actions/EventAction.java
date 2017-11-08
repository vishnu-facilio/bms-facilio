package com.facilio.events.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.events.constants.EventConstants;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class EventAction extends ActionSupport {

	private List<EventContext> events;
	public List<EventContext> getEvents() {
		return events;
	}
	public void setEvents(List<EventContext> events) {
		this.events = events;
	}
	
	@SuppressWarnings("unchecked")
	public String eventList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain eventListChain = EventConstants.getEventListChain();
		eventListChain.execute(context);
		
		setEvents((List<EventContext>) context.get(EventConstants.EVENT_LIST));
		return SUCCESS;
	}
	
	private EventProperty eventProperty;
	public EventProperty getEventProperty() {
		return eventProperty;
	}
	public void setEventProperty(EventProperty eventProperty) {
		this.eventProperty = eventProperty;
	}
	
	private EventRule eventRule;
	public EventRule getEventRule() {
		return eventRule;
	}
	public void setEventRule(EventRule eventRule) {
		this.eventRule = eventRule;
	}
	
	private Map<Long, Criteria> eventCriteriaMap;
	public Map<Long, Criteria> getEventCriteriaMap() {
		return eventCriteriaMap;
	}
	public void setEventCriteriaMap(Map<Long, Criteria> eventCriteriaMap) {
		this.eventCriteriaMap = eventCriteriaMap;
	}
	
	@SuppressWarnings("unchecked")
	public String eventRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain eventRulesChain = EventConstants.getEventRulesChain();
		eventRulesChain.execute(context);
		
		setEventProperty((EventProperty) context.get(EventConstants.EVENT_PROPERTY));
		setEventRule((EventRule) context.get(EventConstants.EVENT_RULE));
		setEventCriteriaMap((Map<Long, Criteria>) context.get(EventConstants.EVENT_CRITERIA_MAP));
		
		return SUCCESS;
	}
	
	public String updateEventProperty() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_PROPERTY, eventProperty);
		
		Chain updateEventPropertyChain = EventConstants.updateEventPropertyChain();
		updateEventPropertyChain.execute(context);
		
		return SUCCESS;
	}
	
	private Map<Integer, Condition> filterConditions;
	public Map<Integer, Condition> getFilterConditions() {
		return filterConditions;
	}
	public void setFilterConditions(Map<Integer, Condition> filterConditions) {
		this.filterConditions = filterConditions;
	}
	
	private String filterPattern;
	public String getFilterPattern() {
		return filterPattern;
	}
	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
	}
	
	public String saveEventFilter() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		context.put(EventConstants.FILTER_CRITERIA_PATTERN, filterPattern);
		context.put(EventConstants.FILTER_CONDITIONS, filterConditions);
		
		Chain updateEventFilterChain = EventConstants.updateEventFilterChain();
		updateEventFilterChain.execute(context);
		
		return SUCCESS;
	}
	
	private Map<Integer, Condition> customizeConditions;
	public Map<Integer, Condition> getCustomizeConditions() {
		return customizeConditions;
	}
	public void setCustomizeConditions(Map<Integer, Condition> customizeConditions) {
		this.customizeConditions = customizeConditions;
	}
	
	private String customizePattern;
	public String getCustomizePattern() {
		return customizePattern;
	}
	public void setCustomizePattern(String customizePattern) {
		this.customizePattern = customizePattern;
	}
	
	public String saveEventTransformRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		context.put(EventConstants.CUSTOMIZE_CRITERIA_PATTERN, customizePattern);
		context.put(EventConstants.CUSTOMIZE_CONDITIONS, customizeConditions);
		
		Chain updateEventTransformRuleChain = EventConstants.updateEventTransformRuleChain();
		updateEventTransformRuleChain.execute(context);
		
		return SUCCESS;
	}
}
