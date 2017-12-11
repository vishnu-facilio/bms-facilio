package com.facilio.events.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class EventAction extends ActionSupport {

	private JSONObject payload;
	public JSONObject getPayload() {
		return payload;
	}
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	
	public String addEvent() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, payload);
		Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
		getAddEventChain.execute(context);
		
		return SUCCESS;
	}
	
	private long eventId = -1;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	public String eventDetail() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_ID, eventId);
		Chain getEventDetailChain = EventConstants.EventChainFactory.getEventDetailChain();
		getEventDetailChain.execute(context);
		
		setEvent((EventContext) context.get(EventConstants.EventContextNames.EVENT));
		
		return SUCCESS;
	}
	
	private EventContext event;
	public EventContext getEvent() {
		return event;
	}
	public void setEvent(EventContext event) {
		this.event = event;
	}
	
	private List<EventContext> events;
	public List<EventContext> getEvents() {
		return events;
	}
	public void setEvents(List<EventContext> events) {
		this.events = events;
	}
	
	private long alarmId = -1;
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	
	@SuppressWarnings("unchecked")
	public String eventList() throws Exception {
		
		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
 		
 		int offset = (this.page - 1) * this.perPage;
 		JSONObject pagination = new JSONObject();
 		pagination.put("offset", offset);
 		pagination.put("limit", this.perPage);
 		
 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
 		context.put(EventConstants.EventContextNames.ALARM_ID, alarmId);
 		
		Chain eventListChain = EventConstants.EventChainFactory.getEventListChain();
		eventListChain.execute(context);
		
		setEvents((List<EventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST));
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
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	@SuppressWarnings("unchecked")
	public String eventRules() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain eventRulesChain = EventConstants.EventChainFactory.getEventRulesChain();
		eventRulesChain.execute(context);
		
		setEventRules((List<EventRule>) context.get(EventConstants.EventContextNames.EVENT_RULE_LIST));
		
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
		context.put(EventConstants.EventContextNames.EVENT_RULE, eventRule);
		
		Chain addEventRuleChain = EventConstants.EventChainFactory.addEventRuleChain();
		addEventRuleChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateEventRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_RULE, eventRule);
		
		Chain updateEventRule = EventConstants.EventChainFactory.updateEventRulesChain();
		updateEventRule.execute(context);
		
		return SUCCESS;
	}
	
	public String getAllNodes() throws Exception {
		setNodes(EventAPI.getAllNodes(AccountUtil.getCurrentOrg().getOrgId()));
		return SUCCESS;
	}
	
	public String updateNodeWithAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.NODE, node);
		context.put(EventConstants.EventContextNames.ASSET_ID, assetId);
		
		Chain updateAssetChain = EventConstants.EventChainFactory.updateNodeToAssetMappingChain();
		updateAssetChain.execute(context);
		result = (String) context.get(FacilioConstants.ContextNames.RESULT);
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	
	private String node;
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	private List<Map<String, Object>> nodes;
	public List<Map<String, Object>> getNodes() {
		return nodes;
	}
	public void setNodes(List<Map<String, Object>> nodes) {
		this.nodes = nodes;
	}
	
	private int page = 1;
	public int getPage() {
		return this.page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	private int perPage = 50;
	public int getPerPage() {
		return this.perPage;
	}
	
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
 }
