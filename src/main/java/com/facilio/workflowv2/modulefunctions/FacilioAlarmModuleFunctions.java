package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FacilioAlarmModuleFunctions extends FacilioModuleFunctionImpl {
	
	public List<Map<String, Object>> getTopNAlarms(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		String moduleName = (String)objects.get(1);
		int limit = Integer.parseInt(objects.get(2).toString());
		Criteria criteria = null;
		if (objects.size() >=4 ) {
			criteria = (Criteria)objects.get(3);
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
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);
		selectFields.addAll(fields);
		selectFields.add(durationField);
		
		SelectRecordsBuilder<? extends AlarmOccurrenceContext> selectBuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(selectFields)
				.module(module)
				.beanClass(ContextNames.getClassFromModule(module))
				.groupBy(fieldMap.get("alarm").getCompleteColumnName())
				.orderBy(durationField.getName() + " desc")
				.limit(limit);
		if (criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		return props;
	}

}
