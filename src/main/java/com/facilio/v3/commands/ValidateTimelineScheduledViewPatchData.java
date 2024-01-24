package com.facilio.v3.commands;

import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.util.TimelineViewUtil;
import com.facilio.weekends.WeekendUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;

public class ValidateTimelineScheduledViewPatchData extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ValidateTimelineScheduledViewPatchData.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        TimelineScheduledViewContext timelineView = viewObj.getTimelineScheduledViewContext();

        if (timelineView == null) {
            throw new IllegalArgumentException("Invalid View details passed");
        }

        JSONObject updateData = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);
        JSONObject oldData = FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP));

        FacilioField groupByField = timelineView.getGroupByField();
        FacilioField startTimeField = timelineView.getStartDateField();
        FacilioField endTimeField = timelineView.getEndDateField();

        boolean isAllowRescheduling = timelineView.isAllowRescheduling();
        boolean isAllowReAssignment = timelineView.isAllowReAssignment();
        boolean isAllowGroupAssignment = timelineView.isAllowGroupAssignment();
        boolean isAllowPastAssignment = timelineView.isAllowPastAssignment();
        boolean isDisableWeekends = timelineView.isDisableWeekends();

        String groupFieldVal = updateData.containsKey(groupByField.getName()) ? TimelineViewUtil.getTimelineSupportedFieldValue(updateData.get(groupByField.getName()), groupByField) : null;
        String oldGroupFieldVal = oldData.containsKey(groupByField.getName()) ? TimelineViewUtil.getTimelineSupportedFieldValue(oldData.get(groupByField.getName()), groupByField) : null;

        String startFieldVal = updateData.containsKey(startTimeField.getName()) ? String.valueOf(updateData.get(startTimeField.getName())) : null;
        String oldStartFieldVal = oldData.containsKey(startTimeField.getName()) ? String.valueOf(oldData.get(startTimeField.getName())) : null;

        String endFieldVal = null, oldEndFieldVal = null;
        if(endTimeField != null) {
            endFieldVal = (updateData.containsKey(endTimeField.getName()) && updateData.get(endTimeField.getName()) != null) ? String.valueOf(updateData.get(endTimeField.getName())) : null;
            oldEndFieldVal = (oldData.containsKey(endTimeField.getName()) && oldData.get(endTimeField.getName()) != null) ? String.valueOf(oldData.get(endTimeField.getName())) : null;
        }
        if (startFieldVal == null) {
            throw new IllegalArgumentException("Invalid time passed");
        }

        ZonedDateTime zonedStartDateTime = DateTimeUtil.getZonedDateTime(Long.parseLong(startFieldVal));
        String patchType = null;

        if (!startFieldVal.equals(oldStartFieldVal) || endFieldVal != null && !endFieldVal.equals(oldEndFieldVal)) {
            if (isAllowRescheduling) {
                patchType = FacilioConstants.ContextNames.TIMELINE_PATCHTYPE_RESCHEDULE;
            } else {
                throw new IllegalArgumentException("Rescheduling not allowed");
            }
        }

        if (oldGroupFieldVal != null && oldGroupFieldVal.equals("-1") && (groupFieldVal != null && !groupFieldVal.equals("-1"))) {
            if (isAllowGroupAssignment) {
                patchType = FacilioConstants.ContextNames.TIMELINE_PATCHTYPE_GROUPASSIGN;
            } else {
                throw new IllegalArgumentException("Group Assignment not allowed");
            }
        }

        if (oldGroupFieldVal != null && !oldGroupFieldVal.equals("-1") && groupFieldVal != null && !oldGroupFieldVal.equals(groupFieldVal)) {
            if (isAllowReAssignment) {
                patchType = FacilioConstants.ContextNames.TIMELINE_PATCHTYPE_REASSIGN;
            } else {
                throw new IllegalArgumentException("Group change not allowed");
            }
        }

        if (!isAllowPastAssignment && !startFieldVal.equals(oldStartFieldVal)) {
            ZonedDateTime currentTime = DateTimeUtil.getZonedDateTime(DateTimeUtil.getDayStartTime());
            if (zonedStartDateTime.isBefore(currentTime)) {
                throw new IllegalArgumentException("Start time cannot be a past time");
            }
        }

        if (isDisableWeekends && timelineView.getWeekendId() > 0 && (WeekendUtil.isWeekendDate(zonedStartDateTime, timelineView.getWeekend())
                                                                || (endFieldVal != null && WeekendUtil.isWeekendDate(DateTimeUtil.getZonedDateTime(Long.parseLong(endFieldVal)), timelineView.getWeekend())))) {
            throw new IllegalArgumentException("Cannot reschedule to weekend");
        }

        if (patchType != null) {
            context.put(FacilioConstants.ContextNames.TIMELINE_PATCHTYPE, patchType);
        }

        return false;
    }
}
