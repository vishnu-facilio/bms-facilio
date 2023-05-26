package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeleteIAMUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        List<Long> userIds = (List<Long>) context.get(FacilioConstants.ContextNames.USER_IDS);
        if(CollectionUtils.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                ApplicationUserUtil.deleteUser(orgId, userId);
            }
        }
        return false;
    }
}
