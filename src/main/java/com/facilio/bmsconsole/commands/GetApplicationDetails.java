package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;

public class GetApplicationDetails extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		ApplicationContext application = null;
		if (appId == -1) {
			application = ApplicationApi.getDefaultApplication();
		} else if (appId > 0) {
			application = ApplicationApi.getApplicationForId(appId);
		} else {
			throw new IllegalArgumentException("Invalid Application Id");
		}
		if (application != null) {
			List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabGroupsForAppId(appId);
			if (webTabGroups != null && !webTabGroups.isEmpty()) {
				for (WebTabGroupContext webTabGroup : webTabGroups) {
					List<WebTabContext> webTabs = ApplicationApi.getWebTabsForWebGroup(webTabGroup.getId());
					if (webTabs != null && !webTabs.isEmpty()) {
						for (WebTabContext webtab : webTabs) {
							List<NewPermission> permissions = ApplicationApi.getPermissionsForWebTab(webtab.getId());
							webtab.setPermissions(permissions);
						}
					}
					webTabGroup.setWebTabs(webTabs);
				}
			}
			application.setWebTabGroups(webTabGroups);
			context.put(FacilioConstants.ContextNames.APPLICATION, application);
		} else {
			throw new IllegalArgumentException("Application Not Found");
		}
		return false;
	}

}
