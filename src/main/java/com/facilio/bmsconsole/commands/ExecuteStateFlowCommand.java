package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

public class ExecuteStateFlowCommand extends ExecuteAllWorkflowsCommand {

    public ExecuteStateFlowCommand() {
        super(WorkflowRuleContext.RuleType.STATE_FLOW);
    }

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
        List<WorkflowRuleContext> workflowRules = super.getWorkflowRules(module, activities, records, context);
        List<WorkflowRuleContext> newWorkflowRules = null;

        // Re-arrange execution order
        if (CollectionUtils.isNotEmpty(workflowRules)) {
            List<Long> formStateFlowIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(records)) {
                List<Long> allFormIds = records.stream().filter(record -> record.getFormId() > 0).map(ModuleBaseWithCustomFields::getFormId).collect(Collectors.toList());
                List<FacilioForm> formsFromDB = FormsAPI.getFormsFromDB(allFormIds);
                if (CollectionUtils.isNotEmpty(formsFromDB)) {
                    formStateFlowIds = formsFromDB.stream().filter(form -> form.getStateFlowId() > 0).map(FacilioForm::getStateFlowId).collect(Collectors.toList());
                }
            }

        	newWorkflowRules = new ArrayList<>();
	        for (WorkflowRuleContext workflowRuleContext : workflowRules) {
	            StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) workflowRuleContext;
	            if (formStateFlowIds.contains(stateFlowRuleContext.getId())) {
                    StateFlowRuleContext cloned = FieldUtil.cloneBean(stateFlowRuleContext, StateFlowRuleContext.class);
                    cloned.setShouldCheckOnlyFormBased(true);
	                newWorkflowRules.add(0, cloned);
	            }
	            newWorkflowRules.add(stateFlowRuleContext);
	        }
        }
        return newWorkflowRules;
    }
}
