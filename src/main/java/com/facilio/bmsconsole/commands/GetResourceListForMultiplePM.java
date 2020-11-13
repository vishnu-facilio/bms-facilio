package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

		Long resourceId = preventivemaintenance.getBaseSpaceId();
		if (resourceId == null || resourceId < 0) {
			resourceId = preventivemaintenance.getSiteId();
		}
		List<Long> resIds;
		if (preventivemaintenance.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			List<Long> pmSites = PreventiveMaintenanceAPI.getPMSites(preventivemaintenance.getId());
			resIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(preventivemaintenance.getAssignmentTypeEnum(), pmSites, preventivemaintenance.getSpaceCategoryId(), preventivemaintenance.getAssetCategoryId(), null, preventivemaintenance.getPmIncludeExcludeResourceContexts());
		} else {
			resIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(preventivemaintenance.getAssignmentTypeEnum(), resourceId, preventivemaintenance.getSpaceCategoryId(), preventivemaintenance.getAssetCategoryId(), null, preventivemaintenance.getPmIncludeExcludeResourceContexts());
		}


		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		List<PMTriggerContext> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(preventivemaintenance);

		if (superAdmin.getOuid() == AccountUtil.getCurrentUser().getOuid() || !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SKIP_TRIGGERS)) {
			context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCE_IDS, resIds);
			context.put(FacilioConstants.ContextNames.MULTI_PM_RESOURCES, ResourceAPI.getResources(resIds, false));
			return false;
		}

		if (!CollectionUtils.isEmpty(pmTriggers)) {
			List<PMTriggerContext> userTriggers = pmTriggers.stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.USER).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(userTriggers) && superAdmin.getOuid() != AccountUtil.getCurrentUser().getOuid() && !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SKIP_TRIGGERS)) {
				return false;
			}
			Map<Long, PMTriggerContext> userTriggerMap = new HashMap<>();

			for (PMTriggerContext userTrigger: userTriggers) {
				userTriggerMap.put(userTrigger.getId(), userTrigger);
			}

			Set<Long> userTriggerIds = userTriggers.stream().map(PMTriggerContext::getId).collect(Collectors.toSet());

			Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(preventivemaintenance.getId());

			List<Long> filteredRes = new ArrayList<>();
			for (Long resId: resIds) {
				PMResourcePlannerContext resourcePlannerContext = pmResourcePlanner.get(resId);
				List<PMTriggerContext> triggerContexts;
				if (resourcePlannerContext != null) {
					triggerContexts = resourcePlannerContext.getTriggerContexts();
				} else {
					triggerContexts = PreventiveMaintenanceAPI.getDefaultTrigger(preventivemaintenance.isDefaultAllTriggers(), pmTriggers);
				}


				for (PMTriggerContext triggerContext: triggerContexts) {
					if (userTriggerIds.contains(triggerContext.getId())) {
						PMTriggerContext userTrigger = userTriggerMap.get(triggerContext.getId());
						if (userTrigger.getSharingContext() == null || userTrigger.getSharingContext().isAllowed()) {
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

		return false;
	}

}
