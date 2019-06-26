package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.time.DateTimeUtil;

public class InsertNewEventsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<BaseEventContext> baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
//			Map<Type, List<BaseEventContext>>  eventsMap = new HashMap<>();
			for (BaseEventContext baseEvent : baseEvents) {
//				List<BaseEventContext> list = eventsMap.get(baseEvent.getAlarmType());
//				if (list == null) {
//					list = new ArrayList<>();
//					eventsMap.put(baseEvent.getAlarmType(), list);
//				}
				updateEventObject(baseEvent);
//				list.add(baseEvent);
			}
			
//			context.put("eventMap", eventsMap);
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
