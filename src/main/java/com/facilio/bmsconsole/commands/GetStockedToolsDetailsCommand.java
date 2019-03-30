package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class GetStockedToolsDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			ToolContext stockedTools = (ToolContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (stockedTools != null && stockedTools.getId() > 0) {
				if (stockedTools.getToolType().getId() != -1) {
					ToolTypesContext tool = ToolsApi.getToolTypes(stockedTools.getToolType().getId());
					stockedTools.setToolType(tool);
				}
				
				if (stockedTools.getStoreRoom().getId() != -1) {
					Map<Long, StoreRoomContext> storeroomMap = StoreroomApi.getStoreRoomMap
							((stockedTools.getStoreRoom().getId()));
					stockedTools.setStoreRoom(storeroomMap.get(stockedTools.getStoreRoom().getId()));
				}
			}
			context.put(FacilioConstants.ContextNames.TOOL, stockedTools);
		}
		return false;
	}

}