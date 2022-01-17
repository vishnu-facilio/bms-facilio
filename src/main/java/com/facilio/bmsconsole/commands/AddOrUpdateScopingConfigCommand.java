package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

public class AddOrUpdateScopingConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScopingContext scopingContext = (ScopingContext)context.get(FacilioConstants.ContextNames.SCOPING_CONTEXT);
        if(CollectionUtils.isEmpty(scopingContext.getScopingConfigList())){
            throw new IllegalArgumentException("Invalid Scoping Config");
        }
        if(scopingContext.getId() > 0) {
            ApplicationApi.deleteScopingConfig(scopingContext.getId());
        }
        else {
            ApplicationApi.addScoping(scopingContext);
        }
        for(ScopingConfigContext sc : scopingContext.getScopingConfigList()) {
            sc.setScopingId(scopingContext.getId());
        }
        ApplicationApi.addScopingConfigForApp(scopingContext.getScopingConfigList());


        return false;
    }
}
