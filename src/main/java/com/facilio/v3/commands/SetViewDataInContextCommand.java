package com.facilio.v3.commands;

import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.TimelineRequest;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SetViewDataInContextCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(SetViewDataInContextCommand.class.getName());

    private static final String GROUP_CONCAT_FIELD_NAME = "__groupConcat";
    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        TimelineRequest timelineRequest = (TimelineRequest) context.get(FacilioConstants.ContextNames.TIMELINE_REQUEST);

        if (!timelineRequest.isGetUnGrouped() && CollectionUtils.isEmpty(timelineRequest.getGroupIds())) {
            throw new IllegalArgumentException("At least one group id should be passed");
        }

        TimelineViewContext viewObj = (TimelineViewContext)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if(viewObj == null)
        {
            throw new IllegalArgumentException("Invalid View details passed");
        }

        boolean getUnscheduledOnly = (boolean) context.get(FacilioConstants.ContextNames.TIMELINE_GET_UNSCHEDULED_DATA);
        FacilioField timelineGroupField = viewObj.getGroupByField();
        FacilioField startTimeField = viewObj.getStartDateField();
        FacilioField endTimeField = viewObj.getEndDateField();

        Criteria viewCriteria = viewObj.getCriteria();
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);

        Criteria mainCriteria = buildMainCriteria(startTimeField, endTimeField, timelineRequest, timelineGroupField, viewCriteria, filterCriteria, true, getUnscheduledOnly);

        context.put(FacilioConstants.ContextNames.TIMELINE_GROUP_FIELD, timelineGroupField);
        context.put(FacilioConstants.ContextNames.TIMELINE_STARTTIME_FIELD, startTimeField);
        context.put(FacilioConstants.ContextNames.TIMELINE_ENDTIME_FIELD, endTimeField);
        context.put(FacilioConstants.ContextNames.TIMELINE_DATA_CRITERIA, mainCriteria);

        return false;
    }

    private Criteria buildMainCriteria(FacilioField startTimeField, FacilioField endTimeField, TimelineRequest timelineRequest, FacilioField timelineGroupField, Criteria viewCriteria, Criteria filterCriteria, boolean applyFilter, boolean getUnscheduledOnly) {
        Criteria mainCriteria = new Criteria();

        if(getUnscheduledOnly) {
            Criteria timeCriteria = new Criteria();
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, CommonOperators.IS_EMPTY));
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(endTimeField, CommonOperators.IS_EMPTY));
            mainCriteria.andCriteria(timeCriteria);
        }
        else {
            Criteria timeCriteria = new Criteria();
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));
            timeCriteria.addOrCondition(CriteriaAPI.getCondition(endTimeField, timelineRequest.getDateValue(), DateOperators.BETWEEN));
            mainCriteria.andCriteria(timeCriteria);

            Criteria rollOverCriteria = new Criteria();
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, String.valueOf(timelineRequest.getStartTime()), NumberOperators.LESS_THAN));
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(endTimeField, String.valueOf(timelineRequest.getEndTime()), NumberOperators.GREATER_THAN));
            mainCriteria.orCriteria(rollOverCriteria);
        }

        Criteria groupCriteria = new Criteria();
        if (CollectionUtils.isNotEmpty(timelineRequest.getGroupIds())) {
            groupCriteria.addAndCondition(CriteriaAPI.getCondition(timelineGroupField, StringUtils.join(timelineRequest.getGroupIds(), ","), NumberOperators.EQUALS));
        }
        if (timelineRequest.isGetUnGrouped()) {
            groupCriteria.addOrCondition(CriteriaAPI.getCondition(timelineGroupField, CommonOperators.IS_EMPTY));
        }
        mainCriteria.andCriteria(groupCriteria);

        if(viewCriteria != null)
        {
            mainCriteria.andCriteria(viewCriteria);
        }
        if (applyFilter && filterCriteria != null && !filterCriteria.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andCriteria(filterCriteria);
            mainCriteria.andCriteria(criteria);
        }
        return mainCriteria;
    }

}
