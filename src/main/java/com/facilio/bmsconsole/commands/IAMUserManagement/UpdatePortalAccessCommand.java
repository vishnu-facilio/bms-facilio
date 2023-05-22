package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdatePortalAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isPortal = (Boolean) context.get(FacilioConstants.ContextNames.IS_PORTAL);
        List<Long> peopleIds = (List<Long>) context.get(FacilioConstants.ContextNames.PEOPLE_IDS);
        String appLinkName = (String) context.get(FacilioConstants.ContextNames.APP_LINKNAME);
        boolean isPortalAccess = (boolean) context.get(FacilioConstants.ContextNames.IS_PORTAL_ACCESS);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        if(appLinkName == null && (appId != null && appId > 0)){
            ApplicationContext app = ApplicationApi.getApplicationForId(appId);
            if (app != null) {
                appLinkName = app.getLinkName();
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Invalid Application");
            }
        }

        if(isPortal){
            if(CollectionUtils.isNotEmpty(peopleIds)) {
                for (Long peopleId : peopleIds) {
                    V3PeopleContext peopleContext = new V3PeopleContext();
                    peopleContext.setId(peopleId);
                    V3PeopleAPI.updatePortalAccess(peopleContext, appLinkName, isPortalAccess);
                }
            }
        }

        return false;
    }
}
