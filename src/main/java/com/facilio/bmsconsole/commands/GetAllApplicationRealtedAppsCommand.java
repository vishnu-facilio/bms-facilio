package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllApplicationRealtedAppsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId=(Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        FacilioUtil.throwIllegalArgumentException(appId<=0||appId==null,"Application id cannot be empty");
        List<ApplicationContext> relatedApplication = ApplicationApi.getRelatedApplications(appId);
        ApplicationApi.setApplicationDomain(relatedApplication);
        context.put(FacilioConstants.ContextNames.RELATED_APPLICATIONS, relatedApplication);
        return false;
    }
}
