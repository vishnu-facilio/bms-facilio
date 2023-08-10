package com.facilio.fsm.exception;

public enum FSMErrorCode {
    // Service Order error codes
    UNKOWN_ERROR(Severity.ERROR, "We're sorry, but an unknown error has occurred. Please try again later or contact support for further assistance."),
    SO_CANCEL_FAILED(Severity.ERROR, "Service Order cannot be cancelled as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_COMPLETE_WARNING(Severity.WARNING, "Service Order can only be completed when it is in the 'In Progress' state."),
    SO_CLOSE_WARNING(Severity.WARNING, "Service Order can only be closed when it is in the 'Completed' state."),
    SO_AUTOCREATE_SCHEDULEDSTART_TIME(Severity.WARNING, "Please enter the Schedule Start Time to proceed with the auto-creation of service appointment."),
    SO_AUTOCREATE_SCHEDULEDEND_TIME(Severity.WARNING, "Please enter the Schedule End Time to proceed with the auto-creation of service appointment."),
    SO_AUTOCREATE_TASKS_AVAILABILIY(Severity.WARNING, "Please add at least one task to proceed with the auto-creation of service appointment."),
    SO_VENDOR_INTERNAL_FIELD_AGENT(Severity.ERROR, "Service Order cannot be assigned to an internal technician when the Vendor field is filled."),
    SO_SITE_MANDATORY(Severity.ERROR, "Please enter Site to proceed with Service Order creation."),
    SO_SITE_PREVENT_CHANGE(Severity.ERROR, "Site cannot be modified after creating Service Order."),
    SO_NAME_MANDATORY(Severity.ERROR, "Please enter Name to proceed with Service Order creation."),
    SO_NAME_PREVENT_CHANGE(Severity.ERROR, "Name cannot be modified after creating Service Order."),
    SO_PRIORITY_MANDATORY(Severity.ERROR, "Please select Priority level to proceed with Service Order creation"),
    SO_TERRITORY_PREVENT_CHANGE(Severity.ERROR, "Territory cannot be modified after creating Service Order."),
    SO_AUTOCREATE_PREVENT_CHANGE(Severity.ERROR, "AutoCreate SA cannot be modified after creating Service Order."),
    SO_CANCEL_EDIT_FAILED(Severity.ERROR, "Service order cannot be edited as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_ID_NOT_EMPTY(Severity.ERROR, "Service Order Id cannot be empty."),

    // Service Appointment error codes
    SA_TIMESHEET_ALREADY_RUNNING(Severity.ERROR, "You cannot start this service appointment while a timesheet is already running."),
    SA_TRIP_ALREADY_RUNNING(Severity.ERROR, "You cannot start a trip while another is already running."),


    // Time Sheet error codes
    TIME_SHEET_NOT_ENOUGH_DETAILS(Severity.ERROR, "Not enough details to create a timesheet"),

    // Trip error codes
    TRIP_NOT_ENOUGH_DETAILS(Severity.ERROR, "Not enough details to create a trip");

    private final Severity severity;
    private final String message;

    FSMErrorCode(Severity severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
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