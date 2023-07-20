package com.facilio.fsm.commands;

import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.serviceOrders.SOAddOnCommandV3;
import com.facilio.fsm.commands.serviceOrders.AutoCreateSA;
import com.facilio.fsm.commands.serviceOrders.SOStatusChangeCommandV3;
import com.facilio.fsm.commands.serviceOrders.SOStatusChangeViaSTCommandV3;
import com.facilio.fsm.commands.serviceOrders.VerifySOStatusUpdate;
import com.facilio.fsm.commands.serviceTasks.TaskStatusUpdate;
import com.facilio.fsm.commands.serviceTasks.UpdateServiceOrderStatus;
import com.facilio.fsm.commands.serviceTasks.UpdateServiceOrderTime;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getServiceOrderBeforeSaveCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SOStatusChangeCommandV3());
        c.addCommand(new SOAddOnCommandV3());
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getTaskBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new TaskStatusUpdate());
        return c;
    }
    public static FacilioChain getTaskAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new SOStatusChangeViaSTCommandV3());
        return c;
    }
    public static FacilioChain getTaskAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        //updating the service appointment status based on task status
//        c.addCommand(new UpdateServiceAppointment());
        //updating the service order duration/start/end time actuals
        c.addCommand(new UpdateServiceOrderTime());
        //updating the service order status based on service appointment status
        c.addCommand(new UpdateServiceOrderStatus());
        //Change the status of Service Order to in progress or scheduled based on tasks
        c.addCommand(new SOStatusChangeViaSTCommandV3());
        return c;
    }
    public static FacilioChain getTaskBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
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

    public static FacilioChain afterSOUpdaqteChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getSOBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new VerifySOStatusUpdate());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
        return c;
    }
}
