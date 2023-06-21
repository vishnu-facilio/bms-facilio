package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingAlarmOccurrenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetAlarmRcaDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long alarmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ReadingAlarm baseAlarm = (ReadingAlarm) NewAlarmAPI.getAlarm(alarmId);

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE);
        long ruleId = (long) context.get(FacilioConstants.ContextNames.RULE_ID);
        List<Long> rcaIds = new ArrayList<>();
        List <Long> ruleIds=new ArrayList<>();
        if (ruleId > 0) {
            List<FacilioField> fields ;
            Map<String, FacilioField> fieldMap ;
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
            if (!baseAlarm.getIsNewReadingRule()) {
                fields=FieldFactory.getWorkflowRuleRCAMapping();
                fieldMap=FieldFactory.getAsMap(fields);
                selectBuilder.table("Workflow_RCA_Mapping")
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(ruleId), NumberOperators.EQUALS))
                        .select(FieldFactory.getWorkflowRuleRCAMapping());
            }
            else{
                fields=FieldFactory.getReadingRuleRCAMapping();
                fieldMap=FieldFactory.getAsMap(fields);
                selectBuilder.table(ModuleFactory.getReadingRuleRCAMapping().getTableName())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(ruleId), NumberOperators.EQUALS))
                        .select(FieldFactory.getReadingRuleRCAMapping());
            }

            List<Map<String, Object>> props = selectBuilder.get();
            if (props != null && !props.isEmpty()) {
                for (Map<String, Object> prop : props) {
                    rcaIds.add((Long) prop.get("rcaRule"));
                    ruleIds.add((Long)prop.get("rule"));
                }
            }
        }
        if (rcaIds.size() > 0) {
            DateOperators operator = DateOperators.CURRENT_WEEK;
            DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
            if (dateRange == null) {
                Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
                if (dateOperatorInt != null && dateOperatorInt > -1) {
                    String dateOperatorValue = (String) context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE);
                    operator = (DateOperators) Operator.getOperator(dateOperatorInt);
                    dateRange = operator.getRange(dateOperatorValue);
                }
            }
//            List<AlarmOccurrenceContext> alarmOccurrence = ArrayList<AlarmOccurrenceContext>

            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            SelectRecordsBuilder<ReadingAlarmOccurrenceContext> buildesr = new SelectRecordsBuilder<ReadingAlarmOccurrenceContext>().module(module)
                    .beanClass(ReadingAlarmOccurrenceContext.class).select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS))
//                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(ruleId), NumberOperators.EQUALS));

            Condition condition1 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), String.valueOf(dateRange.getStartTime()),
                    NumberOperators.GREATER_THAN_EQUAL);
            Condition condition2 = CriteriaAPI.getCondition(fieldMap.get("clearedTime"), CommonOperators.IS_EMPTY);

            Criteria criterias = new Criteria();

            criterias.addOrCondition(condition1);
            criterias.addOrCondition(condition2);

            buildesr.andCriteria(criterias);

            List<ReadingAlarmOccurrenceContext> occurrenceList = buildesr.get();

            if (occurrenceList.size() > 0) {
                String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
                String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();
                FacilioField rule = modBean.getField("rule", FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE);
                LookupField rulelookup = (LookupField) rule;
                rulelookup.setLookupModule(ModuleFactory.getReadingRuleModule());
                StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
                        .append(clearedTimeFieldColumn).append(",").append(String.valueOf(System.currentTimeMillis())).append(") - ")
                        .append(createdTimeFieldColumn).append(")");
                List<FacilioField> selectFields = new ArrayList<>();
                FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);
                selectFields.add(durationField);
                selectFields.addAll(FieldFactory.getCountField(module));
                selectFields.add(fieldMap.get("alarm"));
                selectFields.add(fieldMap.get("rule"));
                SelectRecordsBuilder<ReadingAlarmOccurrenceContext> builder = new SelectRecordsBuilder<ReadingAlarmOccurrenceContext>().module(module)
                        .beanClass(ReadingAlarmOccurrenceContext.class).select(selectFields)
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), rcaIds, NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(baseAlarm.getResource().getId()), NumberOperators.EQUALS))
//                    .fetchLookup(rulelookup)
                        .groupBy(fieldMap.get("rule").getCompleteColumnName());
                if (occurrenceList != null && occurrenceList.size() > 0) {
                    Criteria criteria = new Criteria();
                    List<Condition> conditions = new ArrayList<Condition>();
                    for (ReadingAlarmOccurrenceContext occurrence : occurrenceList) {
                        long createdTime = occurrence.getCreatedTime();
                        long clearedTime = occurrence.getClearedTime() > 0 ? occurrence.getClearedTime() : dateRange.getEndTime();

                        conditions.add(CriteriaAPI.getCondition(fieldMap.get("createdTime"), createdTime + "," + clearedTime, DateOperators.BETWEEN));

//                    DateRange range = new DateRange();
//                    range.setStartTime(occurrence.getCreatedTime());
//                    range.setEndTime(occurrence.getClearedTime() > 0 ? occurrence.getClearedTime() : System.currentTimeMillis());
//                    builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
                    }
                    criteria.groupOrConditions(conditions);
                    builder.andCriteria(criteria);
                }
                if((int)context.get(FacilioConstants.ContextNames.PAGE)!=0) {
                    int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
                    int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);

                    if (perPage != -1) {
                        int offset = ((page - 1) * perPage);
                        if (offset < 0) {
                            offset = 0;
                        }

                        builder.offset(offset);
                        builder.limit(perPage);

                    }
                }

                List<Map<String, Object>> list = builder.getAsProps();
                for (Map<String, Object> prop : list) {
                    Object alarmObject = prop.get("alarm");
                    Object ruleObject = prop.get("rule");
                    prop.put("rule", NewReadingRuleAPI.getReadingRules(Collections.singletonList((Long) ((Map) ruleObject).get("id"))).get(0));
                    prop.put(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, NewAlarmAPI.getLatestAlarmOccurance((Long) ((Map) alarmObject).get("id")));
                }
                context.put(FacilioConstants.ContextNames.RCA_ALARMS, list);

            }

        }
        return false;
    }

}

