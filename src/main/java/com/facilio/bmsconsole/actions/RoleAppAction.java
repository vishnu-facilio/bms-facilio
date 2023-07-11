package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RoleAppAction extends FacilioAction {

    @Getter
    @Setter
    private List<Long> appIds;
    public String getRolesApps() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appIds);
        FacilioChain chain = ReadOnlyChainFactory.fetchRolesAppsChain();
        chain.execute(context);
        setResult(FacilioConstants.ContextNames.ROLES_APPS,context.get(FacilioConstants.ContextNames.ROLES_APPS));
        return SUCCESS;
    }
}
