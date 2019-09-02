package com.facilio.bmsconsole.commands;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.RCAAlarm;
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
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateRange;


public class GetMLSummmaryDetail extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long mlAnomalyId = (long) context.get(ContextNames.ALARM_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
		LookupField resourceLookup = (LookupField) FieldFactory.getField("resource", "RESOURCE_ID", FieldType.LOOKUP);
		resourceLookup.setLookupModule(ModuleFactory.getResourceModule());
//		FacilioModule occurrenceModule = modBean.getModule(ContextNames.ALARM_OCCURRENCE);
//		List<FacilioField> occurrenceFields = modBean.getAllFields(occurrenceModule.getName());
//		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(occurrenceFields);
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
		SelectRecordsBuilder<MLAlarmOccurenceContext> builder = new SelectRecordsBuilder<MLAlarmOccurenceContext>().module(module)
				.beanClass(MLAlarmOccurenceContext.class).select(modBean.getAllFields(module.getName()))
				.fetchLookup(resourceLookup)
				.andCondition(CriteriaAPI.getCondition("PARENTID", "parentID", String.valueOf(mlAnomalyId),  NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", dateRange.toString(), DateOperators.BETWEEN))
				.groupBy("PARENTID");
		List<MLAlarmOccurenceContext> list = builder.get();
		context.put(ContextNames.ML_RCA_ALARMS, list);
		return false;
	}

}
