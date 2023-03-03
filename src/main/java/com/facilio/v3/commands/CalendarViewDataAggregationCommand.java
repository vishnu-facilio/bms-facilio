package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.calendarview.CalendarViewUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class CalendarViewDataAggregationCommand extends FacilioCommand {
    private static final String MIN_START_DATE = "__minStartDate";
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CalendarViewRequestContext calendarViewRequest = (CalendarViewRequestContext) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();
        FacilioField endTimeField = calendarViewObj.getEndDateField();
        FacilioField idField = FieldFactory.getIdField(module);

        // --- Data Aggregation Builder ---
        Criteria mainCriteria = CalendarViewUtil.calendarTimeCriteria(startTimeField, endTimeField, calendarViewRequest);

        Collection<FacilioField> aggregateFields = new ArrayList<>();
        StringJoiner aggregateGroupBy = new StringJoiner(",");

        // Since GROUP BY clause cannot contain non-aggregated column, we make use of MIN/MAX Aggregation to get START_DATE field in result
        FacilioField minValueField = BmsAggregateOperators.NumberAggregateOperator.MIN.getSelectField(startTimeField);
        minValueField.setName(MIN_START_DATE);
        aggregateFields.add(minValueField);

        FacilioField startTimeAggrField = calendarViewRequest.getDateAggregator().getSelectField(startTimeField);
        aggregateFields.add(startTimeAggrField);
        aggregateGroupBy.add(startTimeAggrField.getCompleteColumnName());

        aggregateFields.add(BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(idField));

        SelectRecordsBuilder<ModuleBaseWithCustomFields> aggregateBuilder = CalendarViewUtil.getSelectRecordsBuilder(context, moduleBean, false);
        aggregateBuilder.setAggregation()
                            .select(aggregateFields)
                            .andCriteria(mainCriteria)
                            .groupBy(aggregateGroupBy.toString());

        // Returns START_DATE vs count(id) in the timeFrame
        List<Map<String, Object>> aggregateValue = aggregateBuilder.getAsProps();

        context.put(FacilioConstants.ViewConstants.CALENDAR_VIEW_AGGREGATE_DATA, aggregateValue);

        return false;
    }
}
