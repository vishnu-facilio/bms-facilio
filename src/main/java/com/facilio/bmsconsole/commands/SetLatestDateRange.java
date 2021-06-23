package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.time.DateTimeUtil;

public class SetLatestDateRange extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean defaultDate =  (boolean) context.get(FacilioConstants.ContextNames.DEFAULT_DATE);
		if(defaultDate){
			List<ReadingAnalysisContext> metrics = (List<ReadingAnalysisContext>) context.get(FacilioConstants.ContextNames.REPORT_Y_FIELDS);
			
			FacilioModule module = ModuleFactory.getReadingDataMetaModule();
			List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField resourceIdField = fieldMap.get("resourceId");
			FacilioField fieldIdField = fieldMap.get("fieldId");
			FacilioField valueField = fieldMap.get("value");
			
			Criteria pkCriteriaList = new Criteria();
			for (ReadingAnalysisContext metric : metrics) {
				Criteria pkCriteria = new Criteria();
				pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, metric.getParentId(), NumberOperators.EQUALS));
				pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(metric.getyAxis().getFieldId()), NumberOperators.EQUALS));
				pkCriteria.addAndCondition(CriteriaAPI.getCondition(valueField, "-1", NumberOperators.NOT_EQUALS));
				pkCriteriaList.orCriteria(pkCriteria);
			}
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCriteria(pkCriteriaList)
														.orderBy("ttime")
														.limit(1);
			if(!(builder.get().isEmpty())){
				Long startTime = DateTimeUtil.getDayStartTimeOf(Long.parseLong(builder.get().get(0).get("ttime").toString()));
				long currentTime = DateTimeUtil.getDayEndTimeOf(System.currentTimeMillis());
				
				context.put(FacilioConstants.ContextNames.START_TIME, startTime);
				context.put(FacilioConstants.ContextNames.END_TIME, currentTime);
				context.put(FacilioConstants.ContextNames.DATE_OPERATOR, DateOperators.BETWEEN.getOperatorId());
			}
		}
		return false;
	}

}