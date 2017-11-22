package com.facilio.events.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
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
	    
	    return event;
	}
	
	public static void updateEvent(EventContext event, long orgId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
				.table("Event")
				.fields(EventConstants.EventFieldFactory.getEventFields())
				.andCustomWhere("ORGID = ? AND ID = ?", orgId, event.getId());
		updatebuilder.update(FieldUtil.getAsProperties(event));
	}
}
