package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateContactDirectoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<PeopleContext> people = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);

        if(CollectionUtils.isNotEmpty(people)) {
            for(PeopleContext ppl : people) {
                //update Contact directory fields if people is associated
                List<ContactDirectoryContext> contactList = CommunityFeaturesAPI.getContacts(ppl.getId());
                if (CollectionUtils.isNotEmpty(contactList)) {
                    List<Long> ids = new ArrayList<>();
                    for (ContactDirectoryContext contact : contactList) {
                        ids.add(contact.getId());
                    }
                    CommunityFeaturesAPI.updateContactDirectoryList(ids, ppl);
                }
            }
        }
        return false;
    }
}
