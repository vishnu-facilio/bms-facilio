package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetAddPurchasedToolCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PurchasedToolContext> purchasedTool = (List<PurchasedToolContext>) context
				.get(FacilioConstants.ContextNames.PURCHASED_TOOL);
		long toolId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<PurchasedToolContext> ptToBeAdded = new ArrayList<>();
		List<PurchasedToolContext> purchaseToolsList = new ArrayList<>();
		long toolTypeId = -1;
		if (purchasedTool != null && !purchasedTool.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
			List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

			FacilioModule purchasedToolModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
			List<FacilioField> purchasedToolFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
			 Map<String, FacilioField> purchasedToolFieldMap =  FieldFactory.getAsMap(purchasedToolFields);
			
			
			FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
			List<FacilioField> toolTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

			SelectRecordsBuilder<ToolContext> itemselectBuilder = new SelectRecordsBuilder<ToolContext>()
					.select(toolFields).table(toolModule.getTableName()).moduleName(toolModule.getName())
					.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(toolId, toolModule));

			List<ToolContext> tools = itemselectBuilder.get();
			ToolContext tool = new ToolContext();
			if (tools != null && !tools.isEmpty()) {
				tool = tools.get(0);
			}
			
			if (purchasedTool != null && !tools.isEmpty()) {
				toolTypeId = tool.getToolType().getId();
				SelectRecordsBuilder<ToolTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
						.select(toolTypesFields).table(toolTypesModule.getTableName())
						.moduleName(toolTypesModule.getName()).beanClass(ToolTypesContext.class)
						.andCondition(CriteriaAPI.getIdCondition(toolTypeId, toolTypesModule));
				
				List<ToolTypesContext> toolTypes = itemTypesselectBuilder.get();
				ToolTypesContext toolType = null;
				if (toolTypes != null && !toolTypes.isEmpty()) {
					toolType = toolTypes.get(0);
				}

				if (toolType == null) {
					throw new IllegalArgumentException("No such tool found");
				}

				for (PurchasedToolContext pt : purchasedTool) {
					pt.setTool(tool);
					pt.setToolType(toolType);
					pt.setCostDate(System.currentTimeMillis());
					pt.setIsUsed(false);
					if (pt.getId() <= 0) {
						// Insert
						purchaseToolsList.add(pt);
						ptToBeAdded.add(pt);
					} else {
						purchaseToolsList.add(pt);
						updatePurchasedTool(purchasedToolModule, purchasedToolFields, pt);
					}

				}
				if (ptToBeAdded != null && !ptToBeAdded.isEmpty()) {
					addPurchasedTool(purchasedToolModule, purchasedToolFields, ptToBeAdded);
				}
			}
			
			long lastPurchasedDate= getLastPurchasedToolDateForToolId(toolId, purchasedToolModule, purchasedToolFields, purchasedToolFieldMap);
			
			ToolContext update_tool = new ToolContext();
			update_tool.setId(toolId);
			update_tool.setLastPurchasedDate(lastPurchasedDate);
			updateLastPurchasedDateForTool(toolModule,toolFields, update_tool);
			
			context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchaseToolsList);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, purchaseToolsList);
			context.put(FacilioConstants.ContextNames.TOOL_ID, toolId);
			context.put(FacilioConstants.ContextNames.TOOL_IDS, Collections.singletonList(toolId));
			context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
			context.put(FacilioConstants.ContextNames.RECORD, tool);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, Collections.singletonList(toolTypeId));

		}
		context.put(FacilioConstants.ContextNames.TOOL_ID, toolId);
		context.put(FacilioConstants.ContextNames.TOOL_IDS, Collections.singletonList(toolId));
		context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
		return false;
	}

	private void addPurchasedTool(FacilioModule module, List<FacilioField> fields, List<PurchasedToolContext> parts)
			throws Exception {
		InsertRecordBuilder<PurchasedToolContext> readingBuilder = new InsertRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).addRecords(parts);
		readingBuilder.save();
	}

	private void updatePurchasedTool(FacilioModule module, List<FacilioField> fields, PurchasedToolContext part)
			throws Exception {

		UpdateRecordBuilder<PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println(Thread.currentThread().getName() + "Exiting updateCosts in  AddorUpdateCommand#######  ");

	}
	
	private long getLastPurchasedToolDateForToolId(long id, FacilioModule module, List<FacilioField> purchasedToolFields, Map<String, FacilioField> fieldMap) throws Exception {
		long lastPurchasedDate = -1;
		SelectRecordsBuilder<PurchasedToolContext> itemselectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
				.select(purchasedToolFields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedToolContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("tool"), String.valueOf(id), NumberOperators.EQUALS))
				.orderBy("COST_DATE DESC");
		List<PurchasedToolContext> purchasedToolContexts = itemselectBuilder.get();
		if(purchasedToolContexts!=null && !purchasedToolContexts.isEmpty()) {
			lastPurchasedDate = purchasedToolContexts.get(0).getCostDate();
		}
		
		return lastPurchasedDate;
	}
	
	private void updateLastPurchasedDateForTool(FacilioModule module, List<FacilioField> fields, ToolContext part)
			throws Exception {

		UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);


	}

}
