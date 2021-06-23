package com.facilio.bmsconsole.commands.anomaly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetAnomalyMetricsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long alarmId = (long) context.get(ContextNames.ALARM_ID);
		long resourceId = (long) context.get(ContextNames.RESOURCE_ID);
		boolean isRCA = (boolean) context.getOrDefault(ContextNames.IS_RCA, false);
		
		DateOperators operator = DateOperators.CURRENT_YEAR;
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		if (dateRange == null) {
			Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
			if (dateOperatorInt != null && dateOperatorInt > -1) {
				operator = (DateOperators) Operator.getOperator(dateOperatorInt);
			}
			dateRange = operator.getRange(null);
			context.put(ContextNames.DATE_RANGE, dateRange);
		} 
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = FacilioConstants.ContextNames.ALARM_OCCURRENCE;
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioField createdTimeField = fieldMap.get("createdTime");
		String createdTimeFieldColumn = createdTimeField.getColumnName();
		FacilioField clearedTimeField = fieldMap.get("clearedTime");
		String clearedTimeFieldColumn = clearedTimeField.getColumnName();
		
		String durationColumn = "("+clearedTimeFieldColumn + "-" + createdTimeFieldColumn+")/1000";
		FacilioField durationField = FieldFactory.getField("duration", durationColumn, FieldType.NUMBER);
		
		FacilioField alarmField = fieldMap.get("alarm");
//		List<FacilioField> selectFields = new ArrayList<>();
//		selectFields.add(FieldFactory.getField("createdTime", clearedTimeFieldColumn, FieldType.NUMBER));
		
		SelectRecordsBuilder<AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.moduleName(moduleName)
//				.select(selectFields)
//				.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS))
				.aggregate(NumberAggregateOperator.AVERAGE, durationField)
				.aggregate(CommonAggregateOperator.COUNT, alarmField)
				.aggregate(NumberAggregateOperator.MIN, createdTimeField)
				.groupBy(DateAggregateOperator.MONTH.getSelectField(createdTimeField).getCompleteColumnName())
				.orderBy("createdTime")
				;
		
		if (dateRange != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(createdTimeField, dateRange.toString(), DateOperators.BETWEEN));
		}

		if (isRCA) {
			FacilioModule anomalyOccurrence = modBean.getModule(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
			Map<String, FacilioField> anomalyOccurrenceFields = FieldFactory.getAsMap(modBean.getAllFields(anomalyOccurrence.getName()));
			selectBuilder.innerJoin(anomalyOccurrence.getTableName())
						 .on("AlarmOccurrence.ID = " + anomalyOccurrence.getTableName() + ".ID")
						 .andCondition(CriteriaAPI.getCondition(anomalyOccurrenceFields.get("parentAlarm"), String.valueOf(alarmId), PickListOperators.IS))
						 .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(resourceId), NumberOperators.EQUALS))
						 .andCondition(CriteriaAPI.getCondition(anomalyOccurrenceFields.get("mlanomalyType"),
									String.valueOf(MLAlarmOccurenceContext.MLAnomalyType.RCA.getIndex()), NumberOperators.EQUALS));
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		context.put("metrics", props);
		
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(createdTimeField);
		selectFields.add(clearedTimeField);
		
		selectBuilder = NewAlarmAPI.getAlarmBuilder(alarmId, dateRange.getStartTime(), dateRange.getEndTime(), selectFields, fieldMap);
		
		List<Map<String, Object>> ranges = selectBuilder.getAsProps();
		context.put("anomalyDateRanges", ranges);
		
		return false;
	}

}
