package com.facilio.fsm.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderItemsAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long reservationId;

    public String serviceOrderItemFromReservation() throws Exception {
        FacilioChain chain = FsmTransactionChainFactoryV3.getServiceOrderItemFromReservation();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION,reservationId);
        chain.execute();
        setData(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS)));
        return V3Action.SUCCESS;
    }

}
