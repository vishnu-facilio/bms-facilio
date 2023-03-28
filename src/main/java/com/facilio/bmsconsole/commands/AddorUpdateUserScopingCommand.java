package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class AddorUpdateUserScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
            if (user != null && user.getPeopleId() > -1) {
                userScopeBean.updatePeopleScoping(user.getPeopleId(), user.getScopingId());
            }
        } else {
            //commenting until ui is live
            if (user.getScopingId() != null && user.getScopingId() > 0) {
                //FacilioUtil.throwIllegalArgumentException(user.getScoping() == null, "Invalid scoping");
                Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APPLICATION_ID, -1l);
                if (appId == null || appId <= 0) {
                    appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                }
                ApplicationApi.updateScopingForUser(user.getScopingId(), appId, user.getOuid());
            }
        }
        return false;
    }
}
