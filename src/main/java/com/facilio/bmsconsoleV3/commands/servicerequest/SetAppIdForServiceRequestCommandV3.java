package com.facilio.bmsconsoleV3.commands.servicerequest;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SetAppIdForServiceRequestCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3ServiceRequestContext> requests = Constants.getRecordList((FacilioContext) context);
        long currentAppId = AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1L;

        if(CollectionUtils.isNotEmpty(requests)) {
            for (V3ServiceRequestContext serviceRequestContext : requests) {
                serviceRequestContext.setAppId(currentAppId);
            }
        }
        return false;
    }

}
