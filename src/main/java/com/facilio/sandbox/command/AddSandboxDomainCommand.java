package com.facilio.sandbox.command;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class AddSandboxDomainCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);
        Organization currentOrg = IAMOrgUtil.getOrg(AccountUtil.getCurrentOrg().getOrgId());
        AppDomain sandboxDomain = new AppDomain(
                currentOrg.getDomain() + "." + FacilioProperties.getSandboxSubDomain(),
                AppDomain.AppDomainType.FACILIO.getIndex(), AppDomain.GroupType.FACILIO.getIndex(), sandboxConfig.getSandboxOrgId(),
                AppDomain.DomainType.DEFAULT.getIndex());

        List<AppDomain> appDomains = new ArrayList<>();
        appDomains.add(sandboxDomain);

        IAMAppUtil.addAppDomains(appDomains);

        return false;
    }

}
