package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkToolAdditionCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ToolContext> toolsList = (List<ToolContext>) context.get(FacilioConstants.ContextNames.TOOLS);
		if (toolsList != null && !toolsList.isEmpty()) {
			int size = 0;
			long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
			List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
			Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);
			List<Long> toolTypesId = new ArrayList<>();
			Map<Long, Long> toolTypeVsTool = new HashMap<>();
			SelectRecordsBuilder<ToolContext> toolselectBuilder = new SelectRecordsBuilder<ToolContext>()
					.select(toolFields).table(toolModule.getTableName()).moduleName(toolModule.getName())
					.beanClass(ToolContext.class).andCondition(CriteriaAPI.getCondition(toolFieldMap.get("storeRoom"),
							String.valueOf(storeRoomId), NumberOperators.EQUALS));

			List<ToolContext> tools = toolselectBuilder.get();
			if (tools != null && !tools.isEmpty()) {
				for (ToolContext tool : tools) {
					toolTypesId.add(tool.getToolType().getId());
					toolTypeVsTool.put(tool.getToolType().getId(), tool.getId());
				}
			}

			List<ToolContext> toolToBeAdded = new ArrayList<>();
			for (ToolContext tool : toolsList) {
				tool.setLastPurchasedDate(System.currentTimeMillis());
				if (!toolTypesId.contains(tool.getToolType().getId())) {
					toolToBeAdded.add(tool);
				} else {
					tool.setId(toolTypeVsTool.get(tool.getToolType().getId()));
					updateTool(toolModule, toolFields, tool);
					size += 1;
				}
			}

			if (toolToBeAdded != null && !toolToBeAdded.isEmpty()) {
				size += addTool(toolModule, toolFields, toolToBeAdded);
			}

			List<Long> toolIds = new ArrayList<>();
			List<Long> toolTypesIds = new ArrayList<>();
			List<PurchasedToolContext> purchasedTools = new ArrayList<>();
			Map<Long, List<PurchasedToolContext>> toolVsPurchaseTool = new HashMap<>();
			for (ToolContext tool : toolsList) {
				tool.setToolType(ToolsApi.getToolTypes(tool.getToolType().getId()));
				toolIds.add(tool.getId());
				toolTypesIds.add(tool.getToolType().getId());
				List<PurchasedToolContext> pTools = new ArrayList<>();
				if (tool.getPurchasedTools() != null && !tool.getPurchasedTools().isEmpty()) {
					for (PurchasedToolContext pTool : tool.getPurchasedTools()) {
						pTool.setTool(tool);
						pTool.setToolType(tool.getToolType());
						pTool.setCostDate(System.currentTimeMillis());
						pTools.add(pTool);
						purchasedTools.add(pTool);
					}
					tool.setPurchasedTools(null);
					toolVsPurchaseTool.put(tool.getId(), pTools);
				}
			}
			if (purchasedTools != null && !purchasedTools.isEmpty()) {
				size += addPurchasedTool(purchasedTools);
			}

			setImportProcessContext(context, size);

			context.put(FacilioConstants.ContextNames.RECORD_LIST, toolsList);
			context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
			context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
			context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, toolVsPurchaseTool);
		}
		return false;
	}

	private int addTool(FacilioModule module, List<FacilioField> fields, List<ToolContext> tool) throws Exception {
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields).addRecords(tool);
		readingBuilder.save();
		return readingBuilder.getRecords().size();
	}

	private void updateTool(FacilioModule module, List<FacilioField> fields, ToolContext tool) throws Exception {
		UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);
	}

	private int addPurchasedTool(List<PurchasedToolContext> tool) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
		InsertRecordBuilder<PurchasedToolContext> readingBuilder = new InsertRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).addRecords(tool);
		readingBuilder.save();
		return readingBuilder.getRecords().size();
	}

	private void setImportProcessContext(Context c, int size) throws ParseException {
		ImportProcessContext importProcessContext = (ImportProcessContext) c
				.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if (importProcessContext != null) {
			JSONObject meta = new JSONObject();
			if (!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getFieldMappingJSON();
				meta.put("Inserted", size + "");
			} else {
				meta.put("Inserted", size + "");
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
		}

	}
}