package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.AggregateOperator.CommonAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetModuleReportCardsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Map<String, Object>> cards = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.REPORT_CARDS);
		for(Map<String, Object> card: cards) {
			String moduleName = (String) card.get("moduleName");
			if (card.containsKey("criteria")) {
				long count = getCount(moduleName, (Criteria) card.get("criteria"));
				card.put("count", count);
				card.remove("criteria");
			}
			if (card.containsKey("secondaryCriteria")) {
				long count2 = getCount(moduleName, (Criteria) card.get("secondaryCriteria"));
				card.put("secondaryCount", count2);
				card.remove("secondaryCriteria");
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private long getCount(String moduleName, Criteria criteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioField idField = FieldFactory.getIdField(module);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.aggregate(CommonAggregateOperator.COUNT, idField)
				.andCriteria(criteria);
				;
		
		List<Map<String, Object>> props = builder.getAsProps();
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get(idField.getName());
		}
		return 0;
	}

}
