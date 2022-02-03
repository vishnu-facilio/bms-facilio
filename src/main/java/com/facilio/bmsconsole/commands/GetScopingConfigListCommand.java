package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Map;

public class GetScopingConfigListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        Long scopingId = (Long) context.get(FacilioConstants.ContextNames.SCOPING_ID);
        if(scopingId == null || scopingId <= 0){
            throw new IllegalArgumentException("Scoping Id is invalid");
        }
        Map<Long, ScopingConfigContext> scopingMap = ApplicationApi.getScopingMapForApp(scopingId);
        context.put(FacilioConstants.ContextNames.SCOPING_CONTEXT, scopingMap.values());
        return false;
    }
}
