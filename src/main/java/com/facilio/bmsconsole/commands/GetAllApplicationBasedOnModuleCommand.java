package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;

public class GetAllApplicationBasedOnModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
		List<ApplicationContext> applications = null;
		
		if (moduleName != null) {
			applications = ApplicationApi.getApplicationsContainsModule(moduleName);
		}
		context.put(FacilioConstants.ContextNames.APPLICATION, applications);
		return false;
	}

	

}
