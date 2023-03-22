package com.facilio.wmsv2.handler;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@TopicHandler(
        topic = Topics.System.auditLogs,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        group = Group.DEFAULT_SINGLE_WORKER,
        recordTimeout = 15
)
public class AuditLogHandler extends BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(AuditLogHandler.class.getName());

    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        AuditLogContext auditLog = new AuditLogContext();
        try {
            JSONObject content = message.getContent();
            auditLog = FieldUtil.getAsBeanFromJson(content, AuditLogContext.class);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            moduleCRUD.addAuditLog(auditLog);
        } catch (Exception e) {
            LOGGER.error("Error in inserting log", e );
            if (auditLog!=null) {
                LOGGER.info("Insertion failed Record Details : \n" + "Subject: " + auditLog.getSubject() + "\nRecord Id: " + auditLog.getRecordId()
                        + "\nRecord Type: " + auditLog.getRecordTypeEnum() + "\nType: " + auditLog.getTypeName() + "\nLink config: " + auditLog.getLinkConfig());
            }
        }
        return null;
    }

    public static class AuditLogContext extends V3Context {
        private String subject;
        public String getSubject() {
            return subject;
        }
        public AuditLogContext setSubject(String subject) {
            if (StringUtils.length(subject) > 255) {
                subject = subject.substring(0, 255);
            }
            this.subject = subject;
            return this;
        }

        private String description;
        public String getDescription() {
            return description;
        }
        public AuditLogContext setDescription(String description) {
            if (StringUtils.length(description) > 2000) {
                description = description.substring(0, 2000);
            }
            this.description = description;
            return this;
        }

//        private String descriptionJSON;
//        public String getDescriptionJSON() {
//            return descriptionJSON;
//        }
//        public void setDescriptionJSON(String descriptionJSON) {
//            this.descriptionJSON = descriptionJSON;
//        }

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

        private long recordId = -1;
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

        private ActionType actionType;
        public int getActionType() {
            if (actionType == null) {
                return -1;
            }
            return actionType.getIndex();
        }
        public ActionType getActionTypeEnum() {
            return actionType;
        }
        public AuditLogContext setActionType(int actionType) {
            this.actionType = ActionType.valueOf(actionType);
            return this;
        }
        public AuditLogContext setActionType(ActionType actionType) {
            this.actionType = actionType;
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

        private String linkConfig;
        public String getLinkConfig() {
            return linkConfig;
        }
        @JsonInclude
        public JSONArray getLinkConfigJSON() {
            if (StringUtils.isNotEmpty(linkConfig)) {
                try {
                    JSONParser parser = new JSONParser();
                    return (JSONArray) parser.parse(linkConfig);
                } catch (ParseException e) {}
            }
            return new JSONArray();
        }
        public AuditLogContext setLinkConfig(String linkConfig) {
            this.linkConfig = linkConfig;
            return this;
        }

        public AuditLogContext() {
        }

        public AuditLogContext(String subject, String description, RecordType recordType, String typeName, long recordId) {
            setSubject(subject);
            setDescription(description);
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
                LOGGER.error(ex);
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

    public enum ActionType implements FacilioEnum {
        ADD("Add"),
        UPDATE("Update"),
        DELETE("Delete"),
        MISCELLANEOUS("miscellaneous")
        ;

        private String name;

        ActionType(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public static ActionType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
