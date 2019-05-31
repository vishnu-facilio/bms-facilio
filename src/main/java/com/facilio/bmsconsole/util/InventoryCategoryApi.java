package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class InventoryCategoryApi {
	public static Map<String, Long> getAllInventoryCategories() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		SelectRecordsBuilder<InventoryCategoryContext> selectBuilder = new SelectRecordsBuilder<InventoryCategoryContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(InventoryCategoryContext.class);
		List<InventoryCategoryContext> inventoryCategories = selectBuilder.get();
		Map<String, Long> inventoryCategoryNameVsIdMap = new HashMap<>();
		if (inventoryCategories != null && !inventoryCategories.isEmpty()) {
			for (InventoryCategoryContext category : inventoryCategories) {
				inventoryCategoryNameVsIdMap.put(category.getName(), category.getId());
			}
			return inventoryCategoryNameVsIdMap;
		}
		return null;
	}
	
	public static long insertInventoryCategory(InventoryCategoryContext category) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		InsertRecordBuilder<InventoryCategoryContext> insertRecordBuilder = new InsertRecordBuilder<InventoryCategoryContext>()
				.module(module).fields(fields);
		long id = insertRecordBuilder.insert(category);
		return id;
	}
}
