package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;


import java.util.List;

public class DeletePortalAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_PORTAL);
        List<Long> peopleIds = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE_IDS);
        if(isPortal){
            if(CollectionUtils.isNotEmpty(peopleIds)) {
                for (Long peopleId : peopleIds) {
                    V3PeopleContext peopleContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, peopleId)),V3PeopleContext.class);
                    ApplicationUserUtil.deletePortalAccess(peopleContext);
                }
            }
        }

        return false;
    }
}
