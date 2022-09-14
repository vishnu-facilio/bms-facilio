package com.facilio.mailtracking;

import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.bean.MailBean;

public class MailConstants {

    public static MailBean getMailBean(long orgId) throws IllegalAccessException, InstantiationException {
        return (MailBean) BeanFactory.lookup("MailBean", orgId);
    }

    public static class Params {
        public static final String ORGID = "orgId";
        public static final String MAIL_JSON = "mailJson";
        public static final String FILES = "files";
        public static final String MAPPER_ID = "mapperId";
        public static final String ID = "id";
        public static final String LOGGER_ID = "logger";
        public static final String MESSAGE_ID = "messageId";
        public static final String HEADER = "header";
        public static final String REGION = "region";
        public static final Object STATUS = "mailStatus";
        public static final String SOURCE_TYPE = "sourceType";
        public static final String RECORD_ID = "recordId";
        public static final String RECORDS_MODULE_ID = "recordsModuleId";
        public static final String RECORD_CREATED_TIME = "recordCreatedTime";
        public static final String MASK_URL = "maskUrl";
        public static final String ORIGINAL_TO = "originalTo";
        public static final String ORIGINAL_CC = "originalCc";
        public static final String ORIGINAL_BCC = "originalBcc";

    }

    public static class ContextNames {
        public static final String OUTGOING_MAIL_LOGGER = "outgoingMailContext";
    }

    public static class ModuleNames {
        public static final String OUTGOING_MAIL_LOGGER = "outgoingMailLogger";
        public static final String OUTGOING_RECIPIENT_LOGGER = "outgoingRecipientLogger";
        public static final String OUTGOING_MAIL_ATTACHMENTS = "outgoingMailAttachments";
    }
    
}
