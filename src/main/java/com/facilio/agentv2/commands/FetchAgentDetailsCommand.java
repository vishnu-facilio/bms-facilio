package com.facilio.agentv2.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class FetchAgentDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long id = (long) context.get(ContextNames.ID);
		FacilioAgent agent = AgentApiV2.getAgent(id);
		
		setWorkflows(agent);

		if(agent.getAgentTypeEnum().isAgentService()) {
			Map<String, Object> params = CloudAgentUtil.fetchAgentDetails(agent);
			if (agent.getAgentTypeEnum() == AgentType.CLOUD_ON_SERVICE) {
				if (params.get("workflow") != null) {
					agent.setWorkflow(FieldUtil.getAsBeanFromMap((Map<String, Object>)params.get("workflow"), WorkflowContext.class));
				}
			}
			else {
				agent.setParams(params);
			}
		}

		context.put(ContextNames.AGENT, agent);
		
		return false;
	}

	private void setWorkflows(FacilioAgent agent) throws Exception {
		List<Long> workflowIds = new ArrayList<>();
		if (agent.getTransformWorkflowId() > 0) {
			workflowIds.add(agent.getTransformWorkflowId());
		}
		if(agent.getAgentTypeEnum() == AgentType.CLOUD){
			if (agent.getWorkflowId() > 0) {
				workflowIds.add(agent.getWorkflowId());
			}
		}
		if (!workflowIds.isEmpty()) {
			Map<Long, WorkflowContext> workflowsAsMap = WorkflowUtil.getWorkflowsAsMap(workflowIds);
			if (agent.getWorkflowId() > 0) {
				agent.setWorkflow(workflowsAsMap.get(agent.getWorkflowId()));
			}
			if (agent.getTransformWorkflowId() > 0) {
				agent.setTransformWorkflow(workflowsAsMap.get(agent.getTransformWorkflowId()));
			}
		}
	}

}
