package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
				.select(modBean.getAllFields("inventoryCategory")).moduleName("inventoryCategory")
				.beanClass(InventoryCategoryContext.class);
		return selectBuilder.get();
	}

	public static List<ItemTypesCategoryContext> getItemTypesCategoryList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemTypesCategoryContext> selectBuilder = new SelectRecordsBuilder<ItemTypesCategoryContext>()
				.select(modBean.getAllFields("itemTypesCategory")).moduleName("itemTypesCategory")
				.beanClass(ItemTypesCategoryContext.class);
		return selectBuilder.get();
	}

	public static List<ToolTypesCategoryContext> getToolTypesCategoryList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolTypesCategoryContext> selectBuilder = new SelectRecordsBuilder<ToolTypesCategoryContext>()
				.select(modBean.getAllFields("toolTypesCategory")).moduleName("toolTypesCategory")
				.beanClass(ToolTypesCategoryContext.class);
		return selectBuilder.get();
	}
	
	public static List<ItemTypesStatusContext> getItemTypesStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemTypesStatusContext> selectBuilder = new SelectRecordsBuilder<ItemTypesStatusContext>()
				.select(modBean.getAllFields("itemTypesStatus")).moduleName("itemTypesStatus")
				.beanClass(ItemTypesStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ToolTypesStatusContext> getToolTypesStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolTypesStatusContext> selectBuilder = new SelectRecordsBuilder<ToolTypesStatusContext>()
				.select(modBean.getAllFields("toolTypesStatus")).moduleName("toolTypesStatus")
				.beanClass(ToolTypesStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ItemStatusContext> getItemStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ItemStatusContext> selectBuilder = new SelectRecordsBuilder<ItemStatusContext>()
				.select(modBean.getAllFields("itemStatus")).moduleName("itemStatus")
				.beanClass(ItemStatusContext.class);
		return selectBuilder.get();
	}
	
	public static List<ToolStatusContext> getToolStatusList() throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<ToolStatusContext> selectBuilder = new SelectRecordsBuilder<ToolStatusContext>()
				.select(modBean.getAllFields("toolStatus")).moduleName("toolStatus")
				.beanClass(ToolStatusContext.class);
		return selectBuilder.get();
	}
}
