package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddPortalRequestsDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(wos) && AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() != AppDomain.AppDomainType.FACILIO){
            for(V3WorkOrderContext workorder : wos){
                workorder.setSourceType(V3TicketContext.SourceType.SERVICE_PORTAL_REQUEST.getIntVal());
                workorder.setSendForApproval(true);
                FacilioStatus preOpenStatus = TicketAPI.getStatus("preopen");
                workorder.setStatus(preOpenStatus);
                if (workorder.getRequester() == null && AccountUtil.getCurrentUser() != null) {
                    workorder.setRequester(AccountUtil.getCurrentUser());
                }
            }

        }
        return false;
    }
}
