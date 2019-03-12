package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ItemsApi {
	public static Map<Long, ItemTypesContext> getItemsMap(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemTypesContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.getAsMap();
	}

	public static ItemTypesContext getItem(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemTypesContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ItemTypesContext> items = selectBuilder.get();
		if (items != null && !items.isEmpty()) {
			return items.get(0);
		}
		return null;
	}
}
