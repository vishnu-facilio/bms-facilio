package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateRange;


public class GetMLSummmaryDetail extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllWorkflowsCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long mlAnomalyId = (long) context.get(ContextNames.ALARM_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
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
		List<FacilioField> fields  = modBean.getAllFields(module.getName());
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();
		FacilioField alarmId = fieldMap.get("alarm");
		FacilioField resourceFieldColumn = fieldMap.get("resource");
		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(String.valueOf(System.currentTimeMillis())).append(") - ")
				.append(createdTimeFieldColumn).append(")")
				;
		List<FacilioField> selectFields = new ArrayList<>();
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);
		selectFields.add(durationField);
		selectFields.add(alarmId);
		selectFields.addAll(FieldFactory.getCountField(module));
		LookupField resourceLookup;
		if (!(resourceFieldColumn instanceof LookupField)) {
			LOGGER.debug("resource not lookup");
			resourceFieldColumn = fields.stream().filter(field -> field.getName().equals("resource")).findFirst().get();
		}
		resourceLookup = (LookupField) resourceFieldColumn;
		SelectRecordsBuilder<MLAlarmOccurenceContext> builder = new SelectRecordsBuilder<MLAlarmOccurenceContext>().module(module)
				.beanClass(MLAlarmOccurenceContext.class).select(selectFields)
				.fetchSupplement(resourceLookup)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentAlarm"), String.valueOf(mlAnomalyId),  NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN))
				.groupBy(fieldMap.get("resource").getCompleteColumnName());
		List<Map<String, Object>> list = builder.getAsProps();
		for (Map<String, Object> prop : list) {
			Object alarmObject = prop.get("alarm");
			prop.put(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, MLAPI.getRCALastOccurrence((Long) ((Map) alarmObject).get("id"), mlAnomalyId));
		}
		context.put(ContextNames.ML_RCA_ALARMS, list);
		return false;
	}

}
