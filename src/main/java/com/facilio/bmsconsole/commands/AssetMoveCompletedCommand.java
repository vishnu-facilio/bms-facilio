package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

;

public class AssetMoveCompletedCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

//		if (module.getName().equals("assetMovement")) {
//			if (facilioStatus.getStatus().equals("Closed") &&
//					record.getStateFlowId() == StateFlowRulesAPI.getDefaultStateFlow(module).getId()) {
//				AssetContext asset = new AssetContext();
//				asset.setId(1);
//				asset.setSiteId(1);
//				asset.setCurrentSpaceId(1);
//				FacilioModule assetModule = modBean.getModule("asset");
//				List<FacilioField> assetFields =  modBean.getAllFields("asset");
//				UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
//						.module(assetModule)
//						.fields(assetFields)
//						.andCondition(CriteriaAPI.getIdCondition(record.getId(), module));
//				updateBuilder.update(asset);
//			}
//		}
		return false;
	}

}