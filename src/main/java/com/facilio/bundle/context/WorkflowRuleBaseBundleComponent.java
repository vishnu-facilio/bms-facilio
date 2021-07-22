package com.facilio.bundle.context;

import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public abstract  class WorkflowRuleBaseBundleComponent implements BundleComponentInterface {

	public abstract WorkflowRuleContext.RuleType getWorkflowRuleType();
	
	@Override
	public JSONObject getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowRuleContext workflowRule = (WorkflowRuleContext) context.get(BundleConstants.COMPONENT_OBJECT);
		
		JSONObject parentObject = (JSONObject)context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
		
		String moduleName = (String) parentObject.get("name");
		
		for( ActionContext action : workflowRule.getActions()) {
			
			JSONObject templateJSON = getTemplateJSONAsPerType(moduleName,action.getTemplate());
			action.setTemplateJson(templateJSON);
			action.setTemplate(null);
			action.setTemplateId(-1l);
			action.setId(-1);
		}
		return FieldUtil.getAsJSON(workflowRule);
	}

	private JSONObject getTemplateJSONAsPerType(String moduleName, Template template) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject templateJSON = new JSONObject();
		
		switch(template.getTypeEnum()) {
			case WORKFLOW: {
				
				String workflowV2String = (String) ((JSONObject)template.getOriginalTemplate().get("workflowContext")).get("workflowV2String");
				Boolean isV2Script = (Boolean) ((JSONObject)template.getOriginalTemplate().get("workflowContext")).get("isV2Script");
				
				JSONObject workflowJSON = new JSONObject();
				
				workflowJSON.put("workflowV2String", workflowV2String);
				workflowJSON.put("isV2Script", isV2Script);
				
				templateJSON.put("resultWorkflowContext", workflowJSON);
				
				break;
			}
			case JSON: {
				
				Set<String> fieldNames = template.getOriginalTemplate().keySet();
				
				JSONArray fieldMatcherJson = new JSONArray();
				for(String fieldName : fieldNames) {
					
					FacilioField field = modBean.getField(fieldName, moduleName);
					
					JSONObject fieldMatcher = new JSONObject();
			    	
					fieldMatcher.put("columnName", field.getCompleteColumnName());
					fieldMatcher.put("field", fieldName);
					fieldMatcher.put("isSpacePicker", false);
					fieldMatcher.put("value", template.getOriginalTemplate().get(fieldName));
					
					fieldMatcherJson.add(fieldMatcher);
				}
		    	
		    	templateJSON.put("fieldMatcher",fieldMatcherJson);
		    	
		    	break;
			}
			case EMAIL:
			case SMS:
			case PUSH_NOTIFICATION: {
				
				templateJSON = template.getOriginalTemplate();
				
				templateJSON.put("ftl", template.getFtl());
				break;
			}
			default: {
				templateJSON = template.getOriginalTemplate();
				break;
			}
		}
		
		templateJSON.put("name", template.getName());
		if(template.getWorkflow() != null) {
			templateJSON.put("Workflow", FieldUtil.getAsJSON(template.getWorkflow()));
		}
		
		return templateJSON;
	}

	@Override
	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject parentObject = (JSONObject)context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
		String moduleName = (String) parentObject.get("name");
		
		FacilioChain chain = ReadOnlyChainFactory.getCustomModuleWorkflowRulesChain();
        FacilioContext newContext = chain.getContext();
        newContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        newContext.put(FacilioConstants.ContextNames.RULE_TYPE, getWorkflowRuleType().getIntVal());
        chain.execute();

        List<WorkflowRuleContext> workflowRules = (List<WorkflowRuleContext>) newContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
        
        if(workflowRules == null) {
        	return null;
        }
        
        JSONArray returnList = new JSONArray();
		for(WorkflowRuleContext workflowRule : workflowRules) {
			
			context.put(BundleConstants.COMPONENT_OBJECT, workflowRule);
			JSONObject formattedObject = getFormatedObject(context);
			
			returnList.add(formattedObject);
		}
		
		return returnList;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject parentModule = (JSONObject) context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
		
		FacilioModule module = modBean.getModule((String)parentModule.get("name"));
		
		JSONObject workflowRuleJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
		
		WorkflowRuleContext workflowRule = FieldUtil.getAsBeanFromJson(workflowRuleJSON, WorkflowRuleContext.class);
		
		System.out.println("module name ::: "+module.getName() +" workflowRule name -- "+workflowRule.getName());
		
		workflowRule.setModule(module);
		workflowRule.setModuleId(module.getModuleId());
		
		FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext newContext = chain.getContext();
        newContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        newContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);
        chain.execute();
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
