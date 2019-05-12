package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
				if(trigger.getRuleId() != -1) {
					ruleIds.add(trigger.getRuleId());
				}
				triggerIds.add(trigger.getId());
			});
			
			List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getPMJobs(triggerIds,true);
			templateIds.addAll(pmJobs.stream().map(PMJobsContext::getTemplateId).collect(Collectors.toList()));
			
			if (!ruleIds.isEmpty()) {
				WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
			}
			
			FacilioModule triggerModule = ModuleFactory.getPMTriggersModule();
			List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(triggerFields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(triggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField, oldPm.getId()+"", NumberOperators.EQUALS));
			deleteBuilder.delete();
			
		}
		
		TemplateAPI.deleteTemplates(templateIds);
		
		return false;
	}

}
