package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetModuleReportCardsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Map<String, Object>> cards = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.REPORT_CARDS);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		for(Map<String, Object> card: cards) {
			if (card.containsKey("criteria")) {
				long value = getValue(moduleName, (Criteria) card.get("criteria"), (Map<String, Object>) card.get("builderInfo"));
				if (card.containsKey("cardKey")) {
					card.put((String) card.get("cardKey"), value);
					card.remove("cardKey");
				}
				else {
					card.put("count", value);
				}
				card.remove("criteria");
				card.remove("builderInfo");
			}
			if (card.containsKey("secondaryCriteria")) {
				long count2 = getValue(moduleName, (Criteria) card.get("secondaryCriteria"), (Map<String, Object>) card.get("secondaryBuilderInfo"));
				card.put("secondaryCount", count2);
				card.remove("secondaryCriteria");
				card.remove("secondaryBuilderInfo");
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private long getValue(String moduleName, Criteria criteria, Map<String, Object> builderInfo) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioField idField = FieldFactory.getIdField(module);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
				.andCriteria(criteria);
				;
		
		List<FacilioField> fields = null;
		if (builderInfo != null) {
			if (builderInfo.containsKey("field")) {
				fields = (List<FacilioField>) builderInfo.get("field");
				builder.select(fields);
			}
			else {
				builder.aggregate(CommonAggregateOperator.COUNT, idField);
			}
			if (builderInfo.containsKey("groupBy")) {
				builder.groupBy((String) builderInfo.get("groupBy"));
			}
			if (builderInfo.containsKey("orderBy")) {
				builder.orderBy((String) builderInfo.get("orderBy"));
			}
			if (builderInfo.containsKey("limit")) {
				builder.limit((int) builderInfo.get("limit"));
			}
		}
		else {
			builder.aggregate(CommonAggregateOperator.COUNT, idField);
		}
		
		List<Map<String, Object>> props = builder.getAsProps();
		if (props != null && !props.isEmpty()) {
			if (fields != null) {
				FacilioField field = fields.get(0);
				if (field instanceof LookupField) {
					Map<String, Object> valueMap = (Map<String, Object>) props.get(0).get(field.getName());
					return (long) valueMap.get("id");
				}
			}
			return (long) props.get(0).get(idField.getName());
		}
		return 0;
	}

}
