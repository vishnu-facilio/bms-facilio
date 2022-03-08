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
            ScopingContext sc = ApplicationApi.getScoping(scopingContext.getId());
            if(sc != null && sc.isDefault()) {
                throw new IllegalArgumentException("Updation of default Scoping is not permitted");
            }
            else if (scopingContext.isDefault()) {
                throw new IllegalArgumentException("There can be only one default scoping for an application");
            }
            ApplicationApi.deleteScopingConfig(scopingContext.getId());
        }
        else {
            if (scopingContext.isDefault()) {
                throw new IllegalArgumentException("There can be only one default scoping for an application");
            }
            ApplicationApi.addScoping(scopingContext);
        }
        for(ScopingConfigContext sc : scopingContext.getScopingConfigList()) {
            sc.setScopingId(scopingContext.getId());
        }
        ApplicationApi.addScopingConfigForApp(scopingContext.getScopingConfigList());


        return false;
    }
}
