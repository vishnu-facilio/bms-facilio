package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SecurityPolicyAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j
public class MigrateSecurityPolicy {
    public static void migrate() throws Exception {
        long appId = ApplicationApi.getApplicationIdForLinkName("app");
        List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), appId, false, false, -1, -1);

        if (CollectionUtils.isEmpty(users)) {
            LOGGER.error("[migration] No users for org: " + AccountUtil.getCurrentOrg().getOrgId());
            return;
        }

        SecurityPolicy securityPolicy = SecurityPolicyAPI.fetchDefaultSecurityPolicy();
        if (securityPolicy == null) {
            LOGGER.error("[migration] No security policy for org: " + AccountUtil.getCurrentOrg().getOrgId());
            securityPolicy = new SecurityPolicy();
            securityPolicy.setName("Default Policy");
            securityPolicy.setIsDefault(true);
            securityPolicy.setIsMFAEnabled(false);
            securityPolicy.setIsPwdPolicyEnabled(true);
            securityPolicy.setIsWebSessManagementEnabled(false);
            securityPolicy.setPwdMinLength(8);
            securityPolicy.setPwdIsMixed(true);
            securityPolicy.setPwdMinSplChars(1);
            securityPolicy.setPwdMinNumDigits(1);
            securityPolicy.setPwdMinAge(null);
            securityPolicy.setPwdPrevPassRefusal(2);
            securityPolicy.setId(SecurityPolicyAPI.createSecurityPolicy(securityPolicy));
        } else {
            securityPolicy.setPwdPrevPassRefusal(2);
            securityPolicy.setPwdMinAge(-99);
            SecurityPolicyAPI.updateSecurityPolicy(securityPolicy);
        }

        Map<String, FacilioField> accountFields = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
        List<FacilioField> fieldsToBeUpdated = Collections.singletonList(accountFields.get("securityPolicyId"));

        for (User user: users) {
            long uid = user.getUid();
            IAMUser userToBeUpdated = new IAMUser();
            userToBeUpdated.setUid(uid);
            userToBeUpdated.setSecurityPolicyId(securityPolicy.getId());

            LOGGER.error("[migration] Migrated for orgId: " + AccountUtil.getCurrentOrg().getOrgId() + " userId: " + uid);
            FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserv2(userToBeUpdated, fieldsToBeUpdated));
        }
    }
}