package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.ResourceContext;
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
import com.facilio.time.DateRange;

public class MLAPI {
	public static MLContext getSubMeterDetails (long mlAnomalyAlarmId) throws Exception {
		
			return null;
		
	}
	
	public static MLAlarmOccurenceContext getRCALastOccurrence (Long alarmId, Long parentId ) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<MLAlarmOccurenceContext> builder = new SelectRecordsBuilder<MLAlarmOccurenceContext>()
				.beanClass(MLAlarmOccurenceContext.class).moduleName(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE)
				.select(fields).andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"),
						String.valueOf(alarmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentAlarm"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mlanomalyType"),
						String.valueOf(MLAlarmOccurenceContext.MLAnomalyType.RCA.getIndex()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC, ID DESC").limit(1);
		MLAlarmOccurenceContext alarmOccurrenceContext = builder.fetchFirst();
		
		return alarmOccurrenceContext;
		
	}
	
	
	public static List<Map<String, Object>> getAlarmInsight (Context context) throws Exception {
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

		FacilioModule baseAlarmModule = modBean.getModule(ContextNames.BASE_ALARM);
		FacilioModule occurrenceModule = modBean.getModule(ContextNames.ALARM_OCCURRENCE);

		List<FacilioField> baseAlarmFields = modBean.getAllFields(baseAlarmModule.getName());
		List<FacilioField> occurrenceFields = modBean.getAllFields(occurrenceModule.getName());
		Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(baseAlarmFields);
		fieldMaps.putAll(FieldFactory.getAsMap(occurrenceFields));

		List<FacilioField> selectFields = new ArrayList<>();
		String clearedTimeFieldColumn = fieldMaps.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMaps.get("createdTime").getColumnName();
		FacilioField resourceFieldColumn = fieldMaps.get("resource");

		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(System.currentTimeMillis()).append(") - ")
				.append(createdTimeFieldColumn).append(")")
				;
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);

//		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.add(resourceFieldColumn);
		selectFields.addAll(FieldFactory.getCountField(occurrenceModule));
		FacilioField alarmField = fieldMaps.get("alarm");
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(selectFields)
				.beanClass(AlarmOccurrenceContext.class)
				.module(baseAlarmModule)
				.innerJoin(occurrenceModule.getTableName())
				.on(occurrenceModule.getTableName() + ".ALARM_ID = " + baseAlarmModule.getTableName() + ".ID")
				;
		if (alarmId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS))
			.groupBy(alarmField.getCompleteColumnName());
		}

		if (dateRange != null) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("createdTime"), operator));
		}

		List<Map<String, Object>> props = builder.getAsProps();
		// backward-compatibility
		if (CollectionUtils.isNotEmpty(props)) {
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
		return props;
		
	}
}
