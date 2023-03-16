package com.facilio.mailtracking;

import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.services.email.EmailClient;

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
        public static final Object RECIPIENT_STATUS = "recipientStatus";
        public static final String SOURCE_TYPE = "sourceType";
        public static final String MAIL_STATUS = "mailStatus";
        public static final String RECORD_ID = "recordId";
        public static final String RECORDS_MODULE_ID = "recordsModuleId";
        public static final String RECORD_CREATED_TIME = "recordCreatedTime";
        public static final String MASK_URL = "maskUrl";
        public static final String HANDLE_DELEGATION = "handleDelegation";
        public static final String IS_ACTIVE = "isActive";
    }

    public static class Email {
        public static final String FROM = "from";
        public static final String SENDER = EmailClient.SENDER;
        public static final String TO =  EmailClient.TO;
        public static final String CC =  EmailClient.CC;
        public static final String BCC =  EmailClient.BCC;
        public static final String MAIL_TYPE =  EmailClient.MAIL_TYPE;
        public static final String CONTENT_TYPE = "contentType";
        public static final String HTML =  EmailClient.HTML;
        public static final String HTML_CONTENT = "htmlContent";
        public static final String TEXT_CONTENT = "textContent";
        public static final String CONTENT_TYPE_TEXT_HTML =  EmailClient.CONTENT_TYPE_TEXT_HTML;
        public static final String CONTENT_TYPE_TEXT_PLAIN =  EmailClient.CONTENT_TYPE_TEXT_PLAIN;
        public static final String MESSAGE = EmailClient.MESSAGE;
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
