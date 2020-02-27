package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

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
			List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabGroupsForAppId(application.getId());
			if (webTabGroups != null && !webTabGroups.isEmpty()) {
				for (WebTabGroupContext webTabGroup : webTabGroups) {
					List<WebTabContext> webTabs = ApplicationApi.getWebTabsForWebGroup(webTabGroup.getId());
					if (webTabs != null && !webTabs.isEmpty()) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						for (WebTabContext webtab : webTabs) {
							webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
							webtab.setModuleIds(ApplicationApi.getModuleIdsForTab(webtab.getId()));
							if (AccountUtil.getCurrentUser() != null) {
								webtab.setPermissionVal(ApplicationApi.getRolesPermissionValForTab(webtab.getId(),
										AccountUtil.getCurrentUser().getRoleId()));
							}
							if (CollectionUtils.isNotEmpty(webtab.getModuleIds())) {
								List<FacilioModule> modules = new ArrayList<>();
								for (Long moduleId : webtab.getModuleIds()) {
									modules.add(modBean.getModule(moduleId));
								}
								webtab.setModules(modules);
							}
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
