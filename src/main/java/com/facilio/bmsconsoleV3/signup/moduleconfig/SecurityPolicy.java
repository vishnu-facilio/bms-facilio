package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.var;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SecurityPolicy extends SignUpData {
    @Override
    public void addData() throws Exception {
        User currentUser = AccountUtil.getCurrentUser();

        var prop = new HashMap<String, Object>();
        prop.put("name", "Default");
        prop.put("isDefault", true);
        prop.put("isTOTPEnabled", false);
        prop.put("isMOTPEnabled", false);
        prop.put("isPwdPolicyEnabled", true);
        prop.put("pwdMinLength", 8);
        prop.put("pwdIsMixed", true);
        prop.put("pwdMinSplChars", 1);
        prop.put("pwdMinNumDigits", 1);
        prop.put("pwdMinAge", 30);
        prop.put("pwdPrevPassRefusal", 3);
        prop.put("isWebSessManagementEnabled", true);
        prop.put("webSessLifeTime", 15);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
        long securityPolicyId = insertRecordBuilder.table("SecurityPolicies")
                .fields(IAMAccountConstants.getSecurityPolicyFields())
                .insert(prop);


        IAMUser facilioUser = IAMUserUtil.getFacilioUser(-1, currentUser.getUid());
        if (facilioUser.getSecurityPolicyId() < 0) {
            facilioUser.setSecurityPolicyId(securityPolicyId);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

            IAMUtil.getUserBean().updateUserv2(facilioUser, Arrays.asList(fieldMap.get("securityPolicyId")));
        }
    }
}