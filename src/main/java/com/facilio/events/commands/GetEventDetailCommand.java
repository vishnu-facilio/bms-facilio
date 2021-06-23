package com.facilio.events.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.modules.FieldUtil;

public class GetEventDetailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long eventId = (long) context.get(EventConstants.EventContextNames.EVENT_ID);
		if(eventId != -1) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.table("Event")
															.select(EventConstants.EventFieldFactory.getEventFields())
															.andCustomWhere("ORGID = ? AND ID = ?", AccountUtil.getCurrentOrg().getOrgId(), eventId);
			List<Map<String, Object>> eventProps = selectBuilder.get();
			
			if(eventProps != null && !eventProps.isEmpty()) {
				Map<String, Object> eventProp = eventProps.get(0);
				EventContext event = FieldUtil.getAsBeanFromMap(eventProp, EventContext.class);
				
				if (event.getResourceId() != -1) {
					event.setResource(ResourceAPI.getExtendedResource(eventId));
				}
				
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		return false;
	}

}
