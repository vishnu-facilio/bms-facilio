package com.facilio.wmsv2.handler;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
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
            AuditLog auditLog = FieldUtil.getAsBeanFromJson(content, AuditLog.class);
            AuditLogUtil.insertAuditLog(auditLog);
        } catch (Exception e) {
            LOGGER.error("Error in inserting log", e);
        }
        return null;
    }

    public static class AuditLog {
        private Long id;

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        private String subject;
        public String getSubject() {
            return subject;
        }
        public AuditLog setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        private String description;
        public String getDescription() {
            return description;
        }
        public AuditLog setDescription(String description) {
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
        public AuditLog setRecordType(RecordType recordType) {
            this.recordType = recordType;
            return this;
        }

        private String typeName;
        public String getTypeName() {
            return typeName;
        }
        public AuditLog setTypeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        private long recordId;
        public long getRecordId() {
            return recordId;
        }
        public AuditLog setRecordId(long recordId) {
            this.recordId = recordId;
            return this;
        }

        private User performedBy;
        public User getPerformedBy() {
            return performedBy;
        }
        public AuditLog setPerformedBy(User performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        private Long time;
        public Long getTime() {
            return time;
        }
        public AuditLog setTime(Long time) {
            this.time = time;
            return this;
        }

        public AuditLog() {
        }

        public AuditLog(String subject, String description, RecordType recordType, String typeName, long recordId) {
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
                return FieldUtil.getAsJSON(this);
            } catch (Exception ex) {
               // log the exception
            }
            return null;
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
        public int getIndex() {
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
