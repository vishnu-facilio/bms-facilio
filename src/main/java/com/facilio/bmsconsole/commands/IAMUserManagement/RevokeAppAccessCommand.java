package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class RevokeAppAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        List<Long> userIds = (List<Long>) context.get(FacilioConstants.ContextNames.USER_IDS);

        if(CollectionUtils.isNotEmpty(userIds)) {
            for(Long userId : userIds) {
                ApplicationUserUtil.revokeAppAccess(userId,appId);
            }
        }

        return false;
    }
}
