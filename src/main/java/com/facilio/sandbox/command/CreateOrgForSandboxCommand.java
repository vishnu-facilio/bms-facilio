package com.facilio.sandbox.command;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.identity.client.IdentityClient;
import com.facilio.modules.FieldFactory;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.sandbox.utils.SandboxUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class CreateOrgForSandboxCommand extends FacilioCommand implements PostTransactionCommand{
    private Long sandboxOrgId = null;
    private Long sandboxUserId = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sandboxId = (Long)context.get(SandboxConstants.SANDBOX_ID);

        SandboxConfigContext sandboxConfig = SandboxAPI.getSandboxById(sandboxId);

        Organization productionOrg = IAMOrgUtil.getOrg(sandboxConfig.getOrgId());

        User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(sandboxConfig.getOrgId());
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

        Account sbAccount = null;
        try {
            com.facilio.identity.client.dto.Organization sandboxOrg = new com.facilio.identity.client.dto.Organization();
            sandboxOrg.setName(productionOrg.getName() + " - " + sandboxConfig.getName());
            sandboxOrg.setDomain(sandboxConfig.getSubDomain());
            sandboxOrg.setPhone(productionOrg.getPhone());
            sandboxOrg.setTimezone(productionOrg.getTimezone());
            sandboxOrg.setLanguage(productionOrg.getLanguage());
            sandboxOrg.setOrgType(com.facilio.identity.client.dto.Organization.OrgType.SANDBOX);
            sandboxOrg.setProductionOrgId(productionOrg.getOrgId());

            long sandOrgId = IdentityClient.getDefaultInstance().getOrgBean().createOrg(sandboxOrg);

            V3Util.throwRestException((sandOrgId <= 0),ErrorCode.UNHANDLED_EXCEPTION, "Error while creating sandbox Org");


            if(SandboxAPI.getSandboxCount(null) <= 1) {
                AppDomain sandboxDomain = new AppDomain(
                        productionOrg.getDomain() + "." + FacilioProperties.getSandboxSubDomain(),
                        AppDomain.AppDomainType.FACILIO.getIndex(), AppDomain.GroupType.FACILIO.getIndex(), productionOrg.getOrgId(),
                        AppDomain.DomainType.DEFAULT.getIndex());
                sandboxDomain.setDomainCategory(AppDomain.DomainCategory.SANDBOX);
                List<AppDomain> appDomains = new ArrayList<>();
                appDomains.add(sandboxDomain);

                IAMAppUtil.addAppDomains(appDomains);
            }

            com.facilio.identity.client.dto.User sandboxAdmin = new com.facilio.identity.client.dto.User();

            sandboxAdmin.setOrgId(productionOrg.getOrgId());
            sandboxAdmin.setName(sandboxConfig.getName());
            sandboxAdmin.setEmail(superAdminUser.getEmail());
            sandboxAdmin.setUsername(superAdminUser.getEmail());
            sandboxAdmin.setPhone(superAdminUser.getPhone());
            sandboxAdmin.setIdentifier("1");
            sandboxAdmin.setIsDefault(true);

            com.facilio.identity.client.dto.User user = IdentityClient.getDefaultInstance().getUserBean().addSuperUser(sandOrgId, sandboxAdmin, null);
            V3Util.throwRestException((user == null || user.getOrgId() <= 0),ErrorCode.UNHANDLED_EXCEPTION, "Error while creating sandbox user");

            Organization sandboxOrgObj = IAMOrgUtil.getOrg(sandOrgId);
            IAMUser sandboxSuperAdmin = SandboxUtil.constructUserObj(user);

            sbAccount = new Account(sandboxOrgObj, new User(sandboxSuperAdmin));
            sandboxOrgId = sbAccount.getOrg().getOrgId();
            sandboxUserId = sbAccount.getUser().getUid();
            AccountUtil.setCurrentAccount(sbAccount);
            FacilioChain signupChain = TransactionChainFactory.getOrgSignupChain();
            FacilioContext signupContext = signupChain.getContext();
            signupContext.put("orgId", sandOrgId);
            signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupData);
            signupChain.execute();
            AccountUtil.setCurrentAccount(sandboxConfig.getOrgId());
            context.put(PackageConstants.TARGET_ORG_ID, sandOrgId);

            sandboxConfig.setSandboxOrgId(sandOrgId);

            SandboxAPI.updateSandboxConfig(sandboxConfig, FieldFactory.getFacilioSandboxFields());
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            if(sbAccount != null && sbAccount.getOrg() != null && sbAccount.getOrg().getOrgId() > 0) {
                IAMOrgUtil.rollBackSignedUpOrg(sbAccount.getOrg().getOrgId(), sbAccount.getUser().getUid());
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
