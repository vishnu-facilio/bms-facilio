package com.facilio.agentv2;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

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
	
	
}
