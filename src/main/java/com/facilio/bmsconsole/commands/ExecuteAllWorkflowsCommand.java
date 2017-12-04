package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionTemplate;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecuteAllWorkflowsCommand implements Command 
{
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(record != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			if(activityType != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				List<WorkflowRuleContext> workflowRules = WorkflowAPI.getActiveWorkflowRulesFromActivity(orgId, moduleId, activityType.getValue());
				
				if(workflowRules != null && workflowRules.size() > 0) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					
					for(WorkflowRuleContext workflowRule : workflowRules)
					{
						Criteria criteria = workflowRule.getCriteria();
						boolean flag = true;
						if(criteria != null) {
							flag = criteria.computePredicate().evaluate(record);
						}
						
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
										action.getActionType().performAction(actionObj, context);
									}
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
