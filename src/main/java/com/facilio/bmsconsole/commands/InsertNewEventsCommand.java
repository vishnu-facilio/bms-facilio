package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.time.DateTimeUtil;

public class InsertNewEventsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<BaseEventContext> baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			for (BaseEventContext baseEvent : baseEvents) {
				updateEventObject(baseEvent);
			}
		}
		return false;
	}

	private void updateEventObject(BaseEventContext baseEvent) throws Exception {
		if (baseEvent.getSeverity() == null) {
			baseEvent.setSeverity(AlarmAPI.getAlarmSeverity(baseEvent.getSeverityString()));
		}
		
		if (baseEvent.getCreatedTime() == -1) {
			baseEvent.setCreatedTime(DateTimeUtil.getCurrenTime());
		}
		
		if (baseEvent.getSeverity() == null) {
			throw new IllegalArgumentException("Severity of event cannot be empty");
		}
	}

}
