package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class BulkPortalAccessDeleteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_PORTAL);
        List<V3PeopleContext> peopleContextList = (List<V3PeopleContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if(isPortal){
            if (CollectionUtils.isNotEmpty(peopleContextList)) {
                for(V3PeopleContext people : peopleContextList) {
                    ApplicationUserUtil.deletePortalAccess(people);
                }
            }
        }
        return false;
    }
}
