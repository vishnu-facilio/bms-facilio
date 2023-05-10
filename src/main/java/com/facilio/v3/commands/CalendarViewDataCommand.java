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
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class CalendarViewDataCommand extends FacilioCommand {
    private static Logger logger = LogManager.getLogger(CalendarViewDataCommand.class);
    private static final String GROUP_CONCAT_FIELD_NAME = "__groupConcat";
    private static final String DATE_FORMAT = "__date_format";
    private static final String DIFFERENCE = "__difference";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        CalendarViewRequestContext calendarViewRequest = (CalendarViewRequestContext) context.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST);
        List<FacilioField> selectiveFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.SELECTABLE_FIELDS);
        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        V3Config v3Config = Constants.getV3Config(context);

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(moduleName);

        CalendarViewContext calendarViewObj = viewObj.getCalendarViewContext();
        FacilioField startTimeField = calendarViewObj.getStartDateField();
        FacilioField endTimeField = calendarViewObj.getEndDateField();

        FacilioField idField = FieldFactory.getIdField(module);
        String idFieldColumnName = idField.getCompleteColumnName();

        Criteria mainCriteria = CalendarViewUtil.calendarTimeCriteria(startTimeField, endTimeField, calendarViewRequest);

        selectiveFields.add(FieldFactory.getStringField(DATE_FORMAT, "groupMax." + DATE_FORMAT, null));

        // Construct Sub-Query for Group_Concat() (obtain all recordIds that match mainCriteria)
        SelectRecordsBuilder subQueryBuilder = getGroupConcatQuery(module, mainCriteria, startTimeField, endTimeField, idFieldColumnName, calendarViewRequest.getDateAggregator());
        String subQueryString = subQueryBuilder.constructQueryString();

        SelectRecordsBuilder<ModuleBaseWithCustomFields> recordsBuilder = CalendarViewUtil.getSelectRecordsBuilder(context, moduleBean, true);
        recordsBuilder
                .select(selectiveFields)
                .andCriteria(mainCriteria)
                .innerJoinQuery(subQueryString, "groupMax")
                .on(calendarViewRequest.getDateAggregator().getSelectField(startTimeField).getColumnName() + " = groupMax." + DATE_FORMAT);

        // Filter MAX Number of Records per cell
        recordsBuilder.andCustomWhere("FIND_IN_SET(" + idFieldColumnName + ", " + GROUP_CONCAT_FIELD_NAME + ") <= " + (calendarViewRequest.getMaxResultPerCell() + 1));

        if (endTimeField != null) {
            FacilioField differenceField = new FacilioField();
            differenceField.setName(DIFFERENCE);
            differenceField.setColumnName("(" + endTimeField.getCompleteColumnName() + " - " + startTimeField.getCompleteColumnName() + ")");
            recordsBuilder.orderBy("groupMax." + DATE_FORMAT + ", " + differenceField.getCompleteColumnName() + " " + "desc");
            selectiveFields.add(differenceField);
        }

        List<? extends ModuleBaseWithCustomFields> records = recordsBuilder.get();

        Map<String, List<? extends  ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);
        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }

    private SelectRecordsBuilder getGroupConcatQuery(FacilioModule module, Criteria timeCriteria, FacilioField startTimeField, FacilioField endTimeField, String idFieldColumnName,
                                                     BmsAggregateOperators.DateAggregateOperator dateAggregator) throws Exception {
        SelectRecordsBuilder groupConcatQuery = new SelectRecordsBuilder()
                .module(module)
                .beanClass(V3Context.class)
                .andCriteria(timeCriteria);

        Collection<FacilioField> fields = new ArrayList<>();
        StringJoiner groupByJoiner = new StringJoiner(",");

        FacilioField startTimeAggrField = dateAggregator.getSelectField(startTimeField);
        startTimeAggrField.setName(DATE_FORMAT);
        fields.add(startTimeAggrField);
        groupByJoiner.add(startTimeAggrField.getCompleteColumnName());

        FacilioField groupConcatField = getGroupConcatField(idFieldColumnName, startTimeField, endTimeField);
        fields.add(groupConcatField);

        groupConcatQuery.select(fields);
        groupConcatQuery.groupBy(groupByJoiner.toString());

        return groupConcatQuery;
    }

    private FacilioField getGroupConcatField(String idFieldColumnName, FacilioField startTimeField, FacilioField endTimeField) {
        FacilioField field = new FacilioField();
        field.setName(GROUP_CONCAT_FIELD_NAME);
        field.setDisplayName(field.getDisplayName());
        field.setFieldId(field.getFieldId());
        field.setDataType(FieldType.STRING);

        String startFieldColumnName = startTimeField.getCompleteColumnName();

        StringBuilder groupConcatQuery = new StringBuilder();
        groupConcatQuery.append("GROUP_CONCAT(" ).append(idFieldColumnName)
                .append(" ORDER BY ");
        if (endTimeField != null) {
            String endFieldColumnName = endTimeField.getCompleteColumnName();
            groupConcatQuery.append("CASE WHEN (").append(startFieldColumnName).append(" is not null AND ").append(endFieldColumnName).append(" is not null) ")
                    .append("THEN (").append(endFieldColumnName).append(" - ").append(startFieldColumnName).append(") END DESC,");
        }
        groupConcatQuery.append(" CASE WHEN (").append(startFieldColumnName).append(" is not null").append(") ")
                .append("THEN (").append(startFieldColumnName).append(") END ASC, ")
                .append(idFieldColumnName)
                .append(")");

        field.setColumnName(groupConcatQuery.toString());

        return field;
    }
}
