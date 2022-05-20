package com.facilio.agentv2;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Map;

@Getter @Setter
public class AgentV3Action extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String fetchAgent() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getAgentDetailsCommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.ID, getId());
		chain.execute();
		setData(AgentConstants.AGENT, context.get(AgentConstants.AGENT));
		
		return SUCCESS;
	}
	public String getMessageSource() throws Exception{
		FacilioChain chain = ReadOnlyChainFactoryV3.getMessageSourcesCommand();
		FacilioContext context = chain.getContext();
		chain.execute();
		setData(AgentConstants.MESSAGE_SOURCES, context.get(AgentConstants.MESSAGE_SOURCES));

		return SUCCESS;
	}

	public String addMessageSource() throws Exception{
		FacilioChain chain = TransactionChainFactory.getAddMessageSourceChain();
		FacilioContext context = chain.getContext();
		context.put(AgentConstants.MESSAGE_SOURCE,this.getData().get(AgentConstants.MESSAGE_SOURCE));
		chain.execute();
		setData(AgentConstants.ID,context.get(AgentConstants.ID));
		return SUCCESS;
	}
	
	
}
