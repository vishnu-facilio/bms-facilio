package com.facilio.fsm.exception;

import com.amazonaws.services.dynamodbv2.xspec.S;

public enum FSMErrorCode {
    // Service Order error codes
    UNKNOWN_ERROR(Severity.ERROR, "Unknown error occurred", "We're sorry, but an unknown error has occurred. Please try again later or contact support for further assistance."),
    SO_CANCEL_FAILED(Severity.ERROR, "Service Order cancel failed", "Service Order cannot be cancelled as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_COMPLETE_WARNING(Severity.WARNING, "Service Order complete failed", "Service Order can only be completed when it is in the 'In Progress' state."),
    SO_CLOSE_WARNING(Severity.WARNING, "Service Order close failed", "Service Order can only be closed when it is in the 'Completed' state."),
    SO_AUTOCREATE_SCHEDULEDSTART_TIME(Severity.WARNING, "Schedule Start Time is required", "Please enter the Schedule Start Time to proceed with the auto-creation of service appointment."),
    SO_AUTOCREATE_SCHEDULEDEND_TIME(Severity.WARNING, "Schedule End Time is required", "Please enter the Schedule End Time to proceed with the auto-creation of service appointment."),
    SO_AUTOCREATE_TASKS_AVAILABILIY(Severity.WARNING, "At least one task required", "Please add at least one task to proceed with the auto-creation of service appointment."),
    SO_VENDOR_INTERNAL_FIELD_AGENT(Severity.ERROR, "Invalid field agent", "Service Order cannot be assigned to an internal technician when the Vendor field is filled."),
    SO_SITE_MANDATORY(Severity.ERROR, "Site is required", "Please enter Site to proceed with Service Order creation."),
    SO_SITE_PREVENT_CHANGE(Severity.ERROR, "Site cannot be modified", "Site cannot be modified after creating Service Order."),
    SO_NAME_MANDATORY(Severity.ERROR, "Name is required", "Please enter Name to proceed with Service Order creation."),
    SO_NAME_PREVENT_CHANGE(Severity.ERROR, "Name cannot be modified", "Name cannot be modified after creating Service Order."),
    SO_PRIORITY_MANDATORY(Severity.ERROR, "Priority is required", "Please select Priority level to proceed with Service Order creation"),
    SO_TERRITORY_PREVENT_CHANGE(Severity.ERROR, "Territory cannot be modified", "Territory cannot be modified after creating Service Order."),
    SO_AUTOCREATE_PREVENT_CHANGE(Severity.ERROR, "AutoCreate SA cannot be modified", "AutoCreate SA cannot be modified after creating Service Order."),
    SO_CANCEL_EDIT_FAILED(Severity.ERROR, "Service Order cannot be edited", "Service order cannot be edited as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_ID_NOT_EMPTY(Severity.ERROR, "SO ID is required", "Service Order Id cannot be empty."),
    //Planned Inventory
    ALREADY_RESERVED(Severity.ERROR,"Non Reservable","Cannot update a Reserved Item"),
    QUANTITY_REQUIRED(Severity.ERROR,"Non Reservable","Quantity is required for reservation"),
    RESERVATION_TYPE_REQUIRED(Severity.ERROR,"Non Reservable","Reservation Type is required for reservation"),
    ITEM_NOT_PRESENT(Severity.ERROR,"Non Reservable","Item is not present in the mentioned Storeroom"),
    STOREROOM_REQUIRED(Severity.ERROR,"Non Reservable","Storeroom is required for reservation"),
    INSUFFICIENT_QUANTITY(Severity.ERROR,"Non Reservable","Available quantity in Store is less than the requested quantity"),
    STORE_DOES_NOT_SERVE_SITE(Severity.ERROR,"Non Reservable","Storeroom does not serve the selected Service Order's site"),
    TOOL_TYPE_REQUIRED(Severity.ERROR,"Actuals Validation","Tool type is required"),
    STORE_REQUIRED(Severity.ERROR,"Actuals Validation","Store room is required"),
    TOOL_NOT_PRESENT(Severity.ERROR,"Actuals Validation","Tool not present is Storeroom"),
    SERVICE_REQUIRED(Severity.ERROR,"Actuals Validation","Service is required"),

    // Service Appointment error codes
    SA_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The Service Appointment does not match the selected Field Agent due to the following reasons. Do you want to proceed?"),
    SA_TERRITORY_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not associated to the same territory as the service appointment"),
    SA_TIME_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not available for the scheduled service appointment time."),
    SA_SKILL_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","Skills of the selected Field Agent do not match with the required skill for this service appointment"),
    SA_TRIP_ALREADY_RUNNING(Severity.ERROR, "Trip conflict", "You cannot start a trip while another is already running."),
    SA_SCHEDULED_TIME_MISMATCH(Severity.ERROR, "Scheduled time range is invalid", "The service appointment should be scheduled between valid time range."),
    SA_DETAILS_REQUIED(Severity.ERROR, "Mandatory field missing", "Please provide all the required details."),
    SA_RECORD_LOCKED(Severity.ERROR, "Record Locked", "You cannot edit or delete since the record is locked."),
    SA_FIELD_UPDATE_PREVENT(Severity.ERROR,"Not Permitted","You are not permitted to edit these details."),
    INVALID_SA(Severity.ERROR, "Invalid Service Appointment", "Please provide valid service appointment id"),

    // Time Sheet error codes
    TIME_SHEET_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a timesheet"),
    TIME_SHEET_SA_MANDATORY(Severity.ERROR, "Service Appointment is required", "Please select Service Appointment to proceed with Time Sheet creation"),
    TIME_SHEET_TIME_MISMATCH(Severity.ERROR, "Time range is invalid", "Time Sheet should have valid time range."),
    TIMESHEET_ALREADY_RUNNING(Severity.ERROR, "Conflict in Time Sheet", "An ongoing time sheet exists. Please complete the existing time sheet before creating a new one"),


    // Trip error codes
    TRIP_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a trip"),
    TRIP_CANNOT_BE_STARTED(Severity.ERROR,"Trip cannot be started","You currently have an active trip in progress. To manage your ongoing trip, please navigate to the associated service appointment.{0}"),
    TRIP_TIME_MISMATCH(Severity.ERROR, "Time range is invalid", "Trip should have valid time range."),

    //Service Task error codes

    RECORD_LOCKED(Severity.ERROR, "Record Locked", "You cannot edit or delete since the record is locked."),
    UPDATE_PREVENT(Severity.ERROR, "Not Permitted", "You are not permitted to edit these details."),
    NOT_ENOUGH_TASK_IN_SA(Severity.ERROR, "Not Enough Task", "Not Enough Tasks in Service Appointment"),
    SO_ASSET_SPACE_DOESNT_MATCH_SPACE_FIELD(Severity.ERROR, "Space not matched","Asset Space doesn't match the selected Space");

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