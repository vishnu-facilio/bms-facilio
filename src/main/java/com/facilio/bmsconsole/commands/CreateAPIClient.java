package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.APIClient;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class CreateAPIClient extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        APIClient apiClient = (APIClient) context.get(FacilioConstants.ContextNames.MODULE);
        apiClient.setUid(AccountUtil.getCurrentUser().getUid());
        String token;
        if (apiClient.getAuthTypeEnum() == APIClient.APIClientType.APIKEY) {
            token = IAMUserUtil.createApiKey(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getUid(), apiClient.getName());
        } else {
            Map<String, String> res = IAMUserUtil.createOauth2Client(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getUid(), apiClient.getName());
            token = res.get("token");
            context.put(FacilioConstants.ContextNames.CLIENT, res.get("clientId"));
        }

        context.put(FacilioConstants.ContextNames.TOKEN, token);
        return false;
    }
}
