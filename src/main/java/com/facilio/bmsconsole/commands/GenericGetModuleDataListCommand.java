package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenericGetModuleDataListCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
															.select(fields)
															;
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		Integer maxLevel = (Integer) context.get(FacilioConstants.ContextNames.MAX_LEVEL);
		if(maxLevel != null && maxLevel != -1) {
			builder.maxLevel(maxLevel);
		}
		
		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		List<Condition> conditionList = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
		if(conditionList != null && !conditionList.isEmpty()) {
			for(Condition condition : conditionList) {
				builder.andCondition(condition);
			}
		}
		
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		
		return false;
	}

}
