package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class GetResourceListForMultiplePM extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		PreventiveMaintenance preventivemaintenance = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		if(preventivemaintenance == null) {
			Long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			preventivemaintenance = PreventiveMaintenanceAPI.getActivePM(pmId, true);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		}

		if(preventivemaintenance == null) {
			throw new IllegalArgumentException("PreventiveMaintenance Context cannot be null here");
		}

		if(preventivemaintenance.getPmCreationType() != PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
			return false;
		}

		List<PMTriggerContext> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(preventivemaintenance);
		if (CollectionUtils.isEmpty(pmTriggers)) {
			return false;
		}

		List<PMTriggerContext> userTriggers = pmTriggers.stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.USER).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(userTriggers)) {
			return false;
		}

		Map<Long, PMTriggerContext> userTriggerMap = new HashMap<>();

		for (PMTriggerContext userTrigger: userTriggers) {
			userTriggerMap.put(userTrigger.getId(), userTrigger);
		}

		Set<Long> userTriggerIds = userTriggers.stream().map(PMTriggerContext::getId).collect(Collectors.toSet());

		Long resourceId = preventivemaintenance.getBaseSpaceId();
		if (resourceId == null || resourceId < 0) {
			resourceId = preventivemaintenance.getSiteId();
		}
		List<Long> resIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(preventivemaintenance.getAssignmentTypeEnum(), resourceId, preventivemaintenance.getSpaceCategoryId(), preventivemaintenance.getAssetCategoryId(), null, preventivemaintenance.getPmIncludeExcludeResourceContexts());

		Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(preventivemaintenance.getId());

		List<Long> filteredRes = new ArrayList<>();
		for (Long resId: resIds) {
			PMResourcePlannerContext resourcePlannerContext = pmResourcePlanner.get(resId);
			List<PMTriggerContext> triggerContexts = resourcePlannerContext.getTriggerContexts();
			for (PMTriggerContext triggerContext: triggerContexts) {
				if (userTriggerIds.contains(triggerContext.getId())) {
					PMTriggerContext userTrigger = userTriggerMap.get(triggerContext.getId());
					if (userTrigger.getSharingContext().isAllowed()) {
						filteredRes.add(resId);
						break;
					}
				}
			}
		}

		context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCE_IDS, filteredRes);
		context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCES, ResourceAPI.getResources(filteredRes, false));
		return false;
	}

}
