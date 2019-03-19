package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class BulkToolAdditionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ToolContext> toolsList = (List<ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);
		List<Long> toolTypesId = new ArrayList<>();
		Map<Long, Long> toolTypeVsTool = new HashMap<>();
		SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>().select(toolFields)
				.table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition(toolFieldMap.get("storeRoom"), String.valueOf(storeRoomId),
						NumberOperators.EQUALS));

		List<ToolContext> tools = toolselectBuilder.get();
		if (tools != null && !tools.isEmpty()) {
			for (ToolContext tool : tools) {
				toolTypesId.add(tool.getToolType().getId());
				toolTypeVsTool.put(tool.getToolType().getId(), tool.getId());
			}
		}

		List<ToolContext> toolToBeAdded = new ArrayList<>();
		for (ToolContext tool : toolsList) {
			if (!toolTypesId.contains(tool.getToolType().getId())) {
				toolToBeAdded.add(tool);
			} else {
				tool.setId(toolTypeVsTool.get(tool.getToolType().getId()));
			}
		}

		if (toolToBeAdded != null && !toolToBeAdded.isEmpty()) {
			addTool(toolModule, toolFields, toolToBeAdded);
		}
		
		List<Long> toolIds = new ArrayList<>();
		List<Long> toolTypesIds = new ArrayList<>();
		for (ToolContext tool : toolsList) {
			toolIds.add(tool.getId());
			toolTypesIds.add(tool.getToolType().getId());
		}

		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolsList);
		context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
		context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
		context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
		return false;
	}

	private void addTool(FacilioModule module, List<FacilioField> fields, List<ToolContext> tool) throws Exception {
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields).addRecords(tool);
		readingBuilder.save();
	}
}