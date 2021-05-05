package com.facilio.wmsv2.handler;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

@TopicHandler(
        topic = Topics.System.auditLogs,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        sendToAllWorkers = false
)
public class AuditLogHandler extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(AuditLogHandler.class.getName());

    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        try {
            LOGGER.error(message.toString());
            JSONObject content = message.getContent();
            AuditLogContext auditLog = FieldUtil.getAsBeanFromJson(content, AuditLogContext.class);
            AuditLogUtil.insertAuditLog(auditLog);
        } catch (Exception e) {
            LOGGER.error("Error in inserting log", e);
        }
        return null;
    }

    public static class AuditLogContext extends ModuleBaseWithCustomFields {
        private String subject;
        public String getSubject() {
            return subject;
        }
        public AuditLogContext setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        private String description;
        public String getDescription() {
            return description;
        }
        public AuditLogContext setDescription(String description) {
            this.description = description;
            return this;
        }

        private RecordType recordType;
        public int getRecordType() {
            if (recordType != null) {
                return recordType.getIndex();
            }
            return -1;
        }
        public void setRecordType(int recordType) {
            this.recordType = RecordType.valueOf(recordType);
        }
        public RecordType getRecordTypeEnum() {
            return recordType;
        }
        public AuditLogContext setRecordType(RecordType recordType) {
            this.recordType = recordType;
            return this;
        }

        private String typeName;
        public String getTypeName() {
            return typeName;
        }
        public AuditLogContext setTypeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        private long recordId;
        public long getRecordId() {
            return recordId;
        }
        public AuditLogContext setRecordId(long recordId) {
            this.recordId = recordId;
            return this;
        }

        private User performedBy;
        public User getPerformedBy() {
            return performedBy;
        }
        public AuditLogContext setPerformedBy(User performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        private Long time;
        public Long getTime() {
            return time;
        }
        public AuditLogContext setTime(Long time) {
            this.time = time;
            return this;
        }

        private SourceType sourceType;
        public int getSourceType() {
            if (sourceType == null) {
                return -1;
            }
            return sourceType.getIndex();
        }
        public SourceType getSourceTypeEnum() {
            return sourceType;
        }
        public void setSourceType(int sourceType) {
            this.sourceType = SourceType.valueOf(sourceType);
        }
        public void setSourceType(SourceType sourceType) {
            this.sourceType = sourceType;
        }

        private long appId = -1;
        public long getAppId() {
            return appId;
        }
        public void setAppId(long appId) {
            this.appId = appId;
        }

        public AuditLogContext() {
        }

        public AuditLogContext(String subject, String description, RecordType recordType, String typeName, long recordId) {
            this.subject = subject;
            this.description = description;
            this.recordType = recordType;
            this.typeName = typeName;
            this.recordId = recordId;
        }

        public JSONObject toJSON() {
            try {
                if (performedBy == null) {
                    performedBy = AccountUtil.getCurrentUser();
                }
                if (time == null) {
                    time = System.currentTimeMillis();
                }
                if (appId < 0 && AccountUtil.getCurrentAccount() != null) {
                    appId = AccountUtil.getCurrentApp().getId();
                }
                try {
                    if (!AccountUtil.getCurrentAccount().isFromMobile()) {
                        sourceType = SourceType.WEB;
                    }
                    if (AccountUtil.getCurrentAccount().isFromAndroid()) {
                        sourceType = SourceType.ANDROID;
                    }
                    if (AccountUtil.getCurrentAccount().isFromIos()) {
                        sourceType = SourceType.IOS;
                    }
                } catch (Exception ex) {}
                return FieldUtil.getAsJSON(this);
            } catch (Exception ex) {
               // log the exception
            }
            return null;
        }
    }

    public enum SourceType implements FacilioEnum {
        WEB("Web"),
        ANDROID("Android"),
        IOS("iOS"),
        API("API");

        private String name;

        SourceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static SourceType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }
    }

    public enum RecordType implements FacilioEnum {
        MODULE("Module"),
        SETTING("Setting");

        private String name;

        RecordType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static RecordType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
