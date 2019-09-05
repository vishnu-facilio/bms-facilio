package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.InventoryCategoryApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ImportToolCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<PurchasedToolContext> purchasedToolList = (List<PurchasedToolContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		if (purchasedToolList != null && !purchasedToolList.isEmpty() && storeRoomId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, Long> toolNameVsIdMap = ToolsApi.getToolTypesMap();
			Map<String, Long> categoryNameVsIdMap = InventoryCategoryApi.getAllInventoryCategories();
			
			List<ToolContext> toolsList = new ArrayList<>();
			for (PurchasedToolContext purchasedTool : purchasedToolList) {
				ToolTypesContext toolType = new ToolTypesContext();
				ToolContext tool = new ToolContext();
				if (toolNameVsIdMap != null && toolNameVsIdMap.containsKey(purchasedTool.getToolType().getName())) {
					toolType.setId(toolNameVsIdMap.get(purchasedTool.getToolType().getName()));
				} else {
					toolType = purchasedTool.getToolType();
					if ((purchasedTool.getSerialNumber() == null
							|| purchasedTool.getSerialNumber().equalsIgnoreCase("null"))) {
						toolType.setIsRotating(false);
						// tool.setPurchasedTools(null);
					} else if (purchasedTool.getSerialNumber() != null
							&& !purchasedTool.getSerialNumber().equalsIgnoreCase("null")) {
						toolType.setIsRotating(true);
						// tool.setPurchasedTools(Collections.singletonList(purchasedTool));
					}
					InventoryCategoryContext category = new InventoryCategoryContext();
					if (toolType.getCategory() != null) {
						if (categoryNameVsIdMap != null
								&& categoryNameVsIdMap.containsKey(toolType.getCategory().getName())) {
							category.setId(categoryNameVsIdMap.get(toolType.getCategory().getName()));
							toolType.setCategory(category);
						} else {
							category.setName(toolType.getCategory().getName());
							category.setDisplayName(toolType.getCategory().getName());
							category.setId(InventoryCategoryApi.insertInventoryCategory(category));
							if (categoryNameVsIdMap == null) {
								categoryNameVsIdMap = new HashMap<String, Long>();
							}
							categoryNameVsIdMap.put(category.getName(), category.getId());
						}
						toolType.setCategory(category);
					}
					long insertToolTypeId = insertToolType(modBean, toolType);
					if(toolNameVsIdMap == null) {
						toolNameVsIdMap = new HashMap<String, Long>();
					}
					toolNameVsIdMap.put(toolType.getName(), insertToolTypeId);
					toolType.setId(insertToolTypeId);
				}
				if ((purchasedTool.getSerialNumber() == null
						|| purchasedTool.getSerialNumber().equalsIgnoreCase("null"))) {
					tool.setQuantity(purchasedTool.getTool().getQuantity());
					tool.setPurchasedTools(null);
				} else {
					tool.setPurchasedTools(Collections.singletonList(purchasedTool));
				}
				tool.setToolType(toolType);
				tool.setStoreRoom(StoreroomApi.getStoreRoom(storeRoomId));
				toolsList.add(tool);
			}
			context.put(FacilioConstants.ContextNames.TOOLS, toolsList);
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
		}
		return false;
	}

	private long insertToolType(ModuleBean modBean, ToolTypesContext toolType) throws Exception {
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);
		InsertRecordBuilder<ToolTypesContext> insertRecordBuilder = new InsertRecordBuilder<ToolTypesContext>()
				.module(module).fields(fields);
		long id = insertRecordBuilder.insert(toolType);
		return id;
	}

}
