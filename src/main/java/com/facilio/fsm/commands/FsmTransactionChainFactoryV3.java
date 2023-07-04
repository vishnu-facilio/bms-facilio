package com.facilio.fsm.commands;

import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.serviceOrders.AutoCreateSA;
import com.facilio.fsm.commands.serviceTasks.TaskStatusUpdate;
import com.facilio.fsm.commands.serviceTasks.UpdateServiceAppointment;
import com.facilio.fsm.commands.serviceTasks.UpdateServiceOrderStatus;
import com.facilio.fsm.commands.serviceTasks.UpdateServiceOrderTime;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain createTaskChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new TaskStatusUpdate());
        return c;
    }
    public static FacilioChain updateTaskChain() {
        FacilioChain c = getDefaultChain();
        //updating the service appointment status based on task status
        c.addCommand(new UpdateServiceAppointment());
        //updating the service order duration/start/end time actuals
        c.addCommand(new UpdateServiceOrderTime());
        //updating the service order status based on service appointment status
        c.addCommand(new UpdateServiceOrderStatus());
        c.addCommand(new ServiceTaskDurationUpdateCommandV3());
        return c;
    }

    public static FacilioChain afterSOCreateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

}
