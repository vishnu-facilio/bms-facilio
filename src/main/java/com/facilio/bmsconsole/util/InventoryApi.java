package com.facilio.bmsconsole.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryApi {
	
	public static InventoryContext getInventory(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY);
		
		SelectRecordsBuilder<InventoryContext> selectBuilder = new SelectRecordsBuilder<InventoryContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(InventoryContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<InventoryContext> inventories = selectBuilder.get();
		
		if(inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}
	
	public static Map<Long, InventoryVendorContext> getInventoryVendorMap(Collection<Long> idList) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<InventoryVendorContext> selectBuilder = new SelectRecordsBuilder<InventoryVendorContext>()
																		.select(modBean.getAllFields("inventory_vendors"))
																		.moduleName("inventory_vendors")
																		.beanClass(InventoryVendorContext.class);
		return selectBuilder.getAsMap();
	}
	
public static List<InventoryVendorContext> getInventoryVendorList() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<InventoryVendorContext> selectBuilder = new SelectRecordsBuilder<InventoryVendorContext>()
																		.select(modBean.getAllFields("inventory_vendors"))
																		.moduleName("inventory_vendors")
																		.beanClass(InventoryVendorContext.class);
		return selectBuilder.get();
	}

public static List<InventoryCategoryContext> getInventoryCategoryList() throws Exception {
	
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	SelectRecordsBuilder<InventoryCategoryContext> selectBuilder = new SelectRecordsBuilder<InventoryCategoryContext>()
																	.select(modBean.getAllFields("inventoryCategory"))
																	.moduleName("inventoryCategory")
																	.beanClass(InventoryCategoryContext.class);
	return selectBuilder.get();
}
}
