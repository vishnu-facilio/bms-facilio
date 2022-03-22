package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.commons.chain.Context;

public class DeleteClient extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get("id");
        IAMUserUtil.deleteApiClient(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getUid(), id);
        return false;
    }
}
