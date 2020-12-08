package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class WorkOrderPreAdditionHandlingCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if (AccountUtil.getCurrentUser() == null) {
            context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
        }
        if(CollectionUtils.isNotEmpty(wos)) {
            for(V3WorkOrderContext wo : wos) {
                if(wo.getSourceType() == null) {
                    wo.setSourceType(V3TicketContext.SourceType.WEB_ORDER.getIntVal());
                }
            }
        }

        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
        return false;
    }
}
