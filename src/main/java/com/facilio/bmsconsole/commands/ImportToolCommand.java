package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImportToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<PurchasedToolContext> purchasedToolList = (List<PurchasedToolContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		if (purchasedToolList != null && !purchasedToolList.isEmpty() && storeRoomId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, Long> toolNameVsIdMap = ToolsApi.getToolTypesMap();
			List<ToolContext> toolsList = new ArrayList<>();
			for (PurchasedToolContext purchasedTool : purchasedToolList) {
				ToolTypesContext toolType = new ToolTypesContext();
				ToolContext tool = new ToolContext();
				if (toolNameVsIdMap.containsKey(purchasedTool.getToolType().getName())) {
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
					toolType.setId(insertToolType(modBean, toolType));
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
