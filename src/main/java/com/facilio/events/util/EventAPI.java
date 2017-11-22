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
	    	Object value = payload.get(key);
	    	if(key.equals("source"))
	    	{
	    		event.setSource((String) value);
	    	}
	    	else if(key.equals("node"))
	    	{
	    		event.setNode((String) value);
	    	}
	    	else if(key.equals("message"))
	    	{
	    		event.setEventMessage((String) value);
	    	}
	    	else if(key.equals("severity"))
	    	{
	    		event.setSeverity((String) value);
	    	}
	    	else if(key.equals("timestamp")) {
	    		event.setCreatedTime((long) value);
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
	    event.setState(EventState.READY);
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
