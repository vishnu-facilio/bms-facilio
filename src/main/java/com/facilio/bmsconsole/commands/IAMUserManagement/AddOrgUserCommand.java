package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.bean.SandboxBean;
import com.facilio.identity.client.bean.UserBean;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddOrgUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String appLinkName = (String) context.get(FacilioConstants.ContextNames.APP_LINKNAME);
        boolean isPortalUser = (boolean) context.get(FacilioConstants.ContextNames.IS_PORTAL_USER);
        boolean addAppAccess = (boolean) context.get(FacilioConstants.ContextNames.ADD_APP_ACCESS);
        boolean isDeletedUser = (boolean) context.get(FacilioConstants.ContextNames.IS_DELETED_USER);
        PeopleUserContext peopleUserContext = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);

        peopleUserContext.getUser().setDeletedTime(-1L);
        long orgId = peopleUserContext.getUser().getOrgId();
        ApplicationUserUtil.addSandboxUser(orgId, isPortalUser, addAppAccess, peopleUserContext);

        if (isDeletedUser) {
            SandboxBean sandboxBean = IdentityClient.getDefaultInstance().getSandboxBean();
            sandboxBean.deleteSandboxUser(orgId, peopleUserContext.getUid());
        }

        if (isPortalUser && StringUtils.isNotEmpty(appLinkName)) {
            V3PeopleContext peopleContext = new V3PeopleContext();
            peopleContext.setId(peopleUserContext.getPeopleId());

            V3PeopleAPI.updatePortalAccess(peopleContext, appLinkName, true);
        }

        return false;
    }
}
