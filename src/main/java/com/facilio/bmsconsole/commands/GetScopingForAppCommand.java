package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetScopingForAppCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        if(appId == null || appId <= 0){
            throw new IllegalArgumentException("Application Id is invalid");
        }
        List<ScopingContext> scopingContextList = ApplicationApi.getScopingForApp(appId);
        context.put(FacilioConstants.ContextNames.SCOPING_CONTEXT_LIST, scopingContextList);
        return false;
    }
}
