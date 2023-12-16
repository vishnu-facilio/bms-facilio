package com.facilio.remotemonitoring;

public class RemoteMonitorConstants {
    public static final String ALARM_OPEN_FOR_DURATION_OF_TIME = "AlarmOpenForDurationOfTimeJob";
    public static final String CLEAR_ALARM_JOB = "ClearAlarmJob";
    public static final String ALARM_NOT_RECEIVED_JOB = "AlarmNotReceivedJob";
    public static final String FLAGGED_EVENT_SCHEDULED_JOB = "FlaggedEventScheduledJob";
    public static final String FILTER_RULE_CRITERIA = "filterRuleCriteria";
    public static final String FLAGGED_EVENT_BUREAU_TAKE_CUSTODY_JOB = "FlaggedEventBureauTakeCustodyJob";
    public static final String FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB = "FlaggedEventBureauTakeActionJob";
    public static final String FLAGGED_EVENT_BUREAU_ACTIONS = "flaggedEventBureauActions";
    public static final String RAW_ALARMS = "rawAlarms";
    public static final String FLAGGED_EVENT_RULE_ATTACHMENT_MOD_NAME = "flaggedAlarmProcessattachments";
    public static final String FLAGGED_EVENT_ATTACHMENT_MOD_NAME = "flaggedAlarmattachments";
    public static final String INHIBIT_REASON_ID = "inhibitReasonId";
    public static final String FLAGGED_EVENT_AUTO_CLOSURE_SCHEDULED_JOB = "FlaggedEventAutoClosureJob";
    public static final String CLOSE_VALUES = "FlaggedEventAutoClosureJob";
    public static final String ID = "id";
    public static final String SUBJECT = "subject";
    public static final String PRIORITY = "priority";
    public static final String TICKET_MODULE = "ticketModule";
    public static final String FLAGGED_ALARM_STATUSES = "flaggedAlarmStatuses";
    public static final String CLOSURE_RESTRICTION_OPTIONS = "closureRestrictionOptions";
    public static final String ALARM_FILTER_CRITERIA_TYPES = "alarmFilterCriteriaTypes";
    public static final String CONTROLLER_TYPE_MAP = "controllerTypeMap";

    public static class FlaggedEvent {
        public static final String FLAGGED_EVENT = "flaggedEvent";

    }
    public static class SystemAlarmTypes {
        public static final String UNDEFINED = "undefined";
        public static final String HEARBEAT = "heartbeat";
        public static final String CONTROLLER_OFFLINE = "controlleroffline";
        public static final String SITE_OFFLINE = "siteoffline";

    }
}