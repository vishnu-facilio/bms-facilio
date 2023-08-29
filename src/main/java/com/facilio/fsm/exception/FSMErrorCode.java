package com.facilio.fsm.exception;

public enum FSMErrorCode {
    // Service Order error codes
    UNKOWN_ERROR(Severity.ERROR, "Unknown error occurred", "We're sorry, but an unknown error has occurred. Please try again later or contact support for further assistance."),
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

    // Service Appointment error codes
    SA_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The Service Appointment does not match the selected Field Agent due to the following reasons. Do you want to proceed?"),
    SA_TERRITORY_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not associated to the same territory as the service appointment"),
    SA_TIME_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","The selected Field Agent is not available for the scheduled service appointment time."),
    SA_SKILL_MISMATCH(Severity.WARNING,"Dispatch Alert: Mismatched Assignment","Skills of the selected Field Agent do not match with the required skill for this service appointment"),
    SA_TIMESHEET_ALREADY_RUNNING(Severity.ERROR, "Timesheet conflict", "You cannot start this service appointment while a timesheet is in progress or exists for the given time range."),
    SA_TRIP_ALREADY_RUNNING(Severity.ERROR, "Trip conflict", "You cannot start a trip while another is already running."),
    SA_SCHEDULED_TIME_MISMATCH(Severity.ERROR, "Scheduled time range is invalid", "The service appointment should be scheduled between valid time range."),
    SA_DETAILS_REQUIED(Severity.ERROR, "Mandatory field missing", "Please provide all the required details."),
    SA_RECORD_LOCKED(Severity.ERROR, "Record Locked", "You cannot edit or delete since the record is locked."),
    SA_FIELD_UPDATE_PREVENT(Severity.ERROR,"Not Permitted","You are not permitted to edit these details."),

    // Time Sheet error codes
    TIME_SHEET_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a timesheet"),
    TIME_SHEET_SA_MANDATORY(Severity.ERROR, "Service Appointment is required", "Please select Service Appointment to proceed with Time Sheet creation"),
    TIME_SHEET_RECORD_LOCKED(Severity.ERROR, "Record Locked", "You cannot edit or delete since the record is locked."),
    TIME_SHEET_TIME_MISMATCH(Severity.ERROR, "Time range is invalid", "Time Sheet should have valid time range."),
    TIME_SHEET_UPDATE_PREVENT(Severity.ERROR, "Not Permitted", "You are not permitted to edit these details."),


    // Trip error codes
    TRIP_NOT_ENOUGH_DETAILS(Severity.ERROR, "Mandatory field missing", "Not enough details to create a trip"),
    TRIP_CANNOT_BE_STARTED(Severity.ERROR,"Trip cannot be started","You currently have an active trip in progress. To manage your ongoing trip, please navigate to the associated service appointment.{0}"),

    //Service Task error codes
    TIMESHEET_ALREADY_RUNNING(Severity.ERROR, "Timesheet conflict", "Cannot start or resume another service task when timesheet is running");

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