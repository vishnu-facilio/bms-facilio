package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkPermitContext> workPermits = recordMap.get(moduleName);
        List<Long> wos = new ArrayList<Long>();
        if(CollectionUtils.isNotEmpty(workPermits)) {
            WorkOrderContext newWo = new WorkOrderContext();
            newWo.setWorkPermitIssued(true);

            for(V3WorkPermitContext wp: workPermits) {
                if(wp.getTicket() != null && wp.getTicket().getId() > 0 && wp.getModuleState() != null && wp.getModuleState().getStatus().trim().equals("Active")) {
                    V3WorkOrderContext workOrderContext = (V3WorkOrderContext) V3Util.getRecord("workorder",wp.getTicket().getId(),null);
                    if(workOrderContext!= null) {
                        wos.add(wp.getTicket().getId());
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(wos)) {
                FacilioChain c = TransactionChainFactory.getUpdateWorkOrderChain();
                c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, wos);
                c.getContext().put(FacilioConstants.ContextNames.WORK_ORDER, newWo);
                c.execute();
            }

        }
        return false;
    }
}
