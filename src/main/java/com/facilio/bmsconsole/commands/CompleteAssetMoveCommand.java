package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

;

public class CompleteAssetMoveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSet = (Map<String, Map<Long, List<UpdateChangeSet>>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isEmpty(recordIds)) {
			recordIds = new ArrayList<Long>();
			recordIds.add((Long) context.get(FacilioConstants.ContextNames.RECORD_ID));
		}
		if(CollectionUtils.isNotEmpty(recordIds) && changeSet != null && !changeSet.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(MapUtils.isNotEmpty(changeSet)) {
				Map<Long, List<UpdateChangeSet>> moduleChangeSet = changeSet.get(FacilioConstants.ContextNames.ASSET_MOVEMENT);
				if(MapUtils.isNotEmpty(moduleChangeSet)) {
					for(Long recordId : recordIds) {
						List<UpdateChangeSet> updatedSet = moduleChangeSet.get(recordId);
						if(!updatedSet.isEmpty()) {
							for(UpdateChangeSet changes : updatedSet) {
								FacilioField field = modBean.getField(changes.getFieldId()) ;
								if(field != null) {
									if(field.getName().equals("moduleState")) {
										JSONObject info = new JSONObject();
										AssetMovementContext assetMovement = AssetsAPI.getAssetMovementContext(recordId);
										info.put("movementId", recordId);
										info.put("newStatus", changes.getNewValue());
										CommonCommandUtil.addActivityToContext(assetMovement.getAssetId(), -1, AssetActivityType.UPDATE_MOVEMENT, info, (FacilioContext) context);
										FacilioStatus status = StateFlowRulesAPI.getStateContext((long)changes.getNewValue());
										if(status.getType() == StatusType.CLOSED) {
											AssetsAPI.updateAssetMovement(recordId);
											JSONObject completedInfo = new JSONObject();
											completedInfo.put("currentSite", SpaceAPI.getBaseSpace(assetMovement.getToSite()).getName());
											completedInfo.put("currentSpace", SpaceAPI.getBaseSpace(assetMovement.getToSpace()).getName());
											CommonCommandUtil.addActivityToContext(assetMovement.getAssetId(), -1, AssetActivityType.LOCATION, completedInfo, (FacilioContext) context);
										}
									}
								}
							}
						}
					}
				}else{
					AssetMovementContext assetMovement = (AssetMovementContext) context.get(FacilioConstants.ContextNames.RECORD);
					AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.ASSET);
					if (asset != null && (asset.getMoveApprovalNeeded() == null || !asset.getMoveApprovalNeeded())) {
						AssetsAPI.updateAssetMovement((Long) context.get(FacilioConstants.ContextNames.RECORD_ID));
						JSONObject completedInfo = new JSONObject();
						completedInfo.put("currentSite", SpaceAPI.getBaseSpace(assetMovement.getToSite()).getName());
						completedInfo.put("currentSpace", SpaceAPI.getBaseSpace(assetMovement.getToSpace()).getName());
						CommonCommandUtil.addActivityToContext(assetMovement.getAssetId(), -1,AssetActivityType.LOCATION, completedInfo, (FacilioContext) context);
					}
				}
			}
		}
			
		return false;
	}
	
	
}