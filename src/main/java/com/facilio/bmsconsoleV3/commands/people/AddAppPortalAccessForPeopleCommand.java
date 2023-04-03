package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class AddAppPortalAccessForPeopleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String appLinkName = (String) getQueryParamOrThrow(context,FacilioConstants.ContextNames.APP_LINKNAME);
        Long roleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.ROLE_ID);
        String email=(String) getQueryParamOrThrow(context,FacilioConstants.ContextNames.EMAIL);
        Long peopleId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.PEOPLE_ID);
        Long securityPolicyId = (Long) getQueryParamOrThrow(context,FacilioConstants.ContextNames.SECURITY_POLICY_ID);
        boolean isSsoEnabled = V3PeopleAPI.isSsoEnabledForApplication(appLinkName);
        V3PeopleContext existingPeople = (V3PeopleContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId, V3PeopleContext.class);
        if (StringUtils.isEmpty(existingPeople.getEmail())) {
            if(!StringUtils.isEmpty(email)) {
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
                    User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(), AccountUtil.getCurrentOrg().getId(), true);
                    if (user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(roleId);
                        user.setSecurityPolicyId(securityPolicyId);
                        if (isSsoEnabled) {
                            user.setUserVerified(true);
                            user.setInviteAcceptStatus(true);
                        }
                        V3PeopleAPI.enableUser(user);
                        ApplicationApi.addUserInApp(user, false, !isSsoEnabled);
                    } else {
                        if (app.getAppCategoryEnum() == ApplicationContext.AppCategory.PORTALS){
                            updatePeople(existingPeople, appLinkName);
                            V3PeopleAPI.addPortalAppUser(existingPeople, appLinkName, appDomain.getIdentifier(), isSsoEnabled, roleId, existingPeople.getPermissionSets());
                        } else {
                            if(!existingPeople.isUser()) {
                                existingPeople.setUser(true);
                                V3RecordAPI.updateRecord(existingPeople, Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE), Constants.getModBean().getAllFields(FacilioConstants.ContextNames.PEOPLE));
                            }
                            V3PeopleAPI.addAppUser(existingPeople, appLinkName, roleId, !isSsoEnabled);
                        }
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

    private void updatePeople(V3PeopleContext people,String appLinkName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        switch (appLinkName){
            case FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP:
                people.setIsOccupantPortalAccess(true);
                V3RecordAPI.updateRecord(people, modBean.getModule(FacilioConstants.ContextNames.PEOPLE), modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE));
                break;
            case FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP:
                people.setIsEmployeePortalAccess(true);
                V3RecordAPI.updateRecord(people, modBean.getModule(FacilioConstants.ContextNames.PEOPLE), modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE));
                break;
            case FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP:
                V3TenantContactContext tc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3TenantContactContext.class);
                tc.setIsTenantPortalAccess(true);
                V3RecordAPI.updateRecord(tc, modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT), modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT));
                break;
            case FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP:
                V3VendorContactContext vc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3VendorContactContext.class);
                vc.setIsVendorPortalAccess(true);
                V3RecordAPI.updateRecord(vc, modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT), modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
                break;
            case FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP:
                V3ClientContactContext cc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3ClientContactContext.class);
                cc.setIsClientPortalAccess(true);
                V3RecordAPI.updateRecord(cc, modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT), modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT));
                break;
        }
    }
}
