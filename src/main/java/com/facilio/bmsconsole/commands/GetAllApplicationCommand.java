package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.lang3.StringUtils;

public class GetAllApplicationCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Boolean fetchCurrentDomain = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_MY_APPS, false);
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
		String appDomain = (String)context.get(FacilioConstants.ContextNames.APP_DOMAIN);
		List<ApplicationContext> applications = null;
		if(fetchCurrentDomain != null && fetchCurrentDomain && StringUtils.isNotEmpty(appDomain)){
			applications = ApplicationApi.getApplicationsForOrgUser(AccountUtil.getCurrentUser().getOuid(), appDomain);
		}
		else if (moduleName != null) {
			// temp handing for modules based App
			applications = ApplicationApi.getApplicationsForModule(moduleName);
		}
		else {
			applications = ApplicationApi.getAllApplications();
		}
		context.put(FacilioConstants.ContextNames.APPLICATION, applications);
		return false;
	}

}
