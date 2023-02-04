package com.facilio.sandbox.command;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Locale;

@Log4j
public class CreateOrgForSandboxCommand extends FacilioCommand implements PostTransactionCommand {

    private Long sandboxOrgId = null;
    private Long sandboxUserId = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        Organization productionOrg = AccountUtil.getCurrentOrg();
        User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(productionOrg.getOrgId());
        //List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(productionOrg.getOrgId());

        JSONObject signupData = new JSONObject();
        signupData.put("name", sandboxConfig.getName());
        signupData.put("email", superAdminUser.getEmail());
        signupData.put("cognitoId", "facilio");
        signupData.put("domainname", sandboxConfig.getSubDomain());
        signupData.put("isFacilioAuth", true);
        signupData.put("companyname", productionOrg.getName() + " - " + sandboxConfig.getName());
        signupData.put("phone", productionOrg.getPhone());
        signupData.put("timezone", productionOrg.getTimezone());
        signupData.put("language", productionOrg.getLanguage());
        signupData.put("orgType", Organization.OrgType.SANDBOX);
        signupData.put("productionOrgId", productionOrg.getOrgId());

        context.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupData);
        Locale locale = DBConf.getInstance().getCurrentLocale();
        IAMAccount iamAccount = null;
        try {
            iamAccount = IAMOrgUtil.signUpOrg(signupData, locale);
            V3Util.throwRestException((iamAccount == null || iamAccount.getOrg().getOrgId() <= 0),ErrorCode.UNHANDLED_EXCEPTION, "Error while creating sandbox");

            context.put("sandboxAccount", iamAccount);
            sandboxOrgId = iamAccount.getOrg().getOrgId();
            sandboxUserId = iamAccount.getUser().getUid();
            sandboxConfig.setSandboxOrgId(sandboxOrgId);
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            if(iamAccount != null && iamAccount.getOrg() != null && iamAccount.getOrg().getOrgId() > 0) {
                IAMOrgUtil.rollBackSignedUpOrg(iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
            }
            throw e;
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        return false;
    }

    @Override
    public void onError() throws Exception {
        if(sandboxOrgId != null) {
            IAMOrgUtil.rollBackSignedUpOrg(sandboxOrgId, sandboxUserId);
        }
    }
}
