package com.facilio.events.util;


import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;

public class EventAPI {
//	 public static long processEvents(long timestamp, JSONObject object, List<EventRule> eventRules, Map<String, Integer> eventCountMap, long lastEventTime) throws Exception {
//    	FacilioContext context = new FacilioContext();
//    	context.put(EventConstants.EventContextNames.EVENT_RULE_LIST, eventRules);
//    	context.put(EventConstants.EventContextNames.EVENT_TIMESTAMP, timestamp);
//    	context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, object);
//    	context.put(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP, lastEventTime);
//    	context.put(EventConstants.EventContextNames.EVENT_COUNT_MAP, eventCountMap);
//    	
//    	FacilioChain processEventChain = EventConstants.EventChainFactory.processEventChain();
//    	processEventChain.execute(context);
//        return (long) context.get(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP);
//    }
	
	private static final Logger LOGGER = LogManager.getLogger(EventAPI.class.getName());
	private static final FacilioModule SOURCE_TO_RESOURCE_MODULE = EventConstants.EventModuleFactory.getSourceToResourceMappingModule();
	private static final List<FacilioField> SOURCE_TO_RESOURCE_FIELDS = EventConstants.EventFieldFactory.getSourceToResourceMappingFields();

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
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			JSONArray jsonArray = new JSONArray();
			object.put("controllerId", 23);
			jsonArray.add(object);
			context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, jsonArray);
			FacilioChain c = TransactionChainFactory.getV2AddEventPayloadChain();
			c.execute(context);
			return System.currentTimeMillis();
		}
		else {
			FacilioChain processEventChain = EventConstants.EventChainFactory.processEventChain();
			processEventChain.execute(context);
		}
	    return (long) context.get(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP);
	 } 
	 
	public static EventContext transformEvent(EventContext event, JSONTemplate template, Map<String, Object> placeHolders) throws Exception {
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		JSONObject content = template.getTemplate(placeHolders);
		eventProp.putAll(FieldUtil.getAsProperties(content));
		event = FieldUtil.getAsBeanFromMap(eventProp, EventContext.class);
		event.setMessageKey(null);
		eventProp.put("messageKey", event.getMessageKey()); //Setting the new key in case if it's updated
		CommonCommandUtil.appendModuleNameInKey(null, "event", eventProp, placeHolders);//Updating the placeholders with the new event props
		return event;
	}

	public static List<EventContext> processPayloadsAndEvents (List<JSONObject> payloads) throws Exception {
		List<EventContext> events = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();

		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table(EventConstants.EventModuleFactory.getEventModule().getTableName())
												.fields(EventConstants.EventFieldFactory.getEventFields())
											;
		for (JSONObject payload : payloads) {
			EventContext event = EventAPI.processPayload(-1l, payload, orgId);
			
			if(AccountUtil.getCurrentOrg().getId() == 78l) {
				long alarmId = event.getAlarmId();
				if(alarmId == 3463448l) {
					LOGGER.error("EventPayload : "+event.getAlarmId() +" severity "+event.getSeverity());
				}
			}
			events.add(event);
			Map<String, Object> prop = FieldUtil.getAsProperties(event);
			builder.addRecord(prop);
		}

		builder.save();

		List<Map<String, Object>> records = builder.getRecords();
		for (int i = 0; i < records.size(); i++) {
			events.get(i).setId((long) records.get(i).get("id"));
		}

		return events;
	}

	@SuppressWarnings({ "unchecked"})
	private static EventContext processPayload(long timestamp, JSONObject payload, long orgId) throws Exception
	{
		if (orgId == 75) {
			LOGGER.debug("EventSyncJob Payload:::" + payload);
		}
	    EventContext event = new EventContext();
	    Iterator<String> iterator = payload.keySet().iterator();
	    while(iterator.hasNext())
	    {
	    	String key = iterator.next();
	    	if (key != null && !key.isEmpty() && payload.get(key) != null) {
				String value=null;
				if(key.equals(AgentConstants.DATA)){
					event.setSources ((List<Map<String, Object>>)payload.get(key));
					continue;
				}else {
					value = payload.get(key).toString();
				}
				if(key.equalsIgnoreCase("entity") || key.equalsIgnoreCase("condition"))
		    	{
		    		event.setCondition(value);
		    	}
		    	else if(key.equalsIgnoreCase("source"))
		    	{
		    		event.setSource(value);
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
		    	else if (key.equals("resourceId")) {
		    		event.setResourceId(Long.parseLong(value));
		    	}
		    	else if (key.equals("siteId")) {
		    		event.setSiteId(Long.parseLong(value));
		    	}
		    	else if (key.equals("comment")) {
		    		event.setComment(value);
		    	}
				else if (key.equals(AgentConstants.AGENT)) {
					event.setAgentId(getAgent(value));
				}
		    	else {
		    		event.addAdditionInfo(key, value);
		    	}
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
	    
	    if(event.getSource() != null) {
	    	long resourceId = getResourceFromSource(event.getSource(), orgId,event.getControllerId());
	    	if(resourceId != -1) {
	    		if(resourceId != 0) {
	    			event.setResourceId(resourceId);
	    		}
	    	}
	    	else {
	    		addSourceToResourceMapping(event.getSource(), orgId,event.getControllerId(), event.getAgentId());
	    	}
	    }

		if(event.getSources() != null && !event.getSources().isEmpty() && event.getAgentId() < 0) {
			addBulkSources(event.getSources(),event.getAgentId());
		}
	    return event;
	}

	public static void addBulkSources(List<Map<String,Object>> sources,long agentId) {
		bulkAddSourceToResourceMapping(sources,agentId);
	}

	private static void bulkAddSourceToResourceMapping ( List<Map<String, Object>> sources,long agentId ) {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder();
		sources.forEach(source -> {
			try {
				Map<String, Object> map = new HashMap<>();
				if(source.containsKey(AgentConstants.NAME) && source.get(AgentConstants.NAME) != null) {
					map.put(AgentConstants.SOURCE,source.get(AgentConstants.NAME));
					map.put(AgentConstants.AGENT_ID,agentId);
					builder.fields(SOURCE_TO_RESOURCE_FIELDS)
							.table(SOURCE_TO_RESOURCE_MODULE.getTableName())
							.addRecord(map).save();
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred while inserting alarm mapping sources. agentId " + agentId);
			}
		});
	}

	public static long getAgent(String agentKey) throws Exception {
		FacilioAgent agent = AgentApiV2.getAgent(agentKey);
		if(agent != null) {
			return agent.getId();
		}
		return -1L;
	}

	public static void updateSourceResource ( long sourceId ) throws SQLException {
		Map<String, Object> map = new HashMap<>();
		map.put(AgentConstants.RESOURCE_ID,-99L);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(SOURCE_TO_RESOURCE_FIELDS).table(SOURCE_TO_RESOURCE_MODULE.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(sourceId,SOURCE_TO_RESOURCE_MODULE));
		builder.update(map);
	}

	public static void updateEvent(EventContext event, long orgId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
				.table("Event")
				.fields(EventConstants.EventFieldFactory.getEventFields())
				.andCustomWhere("ORGID = ? AND ID = ?", orgId, event.getId());
		updatebuilder.update(FieldUtil.getAsProperties(event));
	}
	
	public static long getResourceFromSource(String source, long orgId,long controllerId) throws Exception {
		FacilioModule module=EventConstants.EventModuleFactory.getSourceToResourceMappingModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(EventConstants.EventFieldFactory.getSourceToResourceMappingFields())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition("SOURCE",EventConstants.EventContextNames.SOURCE, source, StringOperators.IS));
																//.andCustomWhere("ORGID = ? AND SOURCE = ? AND CONTROLLER_ID = ?", orgId, source,controllerId);
		
		if (controllerId != -1) {
			selectRecordBuilder.andCondition(getControllerIdCondition(controllerId, module));
		}
		
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

	public static long addSourceToResourceMapping(String source, long orgId,long controllerId,long agentId) throws SQLException, RuntimeException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", orgId);
		prop.put(EventConstants.EventContextNames.SOURCE, source);
		prop.put(EventConstants.EventContextNames.CONTROLLER_ID, controllerId);
		prop.put(AgentConstants.AGENT_ID, agentId);

		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
																.table(SOURCE_TO_RESOURCE_MODULE.getTableName())
																.fields(SOURCE_TO_RESOURCE_FIELDS)
																.addRecord(prop);
		
		insertRecordBuilder.save();
		return (long) prop.get("id");
	}
	
	public static void updateResourceForSource(long assetId, String source, long orgId,long controllerId) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put(EventConstants.EventContextNames.RESOURCE_ID, assetId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(SOURCE_TO_RESOURCE_MODULE.getTableName())
														.fields(SOURCE_TO_RESOURCE_FIELDS)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("SOURCE",EventConstants.EventContextNames.SOURCE, source, StringOperators.IS))
														.andCondition(getControllerIdCondition(controllerId, SOURCE_TO_RESOURCE_MODULE));
		updateBuilder.update(prop);
	}

	public static Map<String, Object> getSource(long id) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(SOURCE_TO_RESOURCE_MODULE.getTableName())
				.select(SOURCE_TO_RESOURCE_FIELDS)
				.andCondition(CriteriaAPI.getIdCondition(id,SOURCE_TO_RESOURCE_MODULE));
		return selectRecordBuilder.fetchFirst();
	}
	
	public static void updateResourceForSource(long assetId, long id, long orgId) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put(EventConstants.EventContextNames.RESOURCE_ID, assetId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(SOURCE_TO_RESOURCE_MODULE.getTableName())
														.fields(SOURCE_TO_RESOURCE_FIELDS)
														.andCondition(CriteriaAPI.getIdCondition(id, SOURCE_TO_RESOURCE_MODULE));
		updateBuilder.update(prop);
	}
	public static List<Map<String, Object>> getAllSources(FacilioContext context) throws Exception {
		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		JSONObject pagination = (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.PAGINATION,null);
		long agentId = (long)context.getOrDefault(AgentConstants.AGENT_ID,-1L);
		boolean mapped = (boolean)context.getOrDefault("mapped",false);
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(SOURCE_TO_RESOURCE_FIELDS);
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(SOURCE_TO_RESOURCE_MODULE.getTableName());

		if(agentId > 0) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(SOURCE_TO_RESOURCE_MODULE),String.valueOf(agentId),NumberOperators.EQUALS));
		}
		if(mapped) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE));
			fields.add(fieldMap.get("name"));
			fields.add(fieldMap.get("resourceType"));
			selectRecordBuilder.leftJoin(module.getTableName())
					.on(SOURCE_TO_RESOURCE_MODULE.getTableName() + ".RESOURCE_ID = Resources.ID");
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getPointResourceIdField(SOURCE_TO_RESOURCE_MODULE),CommonOperators.IS_NOT_EMPTY));
		} else {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getPointResourceIdField(SOURCE_TO_RESOURCE_MODULE),CommonOperators.IS_EMPTY));
		}
		selectRecordBuilder.select(fields);
		if (pagination != null && !pagination.isEmpty()) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectRecordBuilder.offset(offset);
			selectRecordBuilder.limit(perPage);
		}
		else {
			selectRecordBuilder.limit(50);
		}
		if (fetchCount) {
			selectRecordBuilder.select(new ArrayList<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT,
					FieldFactory.getIdField(SOURCE_TO_RESOURCE_MODULE));
		}
		return selectRecordBuilder.get();
	}

	public static EventContext getEvent(long id) throws Exception {
		List<EventContext> events = getEvents(Collections.singletonList(id));
		if (CollectionUtils.isNotEmpty(events)) {
			return events.get(0);
		}
		return null;
	}
	
	public static List<EventContext> getEvents(Collection<Long> ids) throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(EventConstants.EventFieldFactory.getEventFields())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, EventContext.class);
		}
		return null;
	}
	
	public static List<EventContext> getEvent(Criteria criteria) throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(EventConstants.EventFieldFactory.getEventFields())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<EventContext> events = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				events.add(FieldUtil.getAsBeanFromMap(prop, EventContext.class));
			}
			return events;
		}
		return null;
	}
	
	private static Condition getControllerIdCondition(long id, FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getControllerIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(id));
		return idCondition;
	}
}
