package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetCategoryResourcesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		if (categoryReadingRelModule != null && !categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) { // Don't execute if its asset category reading module
			long category=(long)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
			
			List <? extends ResourceContext> resourceList= null;
			String categoryName=(String)context.get(FacilioConstants.ContextNames.PARENT_MODULE);
			if(categoryName.equals(FacilioConstants.ContextNames.SPACE_CATEGORY)) {
				resourceList=SpaceAPI.getSpaceListOfCategory(category);
			}
			else if (categoryName.equals(FacilioConstants.ContextNames.ASSET_CATEGORY)) {
				resourceList=AssetsAPI.getAssetListOfCategory(category);
			}
			
			if (resourceList != null) {
				context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, resourceList.stream().map(ResourceContext::getId).collect(Collectors.toList()));
			}
		}
		
		return false;
	}

}
