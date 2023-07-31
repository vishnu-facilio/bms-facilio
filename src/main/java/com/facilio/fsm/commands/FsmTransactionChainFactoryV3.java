package com.facilio.fsm.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.LoadWorkorderToolLookupCommand;
import com.facilio.bmsconsole.commands.UpdateTransactionEventTypeCommand;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ItemTransactionRemainingQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.PurchasedItemsQuantityRollUpCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.AddOrUpdateWorkorderCostCommandV3;
import com.facilio.bmsconsoleV3.commands.workOrderInventory.UpdateWorkorderTotalCostCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.actuals.UpdateServiceInvReservationCommand;
import com.facilio.fsm.commands.plans.SetServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.plans.ValidateServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.serviceAppointment.*;
import com.facilio.fsm.commands.serviceOrders.*;
import com.facilio.fsm.commands.serviceTasks.*;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;

import static com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3.getUpdateItemQuantityRollupChain;
import static com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3.getUpdatetoolQuantityRollupChain;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getSOBeforeSaveCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetTaskStatusCommandV3());
        c.addCommand(new SOStatusChangeCommandV3());
        c.addCommand(new SOAddOnCommandV3());
        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getTaskBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new TaskStatusUpdate());
        c.addCommand(new SetPlansCommandV3());
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
        c.addCommand(new SetPlansCommandV3());
        c.addCommand(new ServiceTaskDurationUpdateCommandV3());
        return c;
    }

    public static FacilioChain afterSOCreateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain afterSOUpdateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getSOBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetTaskStatusCommandV3());
        c.addCommand(new VerifySOStatusUpdate());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new rollupServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new rollUpServiceTaskCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetDefaultAppointmentTypeCommand());
        c.addCommand(new SetServiceAppointmentStatusCommand());
        c.addCommand(new rollupServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new rollUpServiceTaskCommand());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getSoPlannedItemsBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateServiceOrderPlannedItemsCommand());
        c.addCommand(new SetServiceOrderPlannedItemsCommand());
        return c;
    }
    public static FacilioChain getServiceOrderItemsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateServiceInvReservationCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        c.addCommand(new AddActivitiesCommand());
        return c;
    }
    public static FacilioChain getServiceOrderToolsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new AddActivitiesCommand());
        return c;
    }

    public static FacilioChain getStatusBasedActions() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new StatusBasedActions());
        return c;
    }
}
