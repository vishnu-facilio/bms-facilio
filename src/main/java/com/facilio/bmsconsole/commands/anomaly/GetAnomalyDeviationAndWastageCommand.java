package com.facilio.bmsconsole.commands.anomaly;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetAnomalyDeviationAndWastageCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetAnomalyDeviationAndWastageCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		
		long alarmId = (long) context.get(ContextNames.ALARM_ID);
		long resourceId = (long) context.get(ContextNames.RESOURCE_ID);
		boolean isRCA = (boolean) context.getOrDefault(ContextNames.IS_RCA, false);
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		List<Map<String, Object>> ranges = (List<Map<String, Object>>) context.get("anomalyDateRanges");
		if (CollectionUtils.isEmpty(ranges)) {
			return false;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule logReadingModule = modBean.getModule("anomalydetectionmllogreadings");
		List<FacilioField> fields = modBean.getAllFields(logReadingModule.getName());
		Map<String, FacilioField> fieldMap= FieldFactory.getAsMap(fields);
		
		FacilioField actualValue = fieldMap.get("actualValue");
		FacilioField adjustedUpperBound = fieldMap.get("adjustedUpperBound");
		String upperBoundColumn = fieldMap.get("adjustedUpperBound").getCompleteColumnName();
		
		String diff = "("+fieldMap.get("actualValue").getCompleteColumnName()+"-"+upperBoundColumn+")";
		FacilioField wastage = FieldFactory.getField("wastage", diff, FieldType.NUMBER); 
		FacilioField deviation = FieldFactory.getField("deviation", diff+"/"+upperBoundColumn+"*100", FieldType.NUMBER); 

		SelectRecordsBuilder<ReadingContext> selectRecordBuilder = new SelectRecordsBuilder<ReadingContext>()
				.module(logReadingModule)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("actualValue"), logReadingModule.getName()+"."+adjustedUpperBound.getName(), FieldOperator.GREATER_THAN))
				.aggregate(NumberAggregateOperator.AVERAGE, deviation)
				.aggregate(NumberAggregateOperator.SUM, wastage)
				.aggregate(NumberAggregateOperator.MIN, fieldMap.get("ttime"))
				.groupBy(DateAggregateOperator.MONTH.getSelectField(fieldMap.get("ttime")).getCompleteColumnName())
				;
		
		Criteria criteria = new Criteria();
		for(Map<String, Object> range: ranges) {
			long startTime = (long) range.get("createdTime");
			if (startTime < dateRange.getStartTime()) {
				startTime = dateRange.getStartTime();
			}
			long clearedTime = -1;
			if (range.containsKey("clearedTime")) {
				clearedTime = (long) range.get("clearedTime");
			}
			if (clearedTime == -1 || clearedTime> dateRange.getEndTime()) {
				clearedTime = dateRange.getEndTime();
			}
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), startTime+","+clearedTime, DateOperators.BETWEEN));
		}
		selectRecordBuilder.andCriteria(criteria);
		List<Map<String, Object>> readings = selectRecordBuilder.getAsProps();
		LOGGER.debug("projections: " + (readings != null ? readings : ""));
		context.put("projections", readings);
		
		return false;
	}

}
