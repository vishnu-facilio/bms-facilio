package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class getRelatedEvents implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
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
