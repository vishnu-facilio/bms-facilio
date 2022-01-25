package com.facilio.v3.util;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.TimelineRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TimelineViewUtil {

    public static Criteria buildMainCriteria(FacilioField startTimeField, FacilioField endTimeField, TimelineRequest timelineRequest, FacilioField timelineGroupField, Criteria viewCriteria, Criteria filterCriteria, boolean getUnscheduledOnly) {
        Criteria mainCriteria = new Criteria();

        if(getUnscheduledOnly) {
            Criteria timeCriteria = new Criteria();
            timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, CommonOperators.IS_EMPTY));
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
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andCriteria(filterCriteria);
            mainCriteria.andCriteria(criteria);
        }
        return mainCriteria;
    }

    public static FacilioContext getTimelineContext(FacilioChain chain, TimelineRequest timelineRequest) throws Exception
    {
        return getTimelineContext(chain, timelineRequest, false, -1, -1, false);
    }
    public static FacilioContext getTimelineContext(FacilioChain chain, TimelineRequest timelineRequest, boolean isListView, int page, int perPage, boolean getUnscheduledData) throws Exception
    {
        FacilioContext context = chain.getContext();

        if (org.apache.commons.collections.MapUtils.isNotEmpty(timelineRequest.getFilters())) {
            JSONParser parser = new JSONParser();
            JSONObject filterJSON = (JSONObject) parser.parse(timelineRequest.getFilters().toJSONString());
            context.put(FacilioConstants.ContextNames.FILTERS, filterJSON);
        }
        if(isListView) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", page);
            pagination.put("perPage", perPage);
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }
        context.put(FacilioConstants.ContextNames.TIMELINE_GET_UNSCHEDULED_DATA, getUnscheduledData);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, timelineRequest.getModuleName());
        context.put(FacilioConstants.ContextNames.CV_NAME, timelineRequest.getViewName());
        context.put(FacilioConstants.ContextNames.TIMELINE_REQUEST, timelineRequest);
        return context;
    }

}
