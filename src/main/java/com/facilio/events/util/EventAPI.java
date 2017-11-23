package com.facilio.events.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class EventAPI {
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
	    	else if(key.equalsIgnoreCase("message"))
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
	    if(event.getSeverity() == null) {
	    	event.setSeverity("Idle");
	    }
	    event.setOrgId(orgId);
	    event.setEventState(EventState.READY);
	    event.setInternalState(EventInternalState.ADDED);
	    
	    if(event.getNode() != null) {
	    	long assetId = getAssetFromNode(event.getNode(), orgId);
	    	if(assetId != -1) {
	    		event.setAssetId(assetId);
	    	}
	    	else {
	    		addNodeToAssetMapping(event.getNode(), orgId);
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
	
	public static long getAssetFromNode(String node, long orgId) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeAssetMappingModule().getTableName())
																.select(EventConstants.EventFieldFactory.getNodeToAssetMappingFields())
																.andCustomWhere("ORGID = ? AND NODE = ?", orgId, node);
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			Long assetId = (Long) props.get(0).get(EventConstants.EventContextNames.ASSET_ID);
			if(assetId != null) {
				return assetId;
			}
		}
		return -1;
	}
	
	public static long addNodeToAssetMapping(String node, long orgId) throws SQLException, RuntimeException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", orgId);
		prop.put(EventConstants.EventContextNames.NODE, node);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeAssetMappingModule().getTableName())
																.fields(EventConstants.EventFieldFactory.getNodeToAssetMappingFields())
																.addRecord(prop);
		
		insertRecordBuilder.save();
		return (long) prop.get("id");
	}
	
	public static void updateAssetForNode(long assetId, String node, long orgId) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put(EventConstants.EventContextNames.ASSET_ID, assetId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(EventConstants.EventModuleFactory.getNodeAssetMappingModule().getTableName())
														.fields(EventConstants.EventFieldFactory.getNodeToAssetMappingFields())
														.andCustomWhere("ORGID = ? AND NODE = ?", orgId, node);
		
		updateBuilder.update(prop);
	}
	
	public static List<Map<String, Object>> getAllNodes(long orgId) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(EventConstants.EventModuleFactory.getNodeAssetMappingModule().getTableName())
																.select(EventConstants.EventFieldFactory.getNodeToAssetMappingFields())
																.andCustomWhere("ORGID = ?", orgId);

		return selectRecordBuilder.get();
	}
}
