package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ItemTypesVendorsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetItemTypesForVendorCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderItemsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderItemsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderItemsFieldMap = FieldFactory.getAsMap(workorderItemsFields);
			long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
			SelectRecordsBuilder<ItemTypesVendorsContext> selectBuilder = new SelectRecordsBuilder<ItemTypesVendorsContext>()
					.select(workorderItemsFields).table(workorderItemsModule.getTableName())
					.moduleName(workorderItemsModule.getName()).beanClass(ItemTypesVendorsContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderItemsFieldMap.get("vendor"),
							String.valueOf(vendorId), PickListOperators.IS));

			List<ItemTypesVendorsContext> ietmVendors = selectBuilder.get();
			if (ietmVendors != null && !ietmVendors.isEmpty()) {
				for (ItemTypesVendorsContext itemVendor : ietmVendors) {
					ItemTypesContext itemTypes = getItemType(itemVendor.getItemType().getId());
					itemVendor.setItemType(itemTypes);
				}
			}
			context.put(FacilioConstants.ContextNames.ITEM_VENDORS, ietmVendors);
		}
		return false;
	}

	
	public static ItemTypesContext getItemType(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(ItemTypesContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ItemTypesContext> itemTypes =  selectBuilder.get();
		if(itemTypes!=null &&!itemTypes.isEmpty()) {
			return itemTypes.get(0);
		}
		return null;
	}
}