package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionTemplate;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;

public class ExecuteAllWorkflowsCommand implements Command 
{
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(record != null) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			if(eventType != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				List<WorkflowRuleContext> workflowRules = WorkflowAPI.getActiveWorkflowRulesFromEvent(orgId, moduleId, eventType.getValue());
				
				if(workflowRules != null && workflowRules.size() > 0) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(OrgInfo.getCurrentOrgInfo()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(UserInfo.getCurrentUser()), placeHolders);
					
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
