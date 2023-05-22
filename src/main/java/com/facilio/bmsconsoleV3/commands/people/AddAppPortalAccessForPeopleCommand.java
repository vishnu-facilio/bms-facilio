package com.facilio.bmsconsoleV3.commands.people;

import com.amazonaws.util.CollectionUtils;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.identity.client.dto.User;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.identity.client.IdentityClient;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AddAppPortalAccessForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String appLinkName = (String) getQueryParamOrThrow(context,FacilioConstants.ContextNames.APP_LINKNAME);
        Long roleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.ROLE_ID);
        String email=(String) getQueryParamOrThrow(context,FacilioConstants.ContextNames.EMAIL);
        Long peopleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.PEOPLE_ID);
        Long securityPolicyId = (Long) context.getOrDefault(FacilioConstants.ContextNames.SECURITY_POLICY_ID,-99L);
        String password = (String) context.getOrDefault(FacilioConstants.ContextNames.PASSWORD,null);
        boolean sendInvitation=(boolean) context.getOrDefault(FacilioConstants.ContextNames.SEND_INVITE,true);
        boolean isSsoEnabled = V3PeopleAPI.isSsoEnabledForApplication(appLinkName);
        if(isSsoEnabled){
            sendInvitation = false;
        }
        V3PeopleContext existingPeople = (V3PeopleContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
        List<com.facilio.accounts.dto.User> users = PeopleAPI.getUsersForPeopleId(existingPeople.getId());
        if (CollectionUtils.isNullOrEmpty(users)) {
            if(StringUtils.isNotEmpty(email)) {
                V3PeopleAPI.validatePeopleEmail(email);
                existingPeople.setEmail(email);
                V3RecordAPI.updateRecord(existingPeople,Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE), Collections.singletonList(Constants.getModBean().getField(FacilioConstants.ContextNames.EMAIL,FacilioConstants.ContextNames.PEOPLE)));
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id is mandatory");
            }
        }

            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appLinkName);
            if (app != null) {
                long appId = app.getId();
                AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
                if (appDomain != null) {
                    com.facilio.identity.client.dto.AppDomain appDomainObj = IdentityClient.getDefaultInstance().getAppDomainBean().getAppDomain(appDomain.getDomain());
                    PeopleUserContext pplUser = new PeopleUserContext();
                    User userObj = new User();
                    userObj.setEmail(existingPeople.getEmail());
                    Organization org = AccountUtil.getCurrentOrg();
                    if(org != null) {
                        userObj.setOrgId(org.getOrgId());
                    }
                    userObj.setUsername(existingPeople.getEmail());
                    userObj.setPhone(existingPeople.getPhone());
                    userObj.setMobile(existingPeople.getMobile());
                    userObj.setName(existingPeople.getName());
                    userObj.setLanguage(existingPeople.getLanguage());
                    userObj.setTimezone(existingPeople.getTimezone());
                    if(securityPolicyId == null || securityPolicyId < 0){
                        securityPolicyId = -99L;
                    }
                    userObj.setSecurityPolicyId(securityPolicyId);
                    userObj.setAppDomain(appDomainObj);
                    userObj.setInvitedBy(AccountUtil.getCurrentUser().getUid());

                    pplUser.setApplicationId(appId);
                    pplUser.setPeopleId(existingPeople.getId());
                    pplUser.setRoleId(roleId);
                    pplUser.setUser(userObj);

                    FacilioContext userAdditionContext = new FacilioContext();
                    userAdditionContext.put(FacilioConstants.ContextNames.USER, pplUser);
                    userAdditionContext.put(FacilioConstants.ContextNames.SEND_INVITE, sendInvitation);
                    userAdditionContext.put(FacilioConstants.ContextNames.PASSWORD, password);
                    userAdditionContext.put(FacilioConstants.ContextNames.APP_LINKNAME,appLinkName);

                    if (app.getAppCategoryEnum() == ApplicationContext.AppCategory.PORTALS){
                        FacilioChain addOrUpdatePortalUserChain = FacilioChainFactory.addOrUpdatePortalUserChain();
                        addOrUpdatePortalUserChain.execute(userAdditionContext);
                    } else {
                        FacilioChain addUserChain = FacilioChainFactory.addUserChain();
                        addUserChain.execute(userAdditionContext);
                    }
                } else {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
                }
            }
        return false;
    }
    private Object getQueryParamOrThrow(Context context, String paramName) {
        Object paramValue = context.get(paramName);
        if (paramValue == null) {
            throw new IllegalArgumentException(paramName + " is mandatory");
        }
        return paramValue;
    }
}
