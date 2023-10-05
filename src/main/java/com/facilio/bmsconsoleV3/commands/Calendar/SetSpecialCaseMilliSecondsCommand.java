package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.enums.EventTypeEnum;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class SetSpecialCaseMilliSecondsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3EventContext> eventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(eventContextList)){
            return false;
        }
        for(V3EventContext eventContext : eventContextList){
            if(eventContext.getEventTypeEnum() == EventTypeEnum.SPECIAL_TYPE){
                int year = eventContext.getScheduledYear();
                int month =  eventContext.getScheduledMonth();
                int date = eventContext.getScheduledDate();
                ZonedDateTime zonedDateTime = ZonedDateTime.of(year,month,date,0,0,0,0, DBConf.getInstance().getCurrentZoneId());
                eventContext.setSpecialCaseMilliSecond(zonedDateTime.toEpochSecond()*1000);
            }
        }
        return false;
    }
}
