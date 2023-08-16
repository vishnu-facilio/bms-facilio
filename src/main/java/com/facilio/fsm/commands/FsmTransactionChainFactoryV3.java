package com.facilio.fsm.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.SetLocalIdCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ItemTransactionRemainingQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.PurchasedItemsQuantityRollUpCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.actuals.UpdateServiceInvReservationCommand;
import com.facilio.fsm.commands.plans.ReserveServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.plans.SetServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.plans.ValidateServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.serviceAppointment.*;
import com.facilio.fsm.commands.serviceOrders.*;
import com.facilio.fsm.commands.serviceTasks.*;
import com.facilio.fsm.commands.timeSheet.CheckForExistingTimeSheetsCommand;
import com.facilio.fsm.commands.timeSheet.StopTimeSheetCommand;
import com.facilio.fsm.commands.trip.CheckForExistingTripsCommand;
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
        return c;
    }

    public static FacilioChain getTaskBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new TaskStatusUpdate());
        c.addCommand(new SetPlansCommandV3());
        c.addCommand(new SOSTAutoCreateBeforeCommand());
        return c;
    }
    public static FacilioChain getTaskAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new SOStatusChangeViaSTCommandV3());
        c.addCommand(new SOSTAutoCreateAfterCommand());
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
        c.addCommand(new UpdateServiceAppointmentOnTaskUpdate());

        return c;
    }
    public static FacilioChain getTaskBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ServiceTaskStatusCheck());
        c.addCommand(new SetPlansCommandV3());
        c.addCommand(new ServiceTaskDurationUpdateCommandV3());
        return c;
    }

    public static FacilioChain afterSOCreateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AutoCreateSA());

//        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain afterSOUpdateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new UpdateSAandTasks());
//        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getSOBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new VerifySOStatusUpdate());
        c.addCommand(new SetTaskStatusCommandV3());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RollUpServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RollUpServiceTaskCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ValidateSACommand());
        c.addCommand(new SetServiceAppointmentNameCommand());
        c.addCommand(new SetDefaultAppointmentTypeCommand());
        c.addCommand(new SetServiceAppointmentStatusCommand());
        c.addCommand(new RollUpServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RollUpServiceTaskCommand());
        c.addCommand(new ScheduleServiceOrderCommand());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getSoPlannedItemsBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateServiceOrderPlannedItemsCommand());
        c.addCommand(new SetServiceOrderPlannedItemsCommand());
        return c;
    }
    public static FacilioChain getSoPlannedItemsAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReserveServiceOrderPlannedItemsCommand());
        c.addCommand(new AddOrUpdateServiceOrderCostCommand());
        return c;
    }
    public static FacilioChain getServiceOrderItemsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateServiceInvReservationCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        c.addCommand(new AddOrUpdateServiceOrderCostCommand());
        return c;
    }
    public static FacilioChain getServiceOrderToolsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new AddOrUpdateServiceOrderCostCommand());
        return c;
    }

    public static FacilioChain getStatusBasedActions() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new StatusBasedActions());
        return c;
    }

    public static FacilioChain getTimeSheetBeforeCreateChain() {
        FacilioChain c= getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckForExistingTimeSheetsCommand());
        return c;
    }

    public static FacilioChain getTripBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckForExistingTripsCommand());
        return c;
    }

    public static FacilioChain startSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StartSACommand());
        return c;
    }

    public static FacilioChain completeSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CompleteSACommand());
        return c;
    }

    public static FacilioChain cancelSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CancelSACommand());
        return c;
    }

    public static FacilioChain startTripChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StartTripCommand());
        return c;
    }

    public static FacilioChain endTripChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new EndTripCommand());
        return c;
    }

    public static FacilioChain dispatchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new DispatchCommand());
        return c;
    }

    public static FacilioChain startTaskChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StartTaskCommand());
        return c;
    }

    public static FacilioChain pauseTaskChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PauseTaskCommand());
        return c;
    }

    public static FacilioChain resumeTaskChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ResumeTaskCommand());
        return c;
    }

    public static FacilioChain completeTaskChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CompleteTaskCommand());
        return c;
    }

    public static FacilioChain cancelTaskChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CancelTaskCommand());
        return c;
    }

    public static FacilioChain stopTimeSheetChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StopTimeSheetCommand());
        return c;
    }
}
