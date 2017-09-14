package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.getModule("workorder").getModuleId();
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			List<WorkflowRuleContext> workflowRules = WorkflowAPI.getWorkflowRulesFromEvent(orgId, moduleId, eventType.getValue());
			
			if(workflowRules != null && workflowRules.size() > 0) {
				Map<String, Object> placeHolders = new HashMap<>();
				appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), placeHolders);
				appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(OrgInfo.getCurrentOrgInfo()), placeHolders);
				appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(UserInfo.getCurrentUser()), placeHolders);
				
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
	
	private void appendModuleNameInKey(String moduleName, String prefix, Map<String, Object> beanMap, Map<String, Object> placeHolders) throws Exception {
		if(beanMap != null) {
			if(moduleName != null && !moduleName.isEmpty() && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				
				if(fields != null && !fields.isEmpty()) {
					for(FacilioField field : fields) {
						if(field.getDataType() == FieldType.LOOKUP) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if(props != null) {
								LookupField lookupField = (LookupField) field;
								if(props.size() <= 2) {
									Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"), 0);
									placeHolders.put(prefix+"."+field.getName(), lookupVal);
									props = FieldUtil.getAsProperties(lookupVal);
								}
								String childModuleName = lookupField.getLookupModule() == null?lookupField.getSpecialType():lookupField.getLookupModule().getName();
								appendModuleNameInKey(childModuleName, prefix+"."+field.getName(), props, placeHolders);
							}
						}
						else {
							placeHolders.put(prefix+"."+field.getName(), beanMap.remove(field.getName()));
						}
					}
				}
			}
			for(Map.Entry<String, Object> entry : beanMap.entrySet()) {
				if(entry.getValue() instanceof Map<?, ?>) {
					appendModuleNameInKey(null, prefix+"."+entry.getKey(), (Map<String, Object>) entry.getValue(), placeHolders);
				}
				else {
					placeHolders.put(prefix+"."+entry.getKey(), entry.getValue());
				}
			}
		}
	}
}
