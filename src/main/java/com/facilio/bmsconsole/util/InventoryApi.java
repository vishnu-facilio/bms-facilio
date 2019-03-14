package com.facilio.bmsconsole.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.context.ItemStatusContext;
import com.facilio.bmsconsole.context.ItemTypesCategoryContext;
import com.facilio.bmsconsole.context.ItemTypesStatusContext;
import com.facilio.bmsconsole.context.ToolStatusContext;
import com.facilio.bmsconsole.context.ToolTypesCategoryContext;
import com.facilio.bmsconsole.context.ToolTypesStatusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class InventoryApi {

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
