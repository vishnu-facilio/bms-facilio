package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteTenantContactsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        List<Long> peopleIdsTobeDeleted = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(recordIds)){
                for(Long recordId : recordIds){
                    List<TenantContactContext> tenantContacts = PeopleAPI.getTenantContacts(recordId, false);
                    if(CollectionUtils.isNotEmpty(tenantContacts)){
                        for(TenantContactContext tc : tenantContacts){
                            peopleIdsTobeDeleted.add(tc.getId());
                        }
                    }
                }


        }
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, peopleIdsTobeDeleted);
        context.put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);

        return false;
    }
}
