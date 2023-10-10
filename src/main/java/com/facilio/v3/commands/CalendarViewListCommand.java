package com.facilio.v3.commands;

import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.calendarview.CalendarViewUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.facilio.modules.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarViewListCommand extends FacilioCommand {
    private static final Logger logger = LogManager.getLogger(CalendarViewListCommand.class);
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean onlyCount = Constants.getOnlyCount(context);
        if (onlyCount) {
            fetchCount(context);
        } else {
            fetchData(context);
        }
        return false;
    }

    private static void fetchCount(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = getSelectBuilder(context, false);
        selectRecordsBuilder.select(null);

        selectRecordsBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
        List<? extends ModuleBaseWithCustomFields> countRecord = selectRecordsBuilder.get();

        context.put(Constants.COUNT, countRecord.get(0).getId());
    }

    private static void fetchData(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        CalendarViewRequestContext calendarViewRequest = (CalendarViewRequestContext) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = getSelectBuilder(context, true);

        int page, perPage = 0, offset = 0;
        if (pagination != null) {
            page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
            perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
            offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            calendarViewRequest.setMaxResultPerCell(perPage);
        }

        if (perPage > 0) {
            selectRecordsBuilder.offset(offset);
            selectRecordsBuilder.limit(perPage);
        }

        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();

        Map<String, List<? extends ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);
        context.put(Constants.RECORD_MAP, recordMap);
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getSelectBuilder(Context context, boolean fetchSupplements) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        CalendarViewRequestContext calendarViewRequest = (CalendarViewRequestContext) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST);

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();
        FacilioField endTimeField = calendarViewObj.getEndDateField();

        FacilioField idField = FieldFactory.getIdField(module);
        String idFieldColumnName = idField.getCompleteColumnName();

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = CalendarViewUtil.getSelectRecordsBuilder(context, moduleBean, fetchSupplements);
        // CalendarView Criteria
        Criteria mainCriteria = CalendarViewUtil.calendarTimeCriteria(startTimeField, endTimeField, calendarViewRequest);
        selectRecordsBuilder.andCriteria(mainCriteria);

        // Skip record if startDateField value > endDateField value
        if (endTimeField != null) {
            Criteria negativeCaseCriteria = new Criteria();
            negativeCaseCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, calendarViewRequest.getDateValue(), DateOperators.BETWEEN));
            negativeCaseCriteria.addOrCondition(CriteriaAPI.getCondition("(" + endTimeField.getCompleteColumnName() + " > " + startTimeField.getCompleteColumnName() + ")", "__difference", Boolean.TRUE.toString(), BooleanOperators.IS));
            selectRecordsBuilder.andCriteria(negativeCaseCriteria);
        }

        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
        if (searchCriteria != null) {
            selectRecordsBuilder.andCriteria(searchCriteria);
        }

        selectRecordsBuilder.orderBy(startTimeField.getCompleteColumnName() + "," + idFieldColumnName);

        return selectRecordsBuilder;
    }
}
