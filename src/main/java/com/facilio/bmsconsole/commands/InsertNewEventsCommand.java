package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
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

		if (baseEvent instanceof BMSEventContext) {
			if (baseEvent.getResource() == null && ((BMSEventContext) baseEvent).getSource() != null) {
				long controllerId = ((BMSEventContext) baseEvent).getController();
				long orgId = AccountUtil.getCurrentOrg().getId();
				long resourceId = EventAPI.getResourceFromSource(((BMSEventContext) baseEvent).getSource(), orgId, controllerId);
				if(resourceId != -1) {
					if (resourceId != 0) {
						baseEvent.setResource(ResourceAPI.getResource(resourceId));
					}
				}
				else {
					EventAPI.addSourceToResourceMapping(((BMSEventContext) baseEvent).getSource(), orgId, controllerId,((BMSEventContext) baseEvent).getAgentId());
				}
			}
		}
		
		if (baseEvent.shouldIgnore()) {
			baseEvent.setSeverity(AlarmAPI.getAlarmSeverity("Info"));
			baseEvent.setEventState(EventContext.EventState.IGNORED);
		}
		else if (!baseEvent.isSuperCalled()) {
			throw new IllegalArgumentException("method shouldIgnore of BaseEvent is never called");
		}
	}

}
