package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class GetCategoryResourcesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetCategoryResourcesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("Inside GetCategoryResourcesCommand");
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);

		long category = (long) context.getOrDefault(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, -1l);
		String parentModule = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		List<? extends ResourceContext> resourceList = getResourceDataList(parentModule, category);


		if (categoryReadingRelModule!=null &&categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {    // parent module will be asset module. so checking categoryreadingrelmodule
			resourceList = AssetsAPI.getAssetListOfCategory(category);
		}

		if (resourceList != null) {
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, resourceList.stream().map(ResourceContext::getId).collect(Collectors.toList()));
		}


		return false;
	}

	private static List<? extends ResourceContext> getResourceDataList(String parentModule, long category) throws Exception {
		switch (parentModule) {
			case FacilioConstants.ContextNames.SPACE_CATEGORY:
				return SpaceAPI.getSpaceListOfCategory(category);
			case FacilioConstants.ContextNames.ASSET_CATEGORY:
				return AssetsAPI.getAssetListOfCategory(category);
			case FacilioConstants.ContextNames.SITE:
				return SpaceAPI.getAllSites();
			case FacilioConstants.ContextNames.FLOOR:
				return SpaceAPI.getAllFloors();
			case FacilioConstants.ContextNames.BUILDING:
				return SpaceAPI.getAllBuildings();
		}
		return new ArrayList<>();
	}

}
