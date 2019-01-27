package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;

public class getResourceListForMultiplePM implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		PreventiveMaintenance preventivemaintenance = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		if(preventivemaintenance == null) {

			Long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			preventivemaintenance = PreventiveMaintenanceAPI.getActivePM(pmId, true);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		}
		if(preventivemaintenance == null) {
			throw new IllegalArgumentException("PreventiveMaintenance Context cannot be null here");
		}
		if(preventivemaintenance.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
			Long resourceId = preventivemaintenance.getBaseSpaceId();
			if (resourceId == null || resourceId < 0) {
				resourceId = preventivemaintenance.getSiteId();
			}
			List<Long> resIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(preventivemaintenance.getAssignmentTypeEnum(), resourceId, preventivemaintenance.getSpaceCategoryId(), preventivemaintenance.getAssetCategoryId(), null, preventivemaintenance.getPmIncludeExcludeResourceContexts());
			
			context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCE_IDS, resIds);
			context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCES, ResourceAPI.getResources(resIds, false));
		}
		return false;
	}

}
