package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.SecurityPolicyAPI;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.Arrays;
import java.util.Map;

public class SecurityPolicy extends SignUpData {
    @Override
    public void addData() throws Exception {
        com.facilio.iam.accounts.context.SecurityPolicy securityPolicy = new com.facilio.iam.accounts.context.SecurityPolicy();
        securityPolicy.setName("Default Policy");
        securityPolicy.setIsDefault(true);
        securityPolicy.setIsMFAEnabled(false);
        securityPolicy.setIsTOTPEnabled(false);
        securityPolicy.setIsMOTPEnabled(false);
        securityPolicy.setIsPwdPolicyEnabled(true);
        securityPolicy.setPwdMinLength(8);
        securityPolicy.setPwdIsMixed(true);
        securityPolicy.setPwdMinSplChars(1);
        securityPolicy.setPwdMinNumDigits(1);
        securityPolicy.setPwdMinAge(30);
        securityPolicy.setPwdPrevPassRefusal(3);
        securityPolicy.setIsWebSessManagementEnabled(true);
        securityPolicy.setWebSessLifeTime(15);

        long securityPolicyId = SecurityPolicyAPI.createSecurityPolicy(securityPolicy);

        User currentUser = AccountUtil.getCurrentUser();
        IAMUser facilioUser = IAMUserUtil.getFacilioUser(-1, currentUser.getUid());
        if (facilioUser.getSecurityPolicyId() < 0) {
            facilioUser.setSecurityPolicyId(securityPolicyId);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

            IAMUtil.getUserBean().updateUserv2(facilioUser, Arrays.asList(fieldMap.get("securityPolicyId")));
        }
    }
}