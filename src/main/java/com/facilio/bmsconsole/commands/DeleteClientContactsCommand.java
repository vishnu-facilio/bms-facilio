package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteClientContactsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        List<Long> peopleIdsTobeDeleted = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(recordIds)){

                for(Long recordId : recordIds){
                    List<ClientContactContext> clientContacts = PeopleAPI.getClientContacts(recordId, false);
                    if(CollectionUtils.isNotEmpty(clientContacts)){
                        for(ClientContactContext tc : clientContacts){
                            peopleIdsTobeDeleted.add(tc.getId());
                        }
                    }
                }


        }
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, peopleIdsTobeDeleted);
        return false;
    }
}
