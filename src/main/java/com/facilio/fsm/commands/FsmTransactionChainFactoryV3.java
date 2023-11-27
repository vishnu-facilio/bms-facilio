package com.facilio.fsm.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.commands.SetLocalIdCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.ItemTransactionRemainingQuantityRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.inventoryrequest.PurchasedItemsQuantityRollUpCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.actuals.*;
import com.facilio.fsm.commands.people.PeopleSkillLevelBeforeSaveCommand;
import com.facilio.fsm.commands.plans.ReserveServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.plans.SAEstimatedItemCostAfterUpdateCommand;
import com.facilio.fsm.commands.plans.SetServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.plans.ValidateServiceOrderPlannedItemsCommand;
import com.facilio.fsm.commands.serviceAppointment.*;
import com.facilio.fsm.commands.serviceOrders.*;
import com.facilio.fsm.commands.servicePlannedMaintenance.*;
import com.facilio.fsm.commands.serviceTasks.*;
import com.facilio.fsm.commands.timeOff.GenerateTimeOffCodeCommand;
import com.facilio.fsm.commands.timeSheet.*;
import com.facilio.fsm.commands.trip.*;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;
import com.facilio.v3.commands.ConstructUpdateCustomActivityCommandV3;
import org.apache.commons.chain.Command;

import static com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3.getUpdateItemQuantityRollupChain;
import static com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3.getUpdatetoolQuantityRollupChain;

public class FsmTransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getSOBeforeSaveCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateServiceOrderRecordCreationCommandV3());
        c.addCommand(new SetTaskStatusCommandV3());
        c.addCommand(new SOStatusChangeCommandV3());
        c.addCommand(new SOAddOnCommandV3());
        return c;
    }

    public static FacilioChain getTaskBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new TaskStatusUpdate());
        //c.addCommand(new SetPlansCommandV3());
        c.addCommand(new SOSTAutoCreateBeforeCommand());
        return c;
    }
    public static FacilioChain getTaskAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        //update the task status before creation
        c.addCommand(new SOStatusChangeViaSTCommandV3());
        c.addCommand(new SOSTAutoCreateAfterCommand());
        c.addCommand(new AddServiceOrderActivityForAddServiceTaskAction_ServiceTask_Command());
        return c;
    }
    public static FacilioChain getTaskAfterPreCreateChain() {
        FacilioChain c = getDefaultChain();
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
        c.addCommand(new PhotoMandatoryValidationCommand());
        //c.addCommand(new SetPlansCommandV3());
        c.addCommand(new ServiceTaskDurationUpdateCommandV3());
        return c;
    }

    public static FacilioChain getTaskAfterDeleteChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddServiceOrderActivityForAddServiceTaskAction_ServiceTask_Command());
        return c;
    }

    public static FacilioChain afterSOCreateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        //c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        c.addCommand(new AutoCreateSA());
        //c.addCommand(new AddServiceOrderActivityForAddServiceTaskAction_ServiceOrder_Command());

//        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain afterSOUpdateChain() {
        FacilioChain c = getDefaultChain();
        //for handling the activity
        //c.addCommand(new UpdatePlansAndSkillsCommandV3());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        c.addCommand(new UpdateSAandTasks());
//        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY));
        //for auto creating the Service appointment
//        c.addCommand(new AutoCreateSA());
        return c;
    }

    public static FacilioChain getSOBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateServiceOrderRecordCreationCommandV3());
        c.addCommand(new VerifySOStatusUpdate());
        c.addCommand(new SetTaskStatusCommandV3());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
//        c.addCommand(new CheckRecordLockCommand());
        c.addCommand(new ValidateSAUpdateCommand());
        c.addCommand(new AssociateServiceTaskInSACommand());
        c.addCommand(new RollUpServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RollUpServiceTaskCommand());
