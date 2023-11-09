package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.enums.EventTypeEnum;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateEventDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() <= 0){
            return false;
        }
        List<V3EventContext> eventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(eventContextList)){
            return false;
        }
        for(V3EventContext eventContext : eventContextList){
            EventTypeEnum eventTypeEnum = eventContext.getEventTypeEnum();
            switch (eventTypeEnum){
                case DAY_OF_THE_WEEK:{
                    if(eventContext.getScheduledDay() == null){
                        throw new IllegalArgumentException("Day Value Can't be null");
                    }
                    break;
                }
                case SEASON:{
                    if(eventContext.getScheduledDay() == null){
                        throw new IllegalArgumentException("Day Value Can't be null");
                    }
                    if(eventContext.getSeasonStartDate() == null || eventContext.getSeasonStartMonth() == null || eventContext.getSeasonEndDate() == null || eventContext.getSeasonEndMonth() == null){
                        throw new IllegalArgumentException("Season Range Fields can't be Empty");
                    }
                    break;
                }
                case MONTHLY:
                case REGULAR_MAINTENANCE: {
                    if(eventContext.getScheduledDate() == null && eventContext.getScheduledWeekNumber() == null){
                        throw new IllegalArgumentException("Please Enter a Day Or Week");
                    }
                    if(eventContext.getScheduledWeekNumber() != null && eventContext.getScheduledDay() == null){
                        throw new IllegalArgumentException("Week Day Value can't be Null");
                    }
                    break;
                }
                case HOLIDAY:{
                    if(eventContext.getScheduledMonth() == null){
                        throw new IllegalArgumentException("Please Choose a Month");
                    }
                    else if(eventContext.getScheduledDate() == null && eventContext.getScheduledWeekNumber() == null){
                        throw new IllegalArgumentException("Choose a Date Or Week Day");
                    }
                    else if(eventContext.getScheduledWeekNumber() != null && eventContext.getScheduledDay() == null){
                        throw new IllegalArgumentException("Week Day Value can't be Null");
                    }
                    break;
                }
                case SPECIAL_TYPE:{
                    if(eventContext.getScheduledYear() == null || eventContext.getScheduledMonth() == null || eventContext.getScheduledDate() == null){
                        throw new IllegalArgumentException("Please Choose a Date");
                    }
                    break;
                }

            }
        }

        return false;
    }
}
