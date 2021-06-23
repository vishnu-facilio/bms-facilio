package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViolationAlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetKPIOccurrencesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<String> ids = (List<String>) context.get(ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isEmpty(ids)) {
			return false;
		}
		
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.VIOLATION_ALARM_OCCURRENCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		List<FacilioField> fields = new ArrayList<>();
		FacilioField formulaField = fieldMap.get("formulaField");
		FacilioField resourceField = fieldMap.get("resource");
		fields.add(formulaField);
		fields.add(resourceField);
		
		SelectRecordsBuilder<ViolationAlarmContext> selectBuilder = new SelectRecordsBuilder<ViolationAlarmContext>()
				.module(module).beanClass(ViolationAlarmContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN))
				.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
				.groupBy(formulaField.getColumnName()+","+resourceField.getColumnName())
				;
		
		Criteria criteriaList = new Criteria();
		for(String id: ids) {
			String[] kpi_resource = id.split("_");
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(formulaField, kpi_resource[0], PickListOperators.IS));
			criteria.addAndCondition(CriteriaAPI.getCondition(resourceField, kpi_resource[1], PickListOperators.IS));
			criteriaList.orCriteria(criteria);
		}
		selectBuilder.andCriteria(criteriaList);
		
		List<Map<String, Object>> alarmList = selectBuilder.getAsProps();
		Map<String, Long> countMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(alarmList)) {
			Map<String, Long> occurrenceMap = new HashMap<>();
			for(Map<String, Object> alarm: alarmList) {
				Map<String, Long> formula = (Map<String, Long>) alarm.get("formulaField");
				Map<String, Long> resource = (Map<String, Long>) alarm.get("resource");
				occurrenceMap.put(formula.get("id")+"_"+resource.get("id"), (long) alarm.get("id"));
			}
			for(String id: ids) {
				long count = 0;
				if (occurrenceMap.containsKey(id)) {
					count = occurrenceMap.get(id);
				}
				countMap.put(id, count);
			}
		}
		context.put(ContextNames.ALARM_COUNT, countMap);
		
		return false;
	}
}
