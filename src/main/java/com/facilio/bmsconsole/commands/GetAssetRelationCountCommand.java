package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetRelationCountCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Boolean showCount = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_RELATIONS_COUNT);
		if (showCount != null && showCount) {
			AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.ASSET);
			Map<String, Long> countMap = new HashMap<>();
			
			countMap.put("readings", ReadingsAPI.getReadingDataMetaCount(assetContext.getId(), true, null));
			countMap.put("attachments", AttachmentsAPI.fetchAttachmentsCount(FacilioConstants.ContextNames.ASSET_ATTACHMENTS, assetContext.getId()));
			countMap.put("notes", NotesAPI.fetchNotesCount(assetContext.getId(),-1,FacilioConstants.ContextNames.ASSET_NOTES));
			
			context.put(FacilioConstants.ContextNames.RELATIONS_COUNT, countMap);
		}
		return false;
	}

}
