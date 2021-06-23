package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;

public class AddActionToDigestConfigCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
			DigestConfigContext config =  (DigestConfigContext)context.get(FacilioConstants.ContextNames.CONFIG);
			JSONArray expressions = (JSONArray)context.get(FacilioConstants.ContextNames.WORK_FLOW_EXPRESSIONS);
			
			if(config.getDefaultTemplateId() == null || config.getDefaultTemplateId() <= 0) {
				throw new IllegalArgumentException("default template id must be associated to digest configuration");
			}
			if(expressions == null || expressions.isEmpty()) {
				throw new IllegalArgumentException("To addresses must be specified for digest configuration");
			}
			
			Template template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, config.getDefaultTemplateId());
			template.setType(Type.EMAIL);
			template.setFtl(true);
			WorkflowContext workflowContext = template.getWorkflow();
			WorkflowUtil.parseExpression(workflowContext);
			
			WorkflowContext workflow = new WorkflowContext(workflowContext.getWorkflowString());
			if(CollectionUtils.isNotEmpty(expressions) && workflow != null) {
				workflow.setExpressions(FieldUtil.getAsJSONArray(workflowContext.getExpressions(), WorkflowExpression.class));
				workflow.setExpressions(expressions);
				workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
				List<ParameterContext> parameters = new ArrayList<ParameterContext>();
				parameters.addAll(workflowContext.getParameters());
				workflow.setParameters(parameters);
				workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
				
				StringJoiner toAddr = new StringJoiner(",");
				for(int i=0;i<expressions.size();i++) {
					String name = (String) ((Map)expressions.get(i)).get("name");
					if(name.contains("user_email")) {
						toAddr.add("${" + name + "}");
					}
				}
				JSONObject templateJSON = template.getOriginalTemplate();
				templateJSON.put("name", template.getName());
				templateJSON.put("workflow",FieldUtil.getAsProperties(workflow));
				templateJSON.put("to", toAddr.toString());
				templateJSON.put("ftl", template.isFtl());
				

				ActionContext action = new ActionContext();
				action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
				action.setStatus(true);
				action.setTemplateJson(templateJSON);
				List<ActionContext> actions = ActionAPI.addActions(Collections.singletonList(action), null);
				context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actions);
				
			}
		
		return false;
	}

}
