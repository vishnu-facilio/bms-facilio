package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class AddGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);

        if (group != null) {
            //	Connection conn = ((FacilioContext) context).getConnectionWithTransaction();

            long groupId = AccountUtil.getGroupBean().createGroup(group, Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP));
            group.setGroupId(groupId);

            context.put(FacilioConstants.ContextNames.GROUP_ID, groupId);
        }
        else {
            throw new IllegalArgumentException("Group Object cannot be null");
        }

        context.put("isOldApi", true);
        return false;
    }
}
