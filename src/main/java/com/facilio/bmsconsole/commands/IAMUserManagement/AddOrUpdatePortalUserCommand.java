package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

public class AddOrUpdatePortalUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);
        boolean sendInvitation = (boolean) context.get(FacilioConstants.ContextNames.SEND_INVITE);
        String appLinkName = (String) context.get(FacilioConstants.ContextNames.APP_LINKNAME);
        String password=(String) context.getOrDefault(FacilioConstants.ContextNames.PASSWORD,null);

        com.facilio.identity.client.dto.AppDomain appDomainObj = ApplicationApi.getAppDomainForApp(user.getApplicationId());
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        if(user!=null) {
            PeopleContext existingPeople = PeopleAPI.getPeopleForId(user.getPeopleId());
            //check if null condition needed
            if(StringUtils.isEmpty(existingPeople.getEmail())){
                throw new IllegalArgumentException("EmailId cannot be null");
            }

            User existingUser = ApplicationUserUtil.getUser(existingPeople.getEmail(), appDomainObj.getIdentifier());
            if (existingUser == null) {

                User newUser = new User();
                newUser.setOrgId(user.getUser().getOrgId());
                newUser.setName(existingPeople.getName());
                newUser.setEmail(existingPeople.getEmail());
                newUser.setUsername(existingPeople.getEmail());
                newUser.setIdentifier(appDomainObj.getIdentifier());
                newUser.setPhone(existingPeople.getPhone());
                newUser.setLanguage(existingPeople.getLanguage());
                newUser.setInvitedBy(AccountUtil.getCurrentUser().getUid());
                newUser.setAppDomain(appDomainObj);
                newUser.setTimezone(existingPeople.getTimezone());
                newUser.setMobile(existingPeople.getMobile());
                newUser.setSecurityPolicyId(user.getUser().getSecurityPolicyId());
                user.setUser(newUser);
                ApplicationUserUtil.addAppUser(user.getUser().getOrgId(),true, user, sendInvitation, password);
            } else {
                existingUser.setSecurityPolicyId(user.getUser().getSecurityPolicyId());
                user.setUser(existingUser);
                user.setUid(existingUser.getUid());
                ApplicationUserUtil.updateAppUser(user);
            }
            V3PeopleContext peopleContext = new V3PeopleContext();
            peopleContext.setId(user.getPeopleId());
            V3PeopleAPI.updatePortalAccess(peopleContext, appLinkName, true);
        }
        return false;
    }
}
