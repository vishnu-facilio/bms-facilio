package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

public class UpdateTimelineViewCalenderTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(FacilioConstants.ContextNames.PMPLANNER);

        for (ModuleBaseWithCustomFields moduleBaseWithCustomField : moduleBaseWithCustomFields) {
            PMPlanner pmPlanner = (PMPlanner) moduleBaseWithCustomField;
            PMTriggerV2 trigger = pmPlanner.getTrigger();
            if(trigger == null){
                continue;
            }
            TimelineViewContext.CalendarViewType calendarViewType = getCalendarViewType(getScheduleInfo(trigger));

            TimelineViewContext timelineViewContext = (TimelineViewContext) pmPlanner.getResourceTimelineView();
            if(timelineViewContext == null){
                timelineViewContext = (TimelineViewContext) ViewAPI.getView(pmPlanner.getResourceTimelineViewId());
            }
            timelineViewContext.setDefaultCalendarView(calendarViewType);
            ViewAPI.updateView(pmPlanner.getResourceTimelineViewId(),timelineViewContext);

            timelineViewContext = (TimelineViewContext) pmPlanner.getStaffTimelineView();
            if(timelineViewContext == null){
                timelineViewContext = (TimelineViewContext) ViewAPI.getView(pmPlanner.getStaffTimelineViewId());
            }
            timelineViewContext.setDefaultCalendarView(calendarViewType);
            ViewAPI.updateView(pmPlanner.getStaffTimelineViewId(),timelineViewContext);
        }
        return false;
    }

    /**
     * Helper function to get the ScheduleInfo object from @param trigger
     *
     * TODO: This method can be moved to some Util class later.
     * @param trigger
     * @return
     * @throws Exception
     */
    private ScheduleInfo getScheduleInfo(PMTriggerV2 trigger) throws Exception {
        String scheduleInfo = trigger.getSchedule();
        JSONParser parser = new JSONParser();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(scheduleInfo), ScheduleInfo.class);
        return schedule;
    }

    /**
     * Helper function get CalendarViewType based on ScheduleInfo's Frequency Type.
     * @param scheduleInfo
     * @return
     */
    private TimelineViewContext.CalendarViewType getCalendarViewType(ScheduleInfo scheduleInfo){
        switch (scheduleInfo.getFrequencyTypeEnum()){
            case DAILY:
                return TimelineViewContext.CalendarViewType.DAY;
            case WEEKLY:
                return TimelineViewContext.CalendarViewType.WEEK;
            case MONTHLY_DAY:
            case MONTHLY_WEEK:
            case QUARTERLY_WEEK:
            case QUARTERLY_DAY:
            case HALF_YEARLY_DAY:
            case HALF_YEARLY_WEEK:
                return TimelineViewContext.CalendarViewType.MONTH;
            case YEARLY:
            case YEARLY_WEEK:
                return TimelineViewContext.CalendarViewType.YEAR;
            case DO_NOT_REPEAT:
            default:
                return TimelineViewContext.CalendarViewType.DAY;
        }
    }
}
