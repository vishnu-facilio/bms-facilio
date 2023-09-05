package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;

public class CalendarNightlyJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        List<V3CalendarContext> calendarContextList = CalendarApi.getAvailableCalendar();
        if(CollectionUtils.isEmpty(calendarContextList)){
            return;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        ZonedDateTime endDate = zonedDateTime.plusDays(90);
        for(V3CalendarContext calendarContext : calendarContextList){
            ZonedDateTime generatedUpto = CalendarApi.getGeneratedUptoOfCalendar(calendarContext.getId());
            if(generatedUpto == null){
                continue;
            }
            ZonedDateTime startDate = generatedUpto.plusDays(1);
            if(startDate.isBefore(endDate)){
                CalendarApi.populateCalendarView(calendarContext.getId(),startDate.toEpochSecond()*1000,endDate.toEpochSecond()*1000);
            }
        }
    }
}
