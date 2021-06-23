package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetToolVendorsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			long toolId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
			SelectRecordsBuilder<ToolTypeVendorContext> selectBuilder = new SelectRecordsBuilder<ToolTypeVendorContext>()
					.select(fields).table(module.getTableName())
					.moduleName(module.getName()).beanClass(ToolTypeVendorContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("toolType"),
							String.valueOf(toolId), PickListOperators.IS));

			List<ToolTypeVendorContext> toolVendors = selectBuilder.get();
			if (toolVendors != null && !toolVendors.isEmpty()) {
				for (ToolTypeVendorContext toolVendor : toolVendors) {
					VendorContext vendor = getVendor(toolVendor.getVendor().getId());
					toolVendor.setVendor(vendor);
				}
			}
			context.put(FacilioConstants.ContextNames.TOOL_VENDORS, toolVendors);
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
