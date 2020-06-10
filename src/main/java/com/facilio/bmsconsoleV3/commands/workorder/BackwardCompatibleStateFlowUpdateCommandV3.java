package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BackwardCompatibleStateFlowUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)){
            V3WorkOrderContext workOrder = wos.get(0);
            List<V3WorkOrderContext> oldWos = (List<V3WorkOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField field = modBean.getField("moduleState", moduleName);
            if (workOrder != null && CollectionUtils.isNotEmpty(oldWos)) {

                // check whether moduleState is found
                if (field != null) {
                    FacilioModule module = modBean.getModule(moduleName);
                    FacilioStatus status = workOrder.getStatus();
                    if (status == null && workOrder.getApprovalStateEnum() != null) {
                        ApprovalState approveState = workOrder.getApprovalStateEnum();
                        if (approveState == ApprovalState.APPROVED) {
                            status = TicketAPI.getStatus(module, "Submitted");
                        } else if (approveState == ApprovalState.REJECTED) {
                            status = TicketAPI.getStatus(module, "Rejected");
                        }
                        workOrder.setStatus(status);
                    }
                    for (V3WorkOrderContext wo : oldWos) {
                        StateFlowRulesAPI.updateState(wo, module, status, false, context);
                    }
                }
            }
        }
        return false;
    }
}
