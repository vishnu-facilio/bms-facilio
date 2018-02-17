package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeletePMTriggersAndTemplatesCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		
		PreventiveMaintenance oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		List<Long> templateIds = templateIds = new ArrayList<>();
		templateIds.add(oldPm.getTemplateId());
		
		if(oldPm.hasTriggers() && newPm.getTriggers() != null) {
			
			List<Long> ruleIds = new ArrayList<>();
			List<Long> triggerIds = new ArrayList<>();
			oldPm.getTriggers().forEach(trigger -> {
				if(trigger.getReadingRuleId() != -1) {
					ruleIds.add(trigger.getReadingRuleId());
				}
				triggerIds.add(trigger.getId());
			});
			
			List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getPMJobs(triggerIds,true);
			templateIds.addAll(pmJobs.stream().map(PMJobsContext::getTemplateId).collect(Collectors.toList()));
			
			if (!ruleIds.isEmpty()) {
				WorkflowAPI.deleteWorkFlowRules(ruleIds);
			}
			
			FacilioModule triggerModule = ModuleFactory.getPMTriggersModule();
			List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(triggerFields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(triggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField, oldPm.getId()+"", NumberOperators.EQUALS));
			deleteBuilder.delete();
			
		}
		
		TemplateAPI.deleteTemplates(UserTemplate.Type.JSON, templateIds);
		
		return false;
	}

}
