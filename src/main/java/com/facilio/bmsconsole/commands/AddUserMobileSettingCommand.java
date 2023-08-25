package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.bean.UserBean;
import com.facilio.identity.client.dto.UserMobileSettings;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;

public class AddUserMobileSettingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		UserMobileSettings userMobileSettings = (UserMobileSettings) context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
		if (userMobileSettings != null) {
			if (StringUtils.isNotEmpty(userMobileSettings.getMobileInstanceId())) {
				userMobileSettings.setFromPortal(AccountUtil.getCurrentAccount().getUser().isPortalUser());
				String appLinkName = AccountUtil.getCurrentApp() == null ? FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP : AccountUtil.getCurrentApp().getLinkName();
				addUserMobileSettings(userMobileSettings, appLinkName);
			}
			else {
				throw new IllegalArgumentException("Mobile Instance ID cannot be null or empty");
			}
		}
		else  {
			throw new IllegalArgumentException("User Mobile Setting Object cannot be null");
		}
		return false;
	}

	public void addUserMobileSettings(UserMobileSettings userMobileSettings, String appLinkName) throws Exception {

		UserBean userBean = IdentityClient.getDefaultInstance().getUserBean();
		if (userMobileSettings.getUserId() == -1) {
			userMobileSettings.setUserId(AccountUtil.getCurrentAccount().getUser().getUid());
		}
		UserMobileSettings currentSetting = userBean.getUserMobileSettings(userMobileSettings.getUserId(), userMobileSettings.getMobileInstanceId());
		if (currentSetting == null) {
			userMobileSettings.setCreatedTime(System.currentTimeMillis());
			userMobileSettings.setAppLinkName(appLinkName);
			long userMobileSettingsId = userBean.addUserMobileSettings(userMobileSettings.getUserId(), userMobileSettings);
			userMobileSettings.setUserMobileSettingId(userMobileSettingsId);
		} else {
			userMobileSettings.setUserMobileSettingId(currentSetting.getUserMobileSettingId());
			userMobileSettings.setUpdatedTime(System.currentTimeMillis());
			userBean.updateUserMobileSettings(currentSetting.getUserMobileSettingId(), userMobileSettings);
		}
	}
}
