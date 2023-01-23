package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class GetAllApplicationBasedOnModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
		List<ApplicationContext> applications = null;
		
		if (moduleName != null) {
			applications = ApplicationApi.getApplicationsContainsModule(moduleName);
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MAINTENANCE_APP_SIGNUP)) {
			Long currentAppId = null;
			if(AccountUtil.getCurrentApp() != null) {
				currentAppId = AccountUtil.getCurrentApp().getId();
			}
			if(currentAppId != null && currentAppId > 0) {
				List<ApplicationContext> appsList = ApplicationApi.getRelatedApplications(currentAppId);
				if(CollectionUtils.isNotEmpty(appsList)) {
					List<Long> relatedAppIds = appsList.stream().map(ApplicationContext::getId).collect(Collectors.toList());
					if(CollectionUtils.isNotEmpty(relatedAppIds)) {
						relatedAppIds.add(currentAppId);
						applications = applications.stream().filter(app -> relatedAppIds.contains(app.getId())).collect(Collectors.toList());
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.APPLICATION, applications);
		return false;
	}

	

}
