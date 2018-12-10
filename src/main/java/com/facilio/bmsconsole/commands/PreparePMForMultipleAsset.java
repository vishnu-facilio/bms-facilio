package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class PreparePMForMultipleAsset implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		if(pm != null && pm.getPmCreationTypeEnum() != null && pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE) && pmJob.getResourceId() > 0) {
			PMIncludeExcludeResourceContext pmIncludeExcludeResourceContext = new PMIncludeExcludeResourceContext();
			pmIncludeExcludeResourceContext.setPmId(pm.getId());
			pmIncludeExcludeResourceContext.setIsInclude(true);
			pmIncludeExcludeResourceContext.setResourceId(pmJob.getResourceId());
			pm.setPmIncludeExcludeResourceContexts(Collections.singletonList(pmIncludeExcludeResourceContext));
		}
		return false;
	}

}
