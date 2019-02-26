package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.mssql.SelectRecordBuilder;

public class ItemsApi {
	public static Map<Long, ItemsContext> getItemsMap(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEMS);
		SelectRecordsBuilder<ItemsContext> selectBuilder = new SelectRecordsBuilder<ItemsContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.getAsMap();
	}

	public static ItemsContext getItem(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEMS);
		SelectRecordsBuilder<ItemsContext> selectBuilder = new SelectRecordsBuilder<ItemsContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemsContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ItemsContext> items = selectBuilder.get();
		if (items != null && !items.isEmpty()) {
			return items.get(0);
		}
		return null;
	}
}
