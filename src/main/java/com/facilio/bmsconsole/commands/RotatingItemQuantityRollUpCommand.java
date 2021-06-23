package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class RotatingItemQuantityRollUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> assetIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<Long> itemIds = new ArrayList<Long>();
		List<Long> toolIds = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(assetIds)) {
			for(Long id : assetIds) {
				AssetContext asset = AssetsAPI.getAssetInfo(id, true);
				context.put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
				context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
				if (asset.getRotatingItem() != null) {
					if (asset.getRotatingItem().getId() > 0) {
					ItemContext item = asset.getRotatingItem();
					itemIds.add(item.getId());
					
					}
				}
				if (asset.getRotatingTool() != null) {
					if (asset.getRotatingTool().getId() > 0) {
						ToolContext tool = asset.getRotatingTool();
						toolIds.add(tool.getId());
					}
				}
			}
			if(CollectionUtils.isNotEmpty(itemIds)) {
				context.put(FacilioConstants.ContextNames.ITEM_IDS, itemIds);
				FacilioChain c = TransactionChainFactory.getUpdateItemQuantityRollupChain();
				c.execute(context);
			}
			if(CollectionUtils.isNotEmpty(toolIds)) {
				context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
				FacilioChain c2 = TransactionChainFactory.getUpdatetoolQuantityRollupChain();
				c2.execute(context);
			}
		}
		return false;
	}

}
