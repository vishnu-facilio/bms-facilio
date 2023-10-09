package com.facilio.fsm.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceOrderToolsAction  extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long plannedToolId;

    public String serviceOrderToolFromPlan() throws Exception {
        FacilioChain chain = FsmTransactionChainFactoryV3.getServiceOrderToolsFromPlan();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_TOOL_ID,plannedToolId);
        chain.execute();
        setData(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_TOOLS)));
        return V3Action.SUCCESS;
    }
}
