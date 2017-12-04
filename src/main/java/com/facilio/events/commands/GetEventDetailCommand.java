package com.facilio.events.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetEventDetailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
				context.put(EventConstants.EventContextNames.EVENT, FieldUtil.getAsBeanFromMap(eventProp, EventContext.class));
			}
		}
		return false;
	}

}
