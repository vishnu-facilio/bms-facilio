package com.facilio.fsm.exception;

import com.amazonaws.services.dynamodbv2.xspec.S;

public enum FSMErrorCode {
    // Work Order error codes
    UNKNOWN_ERROR(Severity.ERROR, "Unknown error occurred", "We apologize, but an unexpected error has occurred. Kindly attempt the action later or reach out to our support team for further assistance."),
    SO_CANCEL_FAILED(Severity.ERROR, "Work Order cancel failed", "Work Order cannot be cancelled as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_COMPLETE_WARNING(Severity.WARNING, "Work Order complete failed", "Work Order can only be completed when it is in the 'In Progress' state."),
    SO_CLOSE_WARNING(Severity.WARNING, "Work Order close failed", "Work Order can only be closed when it is in the 'Completed' state."),
    SO_AUTOCREATE_SCHEDULEDSTART_TIME(Severity.WARNING, "Schedule Start Time is required", "Please enter the Schedule Start Time to proceed with the auto-creation of Appointment."),
    SO_AUTOCREATE_SCHEDULEDEND_TIME(Severity.WARNING, "Schedule End Time is required", "Please enter the Schedule End Time to proceed with the auto-creation of Appointment."),
    SO_AUTOCREATE_TASKS_AVAILABILIY(Severity.WARNING, "At least one task required", "Please add at least one task to proceed with the auto-creation of Appointment."),
    SO_VENDOR_INTERNAL_FIELD_AGENT(Severity.ERROR, "Cannot create Work Order", "Work Order cannot be assigned to an internal technician when the Vendor field is filled."),
    SO_SITE_MANDATORY(Severity.ERROR, "Site is required", "Please enter Site to proceed with Work Order creation."),
    SO_SITE_PREVENT_CHANGE(Severity.ERROR, "Site cannot be modified", "Site cannot be modified after creating Work Order."),
    SO_NAME_MANDATORY(Severity.ERROR, "Name is required", "Please enter Name to proceed with Work Order creation."),
    SO_NAME_PREVENT_CHANGE(Severity.ERROR, "Name cannot be modified", "Name cannot be modified after creating Work Order."),
    SO_PRIORITY_MANDATORY(Severity.ERROR, "Priority is required", "Please select Priority level to proceed with Work Order creation"),
    SO_TERRITORY_PREVENT_CHANGE(Severity.ERROR, "Territory cannot be modified", "Territory cannot be modified after creating Work Order."),
    SO_AUTOCREATE_PREVENT_CHANGE(Severity.ERROR, "AutoCreated Appointment cannot be modified", "AutoCreated Appointment cannot be modified after creating Work Order."),
    SO_CANCEL_EDIT_FAILED(Severity.ERROR, "Work Order cannot be edited", "Work Order cannot be edited as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_ID_NOT_EMPTY(Severity.ERROR, "Work Order ID is required", "Work Order Id cannot be empty."),
    //Planned Inventory
    ALREADY_RESERVED(Severity.ERROR,"Non Reservable","Cannot update a Reserved Item"),
    QUANTITY_REQUIRED(Severity.ERROR,"Non Reservable","Quantity is required for reservation"),
    RESERVATION_TYPE_REQUIRED(Severity.ERROR,"Non Reservable","Reservation Type is required for reservation"),
    ITEM_NOT_PRESENT(Severity.ERROR,"Non Reservable","Item is not present in the mentioned Storeroom"),
    STOREROOM_REQUIRED(Severity.ERROR,"Non Reservable","Storeroom is required for reservation"),
    INSUFFICIENT_QUANTITY(Severity.ERROR,"Non Reservable","Available quantity in Store is less than the requested quantity"),
    STORE_DOES_NOT_SERVE_SITE(Severity.ERROR,"Non Reservable","Storeroom does not serve the selected Work Order's site"),
    TOOL_TYPE_REQUIRED(Severity.ERROR,"Actuals Validation","Tool type is required"),
    STORE_REQUIRED(Severity.ERROR,"Actuals Validation","Store room is required"),
    TOOL_NOT_PRESENT(Severity.ERROR,"Actuals Validation","Tool not present is Storeroom"),
    SERVICE_REQUIRED(Severity.ERROR,"Actuals Validation","Service is required"),

    // Appointment error codes
    SA_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The Appointment does not match the selected Field Agent due to the following reasons. Do you want to proceed?"),
    SA_TERRITORY_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not associated to the same territory as the Appointment"),
    SA_TIME_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not available for the scheduled Appointment time."),
    SA_SKILL_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","Skills of the selected Field Agent do not match with the required skill for this Appointment"),
    SA_TRIP_ALREADY_RUNNING(Severity.ERROR, "Trip cannot be started", "You currently have an active trip in progress. To manage your ongoing trip, please navigate to the associated Appointment"),
    SA_SCHEDULED_TIME_MISMATCH(Severity.ERROR, "Scheduled time range is invalid", "Scheduled start time must precede the scheduled end time."),
    SA_SCHEDULED_TIME_INVALID(Severity.ERROR, "Scheduled time range is invalid", "Please make sure that both the scheduled start and end times are set for a future moment."),
    SA_DETAILS_REQUIED(Severity.ERROR, "Mandatory field missing", "Please provide all the mandatory details."),
    SA_RECORD_LOCKED(Severity.ERROR, "Cannot Edit/Delete Appointment", "Cannot edit or delete the Appointment as it has already been completed or cancelled"),
    SA_FIELD_UPDATE_PREVENT(Severity.ERROR,"Not Permitted","You are not permitted to edit these details."),
    INVALID_SA(Severity.ERROR, "Invalid Appointment", "Please provide valid Appointment id"),
    SA_CANNOT_BE_STARTED(Severity.ERROR,"Appointment cannot be started","Please associate task to start work"),
    SA_CANNOT_DISPATCH(Severity.ERROR,"Appointment cannot be dispatched","Dispatching an appointment that is already in progress, completed, or canceled is not permitted."),

    // Time Sheet error codes
    TIME_SHEET_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a timesheet"),
    TIME_SHEET_SA_MANDATORY(Severity.ERROR, "Appointment is required", "Please select Appointment to proceed with Time Sheet creation"),
    TIME_SHEET_TIME_MISMATCH(Severity.ERROR, "Time range is invalid", "Time Sheet should have valid time range."),
    TIMESHEET_ALREADY_RUNNING(Severity.ERROR, "Work cannot be started", "You currently have an active work in progress. Please complete the ongoing work to start a new one."),
    TIMESHEET_RECORD_LOCKED(Severity.ERROR,"Cannot Edit/Delete Time Sheet","Cannot edit or delete an ongoing Time Sheet."),
    TIMESHEET_TASK_STATUS_ERROR(Severity.ERROR,"Cannot Create/Edit Time Sheet","Please provide valid end time for the completed task."),
    TIMESHEET_TASK_INVALID_STATE_TRANSITION(Severity.ERROR,"Cannot Stop Time Sheet","Task cannot be updated to the given state while closing timesheet"),
    STOP_TIMESHEET_WARNING(Severity.WARNING,"Complete Time Sheet","Completing the timesheet requires action on associated tasks. You can either Pause or Complete all the associated tasks to complete time sheet."),

    // Trip error codes
    TRIP_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a trip"),
    TRIP_CANNOT_BE_STARTED(Severity.ERROR,"Trip cannot be started","You currently have an active trip in progress. Please complete the ongoing trip to start a new one."),
    TRIP_TIME_MISMATCH(Severity.ERROR, "Time range is invalid", "Trip should have valid time range."),
    TRIP_RECORD_LOCKED(Severity.ERROR,"Cannot Edit/Delete Trip","Cannot edit or delete an ongoing trip"),
    TRIP_CANNOT_BE_CREATED(Severity.ERROR,"Trip cannot be created","Please make sure that trip created for a past or current moment."),
    //Service Task error codes
    TASK_RECORD_LOCKED(Severity.ERROR,"Cannot Edit/Delete Task","Cannot edit or delete task after starting work"),
    TASK_COMPLETE_APPOINTMENT(Severity.WARNING,"Complete Task and Appointment","Selected task is the final task of the appointment. You can also mark the Appointment as completed. Do you want to proceed?"),
    UPDATE_PREVENT(Severity.ERROR, "Not Permitted", "You are not permitted to edit these details."),
    NOT_ENOUGH_TASK_IN_SA(Severity.ERROR, "Not Enough Task", "Not Enough Tasks in Appointment"),
    SO_ASSET_SPACE_DOESNT_MATCH_SPACE_FIELD(Severity.ERROR, "Space not matched","Asset Space doesn't match the selected Space"),
    PHOTO_MANDATORY(Severity.ERROR, "Photo Mandatory", "Please upload photos to complete the task");

    private final Severity severity;
    private final String title;
    private final String message;

    FSMErrorCode(Severity severity, String title, String message) {
        this.severity = severity;
        this.title = title;
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public static enum Severity {
        INFO,
        WARNING,
        ERROR
    }
}