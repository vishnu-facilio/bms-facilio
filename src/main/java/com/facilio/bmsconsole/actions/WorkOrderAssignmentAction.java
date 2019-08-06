package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class WorkOrderAssignmentAction extends FacilioAction {

    public String getWorkOrderRecommendedUsers() throws Exception {
        FacilioContext context = new FacilioContext();

        Chain c = ReadOnlyChainFactory.getRecommendedUsers();
        c.execute(context);

        setResult(FacilioConstants.ContextNames.RECOMMENDED_USERS, context.get(FacilioConstants.ContextNames.RECOMMENDED_USERS));

        return SUCCESS;
    }

}
