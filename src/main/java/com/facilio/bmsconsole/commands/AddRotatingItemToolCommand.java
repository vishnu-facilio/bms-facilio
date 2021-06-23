package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddRotatingItemToolCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, null);
		
		if (asset != null)
		{
			if((asset.getRotatingItem() != null && asset.getRotatingItem().getId() > 0) && (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0))
			{
				throw new IllegalArgumentException("An asset can be either a Rotating Item or a Rotating Tool. Can't be both");
			}
			else
			{
			context.put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
				if (asset.getRotatingItem() != null) {
					if (asset.getRotatingItem().getId() > 0) 
						{
							ItemContext item = asset.getRotatingItem();
							context.put(FacilioConstants.ContextNames.ITEM, item);
							FacilioChain c = TransactionChainFactory.getAddOrUpdateItemStockTransactionChain();
							c.execute(context);
						}
				}
				if (asset.getRotatingTool() != null) {
					if (asset.getRotatingTool().getId() > 0)
						{
							ToolContext tool = asset.getRotatingTool();
							context.put(FacilioConstants.ContextNames.TOOL, tool);
							FacilioChain c = TransactionChainFactory.getAddOrUpdateToolStockTransactionChain();
							c.execute(context);
						}
				}
			}
		}
	return false;
	}

}
