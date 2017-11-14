package com.facilio.events.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.events.context.EventThresholdRule;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class EventAction extends ActionSupport {

	private EventContext event;
	public EventContext getEvent() {
		return event;
	}
	public void setEvent(EventContext event) {
		this.event = event;
	}
	
	public String addEvent() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT, event);
		Chain getAddEventChain = EventConstants.getAddEventChain();
		getAddEventChain.execute(context);
		
		return SUCCESS;
	}
	
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
	
	private List<EventRule> eventRules;
	public List<EventRule> getEventRules() {
		return eventRules;
	}
	public void setEventRules(List<EventRule> eventRules) {
		this.eventRules = eventRules;
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
		
		setEventRules((List<EventRule>) context.get(EventConstants.EVENT_RULE_LIST));
		
		return SUCCESS;
	}
	
	public String updateEventProperty() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_PROPERTY, eventProperty);
		
		Chain updateEventPropertyChain = EventConstants.updateEventPropertyChain();
		updateEventPropertyChain.execute(context);
		
		return SUCCESS;
	}
	
	private EventRule eventRule;
	public EventRule getEventRule() 
	{
		return eventRule;
	}
	public void setEventRule(EventRule eventRule) 
	{
		this.eventRule = eventRule;
	}
	
	public String addEventRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		
		Chain addEventRuleChain = EventConstants.addEventRuleChain();
		addEventRuleChain.execute(context);
		
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
	
	private Map<String,String> customizeAlarmTemplate;
	public Map<String,String> getCustomizeAlarmTemplate() {
		return customizeAlarmTemplate;
	}
	public void setCustomizeAlarmTemplate(Map<String,String> customizeAlarmTemplate) {
		this.customizeAlarmTemplate = customizeAlarmTemplate;
	}
	
	public String saveEventTransformRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		context.put(EventConstants.CUSTOMIZE_CRITERIA_PATTERN, customizePattern);
		context.put(EventConstants.CUSTOMIZE_CONDITIONS, customizeConditions);
		context.put(EventConstants.CUSTOMIZE_ALARM_TEMPLATE, customizeAlarmTemplate);
		
		Chain updateEventTransformRuleChain = EventConstants.updateEventTransformRuleChain();
		updateEventTransformRuleChain.execute(context);
		
		return SUCCESS;
	}
	
	private List<EventThresholdRule> eventThresholdRules;
	public List<EventThresholdRule> getEventThresholdRules() {
		return eventThresholdRules;
	}
	public void setEventThresholdRules(List<EventThresholdRule> eventThresholdRules) {
		this.eventThresholdRules = eventThresholdRules;
	}
	
	public String saveThresholdRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		context.put(EventConstants.EVENT_THRESHOLD_RULE_LIST, eventThresholdRules);
		
		Chain updateEventThresholdRulesChain = EventConstants.updateEventThresholdRulesChain();
		updateEventThresholdRulesChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateEventRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EVENT_RULE, eventRule);
		
		Chain updateEventRule = EventConstants.updateEventRulesChain();
		updateEventRule.execute(context);
		
		return SUCCESS;
	}
}
