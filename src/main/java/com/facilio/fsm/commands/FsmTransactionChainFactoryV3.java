package com.facilio.fsm.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.serviceTasks.updateServiceAppointment;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain updateTaskChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new updateServiceAppointment());
        return c;
    }

}
