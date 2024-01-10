package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class ConstructSandboxDomainNameForSummaryPage extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext application = (ApplicationContext) context.get(FacilioConstants.ContextNames.APPLICATION);

        if (application != null) {
            Organization currentOrg = AccountUtil.getCurrentOrg();
            if (currentOrg != null && currentOrg.getOrgType() == Organization.OrgType.SANDBOX.getIndex()) {
                AppDomain appDomain = application.getAppDomain();

                String domainStr = appDomain.getDomain() + "/" + currentOrg.getDomain();
                appDomain.setDomain(domainStr);

                application.setAppDomain(appDomain);
            }
            context.put(FacilioConstants.ContextNames.APPLICATION, application);
        }

        return false;
    }
}
