package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;

public class V3ModuleAction extends V3Action {

	public String moduleList() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.geAllModulesChain();
		FacilioContext context = chain.getContext();
		chain.execute();

		setData("systemModules", context.get("systemModules"));
		setData("customModules", context.get("customModules"));

		return SUCCESS;
	}
}
