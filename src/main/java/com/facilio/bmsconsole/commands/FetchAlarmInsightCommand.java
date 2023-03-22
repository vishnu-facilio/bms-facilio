package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.*;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class FetchAlarmInsightCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(FetchAlarmInsightCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long assetId = (long) context.get(ContextNames.ASSET_ID);
        Boolean isRca = (Boolean) context.get(FacilioConstants.ContextNames.IS_RCA);
        long parentAlarmId = (long) context.getOrDefault(FacilioConstants.ContextNames.PARENT_ALARM_ID, -1l);
        long readingRuleId = (long) context.get(ContextNames.READING_RULE_ID);

        List<Long> assetIds = (List<Long>) context.getOrDefault(ContextNames.RESOURCE_LIST, null);
        if (CollectionUtils.isEmpty(assetIds) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
            List<Long> nsIds = NamespaceAPI.getNsIdForRuleId(readingRuleId, Collections.singletonList(NSType.READING_RULE));
            if (CollectionUtils.isNotEmpty(nsIds)) {
                assetIds = NamespaceAPI.fetchResourceIdsFromNamespaceInclusions(nsIds.get(0));
            }
        }
        long alarmId = (long) context.get(ContextNames.ALARM_ID);
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

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<Map<String, Object>> props = null;
		if ( AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
				props = getNewAlarmProps(modBean, assetId, readingRuleId,  dateRange, operator, alarmId, assetIds, parentAlarmId);
		}
		else {
			props = getAlarmProps(modBean, assetId, readingRuleId, isRca, dateRange, operator, assetIds);
		}

		if (assetId > 0) {
			if (CollectionUtils.isNotEmpty(props)) {
				List<Long> ruleIds = props.stream().map(prop -> (long) prop.get("ruleId")).collect(Collectors.toList());
				List<NewReadingRuleContext> rulesList = NewReadingRuleAPI.getReadingRules(ruleIds);
				if ( AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
					Map<Long, NewReadingRuleContext> rules = rulesList.stream().collect(Collectors.toMap(NewReadingRuleContext::getId, Function.identity()));
					for (Map<String, Object> prop : props) {
						long ruleId = (long) prop.get("ruleId");
						prop.put("subject", rules.get(ruleId).getName());
					}
				}
				else {
					Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false);
					for (Map<String, Object> prop : props) {
						long ruleId = (long) prop.get("ruleId");
						prop.put("subject", rules.get(ruleId).getName());
					}
				}

			}
		}
		if (readingRuleId > 0 || (assetIds != null && assetIds.size() > 0)) {
			if (isRca) {
				List<FacilioField> eventFields = EventConstants.EventFieldFactory.getEventFields();
				Map<String, FacilioField> eventFieldsMap = FieldFactory.getAsMap(eventFields);
				FacilioModule eventModule = EventConstants.EventModuleFactory.getEventModule();
				List<FacilioField> selectedEventFields = new ArrayList<>();
				selectedEventFields.add(eventFieldsMap.get("subRuleId"));
				selectedEventFields.add(eventFieldsMap.get("resourceId"));
				selectedEventFields.add(eventFieldsMap.get("source"));
				selectedEventFields.addAll(FieldFactory.getCountField(eventModule));
				GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
						.table(eventModule.getTableName())
						.select(selectedEventFields)
						.andCondition(CriteriaAPI.getCondition("SUB_RULE_ID", "subRuleId", String.valueOf(readingRuleId), NumberOperators.EQUALS))
						.groupBy(eventFieldsMap.get("resourceId").getCompleteColumnName());
				List<Map<String, Object>> rcaProps = genericSelectRecordBuilder.get();

				if (CollectionUtils.isNotEmpty(rcaProps)) {
					List<Long> resourceIds = rcaProps.stream().map(prop -> (long) prop.get("resourceId")).collect(Collectors.toList());
					for (Map<String, Object> prop : rcaProps) {
						prop.put("subject", prop.get("source"));
					}
					context.put(ContextNames.ALARM_LIST, rcaProps);
					System.out.println("resourcesId" + resourceIds.size());
					return false;
				}
			}
			else if (CollectionUtils.isNotEmpty(props)) {
				List<Long> resourcesId = props.stream().map(prop -> {
					HashMap<Object,Object> hsp = (HashMap<Object, Object>) prop.get("resource");
					return (long) hsp.get("id");
					}).collect(Collectors.toList());
				Map<Long, ResourceContext> resources = ResourceAPI.getResourceAsMapFromIds(resourcesId);
				for (Map<String, Object> prop : props) {
					HashMap<Object,Object> resourceId =  (HashMap<Object, Object>) prop.get("resource");
					prop.put("subject", resources.get((long) resourceId.get("id")).getName());
				}
			}
		}
		context.put(ContextNames.ALARM_LIST, props);

		return false;
	}

	private List<Map<String, Object>> getAlarmProps(ModuleBean modBean, long assetId, long readingRuleId, boolean isRca, DateRange dateRange, Operator operator, List<Long> assetIds) throws Exception {
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<FacilioField> selectFields = new ArrayList<>();
		FacilioField ruleField = fieldMap.get("ruleId");
		String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();
		FacilioField resourceFieldColumn = fieldMap.get("resource");
		String durationColumn = clearedTimeFieldColumn + "-" + createdTimeFieldColumn;
		/*StringBuilder durationAggrColumn = new StringBuilder("SUM( CASE WHEN ")
				.append(clearedTimeFieldColumn).append(" IS NOT NULL THEN ").append(durationColumn)
				.append(" ELSE ").append(String.valueOf(System.currentTimeMillis())).append("-").append(createdTimeFieldColumn)
				.append(" END )")
				;*/
		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(String.valueOf(System.currentTimeMillis())).append(") - ")
				.append(createdTimeFieldColumn).append(")")
				;
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);
		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.add(resourceFieldColumn);
		selectFields.addAll(FieldFactory.getCountField(module));

		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
				.select(selectFields)
				.module(module)
				.beanClass(ReadingAlarmContext.class);

		if (assetId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(assetId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(TicketContext.SourceType.ANOMALY_ALARM.getIndex()), NumberOperators.NOT_EQUALS))
					.groupBy(ruleField.getCompleteColumnName());
		}
		if ((assetIds != null && assetIds.size() > 0)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), StringUtils.join(assetIds, ",") , NumberOperators.EQUALS))
					.groupBy(fieldMap.get("resource").getCompleteColumnName());
		}
		List<Map<String, Object>> rcaProps = new ArrayList<Map<String, Object>>();

		if (readingRuleId > 0) {
			if (!isRca) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(readingRuleId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(TicketContext.SourceType.ANOMALY_ALARM.getIndex()), NumberOperators.NOT_EQUALS))
						.groupBy(resourceFieldColumn.getCompleteColumnName());
			} else {

			}
		}
		if (!isRca) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(ruleField, CommonOperators.IS_NOT_EMPTY))
					.orderBy(durationField.getName() + " desc");
			;
			if (dateRange != null) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
			}
			else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), operator));
			}
		}
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		return props;
	}

	private List<Map<String, Object>> getNewAlarmProps(ModuleBean modBean, long assetId, long ruleId, DateRange dateRange, Operator operator, long alarmId, List<Long> assetIds, long parentAlarmId) throws Exception {
		FacilioModule readingAlarmModule = modBean.getModule(ContextNames.NEW_READING_ALARM);
		FacilioModule occurrenceModule = modBean.getModule(ContextNames.ALARM_OCCURRENCE);

		List<FacilioField> readingAlarmFields = modBean.getAllFields(readingAlarmModule.getName());
		List<FacilioField> occurrenceFields = modBean.getAllFields(occurrenceModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(readingAlarmFields);
		fieldMap.putAll(FieldFactory.getAsMap(occurrenceFields));

		List<FacilioField> selectFields = new ArrayList<>();
		FacilioField ruleField = fieldMap.get("rule");
		String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();
		FacilioField resourceFieldColumn = fieldMap.get("resource");
		FacilioField alarmIdField = fieldMap.get("alarm");

		/*
		 *  Duration fields to get duration only for current ranges
		 * even if it alarm is active beyonds range
		 *
		 */

		StringBuilder durationAggrColumn = new StringBuilder("SUM(")
				.append("( CASE WHEN " + clearedTimeFieldColumn + " IS NULL THEN ")
				.append(" ( CASE WHEN "  + System.currentTimeMillis() + " < " + dateRange.getEndTime())
				.append(" THEN " + System.currentTimeMillis() + " ELSE " + dateRange.getEndTime() + " END )")
				.append(" WHEN " + clearedTimeFieldColumn + " < " + dateRange.getEndTime())
				.append(" THEN " + clearedTimeFieldColumn + " ELSE " + dateRange.getEndTime() + " END )")
				// .append(",").append(System.currentTimeMillis())
				.append(" - ")
				.append("( CASE WHEN " + createdTimeFieldColumn + " > " + dateRange.getStartTime())
				.append(" THEN " + createdTimeFieldColumn + " ELSE " + dateRange.getStartTime() + " END )")
				.append(")")
				;
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);

		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.add(resourceFieldColumn);
		selectFields.add(alarmIdField);
		selectFields.addAll(FieldFactory.getCountField(occurrenceModule));
		FacilioField alarmField = fieldMap.get("alarmId");

		SelectRecordsBuilder<AlarmOccurrenceContext> builder = NewAlarmAPI.getAlarmBuilder(dateRange.getStartTime(), dateRange.getEndTime(), selectFields, fieldMap);
		builder.innerJoin(readingAlarmModule.getTableName())
				.on(occurrenceModule.getTableName() + ".ALARM_ID = " + readingAlarmModule.getTableName() + ".ID");

		if ( parentAlarmId > 0) {

			List<FacilioField> occurrencefields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			Map<String, FacilioField> occurrencefieldMap = FieldFactory.getAsMap(occurrencefields);
			SelectRecordsBuilder<AlarmOccurrenceContext> parentOccurrenceBuilder = NewAlarmAPI.getAlarmBuilder(dateRange.getStartTime(), dateRange.getEndTime(), occurrencefields, occurrencefieldMap);

			parentOccurrenceBuilder.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" + parentAlarmId, NumberOperators.EQUALS));

			List<AlarmOccurrenceContext> occurrenceContexts = parentOccurrenceBuilder.get();
			Criteria criteria = new Criteria();

			LOGGER.info("occurrenceContexts : " + occurrenceContexts.size());
			for (AlarmOccurrenceContext alarmOccurrence : occurrenceContexts)
			{
				long createdTime = alarmOccurrence.getCreatedTime();
				long clearedTime = alarmOccurrence.getClearedTime() > 0 ?alarmOccurrence.getClearedTime() :  dateRange.getEndTime() ;

				criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), createdTime+","+clearedTime, DateOperators.BETWEEN));//
			}
			if (criteria != null) {
				builder.andCriteria(criteria);

			}
		}

		boolean isNewReadingRUle = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE);
		builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("isNewReadingRule"), String.valueOf(isNewReadingRUle), NumberOperators.EQUALS));

		if (assetId > 0 ) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"),( assetId > 0 ? String.valueOf(assetId) : StringUtils.join(assetIds, ",") ), NumberOperators.EQUALS))
					.groupBy(ruleField.getCompleteColumnName());
		}
		if ((assetIds != null && assetIds.size() > 0)) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), StringUtils.join(assetIds, ",") , NumberOperators.EQUALS))
			.groupBy(fieldMap.get("resource").getCompleteColumnName());
		}
		if (ruleId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(ruleId), NumberOperators.EQUALS))
			.groupBy(fieldMap.get("resource").getCompleteColumnName());
		}
		if (alarmId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS))
			.groupBy(alarmField.getCompleteColumnName());
		}

		builder.andCondition(CriteriaAPI.getCondition(ruleField, CommonOperators.IS_NOT_EMPTY))
				.orderBy(durationField.getName() + " desc");

		List<Map<String, Object>> props = builder.getAsProps();
		// backward-compatibility
		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				if (prop.containsKey("rule")) {
					Object ruleProp = prop.get("rule");
					if (prop instanceof Map) {
						prop.put("ruleId", ((Map) ruleProp).get("id"));
					}
					else if (prop instanceof ReadingRuleContext) {
						ReadingRuleContext rule = (ReadingRuleContext) ruleProp;
						prop.put("ruleId", rule.getId());
					}
				}
			}
		}
		return props;
	}

}
