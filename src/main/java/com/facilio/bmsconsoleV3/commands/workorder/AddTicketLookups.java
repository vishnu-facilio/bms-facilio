package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddTicketLookups extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        
        if (isMainApp()) {
            // skipping the command for main app as it uses CommonList for rendering;
            // portal lists are yet to be moved; command to be removed as soon as it is moved.
            return false;
        }

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(wos)) {
            V3TicketAPI.loadWorkOrderLookups(wos);
        }
        return false;
    }

    private boolean isMainApp() {
        AppDomain domain = AccountUtil.getCurrentUser().getAppDomain();
        return domain != null &&
                domain.getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO;
    }
}
