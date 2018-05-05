package com.facilio.events.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRuleContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class EventAPI {
//	 public static long processEvents(long timestamp, JSONObject object, List<EventRule> eventRules, Map<String, Integer> eventCountMap, long lastEventTime) throws Exception {
//    	FacilioContext context = new FacilioContext();
//    	context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, eventRules);
//    	context.put(EventConstants.EventContextNames.EVENT_TIMESTAMP, timestamp);
//    	context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, object);
//    	context.put(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP, lastEventTime);
//    	context.put(EventConstants.EventContextNames.EVENT_COUNT_MAP, eventCountMap);
//    	
//    	Chain processEventChain = EventConstants.EventChainFactory.processEventChain();
//    	processEventChain.execute(context);
//        return (long) context.get(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP);
//    }
	
	public static void populateProcessEventParams(FacilioContext context, long timestamp, JSONObject object, List<EventRuleContext> eventRules, Map<String, Integer> eventCountMap, long lastEventTime) {
    	context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, eventRules);
    	context.put(EventConstants.EventContextNames.EVENT_TIMESTAMP, timestamp);
    	context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, object);
    	context.put(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP, lastEventTime);
    	context.put(EventConstants.EventContextNames.EVENT_COUNT_MAP, eventCountMap);
	}
	 
	 public static long processEvents(long timestamp, JSONObject object, List<EventRuleContext> eventRules, Map<String, Integer> eventCountMap, long lastEventTime) throws Exception {
		FacilioContext context = new FacilioContext();
		populateProcessEventParams(context, timestamp, object, eventRules, eventCountMap, lastEventTime);
		Chain processEventChain = EventConstants.EventChainFactory.processEventChain();
	    processEventChain.execute(context);
	    return (long) context.get(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP);
	 } 
	 
	public static EventContext transformEvent(EventContext event, JSONTemplate template, Map<String, Object> placeHolders) throws Exception {
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		JSONObject content = template.getTemplate(placeHolders);
		eventProp.putAll(FieldUtil.getAsProperties(content));
		event = FieldUtil.getAsBeanFromMap(eventProp, EventContext.class);
		event.setMessageKey(null);
		event.getMessageKey();
		return event;
	}

	@SuppressWarnings({ "unchecked"})
	public static EventContext processPayload(long timestamp, JSONObject payload, long orgId) throws Exception 
	{
	    System.out.println("EventSyncJob Payload:::" + payload);
	    EventContext event = new EventContext();
	    Iterator<String> iterator = payload.keySet().iterator();
	    while(iterator.hasNext())
	    {
	    	String key = iterator.next();
	    	String value = payload.get(key).toString();
	    	if(key.equalsIgnoreCase("source"))
	    	{
	    		event.setSource(value);
	    	}
	    	else if(key.equalsIgnoreCase("node"))
	    	{
	    		event.setNode(value);
	    	}
	    	else if(key.equalsIgnoreCase("message") || key.equalsIgnoreCase("eventMessage"))
	    	{
	    		event.setEventMessage(value);
	    	}
	    	else if(key.equalsIgnoreCase("severity"))
	    	{
	    		event.setSeverity(value);
	    	}
	    	else if(key.equalsIgnoreCase("priority")) {
	    		event.setPriority(value);
	    	}
	    	else if(key.equalsIgnoreCase("alarmClass")) {
	    		event.setAlarmClass(value);
	    	}
	    	else if(key.equals("state")) {
	    		event.setState(value);
	    	}
	    	else if(key.equals("timestamp")) {
	    		event.setCreatedTime(Long.parseLong(value));
	    	}
	    	else
	    	{
	    		event.addAdditionInfo(key, value);
	    	}
	    }
	    if(event.getCreatedTime() == -1) {
	    	event.setCreatedTime(timestamp);
	    }
//	    if(event.getSeverity() == null && (event.getAlarmClass().equals("Critical") || event.getAlarmClass().equals("Major") || event.getAlarmClass().equals("Minor")))
//	    {
//	    		event.setSeverity(event.getAlarmClass());
//	    }
	    if(event.getSeverity() == null) {
	    	event.setSeverity("Info");
	    }
	    event.setOrgId(orgId);
	    event.setEventState(EventState.READY);
	    event.setInternalState(EventInternalState.ADDED);
	    
	    if(event.getNode() != null) {
	    	long resourceId = getResourceFromNode(event.getNode(), orgId);
	    	if(resourceId != -1) {
	    		if(resourceId != 0) {
	    			event.setResourceId(resourceId);
	    		}
	    	}
	    	else {
	    		addNodeToResourceMapping(event.getNode(), orgId);
	    	}
	    }
	    
	    return event;
	}
	
	public static void updateEvent(EventContext event, long orgId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
				.table("Event")
				.fields(EventConstants.EventFieldFactory.getEventFields())
				.andCustomWhere("ORGID = ? AND ID = ?", orgId, event.getId());
		updatebuilder.update(FieldUtil.getAsProperties(event));
	}
	
	public static long getResourceFromNode(String node, long orgId) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeToResourceMappingModule().getTableName())
																.select(EventConstants.EventFieldFactory.getNodeToResourceMappingFields())
																.andCustomWhere("ORGID = ? AND NODE = ?", orgId, node);
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			Long resourceId = (Long) props.get(0).get(EventConstants.EventContextNames.RESOURCE_ID);
			if(resourceId != null) {
				return resourceId;
			}
			else {
				return 0;
			}
		}
		return -1;
	}

	public static void insertEvent(EventContext event, long orgId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
		Map<String, Object> props = FieldUtil.getAsProperties(event);
		insertObject(props);
	}

	public static void insertObject(Map<String, Object> props) throws SQLException {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table("Event")
				.fields(EventConstants.EventFieldFactory.getEventFields());
		builder.addRecord(props);
		builder.save();
	}
	
	public static long addNodeToResourceMapping(String node, long orgId) throws SQLException, RuntimeException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", orgId);
		prop.put(EventConstants.EventContextNames.NODE, node);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeToResourceMappingModule().getTableName())
																.fields(EventConstants.EventFieldFactory.getNodeToResourceMappingFields())
																.addRecord(prop);
		
		insertRecordBuilder.save();
		return (long) prop.get("id");
	}
	
	public static void updateResourceForNode(long assetId, String node, long orgId) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put(EventConstants.EventContextNames.RESOURCE_ID, assetId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(EventConstants.EventModuleFactory.getNodeToResourceMappingModule().getTableName())
														.fields(EventConstants.EventFieldFactory.getNodeToResourceMappingFields())
														.andCustomWhere("ORGID = ? AND NODE = ?", orgId, node);
		
		updateBuilder.update(prop);
	}
	
	public static List<Map<String, Object>> getAllNodes(long orgId) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeToResourceMappingModule().getTableName())
																.select(EventConstants.EventFieldFactory.getNodeToResourceMappingFields())
																.andCustomWhere("ORGID = ?", orgId);

		return selectRecordBuilder.get();
	}
	
	public static EventContext getEvent(long id) throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(EventConstants.EventFieldFactory.getEventFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), EventContext.class);
		}
		return null;
	}
	
	public static List<EventContext> getEvent(Criteria criteria) throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(EventConstants.EventFieldFactory.getEventFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<EventContext> events = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				events.add(FieldUtil.getAsBeanFromMap(props.get(0), EventContext.class));
			}
			return events;
		}
		return null;
	}
}
