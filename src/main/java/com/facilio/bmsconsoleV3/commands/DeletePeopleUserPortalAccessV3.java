package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class DeletePeopleUserPortalAccessV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> peopleIds = Constants.getRecordIds(context);
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            for (Long id : peopleIds) {
                V3PeopleAPI.deletePeopleUsers(id);
            }
        }
        return false;
    }
}
