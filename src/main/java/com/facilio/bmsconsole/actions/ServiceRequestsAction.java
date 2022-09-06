package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ServiceRequestsAction extends FacilioAction {

	
	private static final long serialVersionUID = 1L;

	public String getServcieRequestSubModules() throws Exception{

		FacilioChain chain = ReadOnlyChainFactory.getServiceRequestSubModulesChain();

		chain.execute();

		setResult(FacilioConstants.ContextNames.SUB_MODULES, chain.getContext().get(FacilioConstants.ContextNames.SUB_MODULES));

		return SUCCESS;

		

	}
}
