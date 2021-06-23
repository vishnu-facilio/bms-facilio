package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;

public class getRelatedEvents extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(context.get(FacilioConstants.ContextNames.RECORD_LIST) != null) {
			FacilioModule eventModule = EventConstants.EventModuleFactory.getEventModule();
			List<ReadingAlarmContext> readingAlarms = (List<ReadingAlarmContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			
			for(ReadingAlarmContext readingAlarm:readingAlarms) {
				
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
						.table(eventModule.getTableName())
						.select(EventConstants.EventFieldFactory.getEventFields())
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(eventModule))
						.andCustomWhere("Event.ALARM_ID=?", readingAlarm.getId());

				List<Map<String, Object>> props = selectRecordBuilder.get();
				if(props != null && !props.isEmpty()) {
					for(Map<String, Object> prop:props) {
						EventContext eventContext = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
						readingAlarm.addrelatedEvent(eventContext);
					}
					 
				}
			}
		}
		return false;
	}

}