//        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getServiceAppointmentBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new ValidateSACommand());
        c.addCommand(new SetServiceAppointmentNameCommand());
        c.addCommand(new SetDefaultAppointmentTypeCommand());
        c.addCommand(new SetServiceAppointmentStatusCommand());
        c.addCommand(new AssociateServiceTaskInSACommand());
        c.addCommand(new RollUpServiceAppointmentFieldsCommand());
        c.addCommand(new ValidateSAMismatch());
        c.addCommand(new GenerateSACodeCommand());
        return c;
    }

    public static FacilioChain getServiceAppointmentAfterCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RollUpServiceTaskCommand());
        c.addCommand(new ScheduleServiceOrderCommand());
        c.addCommand(new AddServiceOrderActivityForAddServiceAppointmentAction_ServiceAppointment_Command());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static Command getServiceAppointmentBeforeDeleteChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new CheckRecordLockCommand());
        chain.addCommand(new DelinkTaskOnSADeleteCommand());
        return chain;
    }

    public static Command getServiceAppointmentAfterDeleteChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddServiceOrderActivityForAddServiceAppointmentAction_ServiceAppointment_Command());
        return chain;
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
        c.addCommand(new SAEstimatedItemCostAfterUpdateCommand());
        return c;
    }
    public static FacilioChain getServiceOrderItemsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateServiceInvReservationCommand());
        c.addCommand(new PurchasedItemsQuantityRollUpCommandV3());
        c.addCommand(getUpdateItemQuantityRollupChain());
        c.addCommand(new ItemTransactionRemainingQuantityRollupCommandV3());
        c.addCommand(new AddOrUpdateServiceOrderCostCommand());
        c.addCommand(new SAItemCostAfterCreateCommand());
        return c;
    }
    public static FacilioChain getServiceOrderToolsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(getUpdatetoolQuantityRollupChain());
        c.addCommand(new AddOrUpdateServiceOrderCostCommand());
        c.addCommand(new SAToolCostAfterCreateCommand());
        return c;
    }

    public static FacilioChain getTimeSheetBeforeCreateChain() {
        FacilioChain c= getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckForExistingTimeSheetsCommand());
        c.addCommand(new ValidateTimeSheetCommand());
        c.addCommand(new RollUpTimeSheetFieldsCommand());
        c.addCommand(new GenerateTimeSheetCodeCommand());
        return c;
    }

    public static FacilioChain getTimeSheetAfterCreateChain() {
        FacilioChain c= getDefaultChain();
        c.addCommand(new UpdateSAandTasksOnTimeSheetCreateCommand());
        c.addCommand(new AddTimeSheetServiceAppointmentActivity());
        c.addCommand(new ConstructAddCustomActivityCommandV3());
        return c;
    }

    public static FacilioChain getTimeSheetAfterUpdateChain() {
        FacilioChain c= getDefaultChain();
        c.addCommand(new UpdateSAandTasksOnTimeSheetCreateCommand());
        c.addCommand(new ConstructUpdateCustomActivityCommandV3());
        return c;
    }
    public static FacilioChain getTimeSheetAfterTransactionChain(){
        FacilioChain c = getDefaultChain();
//         c.addCommand(new RollUpSACommand());
//        c.addCommand(new UpdateSAandTasksOnTimeSheetCreateCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.TimeSheet.TIME_SHEET));
        return c;
    }

    public static FacilioChain getTimeSheetBeforeUpdateChain() {
        FacilioChain c= getDefaultChain();
        c.addCommand(new CheckRecordLockForTimeSheetCommand());
        c.addCommand(new CheckForExistingTimeSheetsCommand());
        c.addCommand(new ValidateTimeSheetCommand());
        return c;
    }

    public static FacilioChain getTripBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new CheckForExistingTripsCommand());
        c.addCommand(new GenerateTripCodeCommand());
        c.addCommand(new ValidateAndRollUpTripCommand());
        c.addCommand(new RollUpTripFieldsCommand());
        return c;
    }

    public static FacilioChain getTripBeforeUpdateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckRecordLockForTripCommand());
        c.addCommand(new ValidateAndRollUpTripCommand());
        return c;
    }

    public static FacilioChain getTripBeforeDeleteChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CheckRecordLockForTripCommand());
        return c;
    }

    public static FacilioChain getTimeOffBeforeCreateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetLocalIdCommandV3());
        c.addCommand(new GenerateTimeOffCodeCommand());
        return c;
    }

    public static FacilioChain startSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StartSACommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain completeSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CompleteSACommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain cancelSAChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CancelSACommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain startTripChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new StartTripCommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain endTripForAppointmentChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new EndTripForAppointmentCommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain dispatchChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new DispatchCommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }

    public static FacilioChain rescheduleChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new RescheduleCommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
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
    public static FacilioChain endTripChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new EndTripCommand());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY));
        return c;
    }
    public static FacilioChain getServiceOrderItemFromReservation(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetServiceOrderItemFromReservation());
        return c;
    }
    public static FacilioChain getServiceOrderToolsFromPlan(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetServiceOrderToolFromPlan());
        return c;
    }
    public static FacilioChain getServiceOrderServiceFromPlan(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetServiceOrderServiceFromPlan());
        return c;
    }
    public static FacilioChain getPeopleSkillLevelBeforeSaveCommand(){
        FacilioChain c=getDefaultChain();
        c.addCommand(new PeopleSkillLevelBeforeSaveCommand());
        return c;
    }
    public static FacilioChain getServicePMBeforeUpdateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new PublishServicePMCommand());
        return c;
    }
    public static FacilioChain preCreateServiceOrder(){
        FacilioChain c = FacilioChain.getTransactionChain(1800000);// in ms
        c.addCommand(new GenerateServiceOrdersCommand());
        c.addCommand(new PreCreateServiceOrdersCommand());
        c.addCommand(new UpdateNextRunCommand());
        return c;
    }
    public static FacilioChain getServiceOrderStatusChangeChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchServiceOrdersToChangeStatusCommand());
        c.addCommand(new ScheduleServiceOrdersToChangeStatusCommand());
        return c;
    }
    public static FacilioChain servicePMNightlySchedulerChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ServicePMNightlySchedulerCommand());
        return c;
    }
    public static FacilioChain getServiceOrderPostCreateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new ServiceOrderPreviewToNewStatusUpdateCommand());
        c.addCommand(new UpdateServicePMExecutionTimesCommand());
        c.addCommand(new UpdateCounterInServicePMCommand());
        c.addCommand(new AutoCreateSA());
        c.addCommand(new AddActivitiesCommandV3(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY));
        return c;
    }
    public static FacilioChain getServiceOrderPreCreateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddServiceTaskCommand());
        c.addCommand(new AddPlansCommand());
        return c;
    }
    public static FacilioChain createServicePMFromTemplateChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateServicePMFromTemplateCommand());
        return c;
    }
}
