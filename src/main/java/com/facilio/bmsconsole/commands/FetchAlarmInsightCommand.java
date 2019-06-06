package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchAlarmInsightCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long assetId = (long) context.get(ContextNames.ASSET_ID);
		Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
		DateOperators operator = DateOperators.CURRENT_WEEK;
		if (dateOperatorInt != null && dateOperatorInt != -1) {
			operator = (DateOperators) Operator.getOperator(dateOperatorInt);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<FacilioField> selectFields = new ArrayList<>();
		FacilioField ruleField = fieldMap.get("ruleId");
		FacilioField clearedTimeField = fieldMap.get("clearedTime");
		String durationColumn = clearedTimeField.getColumnName() + "-" + fieldMap.get("createdTime").getColumnName();
		FacilioField durationField = FieldFactory.getField("duration", "SUM(" + durationColumn + ")", FieldType.NUMBER);
		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.addAll(FieldFactory.getCountField(module));
		
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
				.select(selectFields)
				.module(module)
				.beanClass(ReadingAlarmContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(clearedTimeField, CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(ruleField, CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(SourceType.ANOMALY_ALARM.getIntVal()), NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), operator))
				.groupBy(ruleField.getCompleteColumnName())
				.orderBy(durationColumn + " desc")
				;

		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		if (CollectionUtils.isNotEmpty(props)) {
			List<Long> ruleIds = props.stream().map(prop -> (long) prop.get("ruleId")).collect(Collectors.toList());
			Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false, false);
			for (Map<String, Object> prop : props) {
				long ruleId = (long) prop.get("ruleId");
				prop.put("subject", rules.get(ruleId).getName());
			}
		}
		context.put(ContextNames.ALARM_LIST, props);
		
		return false;
	}

}
