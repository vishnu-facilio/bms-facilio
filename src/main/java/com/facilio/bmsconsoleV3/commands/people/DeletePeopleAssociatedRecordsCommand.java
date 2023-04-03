package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.commands.peoplegroup.PeopleGroupUtils;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeletePeopleAssociatedRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> peopleIds = Constants.getRecordIds(context);

        if (CollectionUtils.isNotEmpty(peopleIds)) {
            // mark as delete People associated Records

            PeopleGroupUtils.markAsDeletePeopleGroupMember(peopleIds);
            PeopleAPI.deletePeopleUsers(peopleIds);
        }
        return false;
    }
}
