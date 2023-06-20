package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.ScriptContext;

@ScriptModule(moduleName = FacilioConstants.ContextNames.ALARM_OCCURRENCE)
public class FacilioAlarmModuleFunctions extends FacilioModuleFunctionImpl {
	
	public List<Map<String, Object>> getTopNAlarms(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		String moduleName = (String)objects.get(1);
		int limit = Double.valueOf(objects.get(2).toString()).intValue();
		Criteria criteria = null;
		long duration = -1;
		int durationOperator = 0;
		String groupBy = "alarm";
		if (objects.size() >=4 ) {
			criteria = (Criteria)objects.get(3);
			if (objects.size() >= 5 ) {
				duration =  Double.valueOf(objects.get(4).toString()).longValue();
				durationOperator = Double.valueOf(objects.get(5).toString()).intValue();
				if (objects.size() >= 7 ) {
					groupBy = (String) objects.get(6);
				}
			}
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<FacilioField> selectFields = new ArrayList<>();
		String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();
		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(String.valueOf(System.currentTimeMillis())).append(") - ")
				.append(createdTimeFieldColumn).append(")/1000");
				;
		FacilioField durationField = FieldFactory.getField("durationAggr", durationAggrColumn.toString(), FieldType.NUMBER);
		selectFields.addAll(fields);
		selectFields.add(durationField);
		
		SelectRecordsBuilder<? extends AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(selectFields)
				.module(module)
				.beanClass(ContextNames.getClassFromModule(module))
				.groupBy(fieldMap.get(groupBy).getCompleteColumnName())
				.orderBy(durationField.getName() + " desc")
				.limit(limit);
		if (criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		if (duration != -1) {
			Operator operator = Operator.getOperator(durationOperator);
			Criteria havingCrit = new Criteria();
			havingCrit.addAndCondition(CriteriaAPI.getCondition(durationField, String.valueOf(duration), operator));
			selectBuilder.having(havingCrit.computeWhereClause());
		}
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		return props;
	}

}
