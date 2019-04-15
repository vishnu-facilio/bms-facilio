package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ToolContext tool_rec = (ToolContext) context.get(FacilioConstants.ContextNames.RECORD);
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
		tool_rec.setLastPurchasedDate(System.currentTimeMillis());
		if (!toolTypesId.contains(tool_rec.getToolType().getId())) {
			toolToBeAdded.add(tool_rec);
		} else {
			tool_rec.setId(toolTypeVsTool.get(tool_rec.getToolType().getId()));
		}

		if (toolToBeAdded != null && !toolToBeAdded.isEmpty()) {
			addTool(toolModule, toolFields, toolToBeAdded);
		}

		List<Long> toolIds = new ArrayList<>();
		List<Long> toolTypesIds = new ArrayList<>();
		toolIds.add(tool_rec.getId());
		toolTypesIds.add(tool_rec.getToolType().getId());

		List<PurchasedToolContext> purchasedTools = new ArrayList<>();
		if (tool_rec.getPurchasedTools() != null && !tool_rec.getPurchasedTools().isEmpty()) {
			for (PurchasedToolContext pTool : tool_rec.getPurchasedTools()) {
				pTool.setTool(tool_rec);
				pTool.setToolType(tool_rec.getToolType());
				purchasedTools.add(pTool);
			}
			tool_rec.setPurchasedTools(null);
		} 
//		else {
//			SelectRecordsBuilder<ToolContext> toolBuilder = new SelectRecordsBuilder<ToolContext>().select(toolFields)
//					.table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(ToolContext.class)
//					.andCondition(CriteriaAPI.getIdCondition(tool_rec.getId(), toolModule));
//			List<ToolContext> toolcontext = toolBuilder.get();
//			if (toolcontext != null && !toolcontext.isEmpty()) {
//				tool_rec.setQuantity(tool_rec.getQuantity() + toolcontext.get(0).getCurrentQuantity());
//				tool_rec.setCurrentQuantity(tool_rec.getQuantity() + toolcontext.get(0).getCurrentQuantity());
//				updateTool(toolModule, toolFields, tool_rec);
//			}
//
//		}

		context.put(FacilioConstants.ContextNames.RECORD, tool_rec);
		context.put(FacilioConstants.ContextNames.RECORD_ID, tool_rec.getId());
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
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

	
	private void updateTool(FacilioModule module, List<FacilioField> fields, ToolContext tool) throws Exception {
		UpdateRecordBuilder<ToolContext> readingBuilder = new UpdateRecordBuilder<ToolContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		readingBuilder.update(tool);
	}

}
