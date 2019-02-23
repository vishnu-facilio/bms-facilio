package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.StockedToolsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolsContext;
import com.facilio.bmsconsole.context.InventryContext.CostType;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;

public class GetStockedToolsDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			StockedToolsContext stockedTools = (StockedToolsContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (stockedTools != null && stockedTools.getId() > 0) {
				if (stockedTools.getTool().getId() != -1) {
					ToolsContext tool = ToolsApi.getTool(stockedTools.getTool().getId());
					stockedTools.setTool(tool);
				}
				
				if (stockedTools.getStoreRoom().getId() != -1) {
					Map<Long, StoreRoomContext> storeroomMap = StoreroomApi.getStoreRoomMap
							((stockedTools.getStoreRoom().getId()));
					stockedTools.setStoreRoom(storeroomMap.get(stockedTools.getStoreRoom().getId()));
				}
			}
			context.put(FacilioConstants.ContextNames.STOCKED_TOOLS, stockedTools);
		}
		return false;
	}

}