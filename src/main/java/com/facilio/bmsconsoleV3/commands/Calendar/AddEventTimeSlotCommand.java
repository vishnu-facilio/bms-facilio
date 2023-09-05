package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.context.calendar.V3EventTimeSlotContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEventTimeSlotCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeSlotModule = modBean.getModule(FacilioConstants.Calendar.EVENT_TIME_SLOT_MODULE_NAME);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3EventContext> calendarEventContextList = (List<V3EventContext>) recordMap.get(FacilioConstants.Calendar.EVENT_MODULE_NAME);
        if(CollectionUtils.isEmpty(calendarEventContextList)){
            return false;
        }
        List<ModuleBaseWithCustomFields> records = new ArrayList<>();
        for(V3EventContext v3CalendarEventContext : calendarEventContextList){
            CalendarApi.deleteTimeSlotOfAnEvent(v3CalendarEventContext.getId());
            V3EventContext eventContext = new V3EventContext();
            eventContext.setId(v3CalendarEventContext.getId());
            if(v3CalendarEventContext.getTimeSlotList() != null){
                List<V3EventTimeSlotContext> eventTimeSlotContextList = v3CalendarEventContext.getTimeSlotList();
                for(V3EventTimeSlotContext eventTimeSlotContext : eventTimeSlotContextList){
                    eventTimeSlotContext.setEvent(eventContext);
                    records.add(eventTimeSlotContext);
                }
            }
        }
        if(CollectionUtils.isEmpty(records)){
            return false;
        }
        V3Util.createRecord(timeSlotModule,records);
        return false;
    }
}
