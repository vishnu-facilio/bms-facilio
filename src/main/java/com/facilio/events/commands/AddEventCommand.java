package com.facilio.events.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(EventConstants.EVENT);
		event.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
	    event.setCreatedTime(System.currentTimeMillis());
	    event.setState(EventContext.EventState.READY);
	    event.setInternalState(EventContext.EventInternalState.ADDED);
		
		Map<String, Object> props = FieldUtil.getAsProperties(event);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
															.table("Event")
															.fields(EventConstants.getEventFields())
															.addRecord(props);
		
		builder.save();
		return false;
	}
}
