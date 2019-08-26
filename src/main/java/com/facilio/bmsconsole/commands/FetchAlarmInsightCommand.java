package com.facilio.bmsconsole.commands;

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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchAlarmInsightCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long assetId = (long) context.get(ContextNames.ASSET_ID);
		Boolean isRca = (Boolean) context.get(FacilioConstants.ContextNames.IS_RCA);
		long readingRuleId = (long) context.get(ContextNames.READING_RULE_ID);
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
			props = getNewAlarmProps(modBean, assetId, dateRange, operator);
		}
		else {
			props = getAlarmProps(modBean, assetId, readingRuleId, isRca, dateRange, operator);
		}

		if (assetId > 0) {
			if (CollectionUtils.isNotEmpty(props)) {
				List<Long> ruleIds = props.stream().map(prop -> (long) prop.get("ruleId")).collect(Collectors.toList());
				Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false, false);
				for (Map<String, Object> prop : props) {
					long ruleId = (long) prop.get("ruleId");
					prop.put("subject", rules.get(ruleId).getName());
				}
			}
		}

		if (readingRuleId > 0) {
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

	private List<Map<String, Object>> getAlarmProps(ModuleBean modBean, long assetId, long readingRuleId, boolean isRca, DateRange dateRange, Operator operator) throws Exception {
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
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(TicketContext.SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
					.groupBy(ruleField.getCompleteColumnName());
		}
		List<Map<String, Object>> rcaProps = new ArrayList<Map<String, Object>>();

		if (readingRuleId > 0) {
			if (!isRca) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(readingRuleId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(TicketContext.SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
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

	private List<Map<String, Object>> getNewAlarmProps(ModuleBean modBean, long assetId, DateRange dateRange, Operator operator) throws Exception {
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

		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(System.currentTimeMillis()).append(") - ")
				.append(createdTimeFieldColumn).append(")")
				;
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);

		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.add(resourceFieldColumn);
		selectFields.addAll(FieldFactory.getCountField(occurrenceModule));

		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(selectFields)
				.beanClass(AlarmOccurrenceContext.class)
				.module(readingAlarmModule)
				.innerJoin(occurrenceModule.getTableName())
				.on(occurrenceModule.getTableName() + ".ALARM_ID = " + readingAlarmModule.getTableName() + ".ID")
				;

		if (assetId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(assetId), NumberOperators.EQUALS))
					.groupBy(ruleField.getCompleteColumnName());
		}

		builder.andCondition(CriteriaAPI.getCondition(ruleField, CommonOperators.IS_NOT_EMPTY))
				.orderBy(durationField.getName() + " desc");
		;
		if (dateRange != null) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), operator));
		}

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
