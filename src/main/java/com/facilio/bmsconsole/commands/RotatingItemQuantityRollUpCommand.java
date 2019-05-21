package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class RotatingItemQuantityRollUpCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> assetIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		for(Long id : assetIds) {
			AssetContext asset = AssetsAPI.getAssetInfo(id);
			context.put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
			if (asset.getRotatingItem() != null) {
				if (asset.getRotatingItem().getId() > 0) {
				ItemContext item = asset.getRotatingItem();
				context.put(FacilioConstants.ContextNames.ITEM, item);
				Chain c = TransactionChainFactory.getUpdateItemQuantityRollupChain();
				c.execute(context);
				}
			}
			if (asset.getRotatingTool() != null) {
				if (asset.getRotatingTool().getId() > 0) {
					ToolContext tool = asset.getRotatingTool();
					context.put(FacilioConstants.ContextNames.TOOL, tool);
					Chain c = TransactionChainFactory.getUpdatetoolQuantityRollupChain();
					c.execute(context);
				}
			}
		}
		
		return false;
	}

}
