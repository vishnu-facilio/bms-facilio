package com.facilio.agentv2.iotmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddAndSendIotMessageCommand extends FacilioCommand implements PostTransactionCommand{

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(AddAndSendIotMessageCommand.class.getName());

    IotData data;
    
    @Override
    public boolean executeCommand(Context context) throws Exception {
        data = (IotData) context.get(AgentConstants.DATA);
        FacilioAgent agent = data.getAgent();
        if (agent.getCommandWorkflowId() > 0) {
        	List<IotMessage> processedMessages = new ArrayList<>();
        	for(IotMessage message: data.getMessages()) {
        		JSONObject processedData = executeCommandWorkflow(data.getAgent(), message.getMessageData(), data.getFacilioCommand());
        		if (MapUtils.isNotEmpty(processedData)) {
        			
        			if (agent.getAgentTypeEnum().isAgentService()) {
        				// Adding mandatory values
        				processedData.put("agent", agent.getName());
        				processedData.put("command", data.getFacilioCommand().asInt());
        			}
        			message.setMessageData(processedData);
        			processedMessages.add(message);
            	}
        	}
        	if (processedMessages.isEmpty()) {
        		data.setMessages(null);
        	}
        }
        
        if (data.getMessages() != null) {
        	IotMessageApiV2.addIotData(data);
			IotMessageApiV2.addIotMessage(data, data.getMessages());
        }
        
        return false;
    }
    
    private JSONObject executeCommandWorkflow(FacilioAgent agent, JSONObject object, com.facilio.agent.fw.constants.FacilioCommand command) throws Exception {
    	WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(agent.getCommandWorkflowId());

		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
		FacilioContext context = chain.getContext();

		List<Object> props = new ArrayList<>();
		props.add(object);
		props.add(FieldUtil.getAsProperties(agent));
		props.add(command.toString());

		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
		context.put(WorkflowV2Util.WORKFLOW_PARAMS, props);

		chain.execute();
		
		Map<String, Object> result = (Map<String, Object>) workflowContext.getReturnValue();
		if (result != null) {
			return FacilioUtil.parseJson(JSONObject.toJSONString(result));
		}
		return null;
    }

	@Override
	public boolean postExecute() throws Exception {
		if (data.getMessages() != null) {
			IotMessageApiV2.publishIotMessage(data);
		}
		return false;
	}
}
