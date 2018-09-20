package com.facilio.events.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;

@SuppressWarnings("serial")
public class EventAction extends FacilioAction {

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
	
	public String eventExport() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.ALARM_ID, alarmId);
 		context.put(EventConstants.EventContextNames.FIELD_ID, fieldId);
 		context.put(EventConstants.EventContextNames.TYPE, type);
 		context.put(EventConstants.EventContextNames.PARENT_ID, parentId);
 		
		Chain eventListChain = EventConstants.EventChainFactory.getExportFieldsValue();
		eventListChain.execute(context);
		setEvents((List<EventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST));
		setReadingValues((Map<Long,ReadingContext>) context.get(EventConstants.EventContextNames.READING_VALUES));
		setFileUrl((String) context.get(EventConstants.EventContextNames.FILEURL));
		return SUCCESS;
		
	}
	private String fileUrl;
	
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
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
	private Map<Long, ReadingContext> readingValues;
	public Map<Long, ReadingContext> getReadingValues() {
		return readingValues;
	}
	public void setReadingValues(Map<Long, ReadingContext> readingValues) {
		this.readingValues = readingValues;
	}

	private List<Long> fieldId ;
	public List<Long> getFieldId() {
		return fieldId;
	}
	public void setFieldId(List<Long> fieldId) {
		this.fieldId = fieldId;
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
	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	private long parentId;
//	private List<EventRule> eventRules;
//	public List<EventRule> getEventRules() {
//		return eventRules;
//	}
//	public void setEventRules(List<EventRule> eventRules) {
//		this.eventRules = eventRules;
//	}
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private List<EventRuleContext> eventRules;
	public List<EventRuleContext> getEventRules() {
		return eventRules;
	}
	public void setEventRules(List<EventRuleContext> eventRules) {
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
//		Chain eventRulesChain = EventConstants.EventChainFactory.getActiveEventRuleChain();
//		eventRulesChain.execute(context);
		
		
		setEventRules((List<EventRuleContext>) EventRulesAPI.getAllActiveEventRules());
		
		return SUCCESS;
	}
	
//	private EventRule eventRule;
//	public EventRule getEventRule() 
//	{
//		return eventRule;
//	}
//	public void setEventRule(EventRule eventRule) 
//	{
//		this.eventRule = eventRule;
//	}
	private EventRuleContext eventRule;
	public EventRuleContext getEventRule() {
		return eventRule;
	}
	public void setEventRule(EventRuleContext eventRule) {
		this.eventRule = eventRule;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String fetchEventRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		
		Chain getEventRuleChain = EventConstants.EventChainFactory.getEventRuleChain();
		getEventRuleChain.execute(context);
		
		eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		
		return SUCCESS;
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
		
		Chain updateEventRule = EventConstants.EventChainFactory.updateEventRuleChain();
		updateEventRule.execute(context);
		
		return SUCCESS;
	}
	public String deleteEventRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		Chain deleteEventRule = EventConstants.EventChainFactory.deleteEventRuleChain();
		deleteEventRule.execute(context);
		eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		
		return SUCCESS;
	}
	
	public String getAllSources() throws Exception {
		setSources(EventAPI.getAllSources(AccountUtil.getCurrentOrg().getOrgId()));
		return SUCCESS;
	}
	
	public String updateSourceWithResource() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.SOURCE, source);
		context.put(EventConstants.EventContextNames.RESOURCE_ID, resourceId);
		
		Chain updateAssetChain = EventConstants.EventChainFactory.updateNodeToResourceMappingChain();
		updateAssetChain.execute(context);
		setResult(FacilioConstants.ContextNames.RESULT, (String) context.get(FacilioConstants.ContextNames.RESULT));
		return SUCCESS;
	}
	
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	private List<Map<String, Object>> sources;
	public List<Map<String, Object>> getSources() {
		return sources;
	}
	public void setSources(List<Map<String, Object>> sources) {
		this.sources = sources;
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
	
	/******************      V2 Api    ******************/
	
	public String v2eventList() throws Exception{
		eventList();
		setResult(EventConstants.EventContextNames.EVENT_LIST, events);
		return SUCCESS;
		
	}
	
	public String v2viewEvent() throws Exception{
		eventDetail();
		setResult(EventConstants.EventContextNames.EVENT, event);
		return SUCCESS;
	}
 }
