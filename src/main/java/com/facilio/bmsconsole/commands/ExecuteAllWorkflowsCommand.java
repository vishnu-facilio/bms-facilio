package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionTemplate;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;

public class ExecuteAllWorkflowsCommand implements Command 
{
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		if(record != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.getModule("workorder").getModuleId();
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			List<WorkflowRuleContext> workflowRules = WorkflowAPI.getWorkflowRulesFromEvent(orgId, moduleId, eventType.getValue());
			
			Map<String, String> placeHolders = new HashMap<>(); 
			
			if(workflowRules != null) {
				for(WorkflowRuleContext workflowRule : workflowRules)
				{
					Criteria criteria = workflowRule.getCriteria();
					boolean flag = criteria.computePredicate().evaluate(record);
					if(flag)
					{
						long workflowRuleId = workflowRule.getId();
						List<ActionContext> actions = ActionAPI.getActionsFromWorkflowRule(orgId, workflowRuleId);
						if(actions != null) {
							for(ActionContext action : actions)
							{
								ActionTemplate template = action.getTemplate();
								if(template != null) {
									JSONObject actionObj = template.getTemplate(placeHolders);
									action.getActionType().performAction(actionObj);
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
