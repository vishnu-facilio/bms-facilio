package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
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
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
			.groupBy(ruleField.getCompleteColumnName());
		} 
		List<Map<String, Object>> rcaProps = new ArrayList<Map<String, Object>>();

		if (readingRuleId > 0) {
			if (!isRca) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(readingRuleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
				.groupBy(resourceFieldColumn.getCompleteColumnName());
			} else {
				 List<FacilioField> eventFields = EventConstants.EventFieldFactory.getEventFields();
				Map<String, FacilioField> eventFieldsMap = FieldFactory.getAsMap(eventFields);
				FacilioModule eventModule = EventConstants.EventModuleFactory.getEventModule();
				List<FacilioField> selectedEventFields = new ArrayList<>();
				selectedEventFields.add(eventFieldsMap.get("subRuleId"));
				selectedEventFields.add(eventFieldsMap.get("resourceId"));
				selectedEventFields.addAll(FieldFactory.getCountField(eventModule));			
				GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
						.table(eventModule.getTableName())
						.select(selectedEventFields)
						.andCondition(CriteriaAPI.getCondition("SUB_RULE_ID", "subRuleId", String.valueOf(readingRuleId), NumberOperators.EQUALS))
						.groupBy(eventFieldsMap.get("resourceId").getCompleteColumnName());
		    rcaProps = genericSelectRecordBuilder.get();
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
				if (CollectionUtils.isNotEmpty(rcaProps)) {
					List<Long> resourceIds = rcaProps.stream().map(prop -> (long) prop.get("resourceId")).collect(Collectors.toList());
					for (Map<String, Object> prop : rcaProps) {
						long resourceId = (long) prop.get("resourceId");
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
				System.out.println("resourcesId" + resourcesId.size());
				for (Map<String, Object> prop : props) {
					HashMap<Object,Object> resourceId =  (HashMap<Object, Object>) prop.get("resource");
					prop.put("subject", resources.get((long) resourceId.get("id")).getName());
				}
			}
		}
		context.put(ContextNames.ALARM_LIST, props);
		
		return false;
	}

}
