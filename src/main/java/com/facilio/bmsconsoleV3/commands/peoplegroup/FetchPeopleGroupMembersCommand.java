package com.facilio.bmsconsoleV3.commands.peoplegroup;

import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchPeopleGroupMembersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        List<V3PeopleGroupContext> records = Constants.getRecordListFromContext((FacilioContext) context,FacilioConstants.PeopleGroup.PEOPLE_GROUP);

        PeopleGroupUtils.setPeopleGroupMembers(records);

        return false;
    }
}
