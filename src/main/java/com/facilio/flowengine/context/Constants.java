package com.facilio.flowengine.context;

public class Constants {
    public static final String BLOCK_ID = "blockId";
    public static final String VARIABLE_NAME = "variableName";
    public static final String DATA_TYPE = "dataType";
    public static final String VARIABLE_VALUE = "variableValue";
    public static final String RECORD_ID = "recordId";
    public static final String BLOCK_TYPE = "blockType";
    public static final String CONDITION = "condition";
    public static final String RULE = "rule";
    public static final String MODULE_NAME = "moduleName";
    public static final String NEW_STATE = "newState";
    public static final String WORK_FLOW_ID = "workflowId";
    public static final String FUNCTION_ID = "functionId";
    public static final String CURRENT_RECORD = "currentRecord";
    public static final String PARAMETERS = "parameters";
    public static final String RECORD_DATA = "recordData";
    public static class NotificationBlockConstants{
        public static final String TO = "to";
        public static final String SUBJECT = "subject";
        public static final String MESSAGE = "message";
        public static final String IS_SEND_PUSH_NOTIFICATION = "isSendPushNotification";
        public static final String APPLICATION = "application";
        public static final String RECORD_MODULE_ID = "recordModuleId";
    }
    public static class EmailBlockConstants{
        public static final String FROM_MAIL_ID = "fromMailId";
        public static final String TO = "to";
        public static final String CC = "cc";
        public static final String BCC = "bcc";
        public static final String TEMPLATE_ID = "templateId";
        public static final String SEND_AS_SEPARATE_MAIL = "sendAsSeparateMail";
    }
    public static class ForLoopBlockConstants{
        public static final String ITERABLE_OBJECTS = "iterableObjects";
    }
}
