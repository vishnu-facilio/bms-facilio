package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class GetAssetSpecifcReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if (parentId != -1) {
			AssetContext asset = AssetsAPI.getAssetInfo(parentId);
			List<FacilioModule> readings = ResourceAPI.getResourceSpecificReadings(parentId);
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			
		 	context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, asset.getCategory() != null ? asset.getCategory().getId() : -1);
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		}
		else {
			throw new IllegalArgumentException("Parent ID cannot be null when getting readings for Space");
		}
		return false;
	}

}
