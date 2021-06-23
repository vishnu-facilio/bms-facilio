package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class PreparePMForMultipleAsset extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		PMTriggerContext trigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER);

		if (pm != null && pm.getPmCreationTypeEnum() != null && pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE) && trigger != null) {
			Map<Long, List<PMResourcePlannerContext>> pmResourcesPlanners =
					PreventiveMaintenanceAPI.getPMResourcesPlanners(Collections.singletonList(pm.getId()));

			List<PMResourcePlannerContext> resourcePlannerContexts = pmResourcesPlanners.get(pm.getId());
			pm.setPmIncludeExcludeResourceContexts(new ArrayList<>());
			if (!CollectionUtils.isEmpty(resourcePlannerContexts)) {
				for (PMResourcePlannerContext pmResourcePlannerContext: resourcePlannerContexts) {
					List<PMTriggerContext> triggerContexts = pmResourcePlannerContext.getTriggerContexts();
					if (!CollectionUtils.isEmpty(triggerContexts)) {
						for (PMTriggerContext triggerContext: triggerContexts) {
							if (triggerContext.getId() == trigger.getId()) {
								PMIncludeExcludeResourceContext pmIncludeExcludeResourceContext = new PMIncludeExcludeResourceContext();
								pmIncludeExcludeResourceContext.setPmId(pm.getId());
								pmIncludeExcludeResourceContext.setIsInclude(true);
								pmIncludeExcludeResourceContext.setResourceId(pmResourcePlannerContext.getResourceId());
								pm.getPmIncludeExcludeResourceContexts().add(pmIncludeExcludeResourceContext);
							}
						}
					}
				}
			}
		}
		return false;
	}

}
