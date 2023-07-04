package com.facilio.fsm.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.serviceTasks.taskStatusUpdate;
import com.facilio.fsm.commands.serviceTasks.updateServiceAppointment;
import com.facilio.fsm.commands.serviceTasks.updateServiceOrderStatus;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain createTaskChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new taskStatusUpdate());
        return c;
    }
    public static FacilioChain updateTaskChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new updateServiceAppointment());
        c.addCommand(new updateServiceOrderStatus());
        c.addCommand(new ServiceTaskDurationUpdateCommandV3());
        return c;
    }

}
