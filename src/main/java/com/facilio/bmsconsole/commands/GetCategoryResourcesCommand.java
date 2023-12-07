package com.facilio.bmsconsole.commands;

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

         long category=(long)context.getOrDefault(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1l);
			
			List <? extends ResourceContext> resourceList= null;
			String categoryName=(String)context.get(FacilioConstants.ContextNames.PARENT_MODULE);
			if(categoryName.equals(FacilioConstants.ContextNames.SPACE_CATEGORY)) {
				resourceList=SpaceAPI.getSpaceListOfCategory(category);
			}
			else if (categoryName.equals(FacilioConstants.ContextNames.ASSET_CATEGORY)) {
				resourceList=AssetsAPI.getAssetListOfCategory(category);
			}
            else if (categoryName.equals(FacilioConstants.ContextNames.SITE)) {
                resourceList = SpaceAPI.getAllSites();
            }
			else if (categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {	// parent module will be asset module. so checking categoryreadingrelmodule
				resourceList=AssetsAPI.getAssetListOfCategory(category);
			}
			
			if (resourceList != null) {
				context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, resourceList.stream().map(ResourceContext::getId).collect(Collectors.toList()));
			}

		
		return false;
	}

}
