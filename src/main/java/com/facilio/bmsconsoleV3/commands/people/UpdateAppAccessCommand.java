package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class UpdateAppAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String appLinkName = (String) getQueryParamOrThrow(context,FacilioConstants.ContextNames.APP_LINKNAME);
        Long roleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.ROLE_ID);
        Long uid = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.USER_ID);
        Long peopleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.PEOPLE_ID);
        Long securityPolicyId = (Long) context.getOrDefault(FacilioConstants.ContextNames.SECURITY_POLICY_ID,-99L);
        V3PeopleContext existingPeople = (V3PeopleContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
        if (StringUtils.isEmpty(existingPeople.getEmail())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id is mandatory");
        }

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(appLinkName);
        if (app != null) {
            long appId = app.getId();
                PeopleUserContext pplUser = new PeopleUserContext();
                User userObj = ApplicationUserUtil.getUser(uid);
                if(securityPolicyId == null || securityPolicyId < 0){
                    securityPolicyId = -99L;
                }
                userObj.setSecurityPolicyId(securityPolicyId);
                pplUser.setApplicationId(appId);
                pplUser.setPeopleId(existingPeople.getId());
                pplUser.setRoleId(roleId);
                pplUser.setUser(userObj);
                pplUser.setUid(userObj.getUid());


                FacilioContext userUpdateContext = new FacilioContext();
            userUpdateContext.put(FacilioConstants.ContextNames.USER, pplUser);
            userUpdateContext.put(FacilioConstants.ContextNames.APP_LINKNAME,app.getLinkName());
            userUpdateContext.put(FacilioConstants.ContextNames.SEND_INVITE,false);

                if (app.getAppCategoryEnum() == ApplicationContext.AppCategory.PORTALS){
                    FacilioChain addOrUpdatePortalUserChain = FacilioChainFactory.addOrUpdatePortalUserChain();
                    addOrUpdatePortalUserChain.execute(userUpdateContext);
                } else {
                    FacilioChain updateUserChain = FacilioChainFactory.updateUserChain();
                    updateUserChain.execute(userUpdateContext);
                }
            }
        else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Application");
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
