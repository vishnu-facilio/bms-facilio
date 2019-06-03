package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddRotatingItemToolCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, null);

		if (asset != null) {
			context.put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
			if (asset.getRotatingItem() != null) {
				if (asset.getRotatingItem().getId() > 0) {
				ItemContext item = asset.getRotatingItem();
				context.put(FacilioConstants.ContextNames.ITEM, item);
				Chain c = TransactionChainFactory.getAddOrUpdateItemStockTransactionChain();
				c.execute(context);
				}
			}
			if (asset.getRotatingTool() != null) {
				if (asset.getRotatingTool().getId() > 0) {
					ToolContext tool = asset.getRotatingTool();
					context.put(FacilioConstants.ContextNames.TOOL, tool);
					Chain c = TransactionChainFactory.getAddOrUpdateToolStockTransactionChain();
					c.execute(context);
				}
			}
		}
		return false;
	}

}
