package com.facilio.bmsconsole.commands.anomaly;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class GetEnergyByCDDCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetEnergyByCDDCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		long resourceId = (long) context.get(ContextNames.RESOURCE_ID);
		long siteId = (long) context.get(ContextNames.SITE_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Map<String, Object>> energy = getReadings("energydata", modBean, resourceId, dateRange, "totalEnergyConsumptionDelta");
		List<Map<String, Object>> cdd = getReadings("cdd", modBean, siteId, dateRange, "cdd");
		JSONObject obj = new JSONObject();
		obj.put("energy", energy);
		obj.put("cdd", cdd);
		
		LOGGER.debug("energyCdd: " + obj);
		
		context.put("energyCdd", obj);
		
		return false;
	}
	
	private List<Map<String, Object>> getReadings(String moduleName, ModuleBean modBean, long resourceId, DateRange dateRange, String fieldName) throws Exception {
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap= FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ReadingContext> selectRecordBuilder = new SelectRecordsBuilder<ReadingContext>()
				.moduleName(moduleName)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateRange.getStartTime()+","+dateRange.getEndTime(), DateOperators.BETWEEN))
				.aggregate(NumberAggregateOperator.SUM, fieldMap.get(fieldName))
				.aggregate(NumberAggregateOperator.MIN, fieldMap.get("ttime"))
				.groupBy(DateAggregateOperator.MONTH.getSelectField(fieldMap.get("ttime")).getCompleteColumnName())
				;
		
		return selectRecordBuilder.getAsProps();
	}

}
