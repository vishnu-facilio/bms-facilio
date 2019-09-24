package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class AddRotatingItemToolForImportCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReadingContext asset = (ReadingContext) context.get(FacilioConstants.ContextNames.RECORD);
		AssetContext rotAsset = new AssetContext();
		rotAsset.setId(asset.getId());
		
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, null);
		
		Map<String, Object> rot = (Map<String, Object>) asset.getData().get("rotatingItem");
		ItemContext rotItem = FieldUtil.getAsBeanFromMap(rot, ItemContext.class);
		rotAsset.setRotatingItem(rotItem);
		if (asset != null) {
			context.put(FacilioConstants.ContextNames.ROTATING_ASSET, rotAsset);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);

			context.put(FacilioConstants.ContextNames.ITEM, rotItem);
			Chain c = TransactionChainFactory.getAddOrUpdateItemStockTransactionChain();
			c.execute(context);

		}
		return false;
	}

}
