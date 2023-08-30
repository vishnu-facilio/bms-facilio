package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.TimelineViewUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GetTimelineScheduledViewDataCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetTimelineScheduledViewDataCommand.class.getName());

    private static final String GROUP_CONCAT_FIELD_NAME = "__groupConcat";
    private static final String MIN_START_DATE = "__minStartDate";
    private static final String DATE_FORMAT = "__date_format";

    @Override
    public boolean executeCommand(Context context) throws Exception {
        TimelineRequest timelineRequest = (TimelineRequest) context.get(FacilioConstants.ContextNames.TIMELINE_REQUEST);

        if (!timelineRequest.isGetUnGrouped() && CollectionUtils.isEmpty(timelineRequest.getGroupIds())) {
            throw new IllegalArgumentException("At least one group id should be passed");
        }

        V3Config config = ChainUtil.getV3Config(timelineRequest.getModuleName());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(timelineRequest.getModuleName());
        List<FacilioField> allFields = modBean.getAllFields(module.getName());

        FacilioField idField = FieldFactory.getIdField(module);
        String idFieldColumnName = idField.getCompleteColumnName();

        FacilioView viewObj = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        TimelineScheduledViewContext timelineView = viewObj.getTimelineScheduledViewContext();
        if(viewObj.getTimelineScheduledViewContext() == null)
        {
            throw new IllegalArgumentException("Invalid View details passed");
        }

        FacilioField startTimeField = timelineView.getStartDateField();
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        FacilioField timelineGroupField = timelineView.getGroupByField();

        Criteria mainCriteria = TimelineViewUtil.buildMainCriteria(startTimeField, timelineView.getEndDateField(), timelineRequest,
                                timelineGroupField, viewObj.getCriteria(),
                                filterCriteria, false);

        StringJoiner groupBy = new StringJoiner(",");
        Collection<FacilioField> aggregateFields = new ArrayList<>();

        aggregateFields.add(timelineGroupField);
        groupBy.add(timelineGroupField.getCompleteColumnName());

        FacilioField startTimeAggrField = timelineRequest.getDateAggregator().getSelectField(startTimeField);
        aggregateFields.add(startTimeAggrField);
        groupBy.add(startTimeAggrField.getCompleteColumnName());

        aggregateFields.add(BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(idField));

        FacilioField minValueField = BmsAggregateOperators.NumberAggregateOperator.MIN.getSelectField(startTimeField);
        minValueField.setName(MIN_START_DATE);
        aggregateFields.add(minValueField);
        SelectRecordsBuilder<ModuleBaseWithCustomFields> aggregateBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .setAggregation()
                .select(aggregateFields)
                .groupBy(groupBy.toString())
                .andCriteria(mainCriteria);

        if(Boolean.TRUE.equals(viewObj.isExcludeModuleCriteria())){
            aggregateBuilder.skipModuleCriteria();
        }

        long queryStartTime = System.currentTimeMillis();
        List<Map<String, Object>> aggregateValue = aggregateBuilder.getAsProps();
        LOGGER.error("query for aggregate value: " + aggregateBuilder + "; time taken is: " + (System.currentTimeMillis() - queryStartTime));

        List<FacilioField> allModuleFields = new ArrayList<>(allFields);
        allModuleFields.add(FieldFactory.getStringField(DATE_FORMAT, "groupMax." + DATE_FORMAT, null));

        // Reconstructing criteria for 2nd select builder
        mainCriteria = TimelineViewUtil.buildMainCriteria(startTimeField, timelineView.getEndDateField(), timelineRequest,
                timelineGroupField, viewObj.getCriteria(),
                filterCriteria, false);

        SelectRecordsBuilder subQuerybuilder = getQueryContactQuery(module, timelineGroupField, startTimeField, idFieldColumnName, timelineRequest.getDateAggregator(), mainCriteria);
        if(Boolean.TRUE.equals(viewObj.isExcludeModuleCriteria())){
            subQuerybuilder.skipModuleCriteria();
        }
        String subQuery = subQuerybuilder.constructQueryString();

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(module)
                .innerJoinQuery(subQuery, "groupMax")
                .on(timelineGroupField.getCompleteColumnName() + " <=> groupMax." + timelineGroupField.getName() + " AND "
                        + timelineRequest.getDateAggregator().getSelectField(startTimeField).getColumnName() + " = groupMax." + DATE_FORMAT)
                .beanClass(ChainUtil.getBeanClass(config, module))
                .select(allModuleFields);

        if(Boolean.TRUE.equals(viewObj.isExcludeModuleCriteria())){
            builder.skipModuleCriteria();
        }
        builder.andCustomWhere("FIND_IN_SET(" + idFieldColumnName + ", " + GROUP_CONCAT_FIELD_NAME + ") BETWEEN 1 AND " + timelineRequest.getMaxResultPerCell());

        // Reconstructing criteria for select builder
        mainCriteria = TimelineViewUtil.buildMainCriteria(startTimeField, timelineView.getEndDateField(), timelineRequest,
                timelineGroupField, viewObj.getCriteria(),
                filterCriteria, false);

        builder.andCriteria(mainCriteria);
        builder.addWhereValue(Arrays.asList(subQuerybuilder.paramValues()), 0);

        queryStartTime = System.currentTimeMillis();
        List<Map<String, Object>> recordMapList = builder.getAsProps();
        LOGGER.error("query for actual record value: " + builder + "; time take is: " + (System.currentTimeMillis() - queryStartTime));

        context.put(FacilioConstants.ContextNames.TIMELINE_AGGREGATE_DATA, aggregateValue);
        context.put(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP, recordMapList);

        return false;
    }

    private SelectRecordsBuilder getQueryContactQuery(FacilioModule module, FacilioField timelineGroupField,
                                               FacilioField startTimeField, String idFieldColumnName,
                                               BmsAggregateOperators.DateAggregateOperator dateAggregator, Criteria timeCriteria) throws Exception {
        SelectRecordsBuilder groupConcatQuery = new SelectRecordsBuilder()
                .module(module)
                .beanClass(V3Context.class)
                .andCriteria(timeCriteria);

        StringJoiner groupByJoiner = new StringJoiner(",");
        Collection<FacilioField> fields = new ArrayList<>();

        fields.add(timelineGroupField);
        groupByJoiner.add(timelineGroupField.getCompleteColumnName());

        FacilioField startTimeAggrField = dateAggregator.getSelectField(startTimeField);
        startTimeAggrField.setName(DATE_FORMAT);
        fields.add(startTimeAggrField);
        groupByJoiner.add(startTimeAggrField.getCompleteColumnName());

        FacilioField dateFieldClone = getGroupConcatField(idFieldColumnName, startTimeField.getCompleteColumnName());
        fields.add(dateFieldClone);

        groupConcatQuery.select(fields);
        groupConcatQuery.groupBy(groupByJoiner.toString());
        return groupConcatQuery;
    }

    private FacilioField getGroupConcatField(String idFieldColumnName, String startFieldColumnName) {
        FacilioField field = new FacilioField();
        field.setName(GROUP_CONCAT_FIELD_NAME);
        field.setDisplayName(field.getDisplayName());
        field.setColumnName("GROUP_CONCAT(" + idFieldColumnName + " order by " + startFieldColumnName + "," + idFieldColumnName + ")");
        field.setFieldId(field.getFieldId());
        field.setDataType(FieldType.STRING);
        return field;
    }
}
