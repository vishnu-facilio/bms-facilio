package com.facilio.fsm.exception;

public enum FSMErrorCode {
    // Service Order error codes
    UNKOWN_ERROR(Severity.ERROR, "We're sorry, but an unknown error has occurred. Please try again later or contact support for further assistance."),
    SO_CANCEL_FAILED(Severity.ERROR, "The service order cannot be cancelled as it is already in a 'Cancelled,' 'Completed,' or 'Closed' state."),
    SO_COMPLETE_WARNING(Severity.WARNING, "The service order can only be completed when it is in the 'In Progress' state."),
    SO_CLOSE_WARNING(Severity.WARNING, "The service order can only be closed when it is in the 'Completed' state."),

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