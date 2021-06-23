package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class ConstructAddAssetMovementActivitiesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long assetMovementId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName == null) {
			moduleName = ContextNames.ASSET_MOVEMENT;
		}

		JSONObject info = new JSONObject();
		AssetMovementContext assetMovement = AssetsAPI.getAssetMovementContext(assetMovementId);
		info.put("movementId", assetMovementId);
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.ASSET);
		if (asset != null && asset.getMoveApprovalNeeded() != null && asset.getMoveApprovalNeeded()) {
			CommonCommandUtil.addActivityToContext(assetMovement.getAssetId(), -1, AssetActivityType.CREATE_MOVEMENT,info, (FacilioContext) context);
		}
		return false;
	}

}
