package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetSpaceSpecifcReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if (parentId != -1) {
			Boolean onlyReading = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_READING);
			if (onlyReading == null) {
				onlyReading = false;
			}
			
			SpaceType type = getSpaceType(parentId, context);
			List<FacilioModule> readings = ResourceAPI.getResourceSpecificReadings(parentId);
			if (readings == null) {
				readings = new ArrayList<FacilioModule>(); 
			}
			List<FacilioModule> moduleReadings = SpaceAPI.getDefaultReadings(type, onlyReading);
			if (moduleReadings != null) {
				readings.addAll(moduleReadings);
			}
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
		}
		else {
			throw new IllegalArgumentException("Parent ID cannot be null when getting readings for Space");
		}
		
		return false;
	}
	
	private SpaceType getSpaceType(long parentId, Context context) throws Exception {
		BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(parentId);
		if (baseSpace.getSpaceTypeEnum() == SpaceType.SPACE) {
				SpaceContext space = SpaceAPI.getSpace(parentId);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, space.getSpaceCategory() != null?space.getSpaceCategory().getId():-1);
				context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
		}
		return baseSpace.getSpaceTypeEnum();
	}

}
