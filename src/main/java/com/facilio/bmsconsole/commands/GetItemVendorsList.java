package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemVendorsContext;
import com.facilio.bmsconsole.context.StockedToolsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetItemVendorsList implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderItemsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderItemsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderItemsFieldMap = FieldFactory.getAsMap(workorderItemsFields);
			long itemId = (long) context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID);
			SelectRecordsBuilder<ItemVendorsContext> selectBuilder = new SelectRecordsBuilder<ItemVendorsContext>()
					.select(workorderItemsFields).table(workorderItemsModule.getTableName())
					.moduleName(workorderItemsModule.getName()).beanClass(ItemVendorsContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderItemsFieldMap.get("item"),
							String.valueOf(itemId), PickListOperators.IS));

			List<ItemVendorsContext> ietmVendors = selectBuilder.get();
			if (ietmVendors != null && !ietmVendors.isEmpty()) {
				for (ItemVendorsContext itemVendor : ietmVendors) {
					VendorContext vendor = getVendor(itemVendor.getVendor().getId());
					itemVendor.setVendor(vendor);
				}
			}
			context.put(FacilioConstants.ContextNames.ITEM_VENDORS, ietmVendors);
		}
		return false;
	}

	
	public static VendorContext getVendor(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
		SelectRecordsBuilder<VendorContext> selectBuilder = new SelectRecordsBuilder<VendorContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(VendorContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<VendorContext> vendors =  selectBuilder.get();
		if(vendors!=null &&!vendors.isEmpty()) {
			return vendors.get(0);
		}
		return null;
	}
}
