package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.iam.accounts.util.IAMAppUtil;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class GetAllApplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Boolean fetchCurrentDomain = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_MY_APPS, false);
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
		String appDomain = (String)context.get(FacilioConstants.ContextNames.APP_DOMAIN);
		List<ApplicationContext> applications = null;
		if(fetchCurrentDomain != null && fetchCurrentDomain && StringUtils.isNotEmpty(appDomain)){
			applications = ApplicationApi.getApplicationsForOrgUser(AccountUtil.getCurrentUser().getOuid(), appDomain,fetchCurrentDomain);
		}
		else if (moduleName != null) {
			// temp handing for modules based App
			applications = ApplicationApi.getApplicationsForModule(moduleName);
		}
		else {
			applications = ApplicationApi.getAllApplications();
		}
		ApplicationApi.setApplicationDomain(applications);
		context.put(FacilioConstants.ContextNames.APPLICATION, applications);
		return false;
	}

}
