package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.commons.chain.Context;

public class CreateApiKeyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.RESULT, IAMUserUtil.createApiKey(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getUid()));
        return false;
    }
}
