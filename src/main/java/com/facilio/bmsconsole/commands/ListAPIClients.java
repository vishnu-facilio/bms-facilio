package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.APIClient;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class ListAPIClients extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<APIClient> apiClientList = IAMUserUtil.getApiClientList(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getUid());
        context.put(FacilioConstants.ContextNames.RESULT, apiClientList);
        return false;
    }
}
