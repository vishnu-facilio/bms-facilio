package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateToolVendorCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ToolTypeVendorContext> toolTypeVendorsList = (List<ToolTypeVendorContext>) context.get(FacilioConstants.ContextNames.TOOL_VENDORS_LIST);
		if(toolTypeVendorsList!=null){
			long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule toolVendorModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_VENDORS);
			List<FacilioField> toolVendorFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_VENDORS);
			Map<String, FacilioField> toolVendorFieldMap = FieldFactory.getAsMap(toolVendorFields);
			List<Long> toolTypesId = new ArrayList<>();
			Map<Long, Long> toolTypeVsVendor = new HashMap<>();
			
			SelectRecordsBuilder<ToolTypeVendorContext> toolVendorselectBuilder = new SelectRecordsBuilder<ToolTypeVendorContext>().select(toolVendorFields)
					.table(toolVendorModule.getTableName()).moduleName(toolVendorModule.getName()).beanClass(ToolTypeVendorContext.class)
					.andCondition(CriteriaAPI.getCondition(toolVendorFieldMap.get("vendor"), String.valueOf(vendorId),
							NumberOperators.EQUALS));
			
			List<ToolTypeVendorContext> toolVendorsList = toolVendorselectBuilder.get();
			if (toolVendorsList != null && !toolVendorsList.isEmpty()) {
				for (ToolTypeVendorContext toolVendor : toolVendorsList) {
					toolTypesId.add(toolVendor.getToolType().getId());
					toolTypeVsVendor.put(toolVendor.getToolType().getId(), toolVendor.getId());
				}
			}
			
			List<ToolTypeVendorContext> toolVendorsToBeAdded = new ArrayList<>();
			for (ToolTypeVendorContext toolVendors : toolTypeVendorsList) {
				if (!toolTypesId.contains(toolVendors.getToolType().getId())) {
					toolVendorsToBeAdded.add(toolVendors);
				} else {
					toolVendors.setId(toolTypeVsVendor.get(toolVendors.getToolType().getId()));
					updateToolVendor(toolVendorModule, toolVendorFields, toolVendors);
				}
			}
			
			if (CollectionUtils.isNotEmpty(toolVendorsToBeAdded)) {
				addTool(toolVendorModule, toolVendorFields, toolVendorsToBeAdded);
			}
		}
		return false;
	}

	private void addTool(FacilioModule module, List<FacilioField> fields, List<ToolTypeVendorContext> toolVendor) throws Exception {
		InsertRecordBuilder<ToolTypeVendorContext> readingBuilder = new InsertRecordBuilder<ToolTypeVendorContext>().module(module)
				.fields(fields).addRecords(toolVendor);
		readingBuilder.save();
	}
	
	private void updateToolVendor(FacilioModule module, List<FacilioField> fields, ToolTypeVendorContext toolVendor) throws Exception {
		UpdateRecordBuilder<ToolTypeVendorContext> builder = new UpdateRecordBuilder<ToolTypeVendorContext>().module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(toolVendor.getId(), module));
		builder.update(toolVendor);
	}

}
