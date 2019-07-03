package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetToolTypesForVendorCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
			SelectRecordsBuilder<ToolTypeVendorContext> selectBuilder = new SelectRecordsBuilder<ToolTypeVendorContext>()
					.select(fields).table(module.getTableName())
					.moduleName(module.getName()).beanClass(ToolTypeVendorContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("vendor"),
							String.valueOf(vendorId), PickListOperators.IS));

			List<ToolTypeVendorContext> toolVendors = selectBuilder.get();
			if (toolVendors != null && !toolVendors.isEmpty()) {
				for (ToolTypeVendorContext toolVendor : toolVendors) {
					ToolTypesContext toolTypes = getToolType(toolVendor.getToolType().getId());
					toolVendor.setToolType(toolTypes);
				}
			}
			context.put(FacilioConstants.ContextNames.TOOL_VENDORS, toolVendors);
		}
		return false;
	}

	
	public static ToolTypesContext getToolType(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ToolTypesContext> selectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(ToolTypesContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ToolTypesContext> toolTypes =  selectBuilder.get();
		if(toolTypes!=null &&!toolTypes.isEmpty()) {
			return toolTypes.get(0);
		}
		return null;
	}


}
