package com.facilio.v3.commands;

import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.constants.FacilioConstants.ViewConstants;
import com.facilio.bmsconsole.calendarview.CalendarViewUtil;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import lombok.extern.log4j.Log4j;
import com.facilio.modules.*;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.*;

import static com.facilio.constants.FacilioConstants.ViewConstants.CalendarViewType.DAY;

@Log4j
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

        if (CollectionUtils.isNotEmpty(aggregateValue)) {
            Map<String, Map<String, Object>> timeStampVsAggregateValuesMap = aggregateValue.stream()
                    .collect(Collectors.toMap(prop -> String.valueOf(prop.get(startTimeField.getName())), prop -> prop, (a, b) -> b));

            Map<String, Long> recurssiveTImeStampsMap = getTimeStampVsFormattedTimeMap(calendarViewRequest);

            for (String dateFormat : recurssiveTImeStampsMap.keySet()) {
                if (!timeStampVsAggregateValuesMap.containsKey(dateFormat)) {
                    Map<String, Object> newAggregateValueMap = new HashMap<>();
                    newAggregateValueMap.put("id", 0l);
                    newAggregateValueMap.put(startTimeAggrField.getName(), dateFormat);
                    newAggregateValueMap.put(MIN_START_DATE, recurssiveTImeStampsMap.get(dateFormat));

                    timeStampVsAggregateValuesMap.put(dateFormat, newAggregateValueMap);
                }
            }

            aggregateValue = new ArrayList<>(timeStampVsAggregateValuesMap.values());
        } else {
            aggregateValue = new ArrayList<>();
        }

        context.put(FacilioConstants.ViewConstants.CALENDAR_VIEW_AGGREGATE_DATA, aggregateValue);

        return false;
    }

    public static Map<String, Long> getTimeStampVsFormattedTimeMap(CalendarViewRequestContext calendarViewRequest) {
        Map<String, Long> timeStampVsFormattedTimeMap = new HashMap<>();

        ViewConstants.CalendarViewType calendarViewType = calendarViewRequest.getDefaultCalendarViewEnum();
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        int incrementFieldType = Calendar.DATE;
        switch (calendarViewType) {
            case DAY:
                // HH pattern to include hours
                dateFormat = new SimpleDateFormat("HH");
                incrementFieldType = Calendar.HOUR_OF_DAY;
                break;
            case WEEK:
                // EEE includes the abbreviated day of the week
                dateFormat = new SimpleDateFormat("yyyy MM dd HH");
                incrementFieldType = Calendar.HOUR_OF_DAY;
                break;
            case MONTH:
                dateFormat = new SimpleDateFormat("yyyy MM dd");
                incrementFieldType = Calendar.DATE;
                break;
            case YEAR:
                // includes the year and week of the year (using the 'W' pattern symbol)
                dateFormat = new SimpleDateFormat("yyyy ww");
                incrementFieldType = Calendar.WEEK_OF_YEAR;
                break;
            default:
                break;
        }

        long startTimestamp = calendarViewRequest.getStartTime();
        long endTimestamp = calendarViewRequest.getEndTime();

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startTimestamp);
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endTimestamp);

        while (startCal.before(endCal)) {
            long unixTimestamp = startCal.getTimeInMillis();
            String currDateStr = dateFormat.format(startCal.getTime());
            timeStampVsFormattedTimeMap.put(currDateStr, unixTimestamp);
            startCal.add(incrementFieldType, 1);
        }

        return timeStampVsFormattedTimeMap;
    }
}
