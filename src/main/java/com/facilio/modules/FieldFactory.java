package com.facilio.modules;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.integration.AgentIntegrationKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.modules.fields.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldFactory {

    private static final Map<String, Pair<String, Boolean>> lookupModuleVsSortFieldName = Collections
            .unmodifiableMap(initMap());

    private static Map<String, Pair<String, Boolean>> initMap() {
        Map<String, Pair<String, Boolean>> lookupModuleVsSortFieldName = new HashMap<>();
        lookupModuleVsSortFieldName.put("ticketpriority", Pair.of("sequenceNumber", false));
        lookupModuleVsSortFieldName.put("ticketcategory", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("ticketstatus", Pair.of("status", true));
        lookupModuleVsSortFieldName.put("tickettype", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("assetcategory", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("assetdepartment", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("assettype", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("alarmseverity", Pair.of("cardinality", false));
        lookupModuleVsSortFieldName.put("users", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("resource", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("space", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("itemTypesStatus", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("toolTypesStatus", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("itemStatus", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("toolStatus", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("inventoryCategory", Pair.of("displayName", true));
        lookupModuleVsSortFieldName.put("itemTypes", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("toolTypes", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("storeRoom", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("invite", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("visitor", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("host", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("servicerequestpriority", Pair.of("sequenceNumber", true));
        lookupModuleVsSortFieldName.put("readingalarmcategory", Pair.of("name", true));
        lookupModuleVsSortFieldName.put("siteId", Pair.of("name", true));
        return lookupModuleVsSortFieldName;
    }

    public static Pair<String, Boolean> getSortableFieldName(String moduleName) {
        return lookupModuleVsSortFieldName.get(moduleName);
    }

    public static List<FacilioField> getConnectionApiFields() {
        FacilioModule module = ModuleFactory.getConnectionApiModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getField("orgId","ORGID",module,FieldType.NUMBER));
        fields.add(getField("connectionId", "CONNECTION_ID", module, FieldType.NUMBER));
        fields.add(getNameField(module));
        fields.add(getField("url", "URL", module, FieldType.STRING));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        return fields;
    }
    public static List<FacilioField> getAgentMessageIntegrationFields() {
        FacilioModule module = ModuleFactory.getAgentMessageIntegrationModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        fields.add(getNameField(module));
        fields.add(getField("url", "URL", module, FieldType.STRING));
        fields.add(getField("topic", "TOPIC", module, FieldType.STRING));
        fields.add(getField("clientId", "CLIENT_ID", module, FieldType.STRING));
        fields.add(getField("queueType", "QUEUE_TYPE", module, FieldType.STRING));
        fields.add(getField("preProcessorType", "PREPROCESSOR_TYPE", module, FieldType.STRING));
        fields.add(getField("authType", "AUTH_TYPE", module, FieldType.STRING));
        fields.add(getField("username", "USERNAME", module, FieldType.STRING));
        fields.add(getField("password", "PASSWORD", module, FieldType.STRING));
        fields.add(getField("header", "HEADER", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getAgentVersionLogFields() {
        FacilioModule module = ModuleFactory.getAgentVersionLogModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.ORGID, "ORGID", module, FieldType.NUMBER));
        fields.add(getNewAgentIdField(module));
        fields.add(getField(AgentConstants.VERSION_ID, "VERSION_ID", module, FieldType.NUMBER));
        fields.add(getUpdatedTimeField(module));
        fields.add(getField(AgentConstants.AUTH_KEY, "AUTH_KEY", module, FieldType.STRING));
        fields.add(getCreatedTime(module));


        return fields;

    }

    public static FacilioField getUpdatedTimeField(FacilioModule module) {
        return getField(AgentConstants.UPDATED_TIME, "UPDATED_TIME", module, FieldType.NUMBER);
    }

    public static Collection<FacilioField> getSecretFileFields() {
        FacilioModule module = ModuleFactory.getSecretFileModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("fileId", "FILE_ID", module, FieldType.NUMBER));
        fields.add(getField("fileName", "FILE_NAME", module, FieldType.STRING));
        fields.add(getField("filePath", "FILE_PATH", module, FieldType.STRING));
        fields.add(getField("fileSize", "FILE_SIZE", module, FieldType.NUMBER));
        fields.add(getField("contentType", "CONTENT_TYPE", module, FieldType.STRING));
        fields.add(getField("uploadedTime", "UPLOADED_TIME", module, FieldType.NUMBER));
        fields.add(getField("isDeleted", "IS_DELETED", module, FieldType.BOOLEAN));


        return fields;
    }

    public static FacilioField getSecretFileIdField() {
        return getField("fileId", "FILE_ID", ModuleFactory.getSecretFileModule(), FieldType.NUMBER);
    }

    public static FacilioField getSecretFileNameField() {
        return getField("fileName", "FILE_NAME", ModuleFactory.getSecretFileModule(), FieldType.STRING);
    }


    public static List<FacilioField> getAgentStatsField() {
        FacilioModule agentStatsModule = ModuleFactory.getAgentStatsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getNewAgentIdField(agentStatsModule));
        fields.add(getSiteIdField(agentStatsModule));
        fields.add(getCreatedTime(agentStatsModule));

        fields.add(getField(AgentConstants.HEAP_INITIAL,"HEAP_INIT",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.HEAP_COMMITTED,"HEAP_COMMIT",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.HEAP_MAX,"HEAP_MAX",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.HEAP_FREE,"HEAP_FREE",agentStatsModule,FieldType.NUMBER));

        fields.add(getField(AgentConstants.NON_HEAP_INITIAL,"NON_HEAP_INIT",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.NON_HEAP_COMMITTED,"NON_HEAP_COMMIT",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.NON_HEAP_MAX,"NON_HEAP_MAX",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.NON_HEAP_FREE,"NON_HEAP_FREE",agentStatsModule,FieldType.NUMBER));

        fields.add(getField(AgentConstants.NUMNER_OF_PROCESSORS,"NO_OF_PROCESSORS",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.JAVA_VERSION,"JAVA_VERSION",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.USER_NAME,"USER_NAME",agentStatsModule,FieldType.NUMBER));

        fields.add(getField(AgentConstants.MAX_MEMORY,"MAX_MEMORY",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.FREE_MOMORY,"FREE_MEMORY",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.PROCESS_LOAD,"PROCESS_LOAD",agentStatsModule,FieldType.NUMBER));
        fields.add(getField(AgentConstants.SYSTEM_LOAD,"SYSTEM_LOAD",agentStatsModule,FieldType.NUMBER));

        fields.add(getField(AgentConstants.MISC_DATA,"MISC_DATA",agentStatsModule,FieldType.NUMBER));

        return fields;
    }


    public static class Fields {
        public static List<String> alarmsFieldsInclude = new ArrayList<String>();

        static {
            alarmsFieldsInclude.add("isAcknowledged");
            alarmsFieldsInclude.add("sourceType");
//			alarmsFieldsInclude.add("serialNumber");
            alarmsFieldsInclude.add("clearedBy");
            alarmsFieldsInclude.add("status");
//			alarmsFieldsInclude.add("type");
            alarmsFieldsInclude.add("alarmType");
//			alarmsFieldsInclude.add("category");
            alarmsFieldsInclude.add("resource");
            alarmsFieldsInclude.add("severity");
            alarmsFieldsInclude.add("alarmClass");
            alarmsFieldsInclude.add("alarmPriority");
            alarmsFieldsInclude.add("subject");
            alarmsFieldsInclude.add("previousSeverity");
            alarmsFieldsInclude.add("createdTime");
            alarmsFieldsInclude.add("modifiedTime");
            alarmsFieldsInclude.add("condition");
            alarmsFieldsInclude.add("autoClear");
//			alarmsFieldsInclude.add("noOfAttachments");
        }

        public static List<String> newAlarmsFieldsInclude = new ArrayList<String>();

        static {
            newAlarmsFieldsInclude.add("subject");
            newAlarmsFieldsInclude.add("acknowledged");
            newAlarmsFieldsInclude.add("acknowledgedBy");
            newAlarmsFieldsInclude.add("Severity");
            newAlarmsFieldsInclude.add("acknowledgedTime");
            newAlarmsFieldsInclude.add("lastClearedTime");
            newAlarmsFieldsInclude.add("lastCreatedTime");
            newAlarmsFieldsInclude.add("lastOccurredTime");
            newAlarmsFieldsInclude.add("resource");
            newAlarmsFieldsInclude.add("rule");
            newAlarmsFieldsInclude.add("readingAlarmCategory");
        }

        public static List<String> newOpAlarmsFieldsInclude = new ArrayList<String>();

        static {
            newOpAlarmsFieldsInclude.add("subject");
            newOpAlarmsFieldsInclude.add("Severity");
            newOpAlarmsFieldsInclude.add("lastClearedTime");
            newOpAlarmsFieldsInclude.add("lastCreatedTime");
            newOpAlarmsFieldsInclude.add("lastOccurredTime");
            newOpAlarmsFieldsInclude.add("resource");
        }

        public static List<String> workOrderFieldsInclude = new ArrayList<String>();

        static {
            workOrderFieldsInclude.add("actualWorkDuration");
            workOrderFieldsInclude.add("subject");
            workOrderFieldsInclude.add("actualWorkStart");
            workOrderFieldsInclude.add("actualWorkEnd");
            workOrderFieldsInclude.add("assignedBy");
            workOrderFieldsInclude.add("assignedTo");
            workOrderFieldsInclude.add("assignmentGroup");
            workOrderFieldsInclude.add("category");
            workOrderFieldsInclude.add("createdBy");
            workOrderFieldsInclude.add("createdTime");
            workOrderFieldsInclude.add("totalCost");
            workOrderFieldsInclude.add("dueDate");
            workOrderFieldsInclude.add("trigger");
            workOrderFieldsInclude.add("modifiedTime");
            workOrderFieldsInclude.add("noOfClosedTasks");
            workOrderFieldsInclude.add("priority");
            workOrderFieldsInclude.add("requester");
            workOrderFieldsInclude.add("requestedBy");
            workOrderFieldsInclude.add("resource");
            workOrderFieldsInclude.add("scheduledStart");
            workOrderFieldsInclude.add("sourceType");
            workOrderFieldsInclude.add("status");
            workOrderFieldsInclude.add("moduleState");
            workOrderFieldsInclude.add("type");
            workOrderFieldsInclude.add("vendor");
            workOrderFieldsInclude.add("sendForApproval");
            workOrderFieldsInclude.add("prerequisiteEnabled");
            workOrderFieldsInclude.add("preRequestStatus");
            workOrderFieldsInclude.add("responseDueDate");


        }

        public static List<String> assetFieldsInclude = new ArrayList<String>();

        static {
            assetFieldsInclude.add("category");
            assetFieldsInclude.add("department");
            assetFieldsInclude.add("description");
            assetFieldsInclude.add("localId");
            assetFieldsInclude.add("manufacturer");
            assetFieldsInclude.add("name");
            assetFieldsInclude.add("parentAssetId");
            assetFieldsInclude.add("photoId");
            assetFieldsInclude.add("purchasedDate");
            assetFieldsInclude.add("qrVal");
            assetFieldsInclude.add("resourceType");
            assetFieldsInclude.add("retireDate");
            assetFieldsInclude.add("serialNumber");
            assetFieldsInclude.add("state");
            assetFieldsInclude.add("supplier");
            assetFieldsInclude.add("tagNumber");
            assetFieldsInclude.add("type");
			assetFieldsInclude.add("space");
            assetFieldsInclude.add("unitPrice");
            assetFieldsInclude.add("warrantyExpiryDate");
            assetFieldsInclude.add("distanceMoved");
            assetFieldsInclude.add("connected");
        }

        public static List<String> approvalFormFields = new ArrayList<String>();

        static {
            approvalFormFields.add("assignmentGroup");
            approvalFormFields.add("category");
            approvalFormFields.add("priority");
            approvalFormFields.add("resource");
            approvalFormFields.add("comment");
            approvalFormFields.add("dueDate");
        }

        public static List<String> energyFieldsInclude = new ArrayList<String>();

        static {
            energyFieldsInclude.add("id");
        }

        public static List<String> entityFieldsInclucde = new ArrayList<String>();

        static {
            entityFieldsInclucde.add("condition");
            entityFieldsInclucde.add("source");
            entityFieldsInclucde.add("resourceId");
            entityFieldsInclucde.add("eventMessage");
            entityFieldsInclucde.add("severity");
            entityFieldsInclucde.add("createdTime");
            entityFieldsInclucde.add("priority");
            entityFieldsInclucde.add("alarmClass");
            entityFieldsInclucde.add("state");
        }

        public static Set<String> workorderRequestFieldInclude = new HashSet<String>();

        static {
            workorderRequestFieldInclude.add("subject");
            workorderRequestFieldInclude.add("description");
            workorderRequestFieldInclude.add("priority");
            workorderRequestFieldInclude.add("category");
            workorderRequestFieldInclude.add("resource");
            workorderRequestFieldInclude.add("dueDate");
            workorderRequestFieldInclude.add("requester");
            workorderRequestFieldInclude.add("urgency");
            workorderRequestFieldInclude.add("createdBy");
        }
    }

    public static List<FacilioField> getFormFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getNameField(module));
        fields.add(getDisplayNameField(module));
        fields.add(getModuleIdField(module));
        fields.add(getDescriptionField(module));

        FacilioField formType = new FacilioField();
        formType.setName("formType");
        formType.setDataType(FieldType.NUMBER);
        formType.setColumnName("FORM_TYPE");
        formType.setModule(module);
        fields.add(formType);

        FacilioField labelPosition = new FacilioField();
        labelPosition.setName("labelPosition");
        labelPosition.setDataType(FieldType.NUMBER);
        labelPosition.setColumnName("LABEL_POSITION");
        labelPosition.setModule(module);
        fields.add(labelPosition);

        fields.add(getField("showInMobile", "SHOW_IN_MOBILE", module, FieldType.BOOLEAN));
        fields.add(getField("showInWeb", "SHOW_IN_WEB", module, FieldType.BOOLEAN));
        fields.add(getField("hideInList", "HIDE_IN_LIST", module, FieldType.BOOLEAN));
        fields.add(getField("stateFlowId", "STATE_FLOW_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFormRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormRuleModule();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getSiteIdField(module));
        fields.add(getField("ruleType", "RULE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
        fields.add(getField("formId", "FORM_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FORM_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.LOOKUP));
        fields.add(getField("type", "FORM_RULE_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFormRuleActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormRuleActionModule();

        fields.add(getIdField(module));
        fields.add(getField("formRuleId", "FORM_RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("actionType", "ACTION_TYPE", module, FieldType.NUMBER));

        return fields;
    }
    
    public static List<FacilioField> getFormRuleActionFieldsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormRuleActionFieldModule();

        fields.add(getIdField(module));
        fields.add(getField("formRuleActionId", "FORM_RULE_ACTION_ID", module, FieldType.LOOKUP));
        fields.add(getField("formFieldId", "FORM_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("actionMeta", "ACTION_META_JSON", module, FieldType.STRING));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.LOOKUP));

        return fields;
    }

    private static FacilioField getDisplay_NameField(FacilioModule module) {
		/*FacilioField displayName = new FacilioField();
		displayName.setName("displayName");
		displayName.setDataType(FieldType.STRING);
		displayName.setColumnName("DISPLAY_NAME");
		displayName.setModule(module);*/
        return getField(AgentKeys.DISPLAY_NAME, "DISPLAY_NAME", module, FieldType.STRING);
    }

    public static List<FacilioField> getAgentLogFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentLogModule();
        fields.add(getAgentLogTimeField(module));
        fields.add(getAgentIdField(module));
        fields.add(getField(AgentKeys.DEVICE_ID, "DEVICE_ID", module, FieldType.STRING));
        fields.add(getField(AgentKeys.CONTENT, "CONTENT", module, FieldType.STRING));
        fields.add(getField(AgentKeys.MESSAGE_ID, "MSG_ID", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.COMMAND, "COMMAND", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.COMMAND_STATUS, "COMMAND_STATUS", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        return fields;
    }

    public static List<FacilioField> getAgentThreadDumpFields(){
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentThreadDumpModule();
        fields.add(getIdField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getCreatedTime(module));
        fields.add(getFileId(module));
        return fields;
    }


    public static FacilioField getFileId(FacilioModule module){
        return getField(AgentConstants.FILE_ID,"FILE_ID",FieldType.NUMBER);
    }
    public static FacilioField getAgentIdField(FacilioModule module) {
        return getField(AgentKeys.AGENT_ID, "AGENT_ID", FieldType.NUMBER);
    }

    public static FacilioField getNewAgentIdField(FacilioModule module) {
        return getField(AgentConstants.AGENT_ID, "AGENT_ID", FieldType.NUMBER);
    }

    public static FacilioField getAgentLogTimeField(FacilioModule module) {
        return getField(AgentKeys.TIMESTAMP, "TIME", FieldType.NUMBER);
    }

    public static List<FacilioField> getAgentMessageFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentMessageModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentKeys.RECORD_ID, "RECORD_ID", module, FieldType.STRING));
        fields.add(getAgentMessageStatusField(module));
        fields.add(getAgentMessageStartTimeField(module));
        fields.add(getField(AgentKeys.FINISH_TIME, "FINISH_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static FacilioField getAgentMessageStartTimeField(FacilioModule module) {
        return getField(AgentKeys.START_TIME, "START_TIME", module, FieldType.NUMBER);
    }

    public static FacilioField getAgentMessageStatusField(FacilioModule module) {
        return getField(AgentKeys.MSG_STATUS, "MSG_STATUS", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getAgentMetricsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentMetricsModule();
        fields.add(getIdField(module));
        fields.add(getAgentIdField(module));
        fields.add(getPublishTypeField(module));
        fields.add(getField(AgentKeys.SIZE, "SIZE", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.NO_OF_MESSAGES, "NO_OF_MSGS", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.LAST_UPDATED_TIME, "LAST_UPDATED_TIME", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        return fields;
    }

    public static FacilioField getPublishTypeField(FacilioModule module) {
        return getField(EventUtil.DATA_TYPE, "PUBLISH_TYPE", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getAgentDataFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentDataModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentKeys.DEVICE_DETAILS, "DEVICE_DETAILS", module, FieldType.STRING));
        fields.add(getField(AgentKeys.CONNECTION_STATUS, "Conn_status", module, FieldType.BOOLEAN));
        fields.add(getAgentNameField(module));
        fields.add(getField(AgentKeys.DISPLAY_NAME, "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField(AgentKeys.DATA_INTERVAL, "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.AGENT_TYPE, "TYPE", module, FieldType.STRING));
        fields.add(getField(AgentKeys.VERSION, "VERSION", module, FieldType.STRING));
        fields.add(getField(AgentKeys.LAST_MODIFIED_TIME, "LAST_MODIFIED_TIME", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        fields.add(getField(AgentKeys.LAST_DATA_RECEIVED_TIME, "LAST_DATA_RECEIVED_TIME", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.STATE, "STATE", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.SITE_ID, "SITE_ID", module, FieldType.NUMBER));
        fields.add(getField(AgentKeys.TRANSFORM_WORKFLOW_ID, "TRANSFORM_WORKFLOW_ID", module, FieldType.NUMBER));
        fields.add(getWritableField(module));
        fields.add(getDeletedTimeField(module));
        return fields;
    }

    public static List<FacilioField> getNewAgentFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNewAgentModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.DEVICE_DETAILS, "DEVICE_DETAILS", module, FieldType.STRING));
        fields.add(getField(AgentConstants.CONNECTED, "CONNECTED", module, FieldType.BOOLEAN));
        fields.add(getAgentNameField(module));
        fields.add(getField(AgentConstants.DISPLAY_NAME, "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DATA_INTERVAL, "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.AGENT_TYPE, "TYPE", module, FieldType.STRING));
        fields.add(getField(AgentConstants.VERSION, "VERSION", module, FieldType.STRING));
        fields.add(getField(AgentConstants.LAST_MODIFIED_TIME, "LAST_MODIFIED_TIME", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        fields.add(getField(AgentConstants.LAST_DATA_RECEIVED_TIME, "LAST_DATA_RECEIVED_TIME", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.SITE_ID, "SITE_ID", module, FieldType.NUMBER));
        fields.add(getWritableField(module));
        fields.add(getField(AgentConstants.PROCESSOR_VERSION, "PROCESSOR_VERSION", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.DELETED_TIME, "DELETED_TIME", FieldType.NUMBER));
        fields.add(getField(AgentKeys.TRANSFORM_WORKFLOW_ID, "TRANSFORM_WORKFLOW_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static FacilioField getSubscribedPointCountConditionField() {
        return getField(AgentConstants.SUBSCRIBED_COUNT, "SUM(IF(" + ModuleFactory.getPointModule().getTableName() + ".SUBSCRIBE_STATUS = 3, 1, 0))", FieldType.NUMBER);
    }

    public static FacilioField getSubscriptionInProgressPointCountConditionField() {
        return getField(AgentConstants.SUBSCRIPTION_COUNT, "SUM(IF(" + ModuleFactory.getPointModule().getTableName() + ".SUBSCRIBE_STATUS = 2, 1, 0))", FieldType.NUMBER);
    }

    public static FacilioField getConfiguredPointCountConditionField() {
        return getField(AgentConstants.CONFIGURED_COUNT, "SUM(IF(" + ModuleFactory.getPointModule().getTableName() + ".CONFIGURE_STATUS = 3, 1, 0))", FieldType.NUMBER);
    }

    public static FacilioField getConfigurationInProgressPointCountConditionField() {
        return getField(AgentConstants.CONFIGURATION_INPROGRESS_COUNT, "SUM(IF(" + ModuleFactory.getPointModule().getTableName() + ".CONFIGURE_STATUS = 2, 1, 0))", FieldType.NUMBER);
    }

    public static FacilioField getPointsCount() {
        return getField(AgentConstants.POINTS, "COUNT(" + ModuleFactory.getPointModule().getTableName() + ".ID) ", FieldType.NUMBER);
    }

    public static FacilioField getCountOfDistinctField(FacilioField facilioField, String key) {
        return getField(key, "COUNT(DISTINCT (" + facilioField.getModule().getTableName() + "." + facilioField.getColumnName() + "))", FieldType.NUMBER);
    }

    public static List<FacilioField> getAgentV2LogFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentV2LogModule();
        fields.add(getIdField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getField(AgentConstants.COMMAND, "COMMAND", module, FieldType.NUMBER));
        fields.add(getAgentV2MsgIdField(module));
        fields.add(getField(AgentConstants.STATUS, "STATUS", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.ACTUAL_TIME, "ACTUAL_TIME", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        return fields;
    }

    public static FacilioField getAgentV2MsgIdField(FacilioModule module) {
        return getField(AgentConstants.MESSAGE_ID, "MSGID", module, FieldType.NUMBER);
    }

    /**
     * ID
     * ORGID
     * AGENT_ID
     * CONTROLLER_ID
     * COMMAND
     * CREATED_TIME
     *
     * @return
     */
    public static List<FacilioField> getIotDataFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getIotDataModule();
        fields.add(getIdField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getNewControllerIdField(module));
        fields.add(getField(AgentConstants.COMMAND, "COMMAND", module, FieldType.NUMBER));
        fields.add(getCreatedTime(module));
        return fields;
    }

    /**
     * ID
     * ORGID
     * PARENT_ID
     * STATUS
     * SENT_TIME
     * ACKNOWLEDGE_TIME
     * COMPLETED_TIME
     * MSG_DATA
     *
     * @return
     */
    public static List<FacilioField> getIotMessageFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getIotMessageModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.PARENT_ID, "PARENT_ID", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.COMMAND, "COMMAND", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.SENT_TIME, "SENT_TIME", module, FieldType.DATE_TIME));
        fields.add(getField(AgentConstants.ACK_TIME, "ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField(AgentConstants.STATUS, "STATUS", FieldType.NUMBER));
        fields.add(getField(AgentConstants.COMPLETED_TIME, "COMPLETED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField(AgentConstants.MSG_DATA, "MSG_DATA", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getControllersField() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNewControllerModule();
        fields.add(getIdField(module));
        fields.add(getNameField(module));
        fields.add(getSiteIdField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getField(AgentConstants.DATA_INTERVAL, "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getWritableField(module));
        fields.add(getField(AgentConstants.ACTIVE, "ACTIVE", module, FieldType.NUMBER));
        fields.add(getControllerTypeField(module));
        fields.add(getField(AgentConstants.AVAILABLE_POINTS, "AVAILABLE_POINTS", FieldType.NUMBER));
        fields.add(getField(AgentConstants.CONTROLLER_PROPS, "CONTROLLER_PROPS", module, FieldType.STRING));
        fields.add(getCreatedTime(module));
        fields.add(getLastModifiedTimeField(module));
        return fields;
    }


    private static FacilioField getNewControllerIdField(FacilioModule module) {
        return getField(AgentConstants.CONTROLLER_ID, "CONTROLLER_ID", module, FieldType.NUMBER);
    }

    public static FacilioField getAgentNameField(FacilioModule module) {
        return getField(AgentKeys.NAME, "NAME", module, FieldType.STRING);
    }

    public static List<FacilioField> getAgentVersionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentVersionModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.VERSION, "VERSION", FieldType.STRING));
        fields.add(getField(AgentConstants.DESCRIPTION, "DESCRIPTION", FieldType.STRING));
        fields.add(getCreatedTime(module));
        fields.add(getField(AgentConstants.CREATED_BY, "CREATED_BY", FieldType.STRING));
        fields.add(getField(AgentKeys.URL, "URL", FieldType.STRING));
        return fields;
    }

    public static FacilioField getAuthKeyField(FacilioModule agentVersionModule) {
        return getField(AgentKeys.AUTH_KEY, "AUTH_KEY", FieldType.STRING);
    }

    public static FacilioField getAgentUpdateUrlField(FacilioModule agentVersionModule) {
        return getField(AgentKeys.URL, "URL", FieldType.STRING);
    }

    public static FacilioField getVersionIdField(FacilioModule agentVersionModule) {
        return getField(AgentKeys.URL, "VERSION", FieldType.STRING);
    }

    public static FacilioField getWritableField(FacilioModule module) {
        return getField(AgentKeys.WRITABLE, "WRITABLE", module, FieldType.BOOLEAN);
    }

    public static FacilioField getDeletedTimeField(FacilioModule module) {
        return getField(AgentKeys.DELETED_TIME, "DELETED_TIME", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getAgentControllerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentControllerModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentKeys.AGENT_ID, "AGENT_ID", module, FieldType.STRING));
        fields.add(getField(AgentConstants.NAME, "NAME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DATA_INTERVAL, "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.WRITABLE, "WRITABLE", module, FieldType.BOOLEAN));
        fields.add(getField(AgentConstants.ACTIVE, "ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField(AgentConstants.CONTROLLER_TYPE, "CONTROLLER_TYPE", module, FieldType.STRING));
        fields.add(getField(AgentConstants.CONTROLLER_PROPS, "CONTROLLER_PROPS", module, FieldType.STRING));
        fields.add(getField(AgentConstants.AVAILABLE_POINTS, "AVAILABLE_POINTS", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.PORT_NUMBER, "PORT_NUMBER", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.CREATED_TIME, "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.LAST_MODIFIED_TIME, "LAST_MODIFIED_TIME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.LAST_DATA_SENT_TIME, "LAST_DATA_SENT_TIME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DELETED_TIME, "DELETED_TIME", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getModbusControllerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getModbusControllerModbus();
        fields.add(getField(AgentKeys.ID, "ID", module, FieldType.NUMBER));
        fields.add(getSiteIdField(module));
        fields.add(getField(AgentConstants.SLAVE_ID, "SLAVE_ID", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.IP_ADDRESS, "IP_ADDRESS", module, FieldType.STRING));
        fields.add(getField(AgentConstants.NETWORK_ID, "NETWORK_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static FacilioField getControllerIdCount(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("controllerCount");
        field.setDisplayName("Controller Count");
        field.setDataType(FieldType.NUMBER);
        field.setColumnName("count(Controller.ID)");
        return field;
    }

    public static List<FacilioField> getAgentIntegrationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAgentIntegrationModule();
        fields.add(getIdField(module));
        fields.add(getField(AgentIntegrationKeys.NAME, "NAME", module, FieldType.STRING));
        fields.add(getAgentIntegrationPropKeyField(module));
        fields.add(getField(AgentIntegrationKeys.INTEGRATION_TYPE, "TYPE", module, FieldType.NUMBER));
        fields.add(getAgentIntegrationPropValueField(module));
        return fields;
    }

    public static FacilioField getAgentIntegrationPropKeyField(FacilioModule module) {
        return getField(AgentIntegrationKeys.PROP_KEY, "PROP_KEY", module, FieldType.STRING);
    }

    public static FacilioField getAgentIntegrationPropValueField(FacilioModule module) {
        return getField(AgentIntegrationKeys.PROP_VALUE, "PROP_VALUE", module, FieldType.STRING);
    }

    public static FacilioField getCreatedTime(FacilioModule module) {
        return getField(AgentConstants.CREATED_TIME, "CREATED_TIME", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getFormFieldsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormFieldsModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getDisplayNameField(module));
        fields.add(getNameField(module));

        FacilioField formId = new FacilioField();
        formId.setName("formId");
        formId.setDataType(FieldType.NUMBER);
        formId.setColumnName("FORMID");
        formId.setModule(module);
        fields.add(formId);

        FacilioField displayType = new FacilioField();
        displayType.setName("displayType");
        displayType.setDataType(FieldType.NUMBER);
        displayType.setColumnName("DISPLAY_TYPE");
        displayType.setModule(module);
        fields.add(displayType);

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.NUMBER);
        fieldId.setColumnName("FIELDID");
        fieldId.setModule(module);
        fields.add(fieldId);

        FacilioField required = new FacilioField();
        required.setName("required");
        required.setDataType(FieldType.BOOLEAN);
        required.setColumnName("REQUIRED");
        required.setModule(module);
        fields.add(required);

        FacilioField sequenceNumber = new FacilioField();
        sequenceNumber.setName("sequenceNumber");
        sequenceNumber.setDataType(FieldType.NUMBER);
        sequenceNumber.setColumnName("SEQUENCE_NUMBER");
        sequenceNumber.setModule(module);
        fields.add(sequenceNumber);

        FacilioField span = new FacilioField();
        span.setName("span");
        span.setDataType(FieldType.NUMBER);
        span.setColumnName("SPAN");
        span.setModule(module);

        fields.add(getField("hideField", "HIDE_FIELD", module, FieldType.BOOLEAN));
        fields.add(getField("isDisabled", "IS_DISABLED", module, FieldType.BOOLEAN));
        fields.add(getField("value", "DEFAULT_VALUE", module, FieldType.STRING));
        fields.add(getField("sectionId", "SECTIONID", module, FieldType.NUMBER));

        fields.add(getField("configStr", "CONFIG", module, FieldType.STRING));

        FacilioField allowCreateOptions = new FacilioField();
        allowCreateOptions.setName("allowCreate");
        allowCreateOptions.setDataType(FieldType.BOOLEAN);
        allowCreateOptions.setColumnName("ALLOW_CREATE_OPTIONS");
        allowCreateOptions.setModule(module);
        fields.add(allowCreateOptions);


        fields.add(span);
        return fields;
    }

    public static List<FacilioField> getFormSectionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormSectionModule();

        fields.add(getIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("formId", "FORMID", module, FieldType.NUMBER));
        fields.add(getField("sequenceNumber", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("showLabel", "SHOW_LABEL", module, FieldType.BOOLEAN));

        fields.add(getField("subFormId", "SUB_FORM_ID", module, FieldType.NUMBER));
        fields.add(getField("sectionType", "SECTION_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    private static FacilioField getDisplayNameField(FacilioModule module) {
        FacilioField displayName = new FacilioField();
        displayName.setName("displayName");
        displayName.setDataType(FieldType.STRING);
        displayName.setColumnName("DISPLAYNAME");
        if (module != null) {
            displayName.setModule(module);
        }

        return displayName;
    }

    public static List<FacilioField> getAssetCategoryReadingRelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();

        FacilioField parentCategoryId = new FacilioField();
        parentCategoryId.setName("parentCategoryId");
        parentCategoryId.setDataType(FieldType.NUMBER);
        parentCategoryId.setColumnName("PARENT_CATEGORY_ID");
        parentCategoryId.setModule(module);
        fields.add(parentCategoryId);

        FacilioField readingModuleId = new FacilioField();
        readingModuleId.setName("readingModuleId");
        readingModuleId.setDataType(FieldType.NUMBER);
        readingModuleId.setColumnName("READING_MODULE_ID");
        readingModuleId.setModule(module);
        fields.add(readingModuleId);

        return fields;
    }

    public static List<FacilioField> getSubModuleRelFields() {
        return getSubModuleRelFields(null);
    }

    public static List<FacilioField> getSubModuleRelFields(FacilioModule module) {
        List<FacilioField> field = new ArrayList<>();
        FacilioField parentModuleId = new FacilioField();
        parentModuleId.setName("parentModuleId");
        parentModuleId.setDataType(FieldType.NUMBER);
        parentModuleId.setColumnName("PARENT_MODULE_ID");
        if (module != null) {
            parentModuleId.setModule(module);
        }
        field.add(parentModuleId);

        FacilioField childModuleId = new FacilioField();
        childModuleId.setName("childModuleId");
        childModuleId.setDataType(FieldType.NUMBER);
        childModuleId.setColumnName("CHILD_MODULE_ID");
        if (module != null) {
            childModuleId.setModule(module);
        }
        field.add(childModuleId);


        return field;
    }

    public static List<FacilioField> getMultiLookupFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMultiLookupFieldsModule();

        fields.add(getField("fieldId", "FIELDID", module, FieldType.ID));
        fields.add(getField("relModuleId", "REL_MODULE_ID", module, FieldType.NUMBER));
        fields.add(getField("parentFieldPosition", "PARENT_FIELD_POSITION", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMultiEnumFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMultiEnumFieldsModule();

        fields.add(getField("fieldId", "FIELDID", module, FieldType.ID));
        fields.add(getField("relModuleId", "REL_MODULE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getLookupFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getLookupFieldsModule();

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.ID);
        fieldId.setColumnName("FIELDID");
        fieldId.setModule(module);
        fields.add(fieldId);

        /*fields.add(getOrgIdField(module));*/

        FacilioField specialType = new FacilioField();
        specialType.setName("specialType");
        specialType.setDataType(FieldType.STRING);
        specialType.setColumnName("SPECIAL_TYPE");
        specialType.setModule(module);
        fields.add(specialType);

        FacilioField lookupModuleId = new FacilioField();
        lookupModuleId.setName("lookupModuleId");
        lookupModuleId.setDataType(FieldType.NUMBER);
        lookupModuleId.setColumnName("LOOKUP_MODULE_ID");
        lookupModuleId.setModule(module);
        fields.add(lookupModuleId);

        return fields;
    }

    public static List<FacilioField> getRollUpFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getRollUpFieldsModule();

        fields.add(getIdField(module));
        fields.add(getField("childFieldId", "CHILD_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("childModuleId", "CHILD_MODULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("childCriteriaId", "CHILD_CRITERIA_ID", module, FieldType.LOOKUP));
        fields.add(getField("aggregateFunctionId", "AGGREGATE_FUNCTION", module, FieldType.LOOKUP));
        fields.add(getField("aggregateFieldId", "AGGREGATE_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("parentModuleId", "PARENT_MODULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("parentRollUpFieldId", "PARENT_ROLLUP_FIELD_ID", module, FieldType.LOOKUP));
        return fields;
    }
    
    public static List<FacilioField> getNumberFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNumberFieldModule();

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.ID);
        fieldId.setColumnName("FIELDID");
        fieldId.setModule(module);
        fields.add(fieldId);

        /*fields.add(getOrgIdField(module));*/

        FacilioField unit = new FacilioField();
        unit.setName("unit");
        unit.setDataType(FieldType.STRING);
        unit.setColumnName("UNIT");
        unit.setModule(module);
        fields.add(unit);

        fields.add(getField("metric", "METRIC", module, FieldType.NUMBER));
        fields.add(getField("unitId", "UNIT_ID", module, FieldType.NUMBER));
        fields.add(getField("counterField", "IS_COUNTER_FIELD", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getBooleanFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getBooleanFieldsModule();

        fields.add(getField("fieldId", "FIELDID", module, FieldType.ID));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("trueVal", "TRUE_VAL", module, FieldType.STRING));
        fields.add(getField("falseVal", "FALSE_VAL", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getFileFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFileFieldModule();

        fields.add(getField("fieldId", "FIELDID", module, FieldType.ID));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("format", "FORMAT", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getSystemEnumFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSystemEnumFieldModule();

        fields.add(getField("fieldId", "FIELDID", module, FieldType.ID));
        fields.add(getField("enumName", "ENUM_NAME", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getEnumFieldValuesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
        fields.add(getIdField(module));
        fields.add(getField("fieldId", "FIELDID", module, FieldType.NUMBER));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("index", "IDX", module, FieldType.NUMBER));
        fields.add(getField("value", "VAL", module, FieldType.STRING));
        fields.add(getField("sequence", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("visible", "VISIBLE", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getModuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getModuleModule();

        fields.add(getField("moduleId", "MODULEID", module, FieldType.ID));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("displayName", "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField("tableName", "TABLE_NAME", module, FieldType.STRING));
        fields.add(getField("type", "MODULE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("trashEnabled", "IS_TRASH_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.LOOKUP));
        fields.add(getField("stateFlowEnabled", "STATE_FLOW_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("custom", "IS_CUSTOM", module, FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getAddFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFieldsModule();

        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        fields.add(getNameField(module));

        FacilioField displayName = new FacilioField();
        displayName.setName("displayName");
        displayName.setDataType(FieldType.STRING);
        displayName.setColumnName("DISPLAY_NAME");
        displayName.setModule(module);
        fields.add(displayName);

        FacilioField columnName = new FacilioField();
        columnName.setName("columnName");
        columnName.setDataType(FieldType.STRING);
        columnName.setColumnName("COLUMN_NAME");
        columnName.setModule(module);
        fields.add(columnName);

        FacilioField sequenceNumber = new FacilioField();
        sequenceNumber.setName("sequenceNumber");
        sequenceNumber.setDataType(FieldType.NUMBER);
        sequenceNumber.setColumnName("SEQUENCE_NUMBER");
        sequenceNumber.setModule(module);
        fields.add(sequenceNumber);

        FacilioField dataType = new FacilioField();
        dataType.setName("dataType");
        dataType.setDataType(FieldType.NUMBER);
        dataType.setColumnName("DATA_TYPE");
        dataType.setModule(module);
        fields.add(dataType);

        FacilioField displayType = new FacilioField();
        displayType.setName("displayTypeInt");
        displayType.setDataType(FieldType.NUMBER);
        displayType.setColumnName("DISPLAY_TYPE");
        displayType.setModule(module);
        fields.add(displayType);

        FacilioField required = new FacilioField();
        required.setName("required");
        required.setDataType(FieldType.BOOLEAN);
        required.setColumnName("REQUIRED");
        required.setModule(module);
        fields.add(required);

        FacilioField mainField = new FacilioField();
        mainField.setName("mainField");
        mainField.setDataType(FieldType.BOOLEAN);
        mainField.setColumnName("IS_MAIN_FIELD");
        mainField.setModule(module);
        fields.add(mainField);

        FacilioField defaultField = new FacilioField();
        defaultField.setName("default");
        defaultField.setDataType(FieldType.BOOLEAN);
        defaultField.setColumnName("IS_DEFAULT");
        defaultField.setModule(module);
        fields.add(defaultField);

        return fields;
    }

    public static List<FacilioField> getSelectFieldFields() {
        List<FacilioField> fields = getAddFieldFields();

        FacilioModule module = ModuleFactory.getFieldsModule();

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.NUMBER);
        fieldId.setColumnName("FIELDID");
        fieldId.setModule(module);
        fields.add(fieldId);

        FacilioField defaultField = new FacilioField();
        defaultField.setName("default");
        defaultField.setDataType(FieldType.BOOLEAN);
        defaultField.setColumnName("IS_DEFAULT");
        defaultField.setModule(module);
        fields.add(defaultField);

        FacilioField mainField = new FacilioField();
        mainField.setName("mainField");
        mainField.setDataType(FieldType.BOOLEAN);
        mainField.setColumnName("IS_MAIN_FIELD");
        mainField.setModule(module);
        fields.add(mainField);

        FacilioField disabled = new FacilioField();
        disabled.setName("disabled");
        disabled.setDataType(FieldType.BOOLEAN);
        disabled.setColumnName("DISABLED");
        disabled.setModule(module);
        fields.add(disabled);

        FacilioField styleClass = new FacilioField();
        styleClass.setName("styleClass");
        styleClass.setDataType(FieldType.STRING);
        styleClass.setColumnName("STYLE_CLASS");
        styleClass.setModule(module);
        fields.add(styleClass);

        FacilioField icon = new FacilioField();
        icon.setName("icon");
        icon.setDataType(FieldType.STRING);
        icon.setColumnName("ICON");
        icon.setModule(module);
        fields.add(icon);

        FacilioField placeHolder = new FacilioField();
        placeHolder.setName("placeHolder");
        placeHolder.setDataType(FieldType.STRING);
        placeHolder.setColumnName("PLACE_HOLDER");
        placeHolder.setModule(module);
        fields.add(placeHolder);

        FacilioField accessType = new FacilioField();
        accessType.setName("accessType");
        accessType.setDataType(FieldType.NUMBER);
        accessType.setColumnName("ACCESS_TYPE");
        accessType.setModule(module);
        fields.add(accessType);

        return fields;
    }

    public static List<FacilioField> getUpdateFieldFields() { // Only these
        // fields can be
        // updated
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFieldsModule();

        FacilioField displayName = new FacilioField();
        displayName.setName("displayName");
        displayName.setDataType(FieldType.STRING);
        displayName.setColumnName("DISPLAY_NAME");
        displayName.setModule(module);
        fields.add(displayName);

        FacilioField displayType = new FacilioField();
        displayType.setName("displayTypeInt");
        displayType.setDataType(FieldType.NUMBER);
        displayType.setColumnName("DISPLAY_TYPE");
        displayType.setModule(module);
        fields.add(displayType);

        FacilioField sequenceNumber = new FacilioField();
        sequenceNumber.setName("sequenceNumber");
        sequenceNumber.setDataType(FieldType.NUMBER);
        sequenceNumber.setColumnName("SEQUENCE_NUMBER");
        sequenceNumber.setModule(module);
        fields.add(sequenceNumber);

        FacilioField required = new FacilioField();
        required.setName("required");
        required.setDataType(FieldType.BOOLEAN);
        required.setColumnName("REQUIRED");
        required.setModule(module);
        fields.add(required);

        FacilioField disabled = new FacilioField();
        disabled.setName("disabled");
        disabled.setDataType(FieldType.BOOLEAN);
        disabled.setColumnName("DISABLED");
        disabled.setModule(module);
        fields.add(disabled);

        FacilioField styleClass = new FacilioField();
        styleClass.setName("styleClass");
        styleClass.setDataType(FieldType.STRING);
        styleClass.setColumnName("STYLE_CLASS");
        styleClass.setModule(module);
        fields.add(styleClass);

        FacilioField icon = new FacilioField();
        icon.setName("icon");
        icon.setDataType(FieldType.STRING);
        icon.setColumnName("ICON");
        icon.setModule(module);
        fields.add(icon);

        FacilioField placeHolder = new FacilioField();
        placeHolder.setName("placeHolder");
        placeHolder.setDataType(FieldType.STRING);
        placeHolder.setColumnName("PLACE_HOLDER");
        placeHolder.setModule(module);
        fields.add(placeHolder);

        return fields;
    }

//	public static FacilioField getOrgIdField() {
//		return getOrgIdField(null);
//	}
//
//	public static FacilioField getOrgIdField(FacilioModule module) {
//		FacilioField field = new FacilioField();
//		field.setName("orgId");
//		field.setDisplayName("Org Id");
//		field.setDataType(FieldType.NUMBER);
//		field.setColumnName("ORGID");
//		if (module != null) {
//			field.setModule(module);
//		}
//
//
//		return field;
//		}


    public static FacilioField getSiteIdField() {
        return getSiteIdField(null);
    }

    public static FacilioField getSiteIdField(FacilioModule module) {

        if (module != null && FacilioConstants.ContextNames.SITE.equals(module.getName())) {
            return getIdField(module);
        }

        FacilioField field = new FacilioField();
        field.setName("siteId");
        field.setDisplayName("Site");
        field.setDataType(FieldType.NUMBER);
        field.setColumnName("SITE_ID");
        field.setDefault(true);
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static List<FacilioField> getSystemPointFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getSystemField("sysCreatedTime", module));
        fields.add(getSystemField("sysCreatedBy", module));

        if (module != null && module.getTypeEnum() != FacilioModule.ModuleType.LOOKUP_REL_MODULE && module.getTypeEnum() != FacilioModule.ModuleType.ENUM_REL_MODULE) {
            fields.add(getSystemField("sysModifiedTime", module));
            fields.add(getSystemField("sysModifiedBy", module));
        }

        return fields;
    }

    private static final List<String> systemFields = Collections.unmodifiableList(FieldFactory.getSystemPointFields(null).stream().map(FacilioField::getName).collect(Collectors.toList()));

    public static boolean isSystemField(String fieldName) {
        return systemFields.contains(fieldName);
    }

    public static List<String> getSystemFieldNames() {
        return systemFields;
    }

    public static FacilioField getSystemField(String fieldName, FacilioModule module) {
        //temp
        List<String> specialModuleList = new ArrayList<String>(Arrays.asList("safetyPlan", "hazard", "precaution"));

        switch (fieldName) {
            case "sysCreatedTime":
                return getField("sysCreatedTime", module != null && specialModuleList.contains(module.getName()) ? "Created Time" : "System Created Time", "SYS_CREATED_TIME", module, FieldType.DATE_TIME);
            case "sysModifiedTime":
                return getField("sysModifiedTime", module != null && specialModuleList.contains(module.getName()) ? "Modified Time" : "System Modified Time", "SYS_MODIFIED_TIME", module, FieldType.DATE_TIME);
            case "sysCreatedBy":
                LookupField createdBy = (LookupField) getField("sysCreatedBy", module != null && specialModuleList.contains(module.getName()) ? "Created By" : "System Created By", "SYS_CREATED_BY", module, FieldType.LOOKUP);
                createdBy.setSpecialType(FacilioConstants.ContextNames.USERS);
                return createdBy;
            case "sysModifiedBy":
                LookupField modifiedBy = (LookupField) getField("sysModifiedBy", module != null && specialModuleList.contains(module.getName()) ? "Modified By" : "System Modified By", "SYS_MODIFIED_BY", module, FieldType.LOOKUP);
                modifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
                return modifiedBy;
        }
        return null;
    }

    private static final List<String> baseModuleSystemFields = Collections.unmodifiableList(FieldFactory.getBaseModuleSystemFields(null).stream().map(FacilioField::getName).collect(Collectors.toList()));

    public static boolean isBaseModuleSystemField(String fieldName) {
        return baseModuleSystemFields.contains(fieldName);
    }

    public static List<String> getBaseModuleSystemFieldNames() {
        return baseModuleSystemFields;
    }

    public static FacilioField getBaseModuleSystemField(String fieldName, FacilioModule module) {
        switch (fieldName) {
            case "formId":
                return getField("formId", "FORM_ID", module != null ? module.getParentModule() : null, FieldType.NUMBER);
        }
        return null;
    }

    public static List<FacilioField> getBaseModuleSystemFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getBaseModuleSystemField("formId", module));

        return fields;
    }

    public static List<FacilioField> getImportFieldMappingDisplayFields(FacilioModule module) {
        List<FacilioField> fieldMappingFields = new ArrayList<>();
        fieldMappingFields.add(FieldFactory.getIdField(module));
        fieldMappingFields.add(FieldFactory.getField("building", "Building", "BUILDING", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("floor", "Floor", "FLOOR", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("spaceName", "Space", "SPACE", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("space1", "Space 1", "SPACE_1", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("space2", "Space 2", "SPACE_2", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("space3", "Space 3", "SPACE_3", module, FieldType.STRING));
        fieldMappingFields.add(FieldFactory.getField("asset", "Asset", "ASSET", module, FieldType.STRING));
        return fieldMappingFields;
    }

    public static FacilioField getKinesisField() {
        return getKinesisField(null);
    }

    public static FacilioField getKinesisField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("kinesisTopic");
        field.setDataType(FieldType.STRING);
        field.setColumnName("KINESIS_TOPIC");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getUserIdField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("userId");
        field.setDataType(FieldType.ID);
        field.setColumnName("USERID");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getModuleIdField() {
        return getModuleIdField(null);
    }

    public static FacilioField getModuleIdField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("moduleId");
        field.setDataType(FieldType.NUMBER);
        field.setColumnName("MODULEID");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getIdField() {
        return getIdField(null);
    }

    public static FacilioField getIdField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("id");
        field.setDataType(FieldType.ID);
        field.setDisplayName("Id");
        field.setColumnName("ID");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getIdNotPrimaryField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("id");
        field.setDataType(FieldType.NUMBER);
        field.setDisplayName("Id");
        field.setColumnName("ID");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getNameField(FacilioModule module) {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        if (module != null) {
            name.setModule(module);
        }

        return name;
    }

    public static FacilioField getDescriptionField(FacilioModule module) {
        FacilioField name = new FacilioField();
        name.setName("description");
        name.setDataType(FieldType.STRING);
        name.setColumnName("DESCRIPTION");
        if (module != null) {
            name.setModule(module);
        }

        return name;
    }

    public static FacilioField getContactIdField(FacilioModule module) {
        FacilioField contactId = new FacilioField();
        contactId.setName("contactId");
        contactId.setDataType(FieldType.NUMBER);
        contactId.setColumnName("CONTACT_ID");
        if (module != null) {
            contactId.setModule(module);
        }

        return contactId;
    }

    public static FacilioField getIsDeletedField(FacilioModule module) {
        return getField("deleted", "SYS_DELETED", module, FieldType.BOOLEAN);
    }

    public static FacilioField getSysDeletedTimeField(FacilioModule module) {
        return getField("sysDeletedTime", "SYS_DELETED_TIME", module, FieldType.DATE_TIME);
    }

    public static FacilioField getSysDeletedByField(FacilioModule module) {
        LookupField deletedBy = (LookupField) getField("sysDeletedBy", "SYS_DELETED_BY", module, FieldType.LOOKUP);
        deletedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        return deletedBy;
    }

    public static FacilioField getIsDeletedField() {
        return getIsDeletedField(null);
    }

    public static List<FacilioField> getCountField() {
        return getCountField(null);
    }

    public static List<FacilioField> getCountField(FacilioModule module, FacilioField... field) {
        FacilioField countFld = new FacilioField();
        countFld.setName("count");
        if (field != null && field.length > 0) {
            countFld.setColumnName("COUNT(" + field[0].getCompleteColumnName() + ")");
        } else if (module != null) {
            countFld.setColumnName("COUNT(" + module.getTableName() + ".ID)");
        } else {
            countFld.setColumnName("COUNT(*)");
        }
        countFld.setDataType(FieldType.NUMBER);
        return Collections.singletonList(countFld);
    }

    public static List<FacilioField> getCategoryReadingsFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();

        FacilioField parentCategoryId = new FacilioField();
        parentCategoryId.setName("parentCategoryId");
        parentCategoryId.setDataType(FieldType.NUMBER);
        parentCategoryId.setColumnName("PARENT_CATEGORY_ID");
        parentCategoryId.setModule(module);
        fields.add(parentCategoryId);

        FacilioField readingModuleId = new FacilioField();
        readingModuleId.setName("readingModuleId");
        readingModuleId.setDataType(FieldType.NUMBER);
        readingModuleId.setColumnName("READING_MODULE_ID");
        readingModuleId.setModule(module);
        fields.add(readingModuleId);

        return fields;
    }

    public static List<FacilioField> getEmailSettingFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEmailSettingModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField bcc = new FacilioField();
        bcc.setName("bccEmail");
        bcc.setDataType(FieldType.STRING);
        bcc.setColumnName("BCC_EMAIL");
        bcc.setModule(module);
        fields.add(bcc);

        FacilioField flags = new FacilioField();
        flags.setName("flags");
        flags.setDataType(FieldType.NUMBER);
        flags.setColumnName("FLAGS");
        flags.setModule(module);
        fields.add(flags);

        return fields;
    }

//	public static List<FacilioField> getWorkflowEventFields() {
//		List<FacilioField> fields = new ArrayList<>();
//		FacilioModule module = ModuleFactory.getWorkflowEventModule();
//
//		fields.add(getIdField(module));
//		/*fields.add(getOrgIdField(module));*/
//		fields.add(getModuleIdField(module));
//
//		FacilioField field3 = new FacilioField();
//		field3.setName("activityType");
//		field3.setDataType(FieldType.NUMBER);
//		field3.setColumnName("ACTIVITY_TYPE");
//		field3.setModule(module);
//		fields.add(field3);
//
//		return fields;
//	}

    public static List<FacilioField> getDeviceDetailsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getDeviceDetailsModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField deviceName = new FacilioField();
        deviceName.setName("deviceName");
        deviceName.setDataType(FieldType.STRING);
        deviceName.setColumnName("DEVICE_NAME");
        deviceName.setModule(module);
        fields.add(deviceName);

        FacilioField deviceId = new FacilioField();
        deviceId.setName("deviceId");
        deviceId.setDataType(FieldType.STRING);
        deviceId.setColumnName("DEVICE_ID");
        deviceId.setModule(module);
        fields.add(deviceId);

        FacilioField inUse = new FacilioField();
        inUse.setName("inUse");
        inUse.setDataType(FieldType.BOOLEAN);
        inUse.setColumnName("IN_USE");
        inUse.setModule(module);
        fields.add(inUse);

        FacilioField lastUpdated = new FacilioField();
        lastUpdated.setName("lastUpdatedTime");
        lastUpdated.setDataType(FieldType.NUMBER);
        lastUpdated.setColumnName("LAST_UPDATED_TIME");
        lastUpdated.setModule(module);
        fields.add(lastUpdated);

        FacilioField lastAlertedTimeField = new FacilioField();
        lastAlertedTimeField.setName("lastAlertedTime");
        lastAlertedTimeField.setDataType(FieldType.NUMBER);
        lastAlertedTimeField.setColumnName("LAST_ALERTED_TIME");
        lastAlertedTimeField.setModule(module);
        fields.add(lastAlertedTimeField);

        FacilioField alertFrequency = new FacilioField();
        alertFrequency.setName("alertFrequency");
        alertFrequency.setDataType(FieldType.NUMBER);
        alertFrequency.setColumnName("ALERT_FREQUENCY");
        alertFrequency.setModule(module);
        fields.add(alertFrequency);

        return fields;
    }

    public static List<FacilioField> getWorkflowRuleActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleActionModule();

        FacilioField workflowRuleActionId = new FacilioField();
        workflowRuleActionId.setName("workflowRuleActionId");
        workflowRuleActionId.setDataType(FieldType.ID);
        workflowRuleActionId.setColumnName("WORKFLOW_RULE_ACTION_ID");
        workflowRuleActionId.setModule(module);
        fields.add(workflowRuleActionId);

        FacilioField workflowRuleId = new FacilioField();
        workflowRuleId.setName("workflowRuleId");
        workflowRuleId.setDataType(FieldType.NUMBER);
        workflowRuleId.setColumnName("WORKFLOW_RULE_ID");
        workflowRuleId.setModule(module);
        fields.add(workflowRuleId);

        FacilioField actionId = new FacilioField();
        actionId.setName("actionId");
        actionId.setDataType(FieldType.NUMBER);
        actionId.setColumnName("ACTION_ID");
        actionId.setModule(module);
        fields.add(actionId);

        return fields;
    }

    public static List<FacilioField> getWorkflowRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getNameField(module));

        FacilioField description = new FacilioField();
        description.setName("description");
        description.setDataType(FieldType.STRING);
        description.setColumnName("DESCRIPTION");
        description.setModule(module);
        fields.add(description);

//		FacilioField eventId = new FacilioField();
//		eventId.setName("eventId");
//		eventId.setDataType(FieldType.NUMBER);
//		eventId.setColumnName("EVENT_ID");
//		eventId.setModule(module);
//		fields.add(eventId);

        FacilioField criteriaId = new NumberField();
        criteriaId.setName("criteriaId");
        criteriaId.setDataType(FieldType.NUMBER);
        criteriaId.setColumnName("CRITERIAID");
        criteriaId.setModule(module);
        fields.add(criteriaId);

        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.NUMBER));

        FacilioField executionOrder = new FacilioField();
        executionOrder.setName("executionOrder");
        executionOrder.setDataType(FieldType.NUMBER);
        executionOrder.setColumnName("EXECUTION_ORDER");
        executionOrder.setModule(module);
        fields.add(executionOrder);

        FacilioField status = new FacilioField();
        status.setName("status");
        status.setDataType(FieldType.BOOLEAN);
        status.setColumnName("STATUS");
        status.setModule(module);
        fields.add(status);

        FacilioField ruleType = new FacilioField();
        ruleType.setName("ruleType");
        ruleType.setDataType(FieldType.NUMBER);
        ruleType.setColumnName("RULE_TYPE");
        ruleType.setModule(module);
        fields.add(ruleType);

        fields.add(getField("parentRuleId", "PARENT_RULE_ID", module, FieldType.NUMBER));
        fields.add(getField("onSuccess", "ON_SUCCESS", module, FieldType.BOOLEAN));
        fields.add(getField("versionGroupId", "VERSION_GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("latestVersion", "IS_LATEST_VERSION", module, FieldType.BOOLEAN));
        fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));
        fields.add(getField("dateFieldId", "DATE_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("scheduleType", "SCHEDULE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("interval", "TIME_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("versionNumber", "VERSION_NO", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("modifiedTime", "MODIFIED_TIME", module, FieldType.NUMBER));
        fields.add(getField("time", "JOB_TIME", module, FieldType.STRING));
        fields.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));

        fields.add(getModuleIdField(module));

        FacilioField field3 = new FacilioField();
        field3.setName("activityType");
        field3.setDataType(FieldType.NUMBER);
        field3.setColumnName("ACTIVITY_TYPE");
        field3.setModule(module);
        fields.add(field3);

        return fields;
    }

    public static List<FacilioField> getWorkflowRuleRCAMapping() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleRCAMapping();
        fields.add(getField("rule", "RULE_ID", module, FieldType.NUMBER));
        fields.add(getField("rcaRule", "RCA_RULE_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getWorkflowFieldChangeFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowFieldChangeFieldsModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("oldValue", "OLD_VALUE", module, FieldType.STRING));
        fields.add(getField("newValue", "NEW_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getReadingRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingRuleModule();

        fields.add(getIdField(module));

        FacilioField startValue = new FacilioField();
        startValue.setName("startValue");
        startValue.setDataType(FieldType.NUMBER);
        startValue.setColumnName("START_VALUE");
        startValue.setModule(module);
        fields.add(startValue);

        FacilioField interval = new FacilioField();
        interval.setName("interval");
        interval.setDataType(FieldType.NUMBER);
        interval.setColumnName("INTERVAL_VALUE");
        interval.setModule(module);
        fields.add(interval);

        FacilioField lastValue = new FacilioField();
        lastValue.setName("lastValue");
        lastValue.setDataType(FieldType.NUMBER);
        lastValue.setColumnName("LATEST_VALUE");
        lastValue.setModule(module);
        fields.add(lastValue);

        FacilioField resourceId = new FacilioField();
        resourceId.setName("resourceId");
        resourceId.setDataType(FieldType.NUMBER);
        resourceId.setColumnName("RESOURCE_ID");
        resourceId.setModule(module);
        fields.add(resourceId);

        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("baselineId", "BASELINE_ID", module, FieldType.NUMBER));
        fields.add(getField("aggregation", "AGGREGATION", module, FieldType.STRING));
        fields.add(getField("dateRange", "DATE_RANGE", module, FieldType.NUMBER));
        fields.add(getField("operatorId", "OPERATOR_ID", module, FieldType.NUMBER));
        fields.add(getField("percentage", "PERCENTAGE", module, FieldType.STRING));
        fields.add(getField("readingFieldId", "READING_FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("thresholdType", "THRESHOLD_TYPE", module, FieldType.NUMBER));
        fields.add(getField("clearAlarm", "CLEAR_ALARM", module, FieldType.BOOLEAN));
        fields.add(getField("occurences", "OCCURENCES", module, FieldType.NUMBER));
        fields.add(getField("overPeriod", "OVER_PERIOD", module, FieldType.NUMBER));
        fields.add(getField("consecutive", "IS_CONSECUTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("flapCount", "FLAP_COUNT", module, FieldType.NUMBER));
        fields.add(getField("flapInterval", "FLAP_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("minFlapValue", "MIN_FLAP_VAL", module, FieldType.NUMBER));
        fields.add(getField("maxFlapValue", "MAX_FLAP_VAL", module, FieldType.NUMBER));
        fields.add(getField("flapFrequency", "FLAP_FREQUENCY", module, FieldType.NUMBER));
        fields.add(getField("ruleGroupId", "RULE_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("alarmSeverityId", "ALARM_SEVERITY_ID", module, FieldType.LOOKUP));
        fields.add(getField("triggerExecutePeriod", "TRIGGER_EXECUTE_PERIOD", module, FieldType.NUMBER));
        fields.add(getField("readingRuleType", "READING_RULE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("upperBound", "UPPER_BOUND", module, FieldType.DECIMAL));
        fields.add(getField("lowerBound", "LOWER_BOUND", module, FieldType.DECIMAL));
        fields.add(getField("alarmCategoryId", "ALARM_CATEGORY_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getReadingRuleMetricFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingRuleMetricModule();
        fields.add(getIdField(module));

        fields.add(getField("readingRuleId", "READING_RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getReadingAlarmRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingAlarmRuleModule();

        fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
        fields.add(getField("readingRuleGroupId", "READING_RULE_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("rcaRuleId", "RCA_RULE_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getReadingRuleInclusionsExclusionsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingRuleInclusionsExclusionsModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("ruleGroupId", "RULE_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("isInclude", "IS_INCLUDE", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getReadingRuleAlarmMetaFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingRuleAlarmMetaModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("alarmId", "ALARM_ID", module, FieldType.LOOKUP));
        fields.add(getField("ruleGroupId", "RULE_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("readingFieldId", "READING_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("clear", "IS_CLEAR", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getSLAEntityFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSLAEntityModule();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("verbName", "VERB_NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("moduleId", "MODULEID", module, FieldType.NUMBER));
        fields.add(getField("baseFieldId", "BASE_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("dueFieldId", "DUE_FIELDID", module, FieldType.NUMBER));
//		fields.add(getField("compareFieldId", "COMPARE_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.NUMBER));

        return fields;
    }

//	public static List<FacilioField> getSLAPolicyRuleFields() {
//		List<FacilioField> fields = new ArrayList<>();
//		FacilioModule module = ModuleFactory.getSLAPolicyRuleModule();
//
//		fields.add(getField("id", "ID", module, FieldType.NUMBER));
//
//		return fields;
//	}

//	public static List<FacilioField> getSLAWorkflowRuleFields() {
//		List<FacilioField> fields = new ArrayList<>();
//		FacilioModule module = ModuleFactory.getSLAWorkflowRuleModule();
//
//		fields.add(getField("id", "ID", module, FieldType.NUMBER));
////		fields.add(getField("slaEntityId", "SLA_ENTITY_ID", module, FieldType.NUMBER));
////		fields.add(getField("addDuration", "ADD_DURATION", module, FieldType.NUMBER));
//
//		return fields;
//	}

    public static List<FacilioField> getSLACommitmentDurationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSLACommitmentDurationModule();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("slaEntityId", "SLA_ENTITY_ID", module, FieldType.NUMBER));
        fields.add(getField("slaCommitmentId", "SLA_COMMITMENT_ID", module, FieldType.NUMBER));
        fields.add(getField("addDuration", "ADD_DURATION", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getSlaWorkflowEscalationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSLAWorkflowEscalationModule();

        fields.add(getIdField(module));
        fields.add(getField("slaPolicyId", "SLA_POLICY_ID", module, FieldType.NUMBER));
        fields.add(getField("slaEntityId", "SLA_ENTITY_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.SYSTEM_ENUM));
        fields.add(getField("interval", "TIME_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("actionArray", "ACTION_ARRAY", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getSLARuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSLARuleModule();

        fields.add(getIdField(module));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.NUMBER));

        return fields;
    }


//	public static List<FacilioField> getScheduledRuleFields() {
    //	List<FacilioField> fields = new ArrayList<>();
    //FacilioModule module = ModuleFactory.getScheduledRuleModule();

//		fields.add(getIdField(module));
    /*fields.add(getOrgIdField(module));*/
//		fields.add(getSiteIdField(module));
//		fields.add(getField("dateFieldId", "DATE_FIELD_ID", module, FieldType.LOOKUP));
//		fields.add(getField("scheduleType", "SCHEDULE_TYPE", module, FieldType.NUMBER));
//		fields.add(getField("interval", "TIME_INTERVAL", module, FieldType.NUMBER));
//		fields.add(getField("time", "JOB_TIME", module, FieldType.STRING));
//
//		return fields;
//	}
//
//	public static List<FacilioField> getScheduledRuleJobFields() {
//		List<FacilioField> fields = new ArrayList<>();
//		FacilioModule module = ModuleFactory.getScheduledRuleJobModule();
//
//		fields.add(getIdField(module));
//		/*fields.add(getOrgIdField(module));*/
//		fields.add(getSiteIdField(module));
//		fields.add(getField("ruleId", "RULE_ID", module, FieldType.LOOKUP));
//		fields.add(getField("recordId", "RECORD_ID", module, FieldType.LOOKUP));
//		fields.add(getField("scheduledTime", "SCHEDULED_TIME", module, FieldType.DATE_TIME));
//
//		return fields;
//	}


    public static List<FacilioField> getScheduledActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getScheduledActionModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("actionId", "ACTION_ID", module, FieldType.NUMBER));
        fields.add(getField("frequency", "FREQUENCY_TYPE", module, FieldType.NUMBER));
        fields.add(getField("time", "JOB_TIME", module, FieldType.STRING));

        return fields;
    }


    public static List<FacilioField> getScheduledWorkflowFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getScheduledWorkflowModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("timeZone", "TIMEZONE", module, FieldType.STRING));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("isActive", "IS_ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));
        fields.add(getField("startTime", "START_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getApprovalRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getApprovalRulesModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getField("approvalRuleId", "APPROVAL_RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("rejectionRuleId", "REJECTION_RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("approvalFormId", "APPROVAL_FORM_ID", module, FieldType.LOOKUP));
        fields.add(getField("rejectionFormId", "REJECTION_FORM_ID", module, FieldType.LOOKUP));
        fields.add(getField("approvalButton", "APPROVAL_BUTTON", module, FieldType.STRING));
        fields.add(getField("rejectionButton", "REJECTION_BUTTON", module, FieldType.STRING));
        fields.add(getField("allApprovalRequired", "ALL_APPROVAL_REQUIRED", module, FieldType.BOOLEAN));
        fields.add(getField("approvalOrder", "APPROVAL_ORDER", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getApprovalStepsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getApprovalStepsModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("recordId", "RECORD_ID", module, FieldType.LOOKUP));
        fields.add(getField("actionBy", "ACTION_BY", module, FieldType.LOOKUP));
        fields.add(getField("approverGroup", "APPROVER_GROUP", module, FieldType.LOOKUP));
        fields.add(getField("action", "APPROVAL_ACTION", module, FieldType.NUMBER));
        fields.add(getField("actionTime", "ACTION_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getSharingFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("parentId", "PARENT_ID", module, FieldType.LOOKUP));
        fields.add(getField("userId", "ORG_USERID", module, FieldType.LOOKUP));
        fields.add(getField("roleId", "ROLE_ID", module, FieldType.LOOKUP));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("type", "SHARING_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getActionModule();

        /*fields.add(getOrgIdField(module));*/
        fields.add(getIdField(module));

        FacilioField actionType = new FacilioField();
        actionType.setName("actionType");
        actionType.setDataType(FieldType.NUMBER);
        actionType.setColumnName("ACTION_TYPE");
        actionType.setModule(module);
        fields.add(actionType);

        FacilioField templateType = new FacilioField();
        templateType.setName("defaultTemplateId");
        templateType.setDataType(FieldType.NUMBER);
        templateType.setColumnName("DEFAULT_TEMPLATE_ID");
        templateType.setModule(module);
        fields.add(templateType);

        FacilioField templateId = new FacilioField();
        templateId.setName("templateId");
        templateId.setDataType(FieldType.NUMBER);
        templateId.setColumnName("TEMPLATE_ID");
        templateId.setModule(module);
        fields.add(templateId);

        FacilioField status = new FacilioField();
        status.setName("status");
        status.setDataType(FieldType.BOOLEAN);
        status.setColumnName("STATUS");
        status.setModule(module);
        fields.add(status);

        return fields;
    }

    public static List<FacilioField> getApproverActionsRelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getApproverActionsRelModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("approverId", "APPROVER_ID", module, FieldType.LOOKUP));
        fields.add(getField("actionId", "ACTION_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getUserFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getUserModule();

        FacilioField userid = new FacilioField();
        userid.setName("userid");
        userid.setDataType(FieldType.NUMBER);
        userid.setColumnName("USERID");
        userid.setModule(module);
        fields.add(userid);

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        FacilioField cognito_id = new FacilioField();
        cognito_id.setName("cognito_id");
        cognito_id.setDataType(FieldType.STRING);
        cognito_id.setColumnName("COGNITO_ID");
        cognito_id.setModule(module);
        fields.add(cognito_id);

        FacilioField user_verified = new FacilioField();
        user_verified.setName("user_verified");
        user_verified.setDataType(FieldType.BOOLEAN);
        user_verified.setColumnName("USER_VERIFIED");
        user_verified.setModule(module);
        fields.add(user_verified);

        FacilioField email = new FacilioField();
        email.setName("email");
        email.setDataType(FieldType.STRING);
        email.setColumnName("EMAIL");
        email.setModule(module);
        fields.add(email);

        FacilioField photo_id = new FacilioField();
        photo_id.setName("photo_id");
        photo_id.setDataType(FieldType.NUMBER);
        photo_id.setColumnName("PHOTO_ID");
        photo_id.setModule(module);
        fields.add(photo_id);

        FacilioField timezone = new FacilioField();
        timezone.setName("timezone");
        timezone.setDataType(FieldType.STRING);
        timezone.setColumnName("TIMEZONE");
        timezone.setModule(module);
        fields.add(timezone);

        FacilioField language = new FacilioField();
        language.setName("language");
        language.setDataType(FieldType.STRING);
        language.setColumnName("LANGUAGE");
        language.setModule(module);
        fields.add(language);

        FacilioField phone = new FacilioField();
        phone.setName("phone");
        phone.setDataType(FieldType.STRING);
        phone.setColumnName("PHONE");
        phone.setModule(module);
        fields.add(phone);

        FacilioField mobile = new FacilioField();
        mobile.setName("mobile");
        mobile.setDataType(FieldType.STRING);
        mobile.setColumnName("MOBILE");
        mobile.setModule(module);
        fields.add(mobile);

        FacilioField street = new FacilioField();
        street.setName("street");
        street.setDataType(FieldType.STRING);
        street.setColumnName("STREET");
        street.setModule(module);
        fields.add(street);

        FacilioField city = new FacilioField();
        city.setName("city");
        city.setDataType(FieldType.STRING);
        city.setColumnName("CITY");
        city.setModule(module);
        fields.add(city);

        FacilioField state = new FacilioField();
        state.setName("state");
        state.setDataType(FieldType.STRING);
        state.setColumnName("STATE");
        state.setModule(module);
        fields.add(state);

        FacilioField zip = new FacilioField();
        zip.setName("zip");
        zip.setDataType(FieldType.STRING);
        zip.setColumnName("ZIP");
        zip.setModule(module);
        fields.add(zip);

        FacilioField country = new FacilioField();
        country.setName("country");
        country.setDataType(FieldType.STRING);
        country.setColumnName("COUNTRY");
        country.setModule(module);
        fields.add(country);
        return fields;

    }

    public static List<FacilioField> getOrgUserFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getOrgUserModule();

        FacilioField orguserid = new FacilioField();
        orguserid.setName("orgUserId");
        orguserid.setDataType(FieldType.NUMBER);
        orguserid.setColumnName("ORG_USERID");
        orguserid.setModule(module);
        fields.add(orguserid);

        FacilioField userid = new FacilioField();
        userid.setName("userId");
        userid.setDataType(FieldType.NUMBER);
        userid.setColumnName("USERID");
        userid.setModule(module);
        fields.add(userid);

        FacilioField orgid = new FacilioField();
        orgid.setName("orgId");
        orgid.setDataType(FieldType.NUMBER);
        orgid.setColumnName("ORGID");
        orgid.setModule(module);
        fields.add(orgid);

        FacilioField license = new FacilioField();
        license.setName("license");
        license.setDataType(FieldType.NUMBER);
        license.setColumnName("LICENSE");
        license.setModule(module);
        fields.add(license);

        FacilioField userstatus = new FacilioField();
        userstatus.setName("userStatus");
        userstatus.setDataType(FieldType.BOOLEAN);
        userstatus.setColumnName("USER_STATUS");
        userstatus.setModule(module);
        fields.add(userstatus);

        FacilioField roleid = new FacilioField();
        roleid.setName("roleId");
        roleid.setDataType(FieldType.NUMBER);
        roleid.setColumnName("ROLE_ID");
        roleid.setModule(module);
        fields.add(roleid);

        FacilioField deletedTime = new FacilioField();
        deletedTime.setName("deletedTime");
        deletedTime.setDataType(FieldType.NUMBER);
        deletedTime.setColumnName("DELETED_TIME");
        deletedTime.setModule(module);
        fields.add(deletedTime);

        FacilioField portal_verified = new FacilioField();
        portal_verified.setName("portal_verified");
        portal_verified.setDataType(FieldType.BOOLEAN);
        portal_verified.setColumnName("PORTAL_VERIFIED");
        portal_verified.setModule(module);
        fields.add(portal_verified);

        return fields;

    }

    public static List<FacilioField> getSupportEmailFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getSupportEmailsModule();

        fields.add(getIdField(module));
        fields.add(AccountConstants.getOrgIdField(module));
        fields.add(getSiteIdField(module));

        FacilioField replyName = new FacilioField();
        replyName.setName("replyName");
        replyName.setDataType(FieldType.STRING);
        replyName.setColumnName("REPLY_NAME");
        replyName.setModule(module);
        fields.add(replyName);

        FacilioField actualEmail = new FacilioField();
        actualEmail.setName("actualEmail");
        actualEmail.setDataType(FieldType.STRING);
        actualEmail.setColumnName("ACTUAL_EMAIL");
        actualEmail.setModule(module);
        fields.add(actualEmail);

        FacilioField fwdEmail = new FacilioField();
        fwdEmail.setName("fwdEmail");
        fwdEmail.setDataType(FieldType.STRING);
        fwdEmail.setColumnName("FWD_EMAIL");
        fwdEmail.setModule(module);
        fields.add(fwdEmail);

        fields.add(getField("autoAssignGroupId", "AUTO_ASSIGN_GROUP_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getRequesterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getRequesterModule();

        /*fields.add(getOrgIdField(module));*/

        FacilioField field1 = new FacilioField();
        field1.setName("requesterId");
        field1.setDataType(FieldType.NUMBER);
        field1.setColumnName("REQUESTER_ID");
        field1.setModule(module);
        fields.add(field1);

        fields.add(getNameField(module));

        FacilioField field3 = new FacilioField();
        field3.setName("cognitoId");
        field3.setDataType(FieldType.STRING);
        field3.setColumnName("COGNITO_ID");
        field3.setModule(module);
        fields.add(field3);

        LookupField field4 = new LookupField();
        field4.setName("userVerified");
        field4.setDataType(FieldType.BOOLEAN);
        field4.setColumnName("USER_VERIFIED");
        field4.setModule(module);
        fields.add(field4);

        LookupField field5 = new LookupField();
        field5.setName("email");
        field5.setDataType(FieldType.STRING);
        field5.setColumnName("EMAIL");
        field5.setModule(module);
        fields.add(field5);

        LookupField field6 = new LookupField();
        field6.setName("portalAccess");
        field6.setDataType(FieldType.BOOLEAN);
        field6.setColumnName("PORTAL_ACCESS");
        field6.setModule(module);
        fields.add(field6);

        return fields;
    }

    public static List<FacilioField> getNoteFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNotesModule();

        /*fields.add(getOrgIdField(module));*/

        FacilioField field1 = new FacilioField();
        field1.setName("noteId");
        field1.setDataType(FieldType.NUMBER);
        field1.setColumnName("NOTEID");
        field1.setModule(module);
        fields.add(field1);

        LookupField field2 = new LookupField();
        field2.setName("ownerId");
        field2.setDataType(FieldType.LOOKUP);
        field2.setColumnName("OWNERID");
        field2.setModule(module);
        field2.setSpecialType(FacilioConstants.ContextNames.USER);
        fields.add(field2);

        FacilioField field3 = new FacilioField();
        field3.setName("creationTime");
        field3.setDataType(FieldType.NUMBER);
        field3.setColumnName("CREATION_TIME");
        field3.setModule(module);
        fields.add(field3);

        LookupField field4 = new LookupField();
        field4.setName("title");
        field4.setDataType(FieldType.STRING);
        field4.setColumnName("TITLE");
        field4.setModule(module);
        fields.add(field4);

        LookupField field5 = new LookupField();
        field5.setName("body");
        field5.setDataType(FieldType.STRING);
        field5.setColumnName("BODY");
        field5.setModule(module);
        fields.add(field5);

        return fields;
    }

    public static List<FacilioField> getWorkorderEmailFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();

        fields.add(getIdField(module));

        FacilioField s3MessageIdField = new FacilioField();
        s3MessageIdField.setName("s3MessageId");
        s3MessageIdField.setDataType(FieldType.STRING);
        s3MessageIdField.setColumnName("S3_MESSAGE_ID");
        s3MessageIdField.setModule(module);
        fields.add(s3MessageIdField);

        fields.add(getField("to", "TO_ADDR", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));

        FacilioField isProcessField = new FacilioField();
        isProcessField.setName("isProcessed");
        isProcessField.setDataType(FieldType.BOOLEAN);
        isProcessField.setColumnName("IS_PROCESSED");
        isProcessField.setModule(module);
        fields.add(isProcessField);

        fields.add(getField("requestId", "REQUEST_ID", module, FieldType.NUMBER));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFormSiteRelationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormSiteRelationModule();

        fields.add(getIdField(module));
        fields.add(getField("formId", "FORM_ID", module, FieldType.NUMBER));
        fields.add(getSiteIdField(module));


        return fields;
    }

    public static List<FacilioField> getTicketFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        LookupField statusField = (LookupField) getField("status", "STATUS_ID", module, FieldType.LOOKUP);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        fields.add(statusField);

        LookupField groupField = (LookupField) getField("assignmentGroup", "ASSIGNMENT_GROUP_ID", module,
                FieldType.LOOKUP);
        groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
        fields.add(groupField);

        LookupField userField = (LookupField) getField("assignedTo", "ASSIGNED_TO_ID", module, FieldType.LOOKUP);
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(userField);

        return fields;
    }

    public static List<FacilioField> getTicketActivityFields() {
        FacilioModule module = ModuleFactory.getTicketActivityModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField ticketId = new FacilioField();
        ticketId.setName("ticketId");
        ticketId.setDataType(FieldType.NUMBER);
        ticketId.setColumnName("TICKET_ID");
        ticketId.setModule(module);
        fields.add(ticketId);

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.NUMBER);
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setModule(module);
        fields.add(modifiedTime);

        FacilioField modifiedBy = new FacilioField();
        modifiedBy.setName("modifiedBy");
        modifiedBy.setDataType(FieldType.NUMBER);
        modifiedBy.setColumnName("MODIFIED_BY");
        modifiedBy.setModule(module);
        fields.add(modifiedBy);

        FacilioField activityType = new FacilioField();
        activityType.setName("activityType");
        activityType.setDataType(FieldType.NUMBER);
        activityType.setColumnName("ACTIVITY_TYPE");
        activityType.setModule(module);
        fields.add(activityType);

        FacilioField info = new FacilioField();
        info.setName("infoJsonStr");
        info.setDataType(FieldType.STRING);
        info.setColumnName("INFO");
        info.setModule(module);
        fields.add(info);

        return fields;
    }

    public static List<FacilioField> getAlarmFollowersFeilds() {
        FacilioModule module = ModuleFactory.getAlarmFollowersModule();

        FacilioField alarmId = new FacilioField();
        alarmId.setName("alarmId");
        alarmId.setDataType(FieldType.NUMBER);
        alarmId.setColumnName("ALARM_ID");
        alarmId.setModule(module);

        FacilioField followerType = new FacilioField();
        followerType.setName("type");
        followerType.setDataType(FieldType.STRING);
        followerType.setColumnName("FOLLOWER_TYPE");
        followerType.setModule(module);

        FacilioField follower = new FacilioField();
        follower.setName("follower");
        follower.setDataType(FieldType.STRING);
        follower.setColumnName("FOLLOWER");
        follower.setModule(module);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(alarmId);
        fields.add(followerType);
        fields.add(follower);

        return fields;
    }

    public static List<FacilioField> getTemplateFields() {
        FacilioModule module = ModuleFactory.getTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getNameField(module));

        FacilioField typeField = new FacilioField();
        typeField.setName("type");
        typeField.setDataType(FieldType.NUMBER);
        typeField.setColumnName("TEMPLATE_TYPE");
        typeField.setModule(module);
        fields.add(typeField);

        FacilioField placeholder = new FacilioField();
        placeholder.setName("placeholderStr");
        placeholder.setDataType(FieldType.STRING);
        placeholder.setColumnName("PLACEHOLDER");
        placeholder.setModule(module);
        fields.add(placeholder);

        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("ftl", "IS_FTL", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getEMailTemplateFields() {
        return getEMailTemplateFields(true);
    }

    private static List<FacilioField> getEMailTemplateFields(boolean isIdNeeded) {
        FacilioModule module = ModuleFactory.getEMailTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        if (isIdNeeded) {
            fields.add(getIdField(module));
        }

        FacilioField emailFrom = new FacilioField();
        emailFrom.setName("from");
        emailFrom.setDataType(FieldType.STRING);
        emailFrom.setColumnName("FROM_ADDR");
        emailFrom.setModule(module);
        fields.add(emailFrom);

        FacilioField emailTo = new FacilioField();
        emailTo.setName("to");
        emailTo.setDataType(FieldType.STRING);
        emailTo.setColumnName("TO_ADDR");
        emailTo.setModule(module);
        fields.add(emailTo);

        fields.add(getField("cc", "CC_ADDR", module, FieldType.STRING));
        fields.add(getField("bcc", "BCC_ADDR", module, FieldType.STRING));
        fields.add(getField("sendAsSeparateMail", "SEND_AS_SEPARATE_MAIL", module, FieldType.BOOLEAN));
        fields.add(getField("html", "IS_HTML", module, FieldType.BOOLEAN));

        FacilioField emailSubject = new FacilioField();
        emailSubject.setName("subject");
        emailSubject.setDataType(FieldType.STRING);
        emailSubject.setColumnName("SUBJECT");
        emailSubject.setModule(module);
        fields.add(emailSubject);

        FacilioField emailBody = new FacilioField();
        emailBody.setName("bodyId");
        emailBody.setDataType(FieldType.STRING);
        emailBody.setColumnName("BODY_ID");
        emailBody.setModule(module);
        fields.add(emailBody);

        return fields;
    }

    public static List<FacilioField> getWorkflowTemplateFields() {
        FacilioModule module = ModuleFactory.getWorkflowTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
        fields.add(getField("resultWorkflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("meta", "META", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getSMSTemplateFields() {
        return getSMSTemplateFields(true);
    }

    private static List<FacilioField> getSMSTemplateFields(boolean isIdNeeded) {
        FacilioModule module = ModuleFactory.getSMSTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        if (isIdNeeded) {
            fields.add(getIdField(module));
        }

        FacilioField smsFrom = new FacilioField();
        smsFrom.setName("from");
        smsFrom.setDataType(FieldType.STRING);
        smsFrom.setColumnName("FROM_NUM");
        smsFrom.setModule(module);
        fields.add(smsFrom);

        FacilioField smsTo = new FacilioField();
        smsTo.setName("to");
        smsTo.setDataType(FieldType.STRING);
        smsTo.setColumnName("TO_NUM");
        smsTo.setModule(module);
        fields.add(smsTo);

        FacilioField smsMsg = new FacilioField();
        smsMsg.setName("message");
        smsMsg.setDataType(FieldType.STRING);
        smsMsg.setColumnName("MSG");
        smsMsg.setModule(module);
        fields.add(smsMsg);

        return fields;
    }

    public static List<FacilioField> getCallTemplateFields() {
        FacilioModule module = ModuleFactory.getCallTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        fields.add(getField("from", "FROM_NUM", module, FieldType.STRING));
        fields.add(getField("to", "TO_NUM", module, FieldType.STRING));
        fields.add(getField("message", "MSG", module, FieldType.STRING));
        fields.add(getField("voice", "VOICE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getWhatsappMessageTemplateFields() {
        FacilioModule module = ModuleFactory.getWhatsappMessageTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        fields.add(getField("from", "FROM_NUM", module, FieldType.STRING));
        fields.add(getField("to", "TO_NUM", module, FieldType.STRING));
        fields.add(getField("message", "MSG", module, FieldType.STRING));
        fields.add(getField("isHtmlContent", "IS_HTML_CONTENT", module, FieldType.BOOLEAN));
        fields.add(getField("htmlContentString", "HTML_CONTENT", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getPushNotificationTemplateFields() {
        return getPushNotificationTemplateFields(true);
    }

    private static List<FacilioField> getPushNotificationTemplateFields(boolean isIdNeeded) {
        FacilioModule module = ModuleFactory.getPushNotificationTemplateModule();

        List<FacilioField> fields = new ArrayList<>();

        if (isIdNeeded) {
            fields.add(getIdField(module));
        }

        FacilioField smsTo = new FacilioField();
        smsTo.setName("to");
        smsTo.setDataType(FieldType.STRING);
        smsTo.setColumnName("TO_UID");
        smsTo.setModule(module);
        fields.add(smsTo);

        FacilioField smsMsg = new FacilioField();
        smsMsg.setName("body");
        smsMsg.setDataType(FieldType.STRING);
        smsMsg.setColumnName("MSG");
        smsMsg.setModule(module);
        fields.add(smsMsg);

        FacilioField title = new FacilioField();
        title.setName("title");
        title.setDataType(FieldType.STRING);
        title.setColumnName("TITLE");
        title.setModule(module);
        fields.add(title);

        FacilioField url = new FacilioField();
        url.setName("url");
        url.setDataType(FieldType.STRING);
        url.setColumnName("URL");
        url.setModule(module);
        fields.add(url);
        
        FacilioField appType = new FacilioField();
        appType.setName("appId");
        appType.setDataType(FieldType.NUMBER);
        appType.setColumnName("APP_TYPE");
        appType.setModule(module);
        fields.add(appType);


        return fields;
    }

    public static List<FacilioField> getWebNotificationTemplateFields() {
        return getWebNotificationTemplateFields(true);
    }

    private static List<FacilioField> getWebNotificationTemplateFields(boolean isIdNeeded) {
        FacilioModule module = ModuleFactory.getWebNotificationTemplateModule();

        List<FacilioField> fields = new ArrayList<>();

        if (isIdNeeded) {
            fields.add(getIdField(module));
        }

        FacilioField smsTo = new FacilioField();
        smsTo.setName("to");
        smsTo.setDataType(FieldType.STRING);
        smsTo.setColumnName("TO_UID");
        smsTo.setModule(module);
        fields.add(smsTo);

        FacilioField smsMsg = new FacilioField();
        smsMsg.setName("message");
        smsMsg.setDataType(FieldType.STRING);
        smsMsg.setColumnName("MSG");
        smsMsg.setModule(module);
        fields.add(smsMsg);

        FacilioField title = new FacilioField();
        title.setName("title");
        title.setDataType(FieldType.STRING);
        title.setColumnName("TITLE");
        title.setModule(module);
        fields.add(title);

        FacilioField url = new FacilioField();
        url.setName("url");
        url.setDataType(FieldType.STRING);
        url.setColumnName("URL");
        url.setModule(module);
        fields.add(url);

        return fields;
    }

    public static List<FacilioField> getExcelTemplateFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getExcelTemplatesModule();

        fields.add(getIdField(module));

        FacilioField excelFileId = new FacilioField();
        excelFileId.setName("excelFileId");
        excelFileId.setDataType(FieldType.NUMBER);
        excelFileId.setColumnName("EXCEL_FILE_ID");
        excelFileId.setModule(module);
        fields.add(excelFileId);

        return fields;
    }

    public static List<FacilioField> getJSONTemplateFields() {
        FacilioModule module = ModuleFactory.getJSONTemplateModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        FacilioField content = new FacilioField();
        content.setName("contentId");
        content.setDataType(FieldType.STRING);
        content.setColumnName("CONTENT_ID");
        content.setModule(module);
        fields.add(content);

        return fields;
    }

    public static List<FacilioField> getControlActionTemplateFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControlActionTemplateModule();

        fields.add(getIdField(module));
        fields.add(getField("assetCategory", "ASSET_CATEGORY", module, FieldType.NUMBER));
        fields.add(getField("metric", "METRIC", module, FieldType.STRING));
        fields.add(getField("resource", "RESOURCE", module, FieldType.STRING));
        fields.add(getField("val", "VAL", module, FieldType.STRING));

        fields.add(getField("actionType", "ACTION_TYPE", module, FieldType.NUMBER));
        fields.add(getField("controlActionGroupId", "CONTROL_ACTION_GROUP_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getFormTemplateFields() {
        FacilioModule module = ModuleFactory.getFormTemplatesModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("formId", "FORM_ID", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getTenantFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getTenantModule();

        fields.add(getIdField(module));

        FacilioField nameField = new FacilioField();
        nameField.setName("name");
        nameField.setDataType(FieldType.STRING);
        nameField.setColumnName("NAME");
        nameField.setModule(module);
        fields.add(nameField);

        /*fields.add(getOrgIdField());*/

        FacilioField addressField = new FacilioField();
        addressField.setName("address");
        addressField.setDataType(FieldType.STRING);
        addressField.setColumnName("ADDRESS");
        addressField.setModule(module);
        fields.add(addressField);

        FacilioField contactemailField = new FacilioField();
        contactemailField.setName("contactEmail");
        contactemailField.setDataType(FieldType.STRING);
        contactemailField.setColumnName("CONTACTEMAIL");
        contactemailField.setModule(module);
        fields.add(contactemailField);

        FacilioField contactNumberField = new FacilioField();
        contactNumberField.setName("contactNumber");
        contactNumberField.setDataType(FieldType.STRING);
        contactNumberField.setColumnName("CONTACTNUMBER");
        contactNumberField.setModule(module);
        fields.add(contactNumberField);

        FacilioField templateIdField = new FacilioField();
        templateIdField.setName("templateId");
        templateIdField.setDataType(FieldType.STRING);
        templateIdField.setColumnName("TEMPLATEID");
        templateIdField.setModule(module);
        fields.add(templateIdField);

        return fields;
    }

    public static List<FacilioField> getWorkOrderTemplateFields() {
        List<FacilioField> woTemplateFields = getWOrderTemplateFields();
        LookupField vendorField = (LookupField) getField("vendorId", "VENDOR_ID", ModuleFactory.getWorkOrderTemplateModule(), FieldType.LOOKUP);
        vendorField.setLookupModule(ModuleFactory.getVendorsModule());
        woTemplateFields.add(vendorField);

        try {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                LookupField tenantField = (LookupField) getField("tenantId", "TENANT_ID", ModuleFactory.getWorkOrderTemplateModule(), FieldType.LOOKUP);
                tenantField.setLookupModule(ModuleFactory.getTenantsModule());
                woTemplateFields.add(tenantField);
            }
            if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
                LookupField safetyPlanField = (LookupField) getField("safetyPlanId", "SAFETY_PLAN_ID", ModuleFactory.getWorkOrderTemplateModule(), FieldType.LOOKUP);
                safetyPlanField.setLookupModule(ModuleFactory.getSafetyPlanModule());
                woTemplateFields.add(safetyPlanField);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return woTemplateFields;
    }

    public static List<FacilioField> getPreventiveMaintenanceFields() {
        FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField title = new FacilioField();
        title.setName("title");
        title.setDataType(FieldType.STRING);
        title.setColumnName("TITLE");
        title.setModule(module);
        fields.add(title);

        FacilioField status = new FacilioField();
        status.setName("status");
        status.setDataType(FieldType.NUMBER);
        status.setColumnName("STATUS");
        status.setModule(module);
        fields.add(status);

        FacilioField createdBy = new FacilioField();
        createdBy.setName("createdById");
        createdBy.setDataType(FieldType.NUMBER);
        createdBy.setColumnName("CREATED_BY");
        createdBy.setModule(module);
        fields.add(createdBy);

        FacilioField modifiedBy = new FacilioField();
        modifiedBy.setName("modifiedById");
        modifiedBy.setDataType(FieldType.NUMBER);
        modifiedBy.setColumnName("MODIFIED_BY");
        modifiedBy.setModule(module);
        fields.add(modifiedBy);

        FacilioField creationTime = new FacilioField();
        creationTime.setName("createdTime");
        creationTime.setDataType(FieldType.NUMBER);
        creationTime.setColumnName("CREATION_TIME");
        creationTime.setModule(module);
        fields.add(creationTime);

        FacilioField lastModifiedTime = new FacilioField();
        lastModifiedTime.setName("lastModifiedTime");
        lastModifiedTime.setDataType(FieldType.NUMBER);
        lastModifiedTime.setColumnName("LAST_MODIFIED_TIME");
        lastModifiedTime.setModule(module);
        fields.add(lastModifiedTime);

        FacilioField templateId = new FacilioField();
        templateId.setName("templateId");
        templateId.setDataType(FieldType.NUMBER);
        templateId.setColumnName("TEMPLATE_ID");
        templateId.setModule(module);
        fields.add(templateId);

        /*
         * FacilioField spaceId = new FacilioField(); spaceId.setName("resourceId");
         * spaceId.setDataType(FieldType.NUMBER); spaceId.setColumnName("RESOURCE_ID");
         * spaceId.setModule(module); fields.add(spaceId);
         *
         * FacilioField assignedToId = new FacilioField();
         * assignedToId.setName("assignedToid");
         * assignedToId.setDataType(FieldType.NUMBER);
         * assignedToId.setColumnName("ASSIGNED_TO_ID"); assignedToId.setModule(module);
         * fields.add(assignedToId);
         *
         * FacilioField assignmentGroupId = new FacilioField();
         * assignmentGroupId.setName("assignmentGroupId");
         * assignmentGroupId.setDataType(FieldType.NUMBER);
         * assignmentGroupId.setColumnName("ASSIGNMENT_GROUP_ID");
         * assignmentGroupId.setModule(module); fields.add(assignmentGroupId);
         *
         * FacilioField typeId = new FacilioField(); typeId.setName("typeId");
         * typeId.setDataType(FieldType.NUMBER); typeId.setColumnName("TYPE_ID");
         * typeId.setModule(module); fields.add(typeId);
         */

        fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("currentExecutionCount", "CURRENT_EXECUTION_COUNT", module, FieldType.NUMBER));
        fields.add(getField("maxCount", "MAX_COUNT", module, FieldType.NUMBER));
        fields.add(getField("frequency", "FREQUENCY_TYPE", module, FieldType.NUMBER));
        fields.add(getField("custom", "IS_CUSTOM", module, FieldType.BOOLEAN));
        fields.add(getField("preventOnNoTask", "PREVENT_ON_NO_TASK", module, FieldType.BOOLEAN));

        fields.add(getSiteIdField(module));

        fields.add(getField("baseSpaceId", "BASE_SPACE_ID", module, FieldType.LOOKUP));
        fields.add(getField("spaceCategoryId", "SPACE_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("assignmentType", "ASSIGNMENT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("pmCreationType", "PM_CREARTION_TYPE", module, FieldType.NUMBER));
        fields.add(getField("woGenerationStatus", "WO_GENERATION_STATUS", module, FieldType.BOOLEAN));
        fields.add(getField("woGeneratedUpto", "WO_GENERATED_UPTO", module, FieldType.NUMBER));
        fields.add(getField("isUserTriggerPresent", "IS_USER_TRIGGER_PRESENT", module, FieldType.BOOLEAN));
        fields.add(getField("defaultAllTriggers", "IS_DEFAULT_ALL_TRIGGERS", module, FieldType.BOOLEAN));
        fields.add(getField("woCreationOffset", "WO_CREATION_OFFSET", module, FieldType.NUMBER));
        fields.add(getField("markIgnoredWo","MARK_IGNORED_WO", module,FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getJobFields() {
        FacilioModule module = ModuleFactory.getJobsModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField jobId = new FacilioField();
        jobId.setName("jobId");
        jobId.setDataType(FieldType.ID);
        jobId.setColumnName("JOBID");
        jobId.setModule(module);
        fields.add(jobId);

        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));

        FacilioField jobName = new FacilioField();
        jobName.setName("jobName");
        jobName.setDataType(FieldType.STRING);
        jobName.setColumnName("JOBNAME");
        jobName.setModule(module);
        fields.add(jobName);

        FacilioField isActive = new FacilioField();
        isActive.setName("active");
        isActive.setDataType(FieldType.BOOLEAN);
        isActive.setColumnName("IS_ACTIVE");
        isActive.setModule(module);
        fields.add(isActive);

        FacilioField transactionTimeout = new FacilioField();
        transactionTimeout.setName("transactionTimeout");
        transactionTimeout.setDataType(FieldType.NUMBER);
        transactionTimeout.setColumnName("TRANSACTION_TIMEOUT");
        transactionTimeout.setModule(module);
        fields.add(transactionTimeout);

        FacilioField isPeriodic = new FacilioField();
        isPeriodic.setName("isPeriodic");
        isPeriodic.setDataType(FieldType.BOOLEAN);
        isPeriodic.setColumnName("IS_PERIODIC");
        isPeriodic.setModule(module);
        fields.add(isPeriodic);

        FacilioField period = new FacilioField();
        period.setName("period");
        period.setDataType(FieldType.NUMBER);
        period.setColumnName("PERIOD");
        period.setModule(module);
        fields.add(period);

        fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));

        FacilioField executionTime = new FacilioField();
        executionTime.setName("executionTime");
        executionTime.setDataType(FieldType.NUMBER);
        executionTime.setColumnName("NEXT_EXECUTION_TIME");
        executionTime.setModule(module);
        fields.add(executionTime);

        FacilioField executorName = new FacilioField();
        executorName.setName("executorName");
        executorName.setDataType(FieldType.STRING);
        executorName.setColumnName("EXECUTOR_NAME");
        executorName.setModule(module);
        fields.add(executorName);

        fields.add(getField("endExecutionTime", "END_EXECUTION_TIME", module, FieldType.NUMBER));
        fields.add(getField("maxExecution", "MAX_EXECUTION", module, FieldType.NUMBER));

        FacilioField currentExecutionCount = new FacilioField();
        currentExecutionCount.setName("currentExecutionCount");
        currentExecutionCount.setDataType(FieldType.NUMBER);
        currentExecutionCount.setColumnName("CURRENT_EXECUTION_COUNT");
        currentExecutionCount.setModule(module);
        fields.add(currentExecutionCount);

        return fields;
    }

    public static List<FacilioField> getCriteriaFields() {
        FacilioModule module = ModuleFactory.getCriteriaModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField criteriaId = new FacilioField();
        criteriaId.setName("criteriaId");
        criteriaId.setDataType(FieldType.ID);
        criteriaId.setColumnName("CRITERIAID");
        criteriaId.setModule(module);
        fields.add(criteriaId);

        /*fields.add(getOrgIdField(module));*/

        FacilioField pattern = new FacilioField();
        pattern.setName("pattern");
        pattern.setDataType(FieldType.STRING);
        pattern.setColumnName("PATTERN");
        pattern.setModule(module);
        fields.add(pattern);
        return fields;
    }

    public static List<FacilioField> getConditionFields() {
        FacilioModule module = ModuleFactory.getConditionsModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField conditionId = new FacilioField();
        conditionId.setName("conditionId");
        conditionId.setDataType(FieldType.ID);
        conditionId.setColumnName("CONDITIONID");
        conditionId.setModule(module);
        fields.add(conditionId);

        FacilioField parentCriteriaId = new FacilioField();
        parentCriteriaId.setName("parentCriteriaId");
        parentCriteriaId.setDataType(FieldType.NUMBER);
        parentCriteriaId.setColumnName("PARENT_CRITERIA_ID");
        parentCriteriaId.setModule(module);
        fields.add(parentCriteriaId);

        FacilioField sequence = new FacilioField();
        sequence.setName("sequence");
        sequence.setDataType(FieldType.NUMBER);
        sequence.setColumnName("SEQUENCE");
        sequence.setModule(module);
        fields.add(sequence);

        FacilioField fieldName = new FacilioField();
        fieldName.setName("fieldName");
        fieldName.setDataType(FieldType.STRING);
        fieldName.setColumnName("FIELD_NAME");
        fieldName.setModule(module);
        fields.add(fieldName);

        FacilioField columnName = new FacilioField();
        columnName.setName("columnName");
        columnName.setDataType(FieldType.STRING);
        columnName.setColumnName("COLUMN_NAME");
        columnName.setModule(module);
        fields.add(columnName);

        FacilioField operatorId = new FacilioField();
        operatorId.setName("operatorId");
        operatorId.setDataType(FieldType.NUMBER);
        operatorId.setColumnName("OPERATOR");
        operatorId.setModule(module);
        fields.add(operatorId);

        FacilioField value = new FacilioField();
        value.setName("value");
        value.setDataType(FieldType.STRING);
        value.setColumnName("VAL");
        value.setModule(module);
        fields.add(value);

        FacilioField criteriaValueId = new FacilioField();
        criteriaValueId.setName("criteriaValueId");
        criteriaValueId.setDataType(FieldType.NUMBER);
        criteriaValueId.setColumnName("CRITERIA_VAL_ID");
        criteriaValueId.setModule(module);
        fields.add(criteriaValueId);

        FacilioField jsonValueStrField = getField("jsonValueStr", "JSON_VAL", module, FieldType.STRING);

        FacilioField isExpressionValue = new FacilioField();
        isExpressionValue.setName("isExpressionValue");
        isExpressionValue.setDataType(FieldType.BOOLEAN);
        isExpressionValue.setColumnName("IS_EXPRESSION_VALUE");
        isExpressionValue.setModule(module);
        fields.add(isExpressionValue);

        FacilioField computedWhereClause = new FacilioField();
        computedWhereClause.setName("computedWhereClause");
        computedWhereClause.setDataType(FieldType.STRING);
        computedWhereClause.setColumnName("COMPUTED_WHERE_CLAUSE");
        computedWhereClause.setModule(module);
        fields.add(computedWhereClause);

        return fields;

    }

    public static List<FacilioField> getViewFields() {
        FacilioModule module = ModuleFactory.getViewsModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getNameField(module));

        FacilioField displayName = new FacilioField();
        displayName.setName("displayName");
        displayName.setDataType(FieldType.STRING);
        displayName.setColumnName("DISPLAY_NAME");
        displayName.setModule(module);
        fields.add(displayName);

        FacilioField type = new FacilioField();
        type.setName("type");
        type.setDataType(FieldType.NUMBER);
        type.setColumnName("VIEW_TYPE");
        type.setModule(module);
        fields.add(type);

        fields.add(getModuleIdField(module));

        FacilioField criteria = new FacilioField();
        criteria.setName("criteriaId");
        criteria.setDataType(FieldType.NUMBER);
        criteria.setColumnName("CRITERIAID");
        criteria.setModule(module);
        fields.add(criteria);

        FacilioField isDefault = new FacilioField();
        isDefault.setName("isDefault");
        isDefault.setDataType(FieldType.BOOLEAN);
        isDefault.setColumnName("ISDEFAULT");
        isDefault.setModule(module);
        fields.add(isDefault);

        FacilioField viewOrder = new FacilioField();
        viewOrder.setName("sequenceNumber");
        viewOrder.setDataType(FieldType.NUMBER);
        viewOrder.setColumnName("SEQUENCE_NUMBER");
        viewOrder.setModule(module);
        fields.add(viewOrder);

        FacilioField isHidden = new FacilioField();
        isHidden.setName("isHidden");
        isHidden.setDataType(FieldType.BOOLEAN);
        isHidden.setColumnName("ISHIDDEN");
        isHidden.setModule(module);
        fields.add(isHidden);

        FacilioField moduleName = new FacilioField();
        moduleName.setName("moduleName");
        moduleName.setDataType(FieldType.STRING);
        moduleName.setColumnName("MODULENAME");
        moduleName.setModule(module);
        fields.add(moduleName);

        fields.add(getField("primary", "ISPRIMARY", module, FieldType.BOOLEAN));

        return fields;

    }

    public static List<FacilioField> getDefaultReadingFields(FacilioModule module) {

        List<FacilioField> fields = new ArrayList<>();

        FacilioField actualTtime = getField("actualTtime", "Actual Timestamp", "ACTUAL_TTIME", module, FieldType.DATE_TIME);
        actualTtime.setDefault(true);
        fields.add(actualTtime);

        FacilioField ttime = new FacilioField();
        ttime.setName("ttime");
        ttime.setDisplayName("Timestamp");
        ttime.setDataType(FieldType.DATE_TIME);
        ttime.setColumnName("TTIME");
        ttime.setModule(module);
        ttime.setDefault(true);
        fields.add(ttime);

        FacilioField ttimeDate = new FacilioField();
        ttimeDate.setName("date");
        ttimeDate.setDisplayName("Date");
        ttimeDate.setDataType(FieldType.STRING);
        ttimeDate.setColumnName("TTIME_DATE");
        ttimeDate.setModule(module);
        ttimeDate.setDefault(true);
        fields.add(ttimeDate);

        FacilioField ttimeMonth = new FacilioField();
        ttimeMonth.setName("month");
        ttimeMonth.setDisplayName("Month");
        ttimeMonth.setDataType(FieldType.NUMBER);
        ttimeMonth.setColumnName("TTIME_MONTH");
        ttimeMonth.setModule(module);
        ttimeMonth.setDefault(true);
        fields.add(ttimeMonth);

        FacilioField ttimeWeek = new FacilioField();
        ttimeWeek.setName("week");
        ttimeWeek.setDisplayName("Week");
        ttimeWeek.setDataType(FieldType.NUMBER);
        ttimeWeek.setColumnName("TTIME_WEEK");
        ttimeWeek.setModule(module);
        ttimeWeek.setDefault(true);
        fields.add(ttimeWeek);

        FacilioField ttimeDay = new FacilioField();
        ttimeDay.setName("day");
        ttimeDay.setDisplayName("Day");
        ttimeDay.setDataType(FieldType.NUMBER);
        ttimeDay.setColumnName("TTIME_DAY");
        ttimeDay.setModule(module);
        ttimeDay.setDefault(true);
        fields.add(ttimeDay);

        FacilioField ttimeHour = new FacilioField();
        ttimeHour.setName("hour");
        ttimeHour.setDisplayName("Hour");
        ttimeHour.setDataType(FieldType.NUMBER);
        ttimeHour.setColumnName("TTIME_HOUR");
        ttimeHour.setModule(module);
        ttimeHour.setDefault(true);
        fields.add(ttimeHour);

        FacilioField parent = new FacilioField();
        parent.setName("parentId");
        parent.setDisplayName("Parent");
        parent.setDataType(FieldType.NUMBER);
        parent.setColumnName("PARENT_ID");
        parent.setDefault(true);
        parent.setModule(module);
        fields.add(parent);
        
        SystemEnumField sourceTypeField = (SystemEnumField) getField("sourceType", "Source Type", "SOURCE_TYPE", module, FieldType.SYSTEM_ENUM);
        sourceTypeField.setEnumName("SourceType");
        sourceTypeField.setDefault(true);
        fields.add(sourceTypeField);
        FacilioField sourceIdField = getField("sourceId", "Source Id", "SOURCE_ID", module, FieldType.NUMBER);
        sourceIdField.setDefault(true);
        fields.add(sourceIdField);
//		fields.add(getField("sysCreatedTime", "CREATED_TIME", FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getConnectedAppFields() {
        FacilioModule module = ModuleFactory.getConnectedAppsModule();

        List<FacilioField> fields = new ArrayList<>();

        FacilioField id = getIdField(module);
        id.setDefault(true);
        fields.add(id);
        
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        name.setDefault(true);
        fields.add(name);

        FacilioField linkName = new FacilioField();
        linkName.setName("linkName");
        linkName.setDataType(FieldType.STRING);
        linkName.setColumnName("LINK_NAME");
        linkName.setModule(module);
        linkName.setDefault(true);
        fields.add(linkName);

        FacilioField description = new FacilioField();
        description.setName("description");
        description.setDataType(FieldType.STRING);
        description.setColumnName("DESCRIPTION");
        description.setModule(module);
        description.setDefault(true);
        fields.add(description);

        FacilioField logoId = new FacilioField();
        logoId.setName("logoId");
        logoId.setDataType(FieldType.NUMBER);
        logoId.setColumnName("LOGO_ID");
        logoId.setModule(module);
        logoId.setDefault(true);
        fields.add(logoId);

        FacilioField isActive = getField("isActive", "IS_ACTIVE", module, FieldType.BOOLEAN);
        isActive.setDefault(true);
        fields.add(isActive);
        
        FacilioField showInLauncher = getField("showInLauncher", "SHOW_IN_LAUNCHER", module, FieldType.BOOLEAN);
        showInLauncher.setDefault(true);
        fields.add(showInLauncher);
        
        FacilioField hostingType = getField("hostingType", "HOSTING_TYPE", module, FieldType.SYSTEM_ENUM);
        hostingType.setDefault(true);
        fields.add(hostingType);
        
        FacilioField sandBoxBaseUrl = getField("sandBoxBaseUrl", "SANDBOX_BASE_URL", module, FieldType.STRING);
        sandBoxBaseUrl.setDefault(true);
        fields.add(sandBoxBaseUrl);
        
        FacilioField productionBaseUrl = getField("productionBaseUrl", "PRODUCTION_BASE_URL", module, FieldType.STRING);
        productionBaseUrl.setDefault(true);
        fields.add(productionBaseUrl);
        
        FacilioField startUrl = getField("startUrl", "START_URL", module, FieldType.STRING);
        startUrl.setDefault(true);
        fields.add(startUrl);
        
        return fields;
    }
    
    public static List<FacilioField> getConnectedAppSAMLFields() {
    	FacilioModule module = ModuleFactory.getConnectedAppSAMLModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("connectedAppId", "CONNECTEDAPP_ID", module, FieldType.NUMBER));
        fields.add(getField("subjectType", "SUBJECT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("nameIdFormat", "NAME_ID_FORMAT", module, FieldType.NUMBER));
        fields.add(getField("spEntityId", "SP_ENTITY_ID", module, FieldType.STRING));
        fields.add(getField("spAcsUrl", "SP_ACS_URL", module, FieldType.STRING));
        fields.add(getField("spLogoutUrl", "SP_LOGOUT_URL", module, FieldType.STRING));

        return fields;
	}
	
	public static List<FacilioField> getConnectedAppWidgetsFields() {
		FacilioModule module = ModuleFactory.getConnectedAppWidgetsModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.NUMBER));
        fields.add(getField("connectedAppId", "CONNECTEDAPP_ID", module, FieldType.NUMBER));
        fields.add(getField("widgetName", "WIDGET_NAME", module, FieldType.STRING));
        fields.add(getField("linkName", "LINK_NAME", module, FieldType.STRING));
        fields.add(getField("entityId", "ENTITY_ID", module, FieldType.NUMBER));
        fields.add(getField("entityType", "ENTITY_TYPE", module, FieldType.NUMBER));
        fields.add(getField("resourcePath", "RESOURCE_PATH", module, FieldType.STRING));

        return fields;
	}
	
	public static List<FacilioField> getConnectedAppConnectorsFields() {
		FacilioModule module = ModuleFactory.getConnectedAppConnectorsModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("connectorId", "CONNECTOR_ID", module, FieldType.NUMBER));
        fields.add(getField("connectedAppId", "CONNECTEDAPP_ID", module, FieldType.NUMBER));

        return fields;
	}
	
	public static List<FacilioField> getVariablesFields() {
		FacilioModule module = ModuleFactory.getVariablesModule();

        List<FacilioField> fields = getSystemPointFields(module);
        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("value", "VALUE", module, FieldType.STRING));
        fields.add(getField("connectedAppId", "CONNECTEDAPP_ID", module, FieldType.NUMBER));

        return fields;
	}
    

    public static List<FacilioField> getConnectionFields() {
        FacilioModule module = ModuleFactory.getConnectionModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("serviceName", "SERVICE_NAME", module, FieldType.STRING));
        fields.add(getField("authType", "AUTH_TYPE", module, FieldType.NUMBER));
        fields.add(getField("paramType", "PARAM_TYPE", module, FieldType.NUMBER));
        fields.add(getField("state", "STATE", module, FieldType.NUMBER));
        fields.add(getField("clientId", "CLIENT_ID", module, FieldType.STRING));
        fields.add(getField("clientSecretId", "CLIENT_SECRET_ID", module, FieldType.STRING));
        fields.add(getField("scope", "SCOPE", module, FieldType.STRING));
        fields.add(getField("authorizeUrl", "AUTHORIZE_URL", module, FieldType.STRING));
        fields.add(getField("accessTokenUrl", "ACCESS_TOKEN_URL", module, FieldType.STRING));
        fields.add(getField("refreshTokenUrl", "REFRESH_TOKEN_URL", module, FieldType.STRING));
        fields.add(getField("revokeTokenUrl", "REVOKE_TOKEN_URL", module, FieldType.STRING));
        fields.add(getField("accessToken", "ACCESS_TOKEN", module, FieldType.STRING));
        fields.add(getField("authCode", "AUTH_CODE", module, FieldType.STRING));
        fields.add(getField("refreshToken", "REFRESH_TOKEN", module, FieldType.STRING));
        fields.add(getField("expiryTime", "ACCESS_EXPIRY_TIME", module, FieldType.NUMBER));
        fields.add(getField("refreshTokenExpiryTime", "REFRESH_EXPIRY_TIME", module, FieldType.NUMBER));
        fields.add(getField("secretStateKey", "SECRET_STATE_KEY", module, FieldType.STRING));
        fields.add(getField("accessTokenSetting", "ACCESS_TOKEN_SETTING", module, FieldType.NUMBER));

        fields.add(getSystemField("sysCreatedTime", module));
        fields.add(getSystemField("sysModifiedTime", module));
        fields.add(getSystemField("sysCreatedBy", module));
        fields.add(getSystemField("sysModifiedBy", module));

        return fields;
    }


    public static List<FacilioField> getConnectionParamFields() {
        FacilioModule module = ModuleFactory.getConnectionParamsModule();

        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        fields.add(getField("connectionId", "CONNECTION_ID", module, FieldType.LOOKUP));
        fields.add(getField("connectionApiId", "CONNECTION_API_ID", module, FieldType.LOOKUP));
        fields.add(getField("key", "KEY_STRING", module, FieldType.STRING));
        fields.add(getField("value", "VALUE_STRING", module, FieldType.STRING));
        fields.add(getField("isProperty", "IS_PROPERTY", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getTabWidgetFields() {
        FacilioModule module = ModuleFactory.getTabWidgetModule();

        List<FacilioField> fields = new ArrayList<>();
        /*fields.add(getOrgIdField(module));*/

        FacilioField tabWidgetId = new FacilioField();
        tabWidgetId.setName("tabWidgetId");
        tabWidgetId.setDataType(FieldType.ID);
        tabWidgetId.setColumnName("TAB_WIDGET_ID");
        tabWidgetId.setModule(module);
        fields.add(tabWidgetId);

        FacilioField moduleLinkName = new FacilioField();
        moduleLinkName.setName("moduleLinkName");
        moduleLinkName.setDataType(FieldType.STRING);
        moduleLinkName.setColumnName("MODULE_LINK_NAME");
        moduleLinkName.setModule(module);
        fields.add(moduleLinkName);

        FacilioField tabName = new FacilioField();
        tabName.setName("tabName");
        tabName.setDataType(FieldType.STRING);
        tabName.setColumnName("TAB_NAME");
        tabName.setModule(module);
        fields.add(tabName);

        FacilioField tabLinkName = new FacilioField();
        tabLinkName.setName("tabLinkName");
        tabLinkName.setDataType(FieldType.STRING);
        tabLinkName.setColumnName("TAB_LINK_NAME");
        tabLinkName.setModule(module);
        fields.add(tabLinkName);

        FacilioField widgetName = new FacilioField();
        widgetName.setName("widgetName");
        widgetName.setDataType(FieldType.STRING);
        widgetName.setColumnName("WIDGET_NAME");
        widgetName.setModule(module);
        fields.add(widgetName);

        FacilioField connectedAppId = new FacilioField();
        connectedAppId.setName("connectedAppId");
        connectedAppId.setDataType(FieldType.NUMBER);
        connectedAppId.setColumnName("CONNECTED_APP_ID");
        connectedAppId.setModule(module);
        fields.add(connectedAppId);

        FacilioField resourcePath = new FacilioField();
        resourcePath.setName("resourcePath");
        resourcePath.setDataType(FieldType.STRING);
        resourcePath.setColumnName("RESOURCE_PATH");
        resourcePath.setModule(module);
        fields.add(resourcePath);

        return fields;
    }

    public static List<FacilioField> getBusinessHoursFields() {
        FacilioModule module = ModuleFactory.getBusinessHoursModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("businessHourTypeId", "BUSINESS_HOUR_TYPE_ID", module, FieldType.NUMBER));
        fields.add(getField("customHourTypeId", "CUSTOM_HOUR_TYPE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getShiftField() {
        FacilioModule module = ModuleFactory.getShiftModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        fields.add(getSiteIdField(module));

        FacilioField isSameTime = new FacilioField();
        isSameTime.setName("isSameTime");
        isSameTime.setDataType(FieldType.BOOLEAN);
        isSameTime.setColumnName("ISSAMETIME");
        isSameTime.setModule(module);
        fields.add(isSameTime);

        FacilioField startTime = new FacilioField();
        startTime.setName("startTime");
        startTime.setDataType(FieldType.MISC);
        startTime.setColumnName("START_TIME");
        startTime.setModule(module);
        fields.add(startTime);

        FacilioField endTime = new FacilioField();
        endTime.setName("endTime");
        endTime.setDataType(FieldType.MISC);
        endTime.setColumnName("END_TIME");
        endTime.setModule(module);
        fields.add(endTime);

        FacilioField businessHoursId = new FacilioField();
        businessHoursId.setName("businessHoursId");
        businessHoursId.setDataType(FieldType.MISC);
        businessHoursId.setColumnName("BUSINESSHOURSID");
        businessHoursId.setModule(module);
        fields.add(businessHoursId);

        return fields;
    }

    public static List<FacilioField> getSingleDayBusinessHoursFields() {
        FacilioModule module = ModuleFactory.getSingleDayBusinessHourModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField childId = new FacilioField();
        childId.setName("childId");
        childId.setDataType(FieldType.NUMBER);
        childId.setColumnName("ID");
        childId.setModule(module);
        fields.add(childId);

        FacilioField parentId = new FacilioField();
        parentId.setName("parentId");
        parentId.setDataType(FieldType.NUMBER);
        parentId.setColumnName("PARENT_ID");
        parentId.setModule(module);
        fields.add(parentId);

        FacilioField dayOfWeek = new FacilioField();
        dayOfWeek.setName("dayOfWeek");
        dayOfWeek.setDataType(FieldType.NUMBER);
        dayOfWeek.setColumnName("DAY_OF_WEEK");
        dayOfWeek.setModule(module);
        fields.add(dayOfWeek);

        FacilioField startTime = new FacilioField();
        startTime.setName("startTime");
        startTime.setDataType(FieldType.MISC);
        startTime.setColumnName("START_TIME");
        startTime.setModule(module);
        fields.add(startTime);

        FacilioField endTime = new FacilioField();
        endTime.setName("endTime");
        endTime.setDataType(FieldType.MISC);
        endTime.setColumnName("END_TIME");
        endTime.setModule(module);
        fields.add(endTime);

        return fields;
    }

    public static List<FacilioField> getPublicFileFields() {

        FacilioModule module = ModuleFactory.getPublicFilesModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        fields.add(getField("fileId", "FILEID", module, FieldType.NUMBER));
//		fields.add(getField("key", "FILE_KEY", module, FieldType.STRING));
        fields.add(getField("expiresOn", "EXPIRES_ON", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getFileFields() {
        FacilioModule module = ModuleFactory.getFilesModule();
        List<FacilioField> fields = new ArrayList<>();
        
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));

        FacilioField fileName = new FacilioField();
        fileName.setName("fileName");
        fileName.setDataType(FieldType.STRING);
        fileName.setColumnName("FILE_NAME");
        fileName.setModule(module);
        fields.add(fileName);

        FacilioField fileSize = new FacilioField();
        fileSize.setName("fileSize");
        fileSize.setDataType(FieldType.NUMBER);
        fileSize.setColumnName("FILE_SIZE");
        fileSize.setModule(module);
        fields.add(fileSize);

        FacilioField uploadedBy = new FacilioField();
        uploadedBy.setName("uploadedBy");
        uploadedBy.setDataType(FieldType.NUMBER);
        uploadedBy.setColumnName("UPLOADED_BY");
        uploadedBy.setModule(module);
        fields.add(uploadedBy);

        FacilioField uploadedTime = new FacilioField();
        uploadedTime.setName("uploadedTime");
        uploadedTime.setDataType(FieldType.NUMBER);
        uploadedTime.setColumnName("UPLOADED_TIME");
        uploadedTime.setModule(module);
        fields.add(uploadedTime);

        FacilioField contentType = new FacilioField();
        contentType.setName("contentType");
        contentType.setDataType(FieldType.NUMBER);
        contentType.setColumnName("CONTENT_TYPE");
        contentType.setModule(module);
        fields.add(contentType);
        
        fields.add(getField("filePath", "FILE_PATH", module, FieldType.STRING));
        fields.add(getField("compressedFilePath", "COMPRESSED_FILE_PATH", module, FieldType.STRING));
        fields.add(getField("compressedFileSize", "COMPRESSED_FILE_SIZE", module, FieldType.NUMBER));

        fields.add(getField("fileId", "FILE_ID", module, FieldType.ID));
        fields.add(getField("isDeleted", "IS_DELETED", module, FieldType.BOOLEAN));
        fields.add(getField("deletedTime", "DELETED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("deletedBy", "DELETED_BY", module, FieldType.NUMBER));

        return fields;
    }
    
    public static List<FacilioField> getResizedFileFields() {
        FacilioModule module = ModuleFactory.getResizedFilesModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("fileId", "FILE_ID", module, FieldType.NUMBER));
        fields.add(getField("width", "WIDTH", module, FieldType.NUMBER));
        fields.add(getField("height", "HEIGHT", module, FieldType.NUMBER));
        fields.add(getField("filePath", "FILE_PATH", module, FieldType.STRING));
        fields.add(getField("fileSize", "FILE_SIZE", module, FieldType.NUMBER));
        fields.add(getField("contentType", "CONTENT_TYPE", module, FieldType.STRING));
        fields.add(getField("generatedTime", "GENERATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getZoneRelFields() {
        FacilioModule module = ModuleFactory.getZoneRelModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField zoneId = new FacilioField();
        zoneId.setName("zoneId");
        zoneId.setDataType(FieldType.NUMBER);
        zoneId.setColumnName("ZONE_ID");
        zoneId.setModule(module);
        fields.add(zoneId);

        FacilioField basespaceId = new FacilioField();
        basespaceId.setName("basespaceId");
        basespaceId.setDataType(FieldType.NUMBER);
        basespaceId.setColumnName("BASE_SPACE_ID");
        basespaceId.setModule(module);
        fields.add(basespaceId);

        FacilioField isImmediate = new FacilioField();
        isImmediate.setName("isImmediate");
        isImmediate.setDataType(FieldType.BOOLEAN);
        isImmediate.setColumnName("IS_IMMEDIATE");
        isImmediate.setModule(module);
        fields.add(isImmediate);

        return fields;
    }

    public static List<FacilioField> getControllerFields() {
        FacilioModule module = ModuleFactory.getControllerModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField());

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        FacilioField macAddr = new FacilioField();
        macAddr.setName("macAddr");
        macAddr.setDataType(FieldType.STRING);
        macAddr.setColumnName("MAC_ADDR");
        macAddr.setModule(module);
        fields.add(macAddr);
        fields.add(getDisplay_NameField(module));
        fields.add(getField("dataInterval", "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("batchesPerCycle", "BATCHES_PER_CYCLE", module, FieldType.NUMBER));
        fields.add(getField("agentId", AgentKeys.AGENT_ID, module, FieldType.NUMBER));
        fields.add(getWritableField(module));
        fields.add(getField("active", "ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("controllerType", "CONTROLLER_TYPE", module, FieldType.NUMBER));
        //fields.add(getField("destinationId", "DESTINATION_ID", module, FieldType.STRING));
        fields.add(getField("instanceNumber", "INSTANCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("ipAddress", "IP_ADDRESS", module, FieldType.STRING));
        fields.add(getField("networkNumber", "NETWORK_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("portNumber", "PORT_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("slaveId", "SLAVE_ID", module, FieldType.NUMBER));
        fields.add(getField("comPort", "COM_PORT", module, FieldType.STRING));


        fields.add(getField("availablePoints", "AVAILABLE_POINTS", module, FieldType.NUMBER));
        fields.add(getField("controllerPropsJsonStr", "CONTROLLER_PROPS", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("lastModifiedTime", "LAST_MODIFIED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("lastDataReceivedTime", "LAST_DATA_RECEIVED_TIME", module, FieldType.DATE_TIME));
        fields.add(getDeletedTimeField(module));
        return fields;
    }
//	public static FacilioField getControllerIdField() { return getOrgIdField(null);
//	}


    public static List<FacilioField> getControllerActivityFields() {
        FacilioModule module = ModuleFactory.getControllerActivityModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getField("controllerMacAddr", "CONTROLLER_MAC_ADDR", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("actualTime", "ACTUAL_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("recordTime", "RECORD_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getContollerActivityRecordsFields() {
        FacilioModule module = ModuleFactory.getControllerActivityRecordsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getField("currentRecords", "CURRENT_RECORDS", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getContollerActivityWatcherFields() {
        FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("recordTime", "RECORD_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("dataInterval", "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("level", "LEVEL", module, FieldType.NUMBER));
        fields.add(getField("completionStatus", "COMPLETION_STATUS", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getControllerBuildingRelFields() {
        FacilioModule module = ModuleFactory.getControllerBuildingRelModule();
        List<FacilioField> fields = new ArrayList<>();

        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        FacilioField buildingId = new FacilioField();
        buildingId.setName("buildingId");
        buildingId.setDataType(FieldType.NUMBER);
        buildingId.setColumnName("BUILDING_ID");
        buildingId.setModule(module);
        fields.add(buildingId);
        fields.add(getControllerIdField(module));
        return fields;
    }


    public static List<FacilioField> getPublishDataFields() {
        FacilioModule module = ModuleFactory.getPublishDataModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getControllerIdField(module));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("acknowledgeTime", "ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("responseAckTime", "RESPONSE_ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("pingAckTime", "PING_ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("responseJson", "RESPONSE_JSON", module, FieldType.STRING));
        fields.add(getField("command", "COMMAND", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getPublishMessageFields() {
        FacilioModule module = ModuleFactory.getPublishMessageModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getPublishMessageParentIdField(module));
        fields.add(getField("sentTime", "SENT_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("acknowledgeTime", "ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("responseAckTime", "RESPONSE_ACKNOWLEDGE_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("dataStr", "MSG_DATA", module, FieldType.STRING));
        fields.add(getField("responseJson", "RESPONSE_JSON", module, FieldType.STRING));
        return fields;
    }

    public static FacilioField getPublishMessageParentIdField(FacilioModule module) {
        return getField("parentId", "PARENT_ID", module, FieldType.LOOKUP);
    }

    public static FacilioField getControllerIdField(FacilioModule module) {
        FacilioField controllerId = new FacilioField();
        controllerId.setName(AgentConstants.CONTROLLER_ID);
        controllerId.setDataType(FieldType.NUMBER);
        controllerId.setColumnName("CONTROLLER_ID");
        if (module != null) {
            controllerId.setModule(module);
        }
        return controllerId;
    }

    public static List<FacilioField> getNotificationFields() {
        FacilioModule module = ModuleFactory.getNotificationModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getUserIdField(module));

        FacilioField notificationType = new FacilioField();
        notificationType.setName("notificationTypeVal");
        notificationType.setDataType(FieldType.NUMBER);
        notificationType.setColumnName("NOTIFICATION_TYPE");
        notificationType.setModule(module);
        fields.add(notificationType);

        FacilioField actorId = new FacilioField();
        actorId.setName("actorId");
        actorId.setDataType(FieldType.NUMBER);
        actorId.setColumnName("ACTOR_ID");
        actorId.setModule(module);
        fields.add(actorId);

        FacilioField info = new FacilioField();
        info.setName("info");
        info.setDataType(FieldType.STRING);
        info.setColumnName("INFO");
        info.setModule(module);
        fields.add(info);

        FacilioField isRead = new FacilioField();
        isRead.setName("isRead");
        isRead.setDataType(FieldType.BOOLEAN);
        isRead.setColumnName("IS_READ");
        isRead.setModule(module);
        fields.add(isRead);

        FacilioField readAt = new FacilioField();
        readAt.setName("readAt");
        readAt.setDataType(FieldType.NUMBER);
        readAt.setColumnName("READ_AT");
        readAt.setModule(module);
        fields.add(readAt);

        FacilioField isSeen = new FacilioField();
        isSeen.setName("isSeen");
        isSeen.setDataType(FieldType.BOOLEAN);
        isSeen.setColumnName("IS_SEEN");
        isSeen.setModule(module);
        fields.add(isSeen);

        FacilioField seenAt = new FacilioField();
        seenAt.setName("seenAt");
        seenAt.setDataType(FieldType.NUMBER);
        seenAt.setColumnName("SEEN_AT");
        seenAt.setModule(module);
        fields.add(seenAt);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(module);
        fields.add(createdTime);

        return fields;
    }

    public static List<FacilioField> getVirtualMeterRelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getVirtualMeterRelModule();

        FacilioField virtualMeterId = new FacilioField();
        virtualMeterId.setName("virtualMeterId");
        virtualMeterId.setDataType(FieldType.NUMBER);
        virtualMeterId.setColumnName("VIRTUAL_METER_ID");
        virtualMeterId.setModule(module);
        fields.add(virtualMeterId);

        FacilioField childMeterId = new FacilioField();
        childMeterId.setName("childMeterId");
        childMeterId.setDataType(FieldType.NUMBER);
        childMeterId.setColumnName("CHILD_METER_ID");
        childMeterId.setModule(module);
        fields.add(childMeterId);

        return fields;
    }

    public static List<FacilioField> getHistoricalVMCalculationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getHistoricalVMModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("intervalValue", "INTERVAL_VALUE", module, FieldType.NUMBER));
        fields.add(getField("updateReading", "UPDATE_READING", module, FieldType.BOOLEAN));
        fields.add(getField("runParentMeter", "RUN_PARENT_METER", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getHistoricalLoggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getHistoricalLoggerModule();

        fields.add(getIdField(module));
        fields.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("dependentId", "DEPENDENT_ID", module, FieldType.NUMBER));
        fields.add(getField("loggerGroupId", "LOGGER_GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWorkflowRuleHistoricalLoggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleHistoricalLoggerModule();

        fields.add(getIdField(module));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("loggerGroupId", "LOGGER_GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("alarmCount", "ALARM_COUNT", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFormulaFieldHistoricalLoggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormulaFieldHistoricalLoggerModule();

        fields.add(getIdField(module));
        fields.add(getField("parentId", "FORMULA_ID", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("dependentId", "DEPENDENT_FORMULA_ID", module, FieldType.NUMBER));
        fields.add(getField("loggerGroupId", "LOGGER_GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWorkflowRuleLoggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleLoggerModule();

        fields.add(getIdField(module));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
        fields.add(getField("noOfResources", "NO_OF_RESOURCES", module, FieldType.NUMBER));
        fields.add(getField("resolvedResourcesCount", "RESOLVED_RESOURCES_COUNT", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("totalAlarmCount", "TOTAL_ALARM_COUNT", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getWorkflowRuleResourceLoggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleResourceLoggerModule();

        fields.add(getIdField(module));
        fields.add(getField("parentRuleLoggerId", "PARENT_RULE_LOGGER_ID", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("alarmCount", "ALARM_COUNT", module, FieldType.NUMBER));
        fields.add(getField("modifiedStartTime", "MODIFIED_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("modifiedEndTime", "MODIFIED_END_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getWorkflowRuleHistoricalLogsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowRuleHistoricalLogsModule();

        fields.add(getIdField(module));
        fields.add(getField("parentRuleResourceId", "PARENT_RULE_RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("splitStartTime", "SPLIT_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("splitEndTime", "SPLIT_END_TIME", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("logState", "LOG_STATE", module, FieldType.NUMBER));
        fields.add(getField("errorMessage", "ERROR_MESSAGE", module, FieldType.STRING));
        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getDashboardFolderFields() {
        FacilioModule module = ModuleFactory.getDashboardFolderModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));
        fields.add(getField("parentFolderId", "PARENT_FOLDER_ID", module, FieldType.LOOKUP));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("displayOrder", "DISPLAY_ORDER", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getSpaceFilteredDashboardSettingsFields() {
        FacilioModule module = ModuleFactory.getSpaceFilteredDashboardSettingsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.LOOKUP));
        fields.add(getField("baseSpaceId", "BASESPACE_ID", module, FieldType.LOOKUP));
        fields.add(getField("mobileEnabled", "MOBILE_ENABLED", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getDashboardFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getDashboardModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        fields.add(getField("dashboardFolderId", "DASHBOARD_FOLDER_ID", module, FieldType.LOOKUP));

        FacilioField dashboardName = new FacilioField();
        dashboardName.setName("dashboardName");
        dashboardName.setDataType(FieldType.STRING);
        dashboardName.setColumnName("DASHBOARD_NAME");
        dashboardName.setModule(module);
        fields.add(dashboardName);

        FacilioField dashboardLinkName = new FacilioField();
        dashboardLinkName.setName("linkName");
        dashboardLinkName.setDataType(FieldType.STRING);
        dashboardLinkName.setColumnName("LINK_NAME");
        dashboardLinkName.setModule(module);
        fields.add(dashboardLinkName);

        FacilioField createdByUser = new FacilioField();
        createdByUser.setName("createdByUserId");
        createdByUser.setDataType(FieldType.NUMBER);
        createdByUser.setColumnName("CREATED_BY_USER_ID");
        createdByUser.setModule(module);
        fields.add(createdByUser);

        FacilioField publishStatus = new FacilioField();
        publishStatus.setName("publishStatus");
        publishStatus.setDataType(FieldType.NUMBER);
        publishStatus.setColumnName("PUBLISH_STATUS");
        publishStatus.setModule(module);
        fields.add(publishStatus);

        FacilioField dashboardUrl = new FacilioField();
        dashboardUrl.setName("dashboardUrl");
        dashboardUrl.setDataType(FieldType.STRING);
        dashboardUrl.setColumnName("DASHBOARD_URL");
        dashboardUrl.setModule(module);
        fields.add(dashboardUrl);

        FacilioField baseSpaceId = new FacilioField();
        baseSpaceId.setName("baseSpaceId");
        baseSpaceId.setDataType(FieldType.LOOKUP);
        baseSpaceId.setColumnName("BASE_SPACE_ID");
        baseSpaceId.setModule(module);
        fields.add(baseSpaceId);

        FacilioField displayOrder = new FacilioField();
        displayOrder.setName("displayOrder");
        displayOrder.setDataType(FieldType.NUMBER);
        displayOrder.setColumnName("DISPLAY_ORDER");
        displayOrder.setModule(module);
        fields.add(displayOrder);

        FacilioField showHideMobile = new FacilioField();
        showHideMobile.setName("mobileEnabled");
        showHideMobile.setDataType(FieldType.BOOLEAN);
        showHideMobile.setColumnName("SHOW_HIDE_MOBILE");
        showHideMobile.setModule(module);

        fields.add(showHideMobile);

        fields.add(getField("dateOperator", "DATE_OPERATOR", module, FieldType.NUMBER));
        fields.add(getField("dateValue", "DATE_VALUE", module, FieldType.STRING));
        fields.add(getField("tabEnabled", "IS_TAB_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("dashboardTabPlacement", "DASHBOARD_TAB_PLACEMENT", module, FieldType.NUMBER));
        fields.add(getField("clientMetaJsonString", "CLIENT_META_JSON", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getDashboardTabFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getDashboardTabModule();

        fields.add(getIdField(module));

        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.LOOKUP));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("dashboardTabId", "DASHBOARD_TAB_ID", module, FieldType.LOOKUP));
        fields.add(getField("sequence", "SEQUENCE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWidgetFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));
        fields.add(getField("widgetSettings", "WIDGET_SETTINGS_JSON", module, FieldType.STRING));
        fields.add(getField("metaJSONString", "META_JSON", module, FieldType.STRING));

        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.NUMBER));

        fields.add(getField("dashboardTabId", "DASHBOARD_TAB_ID", module, FieldType.NUMBER));

        FacilioField dashboardWidgetName = new FacilioField();
        dashboardWidgetName.setName("widgetName");
        dashboardWidgetName.setDataType(FieldType.STRING);
        dashboardWidgetName.setColumnName("WIDGET_NAME");
        dashboardWidgetName.setModule(module);
        fields.add(dashboardWidgetName);

        FacilioField dashboardWidgetType = new FacilioField();
        dashboardWidgetType.setName("type");
        dashboardWidgetType.setDataType(FieldType.NUMBER);
        dashboardWidgetType.setColumnName("TYPE");
        dashboardWidgetType.setModule(module);
        fields.add(dashboardWidgetType);

        FacilioField dashboardWidgetUrl = new FacilioField();
        dashboardWidgetUrl.setName("dataOptionDataUrl");
        dashboardWidgetUrl.setDataType(FieldType.STRING);
        dashboardWidgetUrl.setColumnName("WIDGET_URL");
        dashboardWidgetUrl.setModule(module);
        fields.add(dashboardWidgetUrl);

        FacilioField dashboardWidgetDataOptionRefreshIntervel = new FacilioField();
        dashboardWidgetDataOptionRefreshIntervel.setName("dataRefreshIntervel");
        dashboardWidgetDataOptionRefreshIntervel.setDataType(FieldType.NUMBER);
        dashboardWidgetDataOptionRefreshIntervel.setColumnName("DATA_REFRESH_INTERTVEL");
        dashboardWidgetDataOptionRefreshIntervel.setModule(module);
        fields.add(dashboardWidgetDataOptionRefreshIntervel);

        FacilioField dashboardWidgetHeaderTitle = new FacilioField();
        dashboardWidgetHeaderTitle.setName("headerText");
        dashboardWidgetHeaderTitle.setDataType(FieldType.STRING);
        dashboardWidgetHeaderTitle.setColumnName("HEADER_TEXT");
        dashboardWidgetHeaderTitle.setModule(module);
        fields.add(dashboardWidgetHeaderTitle);

        FacilioField dashboardWidgetHeaderSubTitle = new FacilioField();
        dashboardWidgetHeaderSubTitle.setName("headerSubText");
        dashboardWidgetHeaderSubTitle.setDataType(FieldType.STRING);
        dashboardWidgetHeaderSubTitle.setColumnName("HEADER_SUB_TEXT");
        dashboardWidgetHeaderSubTitle.setModule(module);
        fields.add(dashboardWidgetHeaderSubTitle);

        FacilioField dashboardWidgetHeaderIsExport = new FacilioField();
        dashboardWidgetHeaderIsExport.setName("headerIsExport");
        dashboardWidgetHeaderIsExport.setDataType(FieldType.BOOLEAN);
        dashboardWidgetHeaderIsExport.setColumnName("HEADER_IS_EXPORT");
        dashboardWidgetHeaderIsExport.setModule(module);
        fields.add(dashboardWidgetHeaderIsExport);

        return fields;
    }

    public static List<FacilioField> getWidgetChartFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetChartModule();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField chartType = new FacilioField();
        chartType.setName("chartType");
        chartType.setDataType(FieldType.NUMBER);
        chartType.setColumnName("CHART_TYPE");
        chartType.setModule(module);
        fields.add(chartType);

        FacilioField dateFilter = new FacilioField();
        dateFilter.setName("dateFilterId");
        dateFilter.setDataType(FieldType.NUMBER);
        dateFilter.setColumnName("DATE_FILTER");
        dateFilter.setModule(module);
        fields.add(dateFilter);

        fields.add(getField("newReportId", "NEW_REPORT_ID", module, FieldType.LOOKUP));
        fields.add(getField("reportTemplate", "REPORT_TEMPLATE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getWidgetListViewFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetListViewModule();

        fields.add(getIdField(module));

        FacilioField moduleName = new FacilioField();
        moduleName.setName("moduleName");
        moduleName.setDataType(FieldType.STRING);
        moduleName.setColumnName("MODULE_NAME");
        moduleName.setModule(module);
        fields.add(moduleName);

        FacilioField viewName = new FacilioField();
        viewName.setName("viewName");
        viewName.setDataType(FieldType.STRING);
        viewName.setColumnName("VIEW_NAME");
        viewName.setModule(module);
        fields.add(viewName);

        return fields;
    }

    public static List<FacilioField> getWidgetStaticFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetStaticModule();
        fields.add(getIdField(module));

        FacilioField staticKey = new FacilioField();
        staticKey.setName("staticKey");
        staticKey.setDataType(FieldType.STRING);
        staticKey.setColumnName("STATIC_KEY");
        staticKey.setModule(module);
        fields.add(staticKey);

        fields.add(getField("params", "PARAMS_JSON", module, FieldType.STRING));
        fields.add(getField("metaJson", "META_JSON", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getWidgetWebFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetWebModule();

        fields.add(getIdField(module));

        FacilioField webUrl = new FacilioField();
        webUrl.setName("webUrl");
        webUrl.setDataType(FieldType.STRING);
        webUrl.setColumnName("WEB_URL");
        webUrl.setModule(module);
        fields.add(webUrl);

        return fields;
    }

    public static List<FacilioField> getWidgetGraphicsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetGraphicsModule();

        fields.add(getIdField(module));

        FacilioField graphicsId = new FacilioField();
        graphicsId.setName("graphicsId");
        graphicsId.setDataType(FieldType.NUMBER);
        graphicsId.setColumnName("GRAPHICS_ID");
        graphicsId.setModule(module);
        fields.add(graphicsId);

        fields.add(getField("graphicsOptions", "GRAPHICS_OPTIONS", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getWidgetCardFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWidgetCardModule();

        fields.add(getIdField(module));

        fields.add(getField("cardLayout", "CARD_LAYOUT", module, FieldType.STRING));
        fields.add(getField("scriptModeInt", "SCRIPT_MODE", module, FieldType.NUMBER));
        fields.add(getField("customScriptId", "CUSTOM_SCRIPT_ID", module, FieldType.NUMBER));
        fields.add(getField("cardParamsJSON", "CARD_PARAMS", module, FieldType.STRING));
        fields.add(getField("cardStateJSON", "CARD_STATE", module, FieldType.STRING));
        fields.add(getField("conditionalFormattingJSON", "CARD_CONDITIONAL_FORMATTING", module, FieldType.STRING));
        fields.add(getField("cardDrilldownJSON", "CARD_DRILLDOWN", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getReportFolderFields() {

        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportFolder();

        fields.add(getIdField(module));
        fields.add(getModuleIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField parentFolderId = new FacilioField();
        parentFolderId.setName("parentFolderId");
        parentFolderId.setDataType(FieldType.NUMBER);
        parentFolderId.setColumnName("PARENT_FOLDER_ID");
        parentFolderId.setModule(module);
        fields.add(parentFolderId);

        FacilioField buildingId = new FacilioField();
        buildingId.setName("buildingId");
        buildingId.setDataType(FieldType.NUMBER);
        buildingId.setColumnName("BUILDING_ID");
        buildingId.setModule(module);
        fields.add(buildingId);

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        return fields;
    }

    public static List<FacilioField> getReportFields() {

        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReport();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        FacilioField parentFolderId = new FacilioField();
        parentFolderId.setName("parentFolderId");
        parentFolderId.setDataType(FieldType.NUMBER);
        parentFolderId.setColumnName("REPORT_FOLDER_ID");
        parentFolderId.setModule(module);
        fields.add(parentFolderId);

        FacilioField reportEntityId = new FacilioField();
        reportEntityId.setName("reportEntityId");
        reportEntityId.setDataType(FieldType.NUMBER);
        reportEntityId.setColumnName("REPORT_ENTITY_ID");
        reportEntityId.setModule(module);
        fields.add(reportEntityId);

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        FacilioField description = new FacilioField();
        description.setName("description");
        description.setDataType(FieldType.STRING);
        description.setColumnName("DESCRIPTION");
        description.setModule(module);
        fields.add(description);

        FacilioField chartType = new FacilioField();
        chartType.setName("chartType");
        chartType.setDataType(FieldType.NUMBER);
        chartType.setColumnName("CHART_TYPE");
        chartType.setModule(module);
        fields.add(chartType);

        FacilioField secChartType = new FacilioField();
        secChartType.setName("secChartType");
        secChartType.setDataType(FieldType.NUMBER);
        secChartType.setColumnName("SEC_CHART_TYPE");
        secChartType.setModule(module);
        fields.add(secChartType);

        FacilioField xAxis = new FacilioField();
        xAxis.setName("xAxis");
        xAxis.setDataType(FieldType.NUMBER);
        xAxis.setColumnName("X_AXIS");
        xAxis.setModule(module);
        fields.add(xAxis);

        FacilioField xAxisaggregateFunction = new FacilioField();
        xAxisaggregateFunction.setName("xAxisaggregateFunction");
        xAxisaggregateFunction.setDataType(FieldType.NUMBER);
        xAxisaggregateFunction.setColumnName("X_AGGREGATE_FUNCTION");
        xAxisaggregateFunction.setModule(module);
        fields.add(xAxisaggregateFunction);

        FacilioField xAxisLabel = new FacilioField();
        xAxisLabel.setName("xAxisLabel");
        xAxisLabel.setDataType(FieldType.STRING);
        xAxisLabel.setColumnName("X_AXIS_LABEL");
        xAxisLabel.setModule(module);
        fields.add(xAxisLabel);

        FacilioField xAxisUnit = new FacilioField();
        xAxisUnit.setName("xAxisUnit");
        xAxisUnit.setDataType(FieldType.STRING);
        xAxisUnit.setColumnName("X_AXIS_UNIT");
        xAxisUnit.setModule(module);
        fields.add(xAxisUnit);

        FacilioField y1Axis = new FacilioField();
        y1Axis.setName("y1Axis");
        y1Axis.setDataType(FieldType.NUMBER);
        y1Axis.setColumnName("Y1_AXIS");
        y1Axis.setModule(module);
        fields.add(y1Axis);

        FacilioField y1AxisaggregateFunction = new FacilioField();
        y1AxisaggregateFunction.setName("y1AxisaggregateFunction");
        y1AxisaggregateFunction.setDataType(FieldType.NUMBER);
        y1AxisaggregateFunction.setColumnName("Y1_AGGREGATE_FUNCTION");
        y1AxisaggregateFunction.setModule(module);
        fields.add(y1AxisaggregateFunction);

        FacilioField y1AxisLabel = new FacilioField();
        y1AxisLabel.setName("y1AxisLabel");
        y1AxisLabel.setDataType(FieldType.STRING);
        y1AxisLabel.setColumnName("Y1_AXIS_LABEL");
        y1AxisLabel.setModule(module);
        fields.add(y1AxisLabel);

        FacilioField y1AxisUnit = new FacilioField();
        y1AxisUnit.setName("y1AxisUnit");
        y1AxisUnit.setDataType(FieldType.STRING);
        y1AxisUnit.setColumnName("Y1_AXIS_UNIT");
        y1AxisUnit.setModule(module);
        fields.add(y1AxisUnit);

        FacilioField y2Axis = new FacilioField();
        y2Axis.setName("y2Axis");
        y2Axis.setDataType(FieldType.NUMBER);
        y2Axis.setColumnName("Y2_AXIS");
        y2Axis.setModule(module);
        fields.add(y2Axis);

        FacilioField y2AxisaggregateFunction = new FacilioField();
        y2AxisaggregateFunction.setName("y2AxisaggregateFunction");
        y2AxisaggregateFunction.setDataType(FieldType.NUMBER);
        y2AxisaggregateFunction.setColumnName("Y2_AGGREGATE_FUNCTION");
        y2AxisaggregateFunction.setModule(module);
        fields.add(y2AxisaggregateFunction);

        FacilioField y3Axis = new FacilioField();
        y3Axis.setName("y3Axis");
        y3Axis.setDataType(FieldType.NUMBER);
        y3Axis.setColumnName("Y3_AXIS");
        y3Axis.setModule(module);
        fields.add(y3Axis);

        FacilioField y3AxisaggregateFunction = new FacilioField();
        y3AxisaggregateFunction.setName("y3AxisaggregateFunction");
        y3AxisaggregateFunction.setDataType(FieldType.NUMBER);
        y3AxisaggregateFunction.setColumnName("Y3_AGGREGATE_FUNCTION");
        y3AxisaggregateFunction.setModule(module);
        fields.add(y3AxisaggregateFunction);

        FacilioField isComparisionReport = new FacilioField();
        isComparisionReport.setName("isComparisionReport");
        isComparisionReport.setDataType(FieldType.BOOLEAN);
        isComparisionReport.setColumnName("IS_COMPARISION_REPORT");
        isComparisionReport.setModule(module);
        fields.add(isComparisionReport);

        FacilioField isHighResolutionReport = new FacilioField();
        isHighResolutionReport.setName("isHighResolutionReport");
        isHighResolutionReport.setDataType(FieldType.BOOLEAN);
        isHighResolutionReport.setColumnName("IS_HIGHRESOLUTION_REPORT");
        isHighResolutionReport.setModule(module);
        fields.add(isHighResolutionReport);

        FacilioField isCombinationReport = new FacilioField();
        isCombinationReport.setName("isCombinationReport");
        isCombinationReport.setDataType(FieldType.BOOLEAN);
        isCombinationReport.setColumnName("IS_COMBINATION_REPORT");
        isCombinationReport.setModule(module);
        fields.add(isCombinationReport);

        FacilioField xAxisLegend = new FacilioField();
        xAxisLegend.setName("xAxisLegend");
        xAxisLegend.setDataType(FieldType.STRING);
        xAxisLegend.setColumnName("X_AXIS_LEGEND");
        xAxisLegend.setModule(module);
        fields.add(xAxisLegend);

        FacilioField groupBy = new FacilioField();
        groupBy.setName("groupBy");
        groupBy.setDataType(FieldType.NUMBER);
        groupBy.setColumnName("GROUP_BY");
        groupBy.setModule(module);
        fields.add(groupBy);

        FacilioField groupByLabel = new FacilioField();
        groupByLabel.setName("groupByLabel");
        groupByLabel.setDataType(FieldType.STRING);
        groupByLabel.setColumnName("GROUP_BY_LABEL");
        groupByLabel.setModule(module);
        fields.add(groupByLabel);

        FacilioField groupByUnit = new FacilioField();
        groupByUnit.setName("groupByUnit");
        groupByUnit.setDataType(FieldType.STRING);
        groupByUnit.setColumnName("GROUP_BY_UNIT");
        groupByUnit.setModule(module);
        fields.add(groupByUnit);

        FacilioField groupByFieldAggregateFunction = new FacilioField();
        groupByFieldAggregateFunction.setName("groupByFieldAggregateFunction");
        groupByFieldAggregateFunction.setDataType(FieldType.NUMBER);
        groupByFieldAggregateFunction.setColumnName("GROUP_BY_AGGREGATE_FUNCTION");
        groupByFieldAggregateFunction.setModule(module);
        fields.add(groupByFieldAggregateFunction);

        FacilioField limit = new FacilioField();
        limit.setName("limit");
        limit.setDataType(FieldType.NUMBER);
        limit.setColumnName("LLIMIT");
        limit.setModule(module);
        fields.add(limit);

        FacilioField orderBy = new FacilioField();
        orderBy.setName("orderBy");
        orderBy.setDataType(FieldType.STRING);
        orderBy.setColumnName("ORDER_BY");
        orderBy.setModule(module);
        fields.add(orderBy);

        FacilioField orderByFunction = new FacilioField();
        orderByFunction.setName("orderByFunction");
        orderByFunction.setDataType(FieldType.NUMBER);
        orderByFunction.setColumnName("ORDER_BY_FUNCTION");
        orderByFunction.setModule(module);
        fields.add(orderByFunction);

        FacilioField excludeBaseline = new FacilioField();
        excludeBaseline.setName("excludeBaseline");
        excludeBaseline.setDataType(FieldType.BOOLEAN);
        excludeBaseline.setColumnName("EXCLUDE_BASELINE");
        excludeBaseline.setModule(module);
        fields.add(excludeBaseline);

        FacilioField reportOrder = new FacilioField();
        reportOrder.setName("reportOrder");
        reportOrder.setDataType(FieldType.NUMBER);
        reportOrder.setColumnName("REPORT_ORDER");
        reportOrder.setModule(module);
        fields.add(reportOrder);

        FacilioField reportColor = new FacilioField();
        reportColor.setName("reportColor");
        reportColor.setDataType(FieldType.STRING);
        reportColor.setColumnName("REPORT_COLOR");
        reportColor.setModule(module);
        fields.add(reportColor);

        FacilioField isWorkRequestReport = new FacilioField();
        isWorkRequestReport.setName("isWorkRequestReport");
        isWorkRequestReport.setDataType(FieldType.BOOLEAN);
        isWorkRequestReport.setColumnName("IS_WORK_REQUEST_REPORT");
        isWorkRequestReport.setModule(module);
        fields.add(isWorkRequestReport);

        fields.add(getField("customReportClass", "CUSTOM_REPORT_CLASS", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getReportFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportField();

        fields.add(getIdField(module));

        FacilioField fieldLabel = new FacilioField();
        fieldLabel.setName("fieldLabel");
        fieldLabel.setDataType(FieldType.STRING);
        fieldLabel.setColumnName("FIELD_LABEL");
        fieldLabel.setModule(module);
        fields.add(fieldLabel);

        FacilioField unit = new FacilioField();
        unit.setName("unit");
        unit.setDataType(FieldType.STRING);
        unit.setColumnName("UNIT");
        unit.setModule(module);
        fields.add(unit);

        FacilioField moduleFieldId = new FacilioField();
        moduleFieldId.setName("moduleFieldId");
        moduleFieldId.setDataType(FieldType.NUMBER);
        moduleFieldId.setColumnName("MODULE_FIELD_ID");
        moduleFieldId.setModule(module);
        fields.add(moduleFieldId);

        FacilioField formulaFieldId = new FacilioField();
        formulaFieldId.setName("formulaFieldId");
        formulaFieldId.setDataType(FieldType.NUMBER);
        formulaFieldId.setColumnName("FORMULA_FIELD_ID");
        formulaFieldId.setModule(module);
        fields.add(formulaFieldId);

        return fields;

    }

    public static List<FacilioField> getReportDateFilterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportDateFilter();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.NUMBER);
        fieldId.setColumnName("FIELD_ID");
        fieldId.setModule(module);
        fields.add(fieldId);

        FacilioField operatorId = new FacilioField();
        operatorId.setName("operatorId");
        operatorId.setDataType(FieldType.NUMBER);
        operatorId.setColumnName("OPERATOR");
        operatorId.setModule(module);
        fields.add(operatorId);

        FacilioField value = new FacilioField();
        value.setName("value");
        value.setDataType(FieldType.STRING);
        value.setColumnName("VAL");
        value.setModule(module);
        fields.add(value);

        FacilioField startTime = new FacilioField();
        startTime.setName("startTime");
        startTime.setDataType(FieldType.NUMBER);
        startTime.setColumnName("START_TIME");
        startTime.setModule(module);
        fields.add(startTime);

        FacilioField endTime = new FacilioField();
        endTime.setName("endTime");
        endTime.setDataType(FieldType.NUMBER);
        endTime.setColumnName("END_TIME");
        endTime.setModule(module);
        fields.add(endTime);

        return fields;
    }

    public static List<FacilioField> getReportEnergyMeterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportEnergyMeter();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField serviceId = new FacilioField();
        serviceId.setName("serviceId");
        serviceId.setDataType(FieldType.NUMBER);
        serviceId.setColumnName("SERVICE_ID");
        serviceId.setModule(module);
        fields.add(serviceId);

        FacilioField subMeterId = new FacilioField();
        subMeterId.setName("subMeterId");
        subMeterId.setDataType(FieldType.NUMBER);
        subMeterId.setColumnName("SUB_METER_ID");
        subMeterId.setModule(module);
        fields.add(subMeterId);

        FacilioField groupBy = new FacilioField();
        groupBy.setName("groupBy");
        groupBy.setDataType(FieldType.STRING);
        groupBy.setColumnName("GROUP_BY");
        groupBy.setModule(module);
        fields.add(groupBy);

        return fields;
    }

    public static List<FacilioField> getReportSpaceFilterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportSpaceFilter();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField dashboardId = new FacilioField();
        dashboardId.setName("dashboardId");
        dashboardId.setDataType(FieldType.NUMBER);
        dashboardId.setColumnName("DASHBOARD_ID");
        dashboardId.setModule(module);
        fields.add(dashboardId);

        FacilioField siteId = new FacilioField();
        siteId.setName("siteId");
        siteId.setDataType(FieldType.NUMBER);
        siteId.setColumnName("SITE_ID");
        siteId.setModule(module);
        fields.add(siteId);

        FacilioField buildingId = new FacilioField();
        buildingId.setName("buildingId");
        buildingId.setDataType(FieldType.NUMBER);
        buildingId.setColumnName("BUILDING_ID");
        buildingId.setModule(module);
        fields.add(buildingId);

        FacilioField floorId = new FacilioField();
        floorId.setName("floorId");
        floorId.setDataType(FieldType.NUMBER);
        floorId.setColumnName("FLOOR_ID");
        floorId.setModule(module);
        fields.add(floorId);

        fields.add(getField("chillerId", "CHILLER_ID", module, FieldType.NUMBER));

        FacilioField groupBy = new FacilioField();
        groupBy.setName("groupBy");
        groupBy.setDataType(FieldType.STRING);
        groupBy.setColumnName("GROUP_BY");
        groupBy.setModule(module);
        fields.add(groupBy);

        return fields;
    }

    public static List<FacilioField> getReportFormulaFieldFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportFormulaField();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        FacilioField dataType = new FacilioField();
        dataType.setName("dataType");
        dataType.setDataType(FieldType.NUMBER);
        dataType.setColumnName("DATA_TYPE");
        dataType.setModule(module);
        fields.add(dataType);

        FacilioField formula = new FacilioField();
        formula.setName("formula");
        formula.setDataType(FieldType.STRING);
        formula.setColumnName("FORMULA");
        formula.setModule(module);
        fields.add(formula);

        return fields;
    }

    public static List<FacilioField> getReportCriteriaFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportCriteria();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField criteriaId = new FacilioField();
        criteriaId.setName("criteriaId");
        criteriaId.setDataType(FieldType.NUMBER);
        criteriaId.setColumnName("CRITERIA_ID");
        criteriaId.setModule(module);
        fields.add(criteriaId);

        return fields;
    }

    public static List<FacilioField> getReportThresholdFields() {

        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportThreshold();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(module);
        fields.add(name);

        FacilioField value = new FacilioField();
        value.setName("value");
        value.setDataType(FieldType.NUMBER);
        value.setColumnName("VALUE");
        value.setModule(module);
        fields.add(value);

        FacilioField color = new FacilioField();
        color.setName("color");
        color.setDataType(FieldType.STRING);
        color.setColumnName("COLOR");
        color.setModule(module);
        fields.add(color);

        FacilioField lineStyle = new FacilioField();
        lineStyle.setName("lineStyle");
        lineStyle.setDataType(FieldType.NUMBER);
        lineStyle.setColumnName("LINE_STYLE");
        lineStyle.setModule(module);
        fields.add(lineStyle);

        return fields;
    }

    public static List<FacilioField> getReportUserFilterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReportUserFilter();

        fields.add(getIdField(module));

        FacilioField reportId = new FacilioField();
        reportId.setName("reportId");
        reportId.setDataType(FieldType.NUMBER);
        reportId.setColumnName("REPORT_ID");
        reportId.setModule(module);
        fields.add(reportId);

        FacilioField reportFieldId = new FacilioField();
        reportFieldId.setName("reportFieldId");
        reportFieldId.setDataType(FieldType.NUMBER);
        reportFieldId.setColumnName("REPORT_FIELD_ID");
        reportFieldId.setModule(module);
        fields.add(reportFieldId);

        FacilioField whereClause = new FacilioField();
        whereClause.setName("whereClause");
        whereClause.setDataType(FieldType.STRING);
        whereClause.setColumnName("WHERE_CLAUSE");
        whereClause.setModule(module);
        fields.add(whereClause);

        return fields;
    }

    public static List<FacilioField> getFormulaFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormulaModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        FacilioField selectFieldId = new FacilioField();
        selectFieldId.setName("selectFieldId");
        selectFieldId.setDataType(FieldType.NUMBER);
        selectFieldId.setColumnName("SELECT_FIELD_ID");
        selectFieldId.setModule(module);
        fields.add(selectFieldId);

        FacilioField aggregateOperation = new FacilioField();
        aggregateOperation.setName("aggregateOperationValue");
        aggregateOperation.setDataType(FieldType.NUMBER);
        aggregateOperation.setColumnName("AGGREGATE_OPERATION");
        aggregateOperation.setModule(module);
        fields.add(aggregateOperation);

        FacilioField criteriaId = new FacilioField();
        criteriaId.setName("criteriaId");
        criteriaId.setDataType(FieldType.NUMBER);
        criteriaId.setColumnName("CRITERIA_ID");
        criteriaId.setModule(module);
        fields.add(criteriaId);

        return fields;

    }

    public static List<FacilioField> getExpressionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getExpressionModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField selectFieldId = new FacilioField();
        selectFieldId.setName("expressionString");
        selectFieldId.setDataType(FieldType.STRING);
        selectFieldId.setColumnName("EXPRESSION_STRING");
        selectFieldId.setModule(module);
        fields.add(selectFieldId);

        return fields;
    }

    public static List<FacilioField> getWorkflowFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowModule();

        fields.add(getIdField(module));

        fields.add(getField("workflowString", "WORKFLOW_XML_STRING", module, FieldType.STRING));
        fields.add(getField("workflowV2String", "WORKFLOW_STRING", module, FieldType.STRING));

        fields.add(getField("workflowUIMode", "UI_MODE", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("returnType", "RETURN_TYPE", module, FieldType.NUMBER));
        fields.add(getField("isLogNeeded", "IS_LOG_NEEDED", module, FieldType.BOOLEAN));
        fields.add(getField("isV2Script", "IS_V2", module, FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getWorkflowUserFunctionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();

        fields.add(getIdField(module));

        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("nameSpaceId", "NAMESPACE_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getWorkflowNamespaceFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();

        fields.add(getIdField(module));

        fields.add(getField("name", "NAME", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getWorkflowFieldsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowFieldModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));

        FacilioField workflowId = new FacilioField();
        workflowId.setName("workflowId");
        workflowId.setDataType(FieldType.NUMBER);
        workflowId.setColumnName("WORKFLOW_ID");
        workflowId.setModule(module);
        fields.add(workflowId);

        FacilioField fieldId = new FacilioField();
        fieldId.setName("fieldId");
        fieldId.setDataType(FieldType.NUMBER);
        fieldId.setColumnName("FIELD_ID");
        fieldId.setModule(module);
        fields.add(fieldId);

        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("aggregation", "AGGREGATION", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWorkflowLogFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWorkflowLogModule();

        fields.add(getIdField(module));

        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("executionTime", "EXECUTION_TIME", module, FieldType.NUMBER));
        fields.add(getField("input", "INPUT_PARAMS", module, FieldType.STRING));
        fields.add(getField("variableMap", "VARIABLE_MAP", module, FieldType.STRING));
        fields.add(getField("result", "RESULT", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getPMResourceScheduleRuleRelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPMResourceScheduleRuleRelModule();
        fields.add(getIdField(module));

        FacilioField pmId = new FacilioField();
        pmId.setName("pmId");
        pmId.setDataType(FieldType.NUMBER);
        pmId.setColumnName("PM_ID");
        pmId.setModule(module);
        fields.add(pmId);

        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));


        FacilioField postScheduleRuleId = new FacilioField();
        postScheduleRuleId.setName("scheduleRuleId");
        postScheduleRuleId.setDataType(FieldType.NUMBER);
        postScheduleRuleId.setColumnName("SCHEDULE_RULE_ID");
        postScheduleRuleId.setModule(module);
        fields.add(postScheduleRuleId);

        return fields;
    }

    public static List<FacilioField> getPMReminderFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPMReminderModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField pmId = new FacilioField();
        pmId.setName("pmId");
        pmId.setDataType(FieldType.NUMBER);
        pmId.setColumnName("PM_ID");
        pmId.setModule(module);
        fields.add(pmId);

        fields.add(getField("name", "NAME", FieldType.STRING));

        FacilioField type = new FacilioField();
        type.setName("type");
        type.setDataType(FieldType.NUMBER);
        type.setColumnName("REMINDER_TYPE");
        type.setModule(module);
        fields.add(type);

        FacilioField duration = new FacilioField();
        duration.setName("duration");
        duration.setDataType(FieldType.NUMBER);
        duration.setColumnName("DURATION");
        duration.setModule(module);
        fields.add(duration);

        FacilioField postScheduleRuleId = new FacilioField();
        postScheduleRuleId.setName("scheduleRuleId");
        postScheduleRuleId.setDataType(FieldType.NUMBER);
        postScheduleRuleId.setColumnName("SCHEDULE_RULE_ID");
        postScheduleRuleId.setModule(module);
        fields.add(postScheduleRuleId);

        return fields;
    }

    public static List<FacilioField> getPMReminderActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPMReminderActionModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("reminderId", "PM_REMINDER_ID", module, FieldType.LOOKUP));
        fields.add(getField("actionId", "ACTION_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getViewSortColumnFields() {
        FacilioModule module = ModuleFactory.getViewSortColumnsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/

        FacilioField viewID = new FacilioField();
        viewID.setName("viewId");
        viewID.setDataType(FieldType.NUMBER);
        viewID.setColumnName("VIEWID");
        viewID.setModule(module);
        fields.add(viewID);

        FacilioField field = new FacilioField();
        field.setName("fieldId");
        field.setDataType(FieldType.NUMBER);
        field.setColumnName("FIELDID");
        field.setModule(module);
        fields.add(field);

        FacilioField fieldName = new FacilioField();
        fieldName.setName("fieldName");
        fieldName.setDataType(FieldType.STRING);
        fieldName.setColumnName("FIELDNAME");
        fieldName.setModule(module);
        fields.add(fieldName);

        FacilioField orderType = new FacilioField();
        orderType.setName("isAscending");
        orderType.setDataType(FieldType.BOOLEAN);
        orderType.setColumnName("IS_ASCENDING");
        orderType.setModule(module);
        fields.add(orderType);

        return fields;
    }

    public static List<FacilioField> getViewColumnFields() {
        FacilioModule module = ModuleFactory.getViewColumnsModule();
        List<FacilioField> fields = new ArrayList<>();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setDataType(FieldType.NUMBER);
        id.setColumnName("ID");
        id.setModule(module);
        fields.add(id);

        FacilioField view = new FacilioField();
        view.setName("viewId");
        view.setDataType(FieldType.NUMBER);
        view.setColumnName("VIEWID");
        view.setModule(module);
        fields.add(view);

        FacilioField field = new FacilioField();
        field.setName("fieldId");
        field.setDataType(FieldType.NUMBER);
        field.setColumnName("FIELDID");
        field.setModule(module);
        fields.add(field);

        FacilioField displayName = new FacilioField();
        displayName.setName("columnDisplayName");
        displayName.setDataType(FieldType.STRING);
        displayName.setColumnName("DISPLAY_NAME");
        displayName.setModule(module);
        fields.add(displayName);

        FacilioField fieldName = new FacilioField();
        fieldName.setName("fieldName");
        fieldName.setDataType(FieldType.STRING);
        fieldName.setColumnName("FIELDNAME");
        fieldName.setModule(module);
        fields.add(fieldName);

        FacilioField parentFieldId = new FacilioField();
        parentFieldId.setName("parentFieldId");
        parentFieldId.setDataType(FieldType.NUMBER);
        parentFieldId.setColumnName("PARENT_FIELDID");
        parentFieldId.setModule(module);
        fields.add(parentFieldId);

        return fields;
    }

    public static List<FacilioField> getAfterPMReminderWORelFields() {
        FacilioModule module = ModuleFactory.getAfterPMRemindersWORelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        FacilioField pmJobId = new FacilioField();
        pmJobId.setName("pmReminderId");
        pmJobId.setDataType(FieldType.NUMBER);
        pmJobId.setColumnName("PM_REMINDER_ID");
        pmJobId.setModule(module);
        fields.add(pmJobId);

        FacilioField woId = new FacilioField();
        woId.setName("woId");
        woId.setDataType(FieldType.NUMBER);
        woId.setColumnName("WO_ID");
        woId.setModule(module);
        fields.add(woId);

        return fields;
    }

    public static List<FacilioField> getPMTriggerFields() {
        FacilioModule module = ModuleFactory.getPMTriggersModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("pmId", "PM_ID", module, FieldType.NUMBER));
        fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));
        fields.add(getField("startTime", "START_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
        fields.add(getField("frequency", "FREQUENCY", module, FieldType.NUMBER));
        fields.add(getField("assignedTo", "ASSIGNED_TO", module, FieldType.LOOKUP));
        fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
        fields.add(getField("triggerExecutionSource", "TRIGGER_EXECUTION_SOURCE", module, FieldType.NUMBER));
        fields.add(getField("custom", "IS_CUSTOM", module, FieldType.BOOLEAN));
        fields.add(getField("executeOn", "EXECUTE_ON", module, FieldType.NUMBER));
        fields.add(getField("executionOffset", "EXECUTION_OFFSET", module, FieldType.NUMBER));
        fields.add(getField("customModuleId", "CUSTOM_MODULEID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "DATE_FIELDID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getPMResourcePlannerFields() {
        FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("pmId", "PM_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("assignedToId", "ASSIGNED_TO", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getPMResourcePlannerTriggersFields() {
        FacilioModule module = ModuleFactory.getPMResourcePlannerTriggersModule();
        List<FacilioField> fields = new ArrayList<>();

//		fields.add(getOrgIdField(module));
        fields.add(getField("triggerId", "TRIGGER_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourcePlannerId", "PM_RESOURCE_PLANNER_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getPMResourcePlannerReminderFields() {
        FacilioModule module = ModuleFactory.getPMResourcePlannerReminderModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("pmId", "PM_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourcePlannerId", "PM_RESOURCE_PLANNER_ID", module, FieldType.LOOKUP));
        fields.add(getField("reminderId", "PM_REMAINDER_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getBeforePMRemindersTriggerRelFields() {
        FacilioModule module = ModuleFactory.getBeforePMRemindersTriggerRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("pmReminderId", "PM_REMINDER_ID", module, FieldType.NUMBER));
        fields.add(getField("pmTriggerId", "PM_TRIGGER_ID", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getTaskInputOptionsFields() {
        FacilioModule module = ModuleFactory.getTaskInputOptionModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("taskId", "TASK_ID", module, FieldType.STRING));
        fields.add(getField("option", "OPTION_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getTaskSectionFields() {
        FacilioModule module = ModuleFactory.getTaskSectionModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("parentTicketId", "PARENT_TICKET_ID", module, FieldType.NUMBER));
        fields.add(getField("isEditable", "IS_EDITABLE", module, FieldType.NUMBER));
        fields.add(getField("sequenceNumber", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("preRequest", "IS_PRE_REQUEST", module, FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getAlarmEntityFields() {
        FacilioModule module = ModuleFactory.getAlarmEntityModule();
        List<FacilioField> fields = new ArrayList<>();

        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("entityId", "ENTITY_ID", module, FieldType.ID));

        return fields;
    }

    public static List<FacilioField> getReportEntityFields() {
        FacilioModule module = ModuleFactory.getReportEntityModule();
        List<FacilioField> fields = new ArrayList<>();

        /*fields.add(getOrgIdField(module));*/
        fields.add(getIdField(module));

        return fields;
    }

    public static List<FacilioField> getAssignmentTemplateFields() {
        FacilioModule module = ModuleFactory.getAssignmentTemplatesModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("assignedUserId", "USERID", module, FieldType.NUMBER));
        fields.add(getField("assignedGroupId", "GROUPID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getSlaTemplateFields() {
        FacilioModule module = ModuleFactory.getSlaTemplatesModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("slaPolicyJsonStr", "SLAPOLICYJSON", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getViewScheduleInfoFields() {
        FacilioModule module = ModuleFactory.getViewScheduleInfoModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("viewId", "VIEWID", module, FieldType.NUMBER));
        fields.add(getField("fileFormat", "FILE_FORMAT", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));
        fields.add(getField("moduleID", "MODULEID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReportScheduleInfoFields() {
        FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("reportId", "REPORTID", module, FieldType.NUMBER));
        fields.add(getField("fileFormat", "FILE_FORMAT", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReportScheduleInfo1Fields() {
        FacilioModule module = ModuleFactory.getReportScheduleInfo();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getModuleIdField(module));
        fields.add(getField("reportId", "REPORTID", module, FieldType.NUMBER));
        fields.add(getField("fileFormat", "FILE_FORMAT", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));
        fields.add(getField("printParams", "PRINT_PARAMS", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getBaseLineFields() {
        FacilioModule module = ModuleFactory.getBaseLineModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getNameField(module));
        fields.add(getField("spaceId", "SPACE_ID", module, FieldType.NUMBER));
        fields.add(getField("rangeType", "RANGE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.DATE));
        fields.add(getField("endTime", "END_TIME", module, FieldType.DATE));

        return fields;
    }

    public static List<FacilioField> getBaseLineReportsRelFields() {
        FacilioModule module = ModuleFactory.getBaseLineReportRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "BASE_LINE_ID", module, FieldType.NUMBER));
        fields.add(getField("reportId", "REPORT_ID", module, FieldType.NUMBER));
        fields.add(getField("adjustType", "ADJUST_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReadingDataMetaFields() {
        FacilioModule module = ModuleFactory.getReadingDataMetaModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("value", "VALUE", module, FieldType.STRING));
        fields.add(getField("readingDataId", "READING_DATA_ID", module, FieldType.NUMBER));
        fields.add(getField("inputType", "INPUT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("readingType", "READING_TYPE", module, FieldType.NUMBER));
        fields.add(getField("custom", "IS_CUSTOM", module, FieldType.BOOLEAN));
        fields.add(getField("unit", "UNIT", module, FieldType.NUMBER));
        fields.add(getField("isControllable", "IS_CONTROLLABLE", module, FieldType.BOOLEAN));
        fields.add(getField("controlActionMode", "CONTROL_MODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getResetCounterMetaFields() {
        FacilioModule module = ModuleFactory.getResetCounterMetaModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("readingDataId", "READING_DATA_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("startvalue", "START_VALUE", module, FieldType.NUMBER));
        fields.add(getField("endvalue", "END_VALUE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getReadingInputValuesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getReadingInputValuesModule();

        fields.add(getField("rdmId", "RDMID", module, FieldType.NUMBER));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("idx", "IDX", module, FieldType.NUMBER));
        fields.add(getField("inputValue", "INPUT_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getWOrderTemplateFields() {
        FacilioModule module = ModuleFactory.getWorkOrderTemplateModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("subject", "SUBJECT", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));

        LookupField statusField = (LookupField) getField("statusId", "STATUS_ID", module, FieldType.LOOKUP);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        fields.add(statusField);

        LookupField priorityField = (LookupField) getField("priorityId", "PRIORITY_ID", module, FieldType.LOOKUP);
        priorityField.setLookupModule(ModuleFactory.getTicketPriorityModule());
        fields.add(priorityField);

        LookupField categoryField = (LookupField) getField("categoryId", "CATEGORY_ID", module, FieldType.LOOKUP);
        categoryField.setLookupModule(ModuleFactory.getTicketCategoryModule());
        fields.add(categoryField);

        LookupField typeField = (LookupField) getField("typeId", "TYPE_ID", module, FieldType.LOOKUP);
        typeField.setLookupModule(ModuleFactory.getTicketTypeModule());
        fields.add(typeField);

        LookupField groupField = (LookupField) getField("assignmentGroupId", "ASSIGNMENT_GROUP_ID", module,
                FieldType.LOOKUP);
        groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
        fields.add(groupField);

        LookupField userField = (LookupField) getField("assignedToId", "ASSIGNED_TO_ID", module, FieldType.LOOKUP);
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(userField);

        fields.add(getField("qrEnabled", "QR_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.STRING));
        fields.add(getField("duration", "DURATION", module, FieldType.NUMBER));
        fields.add(getField("additionalInfoJsonStr", "ADDITIONAL_INFO", module, FieldType.STRING));

        fields.add(getField("estimatedWorkDuration", "ESTIMATED_WORK_DURATION", module, FieldType.NUMBER));
        fields.add(getField("woCreationOffset", "WO_CREATION_OFFSET", module, FieldType.NUMBER));
        fields.add(getField("workPermitNeeded", "WORK_PERMIT_NEEDED", module, FieldType.BOOLEAN));
        fields.add(getField("sendForApproval", "SEND_FOR_APPROVAL", module, FieldType.BOOLEAN));

        fields.add(getSiteIdField(module));

        return fields;
    }

    public static List<FacilioField> getTaskSectionTemplateFields() {
        FacilioModule module = ModuleFactory.getTaskSectionTemplateModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("isEditable", "IS_EDITABLE", module, FieldType.BOOLEAN));
        fields.add(getField("parentWOTemplateId", "PARENT_WO_TEMPLATE_ID", module, FieldType.LOOKUP));
        fields.add(getField("jobPlanId", "JOB_PLAN_ID", module, FieldType.LOOKUP));
        fields.add(getField("sequenceNumber", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("spaceCategoryId", "SPACE_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("assignmentType", "ASSIGNMENT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("inputType", "INPUT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("attachmentRequired", "ATTACHMENT_REQUIRED", module, FieldType.BOOLEAN));
        fields.add(getField("additionalInfoJsonStr", "ADDITIONAL_INFO", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getPrerequisiteApproversTemplateFields() {
        FacilioModule module = ModuleFactory.getPrerequisiteApproverTemplateModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("parentId", "PARENT_WO_TEMPLATE_ID", module, FieldType.LOOKUP));
        fields.add(getField("userId", "ORG_USERID", module, FieldType.LOOKUP));
        fields.add(getField("roleId", "ROLE_ID", module, FieldType.LOOKUP));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("sharingType", "SHARING_TYPE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getTaskSectionTemplateTriggersFields() {
        FacilioModule module = ModuleFactory.getTaskSectionTemplateTriggersModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("sectionId", "SECTION_ID", module, FieldType.NUMBER));
        fields.add(getField("triggerId", "PM_TRIGGER_ID", module, FieldType.NUMBER));
        fields.add(getField("executeIfNotInTime", "EXECUTE_IF_NOT_IN_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getPMIncludeExcludeResourceFields() {
        FacilioModule module = ModuleFactory.getPMIncludeExcludeResourceModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("pmId", "PM_ID", module, FieldType.LOOKUP));
        fields.add(getField("parentType", "PARENT_TYPE", module, FieldType.NUMBER));

        fields.add(getField("taskSectionTemplateId", "TASK_SECTION_TEMPLATE_ID", module, FieldType.LOOKUP));
        fields.add(getField("taskTemplateId", "TASK_TEMPLATE_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("isInclude", "IS_INCLUDE", module, FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getTaskTemplateFields() {
        FacilioModule module = ModuleFactory.getTaskTemplateModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("priorityId", "PRIORITY_ID", module, FieldType.LOOKUP));
        fields.add(getField("categoryId", "CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("typeId", "TYPE_ID", module, FieldType.LOOKUP));
        fields.add(getField("assignmentGroupId", "ASSIGNMENT_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("assignedToId", "ASSIGNED_TO_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("duration", "DURATION", module, FieldType.LOOKUP));
        fields.add(getField("parentTemplateId", "PARENT_WO_TEMPLATE_ID", module, FieldType.LOOKUP));
        fields.add(getField("jobPlanId", "JOB_PLAN_ID", module, FieldType.LOOKUP));
        fields.add(getField("inputType", "INPUT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("readingFieldId", "READING_ID", module, FieldType.LOOKUP));
        fields.add(getField("sectionId", "SECTION_ID", module, FieldType.LOOKUP));
        fields.add(getField("sequence", "SEQUENCE", module, FieldType.LOOKUP));
        fields.add(getField("attachmentRequiredInt", "ATTACHMENT_REQUIRED", module, FieldType.NUMBER));
        fields.add(getField("additionalInfoJsonStr", "ADDITIONAL_INFO", module, FieldType.STRING));
        fields.add(getField("assignmentType", "ASSIGNMENT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("spaceCategoryId", "SPACE_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getSiteIdField(module));

        return fields;
    }

    public static List<FacilioField> getResourceReadingsFields() {
        FacilioModule module = ModuleFactory.getResourceReadingsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("readingId", "READING_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getKPICategoryFields() {
        FacilioModule module = ModuleFactory.getKPICategoryModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getNameField(module));

        return fields;
    }

    public static List<FacilioField> getFormulaFieldFields() {
        FacilioModule module = ModuleFactory.getFormulaFieldModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getSiteIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("formulaFieldType", "FORMULA_FIELD_TYPE", module, FieldType.NUMBER));
        fields.add(getField("kpiCategory", "KPI_CATEGORY", module, FieldType.LOOKUP));
        fields.add(getField("violationRuleId", "VIOLATION_RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("minTarget", "MIN_TARGET", module, FieldType.DECIMAL));
        fields.add(getField("target", "TARGET", module, FieldType.DECIMAL));
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
        fields.add(getField("frequency", "FREQUENCY_TYPE", module, FieldType.NUMBER));
        fields.add(getField("interval", "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("readingFieldId", "READING_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceType", "RESOURCE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("spaceCategoryId", "SPACE_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("active", "ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("modifiedTime", "MODIFIED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFormulaFieldInclusionsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormulaFieldInclusionsModule();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("formulaId", "FORMULA_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getFormulaFieldResourceJobFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFormulaFieldResourceJobModule();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("formulaId", "FORMULA_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("startTime", "START_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("endTime", "END_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("isSystem", "IS_SYSTEM", module, FieldType.BOOLEAN));
        fields.add(getField("historicalAlarm", "CALCULATE_HISTORICAL_ALARM", module, FieldType.BOOLEAN));
        fields.add(getField("skipOptimisedWorkflow", "SKIP_OPTIMISED_WORKFLOW", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getKPIFields() {
        FacilioModule module = ModuleFactory.getKpiModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("metricId", "METRIC_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("metricName", "METRIC_NAME", module, FieldType.STRING));
        fields.add(getField("kpiCategory", "KPI_CATEGORY", module, FieldType.LOOKUP));
        fields.add(getField("minTarget", "MIN_TARGET", module, FieldType.DECIMAL));
        fields.add(getField("target", "TARGET", module, FieldType.DECIMAL));
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.LOOKUP));
        fields.add(getField("dateFieldId", "DATE_FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("dateOperator", "DATE_OPERATOR", module, FieldType.NUMBER));
        fields.add(getField("dateValue", "DATE_VALUE", module, FieldType.STRING));
        fields.add(getField("aggr", "AGGREGATION", module, FieldType.NUMBER));
        fields.add(getField("active", "ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("modifiedTime", "MODIFIED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReadingRuleFlapsFields() {
        FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.LOOKUP));
        fields.add(getField("flapTime", "FLAP_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getServicePortalFields() {
        FacilioModule module = ModuleFactory.getServicePortalModule();
        List<FacilioField> fields = new ArrayList<>();
        /*fields.add(getOrgIdField(module));*/
        fields.add(getField("portalId", "PORTALID", module, FieldType.ID));
        fields.add(getField("portalType", "PORTALTYPE", module, FieldType.NUMBER));
        fields.add(getField("signup_allowed", "SIGNUP_ALLOWED", module, FieldType.BOOLEAN));
        fields.add(getField("gmailLogin_allowed", "GMAILLOGIN_ALLOWED", module, FieldType.BOOLEAN));
        fields.add(getField("is_public_create_allowed", "IS_PUBLIC_CREATE_ALLOWED", module, FieldType.BOOLEAN));
        fields.add(getField("is_anyDomain_allowed", "IS_ANYDOMAIN_ALLOWED", module, FieldType.BOOLEAN));
        fields.add(getField("captcha_enabled", "CAPTCHA_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("customDomain", "CUSTOM_DOMAIN", module, FieldType.STRING));
        fields.add(getField("whiteListed_domains", "WHITELISTED_DOMAINS", module, FieldType.STRING));
        fields.add(getField("saml_enabled", "SAML_ENABLED", module, FieldType.BOOLEAN));
        fields.add(getField("login_url", "LOGIN_URL", module, FieldType.STRING));
        fields.add(getField("logout_url", "LOGOUT_URL", module, FieldType.STRING));
        fields.add(getField("password_url", "PASSWORD_URL", module, FieldType.STRING));
        fields.add(getField("publicKey", "PUBLICKEY", module, FieldType.NUMBER));
        fields.add(getField("algorithm", "ALGORITHM", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getCalendarColorFields() {
        FacilioModule module = ModuleFactory.getCalendarColorModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        /*fields.add(getOrgIdField(module));*/
        // fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
        fields.add(getField("basedOn", "BASED_ON", module, FieldType.STRING));

        return fields;
    }


    public static List<FacilioField> getPMPlannerSettingsFields() {
        FacilioModule module = ModuleFactory.getPMPlannerSettingsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("columnSettingsJson", "COLUMN_SETTINGS", module, FieldType.STRING));
        fields.add(getField("viewSettingsJson", "VIEW_SETTINGS", module, FieldType.STRING));
        fields.add(getField("timeMetricSettingsJson", "TIME_METRIC_SETTINGS", module, FieldType.STRING));
        fields.add(getField("moveType", "MOVE_TYPE", module, FieldType.STRING));
        fields.add(getField("legendSettingsJson", "LEGEND_SETTINGS", module, FieldType.STRING));
        fields.add(getField("plannerType", "PLANNER_TYPE", FieldType.NUMBER));


        return fields;
    }

    public static List<FacilioField> getInstanceMappingFields() {
        FacilioModule module = ModuleFactory.getInstanceMappingModule();

        List<FacilioField> fields = new ArrayList<>();
//		fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        fields.add(getField("device", "DEVICE_NAME", module, FieldType.STRING));
        fields.add(getField("instance", "INSTANCE_NAME", module, FieldType.STRING));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("categoryId", "ASSET_CATEGORY_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("controllerId", "CONTROLLER_ID", module, FieldType.NUMBER));
        fields.add(getField("mappedTime", "MAPPED_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getUnmodeledDataFields() {
        FacilioModule module = ModuleFactory.getUnmodeledDataModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("instanceId", "INSTANCE_ID", module, FieldType.NUMBER));
        fields.add(getField("newInstanceId", "NEW_INSTANCE_ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("value", "VALUE", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getUnmodeledInstanceFields() {
        FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("device", "DEVICE_NAME", module, FieldType.STRING));
        fields.add(getField("instance", "INSTANCE_NAME", module, FieldType.STRING));
        fields.add(getField("controllerId", "CONTROLLER_ID", module, FieldType.NUMBER));
        fields.add(getField("objectInstanceNumber", "OBJECT_INSTANCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("instanceDescription", "INSTANCE_DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("instanceType", "INSTANCE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("inUse", "IN_USE", module, FieldType.BOOLEAN));
        fields.add(getField("subscribed", "IS_SUBSCRIBED", module, FieldType.BOOLEAN));
        fields.add(getField("thresholdJson", "THRESHOLD_JSON", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        return fields;

    }

    public static List<FacilioField> getPointsFields() {
        FacilioModule module = ModuleFactory.getPointsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.ASSET_CATEGORY_ID, "ASSET_CATEGORY_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("mappedTime", "MAPPED_TIME", module, FieldType.NUMBER));
        fields.add(getDeviceField(module));
        fields.add(getInstanceField(module));
        fields.add(getField(AgentConstants.DISPLAY_NAME, "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getNewControllerIdField(module));
        fields.add(getField("objectInstanceNumber", "OBJECT_INSTANCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("instanceDescription", "INSTANCE_DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("instanceType", "INSTANCE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("dataType", "DATA_TYPE", module, FieldType.NUMBER));
        fields.add(getField("pointPath", "POINT_PATH", module, FieldType.STRING));
        fields.add(getField("isWritable", "IS_WRITABLE", module, FieldType.BOOLEAN));

        fields.add(getField("inUse", "IN_USE", module, FieldType.BOOLEAN));
        SystemEnumField configureStatusfield = (SystemEnumField) getField("configureStatus", "CONFIGURE_STATUS", module, FieldType.SYSTEM_ENUM);
        configureStatusfield.setEnumName("ConfigureStatus");
        fields.add(configureStatusfield);

        fields.add(getField("subscribed", "IS_SUBSCRIBED", module, FieldType.BOOLEAN));
        SystemEnumField subscribeStatusfield = (SystemEnumField) getField("subscribeStatus", "SUBSCRIBE_STATUS", module, FieldType.SYSTEM_ENUM);
        subscribeStatusfield.setEnumName("SubscribeStatus");
        fields.add(subscribeStatusfield);

        fields.add(getField("thresholdJson", "THRESHOLD_JSON", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("unit", "UNIT", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getReadingToolsFields() {

        FacilioModule module = ModuleFactory.getReadingToolsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldOptionType", "FIELD_OPTION_TYPE", module, FieldType.NUMBER));
        fields.add(getField("startTtime", "START_TTIME", module, FieldType.NUMBER));
        fields.add(getField("endTtime", "END_TTIME", module, FieldType.NUMBER));
        fields.add(getField("email", "EMAIL", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getFieldDeviceFields() {
        FacilioModule module = ModuleFactory.getFieldDeviceModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.IDENTIFIER, "IDENTIFIER", module, FieldType.STRING));
        fields.add(getFieldDeviceTypeField(module));
        fields.add(getField(AgentConstants.SITE_ID, "SITE_ID", module, FieldType.NUMBER));
        fields.add(getNameField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getField(AgentConstants.CONTROLLER_PROPS_AS_PROPSSTR, "CONTROLLER_PROPS", module, FieldType.STRING));
        fields.add(getCreatedTime(module));
        fields.add(getNewDeletedTimeField(module));
        return fields;
    }

    public static FacilioField getFieldDeviceTypeField(FacilioModule module) {
        return getField(AgentConstants.CONTROLLER_TYPE, "TYPE", module, FieldType.NUMBER);
    }

    public static FacilioField getNewDeletedTimeField(FacilioModule module) {
        return getField(AgentConstants.DELETED_TIME, "DELETED_TIME", module, FieldType.NUMBER);
    }

    public static FacilioField getFieldDeviceId(FacilioModule module) {
        return getField(AgentConstants.DEVICE_ID, "DEVICE_ID", module, FieldType.NUMBER);
    }

    // using this
    public static List<FacilioField> getPointFields() {
        FacilioModule module = ModuleFactory.getPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField(AgentConstants.NAME, "NAME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DISPLAY_NAME, "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DESCRIPTION, "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DATA_TYPE, "DATA_TYPE", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.POINT_TYPE, "POINT_TYPE", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.DEVICE_NAME, "DEVICE_NAME", module, FieldType.STRING));
        fields.add(getField(AgentConstants.PSEUDO, "PSEUDO", module, FieldType.BOOLEAN));
        //fields.add(getField(AgentConstants.CONTROLLER_ID, "CONTROLLER_ID", module, FieldType.NUMBER));
        fields.add(getControllerIdField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getPointAssetCategoryIdField(module));
        fields.add(getPointResourceIdField(module));
        fields.add(getPointFieldIdField(module));
        fields.add(getField(AgentConstants.WRITABLE, "WRITABLE", module, FieldType.BOOLEAN));
        fields.add(getField(AgentConstants.THRESHOLD_JSON, "THRESHOLD_JSON", module, FieldType.STRING));
        fields.add(getField(AgentConstants.CREATED_TIME, "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.MAPPED_TIME, "MAPPED_TIME", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.UNIT, "UNIT", module, FieldType.NUMBER));
        //fields.add(getNewDeletedTimeField(module));
        SystemEnumField configureStatusfield = (SystemEnumField) getField(AgentConstants.CONFIGURE_STATUS, "CONFIGURE_STATUS", module, FieldType.SYSTEM_ENUM);
        configureStatusfield.setEnumName("ConfigureStatus");
        fields.add(configureStatusfield);

        SystemEnumField subscribeStatusfield = (SystemEnumField) getField(AgentConstants.SUBSCRIBE_STATUS, "SUBSCRIBE_STATUS", module, FieldType.SYSTEM_ENUM);
        subscribeStatusfield.setEnumName("SubscribeStatus");
        fields.add(subscribeStatusfield);
        return fields;
    }

    public static  List<FacilioField> getModbusImportFields(){
        FacilioModule module = ModuleFactory.getModbusImportModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getIdxField(module));
        fields.add(getTypeField(module));
        fields.add(getField(AgentConstants.FILE_ID,"FILEID",module,FieldType.NUMBER));
        fields.add(getImportStatusField(module));
        fields.add(getCreatedTime(module));
        fields.add(getLastModifiedTimeField(module));
        return fields;
    }

    public static FacilioField getImportStatusField(FacilioModule module) {
        return getField(AgentConstants.STATUS,"STATUS",module, FieldType.NUMBER);
    }

    public static FacilioField getTypeField(FacilioModule module) {
        return getField(AgentConstants.TYPE,"TYPE",module,FieldType.NUMBER);
    }

    public static FacilioField getIdxField(FacilioModule module) {
        return getField(AgentConstants.IDX,"IDX",module,FieldType.NUMBER);
    }


    public static FacilioField getPointFieldIdField(FacilioModule module) {
        return getField(AgentConstants.FIELD_ID, "FIELD_ID", module, FieldType.NUMBER);
    }

    public static FacilioField getPointAssetCategoryIdField(FacilioModule module) {
        return getField(AgentConstants.ASSET_CATEGORY_ID, "ASSET_CATEGORY_ID", module, FieldType.NUMBER);
    }

    public static FacilioField getPointResourceIdField(FacilioModule module) {
        return getField(AgentConstants.RESOURCE_ID, "RESOURCE_ID", module, FieldType.NUMBER);
    }

    public static FacilioField getControllerTypeField() {
        FacilioModule module = ModuleFactory.getNewControllerModule();
        return getField(AgentConstants.CONTROLLER_TYPE, "CONTROLLER_TYPE", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getNiagaraPointFields() {
        FacilioModule module = ModuleFactory.getNiagaraPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getControllerIdField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getField(AgentConstants.PATH, "PATH", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getOPCXmlDAPointFields() {
        FacilioModule module = ModuleFactory.getOPCXmlDAPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getControllerIdField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getField(AgentConstants.PATH, "PATH", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getMiscPointFields() {
        FacilioModule module = ModuleFactory.getMiscPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getControllerIdField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getField(AgentConstants.PATH, "PATH", module, FieldType.STRING));
        return fields;
    }


    public static List<FacilioField> getOPCUAPointFields() {
        FacilioModule module = ModuleFactory.getOPCUAPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getControllerIdField(module));
        fields.add(getField(AgentConstants.NAMESPACE, "NAME_SPACE", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.IDENTIFIER, "IDENTIFIER", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getModbusTcpPointFields() {
        FacilioModule module = ModuleFactory.getModbusTcpPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getControllerIdField(module));
        fields.add(getField(AgentConstants.REGISTER_NUMBER, "REGISTER_NUMBER", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.FUNCTION_CODE, "FUNCTION_CODE", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.MODBUS_DATA_TYPE, "MODBUS_DATA_TYPE", module, FieldType.NUMBER));
        return fields;
    }
    public static List<FacilioField> getSystemPointFields(){
        FacilioModule module = ModuleFactory.getSystemPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getControllerIdField(module));
        fields.add(getNameField(module));
        fields.add(getFieldDeviceId(module));
        return fields;
    }

    public static List<FacilioField> getRtuNetworkFields() {
        FacilioModule module = ModuleFactory.getRtuNetworkModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getNameField(module));
        fields.add(getIdField(module));
        fields.add(getNewAgentIdField(module));
        fields.add(getField(AgentConstants.COM_PORT, "COM_PORT", module, FieldType.STRING));
        fields.add(getField(AgentConstants.DATA_BITS, "DATA_BITS", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.STOP_BITS, "STOP_BITS", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.PARITY, "PARITY", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.BAUD_RATE, "BAUD_RATE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getModbusRtuPointFields() {
        FacilioModule module = ModuleFactory.getModbusRtuPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getControllerIdField(module));
        fields.add(getField(AgentConstants.REGISTER_NUMBER, "REGISTER_NUMBER", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.FUNCTION_CODE, "FUNCTION_CODE", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.MODBUS_DATA_TYPE, "MODBUS_DATA_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getBACnetIPPointFields() {
        FacilioModule module = ModuleFactory.getBACnetIPPointModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdNotPrimaryField(module));
        fields.add(getControllerIdField(module));
        fields.add(getFieldDeviceId(module));
        fields.add(getField(AgentConstants.INSTANCE_NUMBER, "INSTANCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField(AgentConstants.INSTANCE_TYPE, "INSTANCE_TYPE", module, FieldType.NUMBER));
        return fields;
    }

    public static FacilioField getInstanceField(FacilioModule module) {

        return getField("instance", "INSTANCE_NAME", module, FieldType.STRING);
    }

    public static FacilioField getDeviceField(FacilioModule module) {

        return getField("device", "DEVICE_NAME", module, FieldType.STRING);
    }


    public static List<FacilioField> getWeatherStationsFields() {

        FacilioModule module = ModuleFactory.getWeatherStationModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("name", "Name", "NAME", module, FieldType.STRING));
        fields.add(getField("latitude", "Latitude", "LAT", module, FieldType.NUMBER));
        fields.add(getField("longtitude", "Longtitude", "LNG", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMarkedReadingFields() {
        FacilioModule module = ModuleFactory.getMarkedReadingModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("dataId", "DATA_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("actualValue", "ACTUAL_VALUE", module, FieldType.STRING));
        fields.add(getField("modifiedValue", "MODIFIED_VALUE", module, FieldType.STRING));
        fields.add(getField("markType", "MARK_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReportColumnFields() {
        FacilioModule module = ModuleFactory.getReportColumnsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("entityId", "REPORT_ENTITY_ID", module, FieldType.LOOKUP));
        fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
        fields.add(getField("baseLineId", "BASE_LINE_ID", module, FieldType.LOOKUP));
        fields.add(getField("baseLineAdjust", "BASE_LINE_ADJUST", module, FieldType.BOOLEAN));
        fields.add(getField("sequence", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
        fields.add(getField("active", "IS_ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("width", "WIDTH", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWidgetVsWorkflowFields() {
        FacilioModule module = ModuleFactory.getWidgetVsWorkflowModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));

        FacilioField workflowId = new FacilioField();
        workflowId.setName("workflowId");
        workflowId.setDataType(FieldType.LOOKUP);
        workflowId.setColumnName("WORKFLOW_ID");
        workflowId.setModule(module);
        fields.add(workflowId);

        FacilioField baseSpaceId = new FacilioField();
        baseSpaceId.setName("baseSpaceId");
        baseSpaceId.setDataType(FieldType.LOOKUP);
        baseSpaceId.setColumnName("BASE_SPACE_ID");
        baseSpaceId.setModule(module);
        fields.add(baseSpaceId);

        fields.add(getField("widgetId", "WIDGET_ID", module, FieldType.LOOKUP));
        fields.add(getField("workflowName", "WORKFLOW_NAME", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getDashboardSharingFields() {
        FacilioModule module = ModuleFactory.getDashboardSharingModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));dataproce
        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.NUMBER));
        fields.add(getField("orgUserId", "ORG_USERID", module, FieldType.NUMBER));
        fields.add(getField("roleId", "ROLE_ID", module, FieldType.NUMBER));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("sharingType", "SHARING_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getViewSharingFields() {
        FacilioModule module = ModuleFactory.getViewSharingModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("viewId", "VIEWID", module, FieldType.NUMBER));
        fields.add(getField("orgUserId", "ORG_USERID", module, FieldType.NUMBER));
        fields.add(getField("roleId", "ROLE_ID", module, FieldType.NUMBER));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("sharingType", "SHARING_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static FacilioField getField(String name, String colName, FieldType type) {
        return getField(name, colName, null, type);
    }

    public static List<FacilioField> getAnomalyConfigFields() {
        FacilioModule module = ModuleFactory.getAnalyticsAnomalyConfigModule();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        fields.add(getField("constant1", "CONSTANT1", module, FieldType.DECIMAL));
        fields.add(getField("constant2", "CONSTANT2", module, FieldType.DECIMAL));
        fields.add(getField("maxDistance", "MAXDISTANCE", module, FieldType.DECIMAL));

        fields.add(getField("dimension1Buckets", "DIMENSION1_BUCKETS", module, FieldType.STRING));
        fields.add(getField("dimension2Buckets", "DIMENSION2_BUCKETS", module, FieldType.STRING));
        fields.add(getField("dimension1Value", "DIMENSION1_VALUE", module, FieldType.STRING));
        fields.add(getField("dimension2Value", "DIMENSION2_VALUE", module, FieldType.STRING));
        fields.add(getField("xAxisDimension", "DIMENSION_x", module, FieldType.STRING));
        fields.add(getField("yAxisDimension", "DIMENSION_y", module, FieldType.STRING));
        fields.add(getField("outlierDistance", "OUTLIER_DISTANCE", module, FieldType.DECIMAL));
        fields.add(getField("historyDays", "HISTORY_DAYS", module, FieldType.NUMBER));
        fields.add(getField("startDateMode", "START_DATE_MODE", module, FieldType.BOOLEAN));
        fields.add(getField("startDate", "START_DATE", module, FieldType.STRING));
        fields.add(getField("meterInterval", "METER_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("clusterSize", "CLUSTER_SIZE", module, FieldType.NUMBER));
        fields.add(getField("bucketSize", "BUCKET_SIZE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getAnomalyTemperatureFields() {
        FacilioModule module = ModuleFactory.getAnalyticsAnomalyModuleWeatherData();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("temperature", "TEMPERATURE", module, FieldType.DECIMAL));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        return fields;
    }


    public static List<FacilioField> getAnomalyIDFields() {
        FacilioModule module = ModuleFactory.getAnalyticsAnomalyIDListModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getAnomalyIDInsertFields() {
        FacilioModule module = ModuleFactory.getAnalyticsAnomalyIDListModule();
        List<FacilioField> fields = new ArrayList<>();
        //fields.add(getOrgIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("meterId", "PARENT_METER_ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("outlierDistance", "OUTLIER_DISTANCE", module, FieldType.DECIMAL));

        return fields;
    }

    public static List<FacilioField> getAnomalyS3URLfileds() {
        FacilioModule module = ModuleFactory.getAnalyticsAnomalyS3URLModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        //fields.add(getOrgIdField(module));
        //fields.add(getOrgIdField(module));
        //fields.add(getField("orgId", "ORG_ID", module, FieldType.NUMBER));
        fields.add(getField("createdDate", "CREATED_DATE", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("s3URL", "S3_URL", module, FieldType.STRING));
        fields.add(getField("isValid", "IS_VALID", module, FieldType.NUMBER));
        fields.add(getField("fileType", "FILE_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getImportProcessFields() {
        FacilioModule module = ModuleFactory.getImportProcessModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        //fields.add(getOrgIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getField("uploadedBy", "UPLOADED_BY", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("columnHeadingString", "COLUMN_HEADING", module, FieldType.STRING));
        fields.add(getField("filePath", "FILE_PATH", module, FieldType.STRING));
        fields.add(getField("fileId", "FILEID", module, FieldType.NUMBER));
        fields.add(getField("filePathFailed", "FILE_PATH_FAILED", module, FieldType.STRING));
        fields.add(getField("fieldMappingString", "FIELD_MAPPING", module, FieldType.STRING));
        fields.add(getField("importTime", "IMPORT_TIME", module, FieldType.NUMBER));
        fields.add(getField("importType", "IMPORT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("importJobMeta", "IMPORT_JOB_META", module, FieldType.STRING));
        fields.add(getField("importSetting", "IMPORT_SETTING", module, FieldType.STRING));
        fields.add(getField("mailSetting", "MAIL_SETTING", module, FieldType.STRING));
        fields.add(getField("importMode", "IMPORT_MODE", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATE_ID", module, FieldType.NUMBER));
        fields.add(getField("totalRows", "ROW_TOTAL", module, FieldType.NUMBER));
        fields.add(getField("firstRowString", "FIRST_ROW_STRING", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getImportPointsFields() {
        FacilioModule module = ModuleFactory.getImportPointsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
        fields.add(getField("columnHeadingString", "COLUMN_HEADING", module, FieldType.STRING));
        fields.add(getField("filePath", "FILE_PATH", module, FieldType.STRING));
        fields.add(getField("fileId", "FILEID", module, FieldType.NUMBER));
        fields.add(getField("filePathFailed", "FILE_PATH_FAILED", module, FieldType.STRING));
        fields.add(getField("fieldMappingString", "FIELD_MAPPING", module, FieldType.STRING));
        fields.add(getField("importTime", "IMPORT_TIME", module, FieldType.NUMBER));
        fields.add(getField("importType", "IMPORT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("importJobMeta", "IMPORT_JOB_META", module, FieldType.STRING));
        fields.add(getField("importSetting", "IMPORT_SETTING", module, FieldType.STRING));
        fields.add(getField("importMode", "IMPORT_MODE", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getImportProcessLogFields() {
        FacilioModule module = ModuleFactory.getImportProcessLogModule();
        List<FacilioField> fields = new ArrayList();
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        //fields.add(getOrgIdField(module));
        fields.add(getField("parentId", "PARENTID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("importId", "IMPORTID", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));
//		fields.add(getField("total_rows" , "ROW_TOTAL", module, FieldType.NUMBER));
        fields.add(getField("rowContextString", "GROUPED_ROWS", module, FieldType.STRING));
        fields.add(getField("error_resolved", "ERROR_RESOLVED", module, FieldType.NUMBER));
        fields.add(getField("correctedRowString", "CORRECTED_ROW", module, FieldType.STRING));
        fields.add(getField("importMode", "IMPORT_MODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getImportPointsLogFields() {
        FacilioModule module = ModuleFactory.getImportPointsLogModule();
        List<FacilioField> fields = new ArrayList();
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
        fields.add(getField("importId", "IMPORTID", module, FieldType.NUMBER));
        fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));
//		fields.add(getField("total_rows" , "ROW_TOTAL", module, FieldType.NUMBER));
        fields.add(getField("rowContextString", "GROUPED_ROWS", module, FieldType.STRING));
        fields.add(getField("error_resolved", "ERROR_RESOLVED", module, FieldType.NUMBER));
        fields.add(getField("correctedRowString", "CORRECTED_ROW", module, FieldType.STRING));
        fields.add(getField("importMode", "IMPORT_MODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getImportTemplateFields() {
        FacilioModule module = ModuleFactory.getImportTemplateModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        //fields.add(getOrgIdField(module));
        fields.add(getField("module", "MODULE", module, FieldType.STRING));
        fields.add(getField("templateName", "TEMPLATE_NAME", module, FieldType.STRING));
        fields.add(getField("uniqueMappingString", "UNIQUE_MAPPING", module, FieldType.STRING));
        fields.add(getField("fieldMappingString", "FIELD_MAPPING", module, FieldType.STRING));
        fields.add(getField("save", "SYS_SHOW", module, FieldType.NUMBER));
        fields.add(getField("templateMeta", "TEMPLATE_META", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getDerivationFields() {
        FacilioModule module = ModuleFactory.getDerivationsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.NUMBER));
        fields.add(getField("formulaId", "FORMULA_FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("analyticsType", "ANALYTICS_TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getOrgUnitsFields() {
        FacilioModule module = ModuleFactory.getOrgUnitsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("metric", "METRIC", module, FieldType.NUMBER));
        fields.add(getField("unit", "UNIT", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getCommonJobPropsFields() {
        FacilioModule module = ModuleFactory.getCommonJobPropsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("jobId", "JOBID", module, FieldType.NUMBER));
        //fields.add(getOrgIdField(module));
        fields.add(getField("jobName", "JOBNAME", module, FieldType.STRING));
        fields.add(getField("props", "PROPS", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getTenantsFields() {
        FacilioModule module = ModuleFactory.getTenantsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getNameField(module));
        fields.add(getDescriptionField(module));
        fields.add(getContactIdField(module));
        fields.add(getField("logoId", "LOGO_ID", module, FieldType.LOOKUP));
        fields.add(getField("zoneId", "ZONE_ID", module, FieldType.LOOKUP));
        fields.add(getField("inTime", "IN_TIME", module, FieldType.DATE));
        fields.add(getField("outTime", "OUT_TIME", module, FieldType.DATE));
        fields.add(getField("sysCreatedTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("sysModifiedTime", "MODIFIED_TIME", module, FieldType.DATE_TIME));

        return fields;
    }


    public static List<FacilioField> getTenantsUserFields() {
        FacilioModule module = ModuleFactory.getTenantsuserModule();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getField("tenantId", "TENANTID", module, FieldType.NUMBER));
        fields.add(getField("ouid", "ORG_USERID", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getTenantsUtilityMappingFields() {
        FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("tenantId", "TENANT_ID", module, FieldType.LOOKUP));
        fields.add(getField("utility", "UTILITY_ID", module, FieldType.NUMBER));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.LOOKUP));
        fields.add(getField("showInPortal", "SHOW_IN_PORTAL", module, FieldType.BOOLEAN));

        return fields;
    }


    public static List<FacilioField> getRateCardFields() {
        FacilioModule module = ModuleFactory.getRateCardModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getNameField(module));
        fields.add(getDescriptionField(module));

        return fields;
    }

    public static List<FacilioField> getRateCardServiceFields() {
        FacilioModule module = ModuleFactory.getRateCardServiceModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("rateCardId", "RATE_CARD_ID", module, FieldType.LOOKUP));
        fields.add(getField("serviceType", "SERVICE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("utility", "UTILITY_ID", module, FieldType.NUMBER));
        fields.add(getField("price", "PRICE", module, FieldType.DECIMAL));
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getModuleLocalIdFields() {
        FacilioModule module = ModuleFactory.getModuleLocalIdModule();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getField("localId", "LAST_LOCAL_ID", module, FieldType.NUMBER));
        fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getBenchmarkFields() {
        FacilioModule module = ModuleFactory.getBenchmarkModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("value", "VAL", module, FieldType.DECIMAL));
        fields.add(getField("metric", "METRIC", module, FieldType.NUMBER));
        fields.add(getField("duration", "DURATION", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getBenchmarkUnitFields() {
        FacilioModule module = ModuleFactory.getBenchmarkUnitModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getField("benchmarkId", "BENCHMARK_ID", module, FieldType.LOOKUP));
        fields.add(getField("unit", "UNIT", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getReportBenchmarkRelFields() {
        FacilioModule module = ModuleFactory.getReportBenchmarkRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("reportId", "REPORT_ID", module, FieldType.NUMBER));
        fields.add(getField("benchmarkId", "BENCHMARK_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getShiftUserRelModuleFields() {
        FacilioModule module = ModuleFactory.getShiftUserRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("ouid", "ORG_USERID", module, FieldType.NUMBER));
        fields.add(getField("shiftId", "SHIFTID", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getShiftBreakRelModuleFields() {
        FacilioModule module = ModuleFactory.getShiftBreakRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("shiftId", "SHIFTID", module, FieldType.NUMBER));
        fields.add(getField("breakId", "BREAK_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getCostFields() {
        FacilioModule module = ModuleFactory.getCostsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getNameField(module));
        fields.add(getField("utility", "UTILITY_ID", module, FieldType.NUMBER));
        fields.add(getField("utilityProvider", "UTILITY_PROVIDER", module, FieldType.NUMBER));
        fields.add(getField("readingId", "READING_ID", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getCostSlabFields() {
        FacilioModule module = ModuleFactory.getCostSlabsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getField("costId", "COST_ID", module, FieldType.LOOKUP));
        fields.add(getNameField(module));
        fields.add(getField("cost", "UNIT_COST", module, FieldType.DECIMAL));
        fields.add(getField("startRange", "START_RANGE", module, FieldType.DECIMAL));
        fields.add(getField("endRange", "END_RANGE", module, FieldType.DECIMAL));
        fields.add(getField("maxUnit", "MAX_UNIT", module, FieldType.DECIMAL));

        return fields;
    }

    public static List<FacilioField> getAdditionalCostFields() {
        FacilioModule module = ModuleFactory.getAdditionalCostModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getField("costId", "COST_ID", module, FieldType.LOOKUP));
        fields.add(getField("cost", "COST", module, FieldType.DECIMAL));
        fields.add(getField("type", "COST_TYPE", module, FieldType.NUMBER));
        fields.add(getField("readingFieldId", "READING_FIELD_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getAssetDepreciationRelFields() {
        FacilioModule module = ModuleFactory.getAssetDepreciationRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("depreciationId", "DEPRECIATION_ID", module, FieldType.NUMBER));
        fields.add(getField("depreciationAmount", "DEPRECIATION_AMOUNT", module, FieldType.NUMBER));
        fields.add(getField("activated", "ACTIVATED", module, FieldType.BOOLEAN));
        fields.add(getField("lastCalculatedId", "LAST_CALCULATED_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getAssetDepreciationCalculationFields() {
        FacilioModule module = ModuleFactory.getAssetDepreciationCalculationModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        LookupField assetLookup = (LookupField) getField("asset", "ASSET_ID", module, FieldType.LOOKUP);
        assetLookup.setLookupModule(ModuleFactory.getAssetsModule());
        fields.add(assetLookup);
        fields.add(getField("depreciationId", "DEPRECIATION_ID", module, FieldType.NUMBER));
        fields.add(getField("calculatedDate", "CALCULATED_DATE", module, FieldType.NUMBER));
        fields.add(getField("currentPrice", "CURRENT_PRICE", module, FieldType.NUMBER));
        fields.add(getField("depreciatedAmount", "DEPRECIATED_AMOUNT", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getCostAssetsFields() {
        FacilioModule module = ModuleFactory.getCostAssetsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getField("costId", "COST_ID", module, FieldType.LOOKUP));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.LOOKUP));
        fields.add(getField("billStartDay", "BILL_START_DAY", module, FieldType.NUMBER));
        fields.add(getField("noOfBillMonths", "NO_OF_BILL_MONTHS", module, FieldType.NUMBER));
        fields.add(getField("firstBillTime", "FIRST_BILL_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getRelationshipFields() {
        FacilioModule module = ModuleFactory.getRelationshipModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("relationshipType", "TYPE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getRelatedAssetesFields() {
        FacilioModule module = ModuleFactory.getRelatedAssetsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("sourceId", "SOURCE_ID", module, FieldType.NUMBER));
        fields.add(getField("targetId", "TARGET_ID", module, FieldType.NUMBER));
        fields.add(getField("relationId", "RELATION_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getReport1FolderFields() {
        FacilioModule module = ModuleFactory.getReportFolderModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getField("parentFolderId", "PARENT_FOLDER_ID", module, FieldType.LOOKUP));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("modifiedTime", "MODIFIED_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getReport1Fields() {
        FacilioModule module = ModuleFactory.getReportModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        //fields.add(getOrgIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getModuleIdField(module));
        fields.add(getField("reportFolderId", "REPORT_FOLDER_ID", module, FieldType.LOOKUP));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("workflowId", "WORKFLOW_ID", module, FieldType.NUMBER));

        fields.add(getField("dateOperator", "DATE_OPERATOR", module, FieldType.NUMBER));
        fields.add(getField("dateValue", "DATE_VALUE", module, FieldType.STRING));
        fields.add(getField("dateRangeJson", "DATE_RANGE_JSON", module, FieldType.STRING));

        fields.add(getField("chartState", "CHART_STATE_JSON", module, FieldType.STRING));
        fields.add(getField("tabularState", "TABULAR_STATE_JSON", module, FieldType.STRING));
        fields.add(getField("commonState", "COMMON_STATE_JSON", module, FieldType.STRING));
        fields.add(getField("reportStateJson", "REPORT_STATE_JSON", module, FieldType.STRING));

        fields.add(getField("benchmarkJson", "BENCHMARK_JSON", module, FieldType.STRING));
        fields.add(getField("dataPointJson", "DATA_POINT_JSON", module, FieldType.STRING));
        fields.add(getField("filtersJson", "FILTERS_JSON", module, FieldType.STRING));
        fields.add(getField("xAggr", "X_AGGR", module, FieldType.NUMBER));
        fields.add(getField("baselineJson", "BASELINE_JSON", module, FieldType.STRING));
        fields.add(getField("analyticsType", "ANALYTICS_TYPE", module, FieldType.NUMBER));
        fields.add(getField("type", "REPORT_TYPE", module, FieldType.NUMBER));

        fields.add(getField("booleanSetting", "BOOLEAN_SETTINGS", module, FieldType.NUMBER));
        fields.add(getField("transformClass", "TRANSFORM_CLASS", module, FieldType.STRING));

        fields.add(getField("moduleType", "MODULE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("userFiltersJson", "USER_FILTER_JSON", module, FieldType.STRING));
        fields.add(getField("template", "REPORT_TEMPLATE", module, FieldType.STRING));
        
        fields.add(getField("timeFilter", "TIME_FILTER", module, FieldType.STRING));
        fields.add(getField("dataFilter", "DATA_FILTER", module, FieldType.STRING));
        return fields;
    }

//	public static List<FacilioField> getModulesFields(){
//		FacilioModule module = ModuleFactory.getModuleModule();
//		List<FacilioField> fields = new ArrayList<>();
//		fields.add(getModuleIdField(module));
//		fields.add(getField("moduleType", "MODULE_TYPE", module, FieldType.NUMBER));
//		return fields;
//	}

    public static List<FacilioField> getReportFieldsFields() {
        FacilioModule module = ModuleFactory.getReportFieldsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldName", "FIELD_NAME", module, FieldType.STRING));
        fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getAnomalyV1ConfigFields() {
        FacilioModule module = ModuleFactory.getAnalyticsV1AnomalyConfigModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        fields.add(getField("dimension1Buckets", "DIMENSION1_BUCKETS", module, FieldType.STRING));
        fields.add(getField("dimension1Value", "DIMENSION1_VALUE", module, FieldType.STRING));
        fields.add(getField("historyDays", "HISTORY_DAYS", module, FieldType.NUMBER));
        fields.add(getField("startDate", "START_DATE", module, FieldType.STRING));
        fields.add(getField("startDateMode", "START_DATE_MODE", module, FieldType.BOOLEAN));
        fields.add(getField("meterInterval", "METER_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("tableValue", "TABLE_VALUE", module, FieldType.DECIMAL));
        fields.add(getField("adjustmentPercentage", "ADJUSTMENT_PERCENTAGE", module, FieldType.DECIMAL));
        fields.add(getField("orderRange", "ORDER_RANGE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getAssetTreeHeirarchyFields() {
        FacilioModule module = ModuleFactory.getAssetTreeHeirarchyModule();
        List<FacilioField> fields = new ArrayList<>();


//        fields.add(getOrgIdField(module));
        fields.add(getField("parentAsset", "PARENT_ASSET", module, FieldType.NUMBER));
        fields.add(getField("childAsset", "CHILD_ASSET", module, FieldType.NUMBER));


        return fields;
    }


//	public static List<FacilioField> getReportDataPointFields() {
//		FacilioModule module = ModuleFactory.getReportDataPointModule();
//		List<FacilioField> fields = new ArrayList<>();
//
//		fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
//		fields.add(getSiteIdField(module));
//		fields.add(getNameField(module));
//		fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
//		fields.add(getModuleIdField(module));
//		fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
//		fields.add(getField("xAxisFieldId", "X_AXIS_FIELD", module, FieldType.LOOKUP));
//		fields.add(getField("xAxisFieldName", "X_AXIS", module, FieldType.STRING));
//		fields.add(getField("xAxisLabel", "X_AXIS_LABEL", module, FieldType.STRING));
//		fields.add(getField("xAxisAggr", "X_AXIS_AGGREGATE_FUNCTION", module, FieldType.NUMBER));
//		fields.add(getField("xAxisUnit", "X_AXIS_UNIT", module, FieldType.NUMBER));
//		fields.add(getField("yAxisFieldId", "Y_AXIS_FIELD", module, FieldType.LOOKUP));
//		fields.add(getField("yAxisFieldName", "Y_AXIS", module, FieldType.STRING));
//		fields.add(getField("yAxisLabel", "Y_AXIS_LABEL", module, FieldType.STRING));
//		fields.add(getField("yAxisAggr", "Y_AXIS_AGGREGATE_FUNCTION", module, FieldType.NUMBER));
//		fields.add(getField("yAxisUnit", "Y_AXIS_UNIT", module, FieldType.NUMBER));
//		fields.add(getField("limit", "FETCH_LIMIT", module, FieldType.NUMBER));
//		fields.add(getField("orderBy", "ORDER_BY", module, FieldType.STRING));
//		fields.add(getField("orderByFunc", "ORDER_BY_FUNCTION", module, FieldType.NUMBER));
//		fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.LOOKUP));
//		fields.add(getField("transformCriteriaId", "TRANSFORM_CRITERIA_ID", module, FieldType.LOOKUP));
//		fields.add(getField("transformWorkflowId", "TRANSFORM_WORKFLOW_ID", module, FieldType.LOOKUP));
//
//
//		return fields;
//	}
//
//	public static List<FacilioField> getReportXCriteriaFields() {
//		FacilioModule module = ModuleFactory.getReportXCriteriaModule();
//		List<FacilioField> fields = new ArrayList<>();
//
//		fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
//		fields.add(getSiteIdField(module));
//		fields.add(getModuleIdField(module));
//		fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
//		fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
//		fields.add(getField("xFieldId", "X_FIELD_ID", module, FieldType.NUMBER));
//		fields.add(getField("xFieldName", "X_FIELD_NAME", module, FieldType.STRING));
//		fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.NUMBER));
//		fields.add(getField("transformWorkflowId", "WORKFLOW_ID", module, FieldType.NUMBER));
//
//		return fields;
//	}
//
//	public static List<FacilioField> getReportBaselineFields() {
//		FacilioModule module = ModuleFactory.getReportBaselineModule();
//		List<FacilioField> fields = new ArrayList<>();
//
//		fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
//		fields.add(getSiteIdField(module));
//		fields.add(getField("baseLineId", "BASE_LINE_ID", module, FieldType.LOOKUP));
//		fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
//		fields.add(getField("adjustType", "ADJUST_TYPE", module, FieldType.NUMBER));
//
//		return fields;
//	}
//	public static List<FacilioField> getReportBenchmarkFields() {
//		FacilioModule module = ModuleFactory.getReportBenchmarkModule();
//		List<FacilioField> fields = new ArrayList<>();
//
//		fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
//		fields.add(getSiteIdField(module));
//		fields.add(getField("benchmarkId", "BENCHMARK_ID", module, FieldType.LOOKUP));
//		fields.add(getField("reportId", "REPORT_ID", module, FieldType.LOOKUP));
//
//		return fields;
//	}

    public static List<FacilioField> getScreenModuleFields() {
        FacilioModule module = ModuleFactory.getScreenModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(AccountConstants.getOrgIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("interval", "REFRESH_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("screenSettingString", "SCREEN_SETTING", module, FieldType.STRING));
        fields.add(getField("siteId", "SITEID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getScreenDashboardRelModuleFields() {
        FacilioModule module = ModuleFactory.getScreenDashboardRelModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("screenId", "SCREEN_ID", module, FieldType.LOOKUP));
        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.LOOKUP));
        fields.add(getField("spaceId", "SPACE_ID", module, FieldType.LOOKUP));
        fields.add(getField("sequence", "SEQUENCE_NO", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getRemoteScreenModuleFields() {
        FacilioModule module = ModuleFactory.getRemoteScreenModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(AccountConstants.getOrgIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("screenId", "SCREEN_ID", module, FieldType.LOOKUP));
        fields.add(getField("token", "TOKEN", module, FieldType.STRING));
        fields.add(getField("sessionStartTime", "SESSION_START_TIME", module, FieldType.NUMBER));
        fields.add(getField("sessionInfo", "SESSION_INFO", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getTVPasscodeFields() {
        FacilioModule module = ModuleFactory.getTVPasscodeModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("code", "CODE", module, FieldType.STRING));
        fields.add(getField("generatedTime", "GENERATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("expiryTime", "EXPIRY_TIME", module, FieldType.NUMBER));
        fields.add(getField("connectedScreenId", "CONNECTED_SCREEN_ID", module, FieldType.NUMBER));
        fields.add(getField("info", "INFO", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getOfflineSyncErrorFields() {
        FacilioModule module = ModuleFactory.getOfflineSyncErrorModule();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));

        LookupField userField = (LookupField) getField("syncedBy", "SYNCED_BY", module, FieldType.LOOKUP);
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(userField);

        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("lastSyncTime", "LAST_SYNC_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("errorInfo", "ERROR_INFO", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getInventoryVendorsFields() {
        FacilioModule module = ModuleFactory.getInventoryVendorsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));
        fields.add(getNameField(module));
        FacilioField email = new FacilioField();
        email.setName("email");
        email.setDataType(FieldType.STRING);
        email.setColumnName("EMAIL");
        email.setModule(module);
        fields.add(email);
        FacilioField phone = new FacilioField();
        phone.setName("phone");
        phone.setDataType(FieldType.STRING);
        phone.setColumnName("PHONE");
        phone.setModule(module);
        fields.add(phone);
        return fields;
    }

    public static List<FacilioField> getMlForecastingLifetimeFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMlForecastingLifetimeModule();

        fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));

        fields.add(getField("assetid", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("criteriaid", "CRITERIA_ID", module, FieldType.NUMBER));
        fields.add(getField("sourcemoduleid", "SOURCE_MODULEID", module, FieldType.NUMBER));
        fields.add(getField("predictedlogfieldid", "PREDICTED_LOG_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("predictedfieldid", "PREDICTED_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("predictioninterval", "PREDICTION_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("lastexecutiontime", "LAST_EXECUTION_TIME", module, FieldType.NUMBER));
        fields.add(getField("datainterval", "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("modelsamplinginterval", "MODEL_SAMPLING_INTERVAL", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMlForecastingFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMlForecastingModule();

        fields.add(getIdField(module));
//		fields.add(getOrgIdField(module));

        fields.add(getField("assetid", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("criteriaid", "CRITERIA_ID", module, FieldType.NUMBER));
        fields.add(getField("sourcemoduleid", "SOURCE_MODULEID", module, FieldType.NUMBER));
        fields.add(getField("predictedlogfieldid", "PREDICTED_LOG_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("predictedfieldid", "PREDICTED_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("predictioninterval", "PREDICTION_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("lastexecutiontime", "LAST_EXECUTION_TIME", module, FieldType.NUMBER));
        fields.add(getField("datainterval", "DATA_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("modelsamplinginterval", "MODEL_SAMPLING_INTERVAL", module, FieldType.NUMBER));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMlForecastingFieldsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMlForecastingFieldsModule();
        fields.add(getIdField(module));
        fields.add(getField("fieldid", "FIELDID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLModule();
        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("modelPath", "MODEL_PATH", module, FieldType.STRING));
        fields.add(getField("sequence", "SEQUENCE", module, FieldType.STRING));
        fields.add(getField("predictionLogModuleID", "PREDICTION_LOG_MODULEID", module, FieldType.NUMBER));
        fields.add(getField("predictionModuleID", "PREDICTION_MODULEID", module, FieldType.NUMBER));
        fields.add(getField("criteriaID", "CRITERIA_ID", module, FieldType.NUMBER));
        fields.add(getField("ruleID", "RULE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMLAssetVariablesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLAssetVariablesModule();
        fields.add(getField("mlId", "ML_ID", module, FieldType.NUMBER));
        fields.add(getField("assetID", "ASSETID", module, FieldType.NUMBER));
        fields.add(getField("variableKey", "VARIABLE_KEY", module, FieldType.STRING));
        fields.add(getField("variableValue", "VARIABLE_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getMLModelVariablesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLModelVariablesModule();
        fields.add(getField("mlID", "ML_ID", module, FieldType.NUMBER));
        fields.add(getField("variableKey", "VARIABLE_KEY", module, FieldType.STRING));
        fields.add(getField("variableValue", "VARIABLE_VALUE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getMLVariablesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLVariablesModule();
        fields.add(getField("mlID", "ML_ID", module, FieldType.NUMBER));
        fields.add(getField("moduleID", "MODULEID", module, FieldType.NUMBER));
        fields.add(getField("fieldID", "FIELDID", module, FieldType.NUMBER));
        fields.add(getField("isSource", "IS_SOURCE", module, FieldType.BOOLEAN));
        fields.add(getField("parentFieldID", "PARENT_FIELDID", module, FieldType.NUMBER));
        fields.add(getField("parentID", "PARENT_ID", module, FieldType.NUMBER));
        fields.add(getField("maxSamplingPeriod", "MAX_SAMPLING_PERIOD", module, FieldType.NUMBER));
        fields.add(getField("futureSamplingPeriod", "FUTURE_SAMPLING_PERIOD", module, FieldType.NUMBER));
        fields.add(getField("aggregation", "AGGREGATION", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getMLCriteriaVariablesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLCriteriaVariablesModule();
        fields.add(getField("moduleID", "MODULEID", module, FieldType.NUMBER));
        fields.add(getField("fieldID", "FIELDID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLLogCheckGamFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLLogReadingModule();
        fields.add(getField("actualValue", "actualValueLog", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("adjustedLowerBound", "adjustedLowerBoundLog", "DECIMAL_CF2", module, FieldType.DECIMAL));
        fields.add(getField("adjustedUpperBound", "adjustedUpperBoundLog", "DECIMAL_CF3", module, FieldType.DECIMAL));
        fields.add(getField("gamAnomaly", "gamAnomalyLog", "DECIMAL_CF4", module, FieldType.DECIMAL));
        fields.add(getField("lowerARMA", "lowerARMALog", "DECIMAL_CF5", module, FieldType.DECIMAL));
        fields.add(getField("lowerAnomaly", "lowerAnomaly", "DECIMAL_CF6", module, FieldType.DECIMAL));
        fields.add(getField("lowerBound", "lowerBound", "DECIMAL_CF7", module, FieldType.DECIMAL));
        fields.add(getField("lowerGAM", "lowerGAMLog", "DECIMAL_CF8", module, FieldType.DECIMAL));
        fields.add(getField("predicted", "predictedLog", "DECIMAL_CF9", module, FieldType.DECIMAL));
        fields.add(getField("predictedResidual", "predictedResidualLog", "DECIMAL_CF10", module, FieldType.DECIMAL));
        fields.add(getField("residual", "residual", "DECIMAL_CF11", module, FieldType.DECIMAL));
        fields.add(getField("temperature", "temperatureLog", "DECIMAL_CF12", module, FieldType.DECIMAL));
        fields.add(getField("upperARMA", "upperARMALog", "DECIMAL_CF13", module, FieldType.DECIMAL));
        fields.add(getField("upperAnomaly", "upperAnomalyLog", "DECIMAL_CF14", module, FieldType.DECIMAL));
        fields.add(getField("upperBound", "upperBoundLog", "DECIMAL_CF15", module, FieldType.DECIMAL));
        fields.add(getField("upperGAM", "upperGAMLog", "DECIMAL_CF16", module, FieldType.DECIMAL));
        fields.add(getField("predictedTime", "PREDICTED_TIME", module, FieldType.DECIMAL));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMLLogPredictCheckGamFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLLogReadingModule();
        fields.add(getField("daywiseenergy", "daywiseenergyLog", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("endofmonthenergy", "endofmonthenergyLog", "DECIMAL_CF2", module, FieldType.DECIMAL));
        fields.add(getField("predictedTime", "PREDICTED_TIME", module, FieldType.NUMBER));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLPredictCheckGamFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLReadingModule();
        fields.add(getField("daywiseenergy", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("endofmonthenergy", "DECIMAL_CF2", module, FieldType.DECIMAL));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLLogLoadPredictFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLLogReadingModule();
        fields.add(getField("daywiseload", "daywiseloadLog", "DECIMAL_CF3", module, FieldType.DECIMAL));
        fields.add(getField("predictedTime", "PREDICTED_TIME", module, FieldType.NUMBER));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLLoadPredictFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLReadingModule();
        fields.add(getField("daywiseload", "DECIMAL_CF3", module, FieldType.DECIMAL));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLCheckGamFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLReadingModule();
        fields.add(getField("actualValue", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("adjustedLowerBound", "DECIMAL_CF2", module, FieldType.DECIMAL));
        fields.add(getField("adjustedUpperBound", "DECIMAL_CF3", module, FieldType.DECIMAL));
        fields.add(getField("gamAnomaly", "DECIMAL_CF4", module, FieldType.DECIMAL));
        fields.add(getField("lowerARMA", "DECIMAL_CF5", module, FieldType.DECIMAL));
        fields.add(getField("lowerAnomaly", "DECIMAL_CF6", module, FieldType.DECIMAL));
        fields.add(getField("lowerBound", "DECIMAL_CF7", module, FieldType.DECIMAL));
        fields.add(getField("lowerGAM", "DECIMAL_CF8", module, FieldType.DECIMAL));
        fields.add(getField("predicted", "DECIMAL_CF9", module, FieldType.DECIMAL));
        fields.add(getField("predictedResidual", "DECIMAL_CF10", module, FieldType.DECIMAL));
        fields.add(getField("residual", "DECIMAL_CF11", module, FieldType.DECIMAL));
        fields.add(getField("temperature", "DECIMAL_CF12", module, FieldType.DECIMAL));
        fields.add(getField("upperARMA", "DECIMAL_CF13", module, FieldType.DECIMAL));
        fields.add(getField("upperAnomaly", "DECIMAL_CF14", module, FieldType.DECIMAL));
        fields.add(getField("upperBound", "DECIMAL_CF15", module, FieldType.DECIMAL));
        fields.add(getField("upperGAM", "DECIMAL_CF16", module, FieldType.DECIMAL));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLLogCheckRatioFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLLogReadingModule();
        fields.add(getField("meterID", "meterIDLog", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("childMeter", "childMeterLog", "DECIMAL_CF2", module, FieldType.DECIMAL));
//		fields.add(getField("ratio","ratioLog","DECIMAL_CF3",module,FieldType.NUMBER));
//		fields.add(getField("upperAnomaly","upperAnomalyLog","DECIMAL_CF4",module,FieldType.NUMBER));
//		fields.add(getField("lowerAnomaly","lowerAnomalyLog","DECIMAL_CF5",module,FieldType.NUMBER));
        fields.add(getField("predictedTime", "PREDICTED_TIME", module, FieldType.NUMBER));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getMLCheckRatioFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getMLReadingModule();
        fields.add(getField("meterID", "DECIMAL_CF1", module, FieldType.DECIMAL));
        fields.add(getField("childMeter", "DECIMAL_CF2", module, FieldType.DECIMAL));
        fields.add(getField("ratio", "DECIMAL_CF3", module, FieldType.DECIMAL));
        fields.add(getField("upperAnomaly", "DECIMAL_CF4", module, FieldType.DECIMAL));
        fields.add(getField("lowerAnomaly", "DECIMAL_CF5", module, FieldType.DECIMAL));
        fields.add(getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
        fields.add(getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getNotificationLoggerFields() {
        FacilioModule module = ModuleFactory.getNotificationLoggerModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("type", "NOTIFICATION_TYPE", module, FieldType.NUMBER));
        fields.add(getField("to", "TO_ADDR", module, FieldType.STRING));
        fields.add(getField("info", "INFO", module, FieldType.STRING));
        fields.add(getField("threadName", "THREAD_NAME", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getSitesForStoreRoomFields() {
        FacilioModule module = ModuleFactory.getSitesForStoreRoomModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("storeRoomId", "STORE_ROOM_ID", module, FieldType.NUMBER));
        fields.add(getField("siteId", "SITE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getMobileDetailFields() {
        FacilioModule module = ModuleFactory.getMobileDetailsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("type", "TYPE", module, FieldType.STRING));
        fields.add(getField("minVersion", "MIN_VERSION", module, FieldType.DECIMAL));

        return fields;
    }

    public static List<FacilioField> getJobPlanFields() {
        FacilioModule module = ModuleFactory.getJobPlanModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getNameField(module));

        return fields;
    }


    public static FacilioField getField(String name, String colName, FacilioModule module, FieldType type) {
        return getField(name, null, colName, module, type);
    }

    public static FacilioField getIdField(String name, String colName, FacilioModule module) {
        return getField(name, colName, module, FieldType.ID);
    }

    public static FacilioField getStringField(String name, String colName, FacilioModule module) {
        return getField(name, colName, module, FieldType.STRING);
    }

    public static FacilioField getDateField(String name, String colName, FacilioModule module) {
        return getField(name, colName, module, FieldType.DATE_TIME);
    }

    public static FacilioField getNumberField(String name, String colName, FacilioModule module) {
        return getField(name, colName, module, FieldType.NUMBER);
    }


    public static FacilioField getField(String name, String displayName, String colName, FacilioModule module,
                                        FieldType type) {
        FacilioField columnFld = null;
        switch (type) {
            case NUMBER:
            case DECIMAL:
                columnFld = new NumberField();
                break;
            case BOOLEAN:
                columnFld = new BooleanField();
                break;
            case LOOKUP:
                columnFld = new LookupField();
                break;
            case ENUM:
                columnFld = new EnumField();
                break;
            case SYSTEM_ENUM:
                columnFld = new SystemEnumField();
                break;
            case FILE:
                columnFld = new FileField();
                break;
            default:
                columnFld = new FacilioField();
                break;
        }
        columnFld.setName(name);
        if (displayName != null) {
            columnFld.setDisplayName(displayName);
        }

        columnFld.setColumnName(colName);
        columnFld.setDataType(type);
        if (module != null) {
            columnFld.setModule(module);
        }
        return columnFld;
    }

    public static Map<String, FacilioField> getAsMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getName, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }

    public static Map<Long, FacilioField> getAsIdMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }

    public static List<FacilioField> getCustomButtonRuleFields() {
        FacilioModule module = ModuleFactory.getCustomButtonRuleModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getIdField(module));
        list.add(getField("formId", "FORM_ID", module, FieldType.NUMBER));
        list.add(getField("buttonType", "BUTTON_TYPE", module, FieldType.NUMBER));
        list.add(getField("allApprovalRequired", "ALL_APPROVAL_REQUIRED", module, FieldType.BOOLEAN));

        return list;
    }

    public static List<FacilioField> getStateRuleTransitionFields() {
        FacilioModule stageRuleModule = ModuleFactory.getStateRuleTransitionModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getIdField(stageRuleModule));
        list.add(getField("fromStateId", "FROM_STATE_ID", stageRuleModule, FieldType.NUMBER));
        list.add(getField("toStateId", "TO_STATE_ID", stageRuleModule, FieldType.NUMBER));
        list.add(getField("stateFlowId", "STATE_FLOW_ID", stageRuleModule, FieldType.NUMBER));
        list.add(getField("formId", "FORM_ID", stageRuleModule, FieldType.NUMBER));
        list.add(getField("formModuleName", "FORM_MODULE_NAME", stageRuleModule, FieldType.STRING));
        SystemEnumField dialogTypeField = (SystemEnumField) getField("dialogType", "DIALOG_TYPE", stageRuleModule, FieldType.SYSTEM_ENUM);
        dialogTypeField.setEnumName("DialogType");
        dialogTypeField.setValues(FacilioEnum.getEnumValues("DialogType"));
        list.add(dialogTypeField);
        list.add(getField("allApprovalRequired", "ALL_APPROVAL_REQUIRED", stageRuleModule, FieldType.BOOLEAN));
        list.add(getField("approvalOrder", "APPROVAL_ORDER", stageRuleModule, FieldType.NUMBER));
        list.add(getField("parallelTransition", "PARALLEL_TRANSITION", stageRuleModule, FieldType.BOOLEAN));
        list.add(getField("buttonType", "BUTTON_TYPE", stageRuleModule, FieldType.NUMBER));
        list.add(getField("type", "TYPE", stageRuleModule, FieldType.NUMBER));
        list.add(getField("scheduleTime", "SCHEDULE_TIME", stageRuleModule, FieldType.NUMBER));

        list.add(getField("shouldExecuteFromPermalink", "SHOULD_EXECUTE_FROM_PERMALINK", stageRuleModule, FieldType.BOOLEAN));

        list.add(getField("showInTenantPortal", "SHOW_IN_TENANT_PORTAL", stageRuleModule, FieldType.BOOLEAN));
        list.add(getField("showInVendorPortal", "SHOW_IN_VENDOR_PORTAL", stageRuleModule, FieldType.BOOLEAN));
        return list;
    }

    public static List<FacilioField> getParallelStateTransitionsStatusFields() {
        FacilioModule module = ModuleFactory.getParallelStateTransitionsStatusModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getIdField(module));
        list.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));
        list.add(getField("fromStateId", "FROM_STATE_ID", module, FieldType.NUMBER));
        list.add(getField("toStateId", "TO_STATE_ID", module, FieldType.NUMBER));
        list.add(getField("stateTransitionId", "STATE_TRANSITION_ID", module, FieldType.NUMBER));

        return list;
    }

    public static List<FacilioField> getAlarmWorkflowRuleFields() {
        FacilioModule module = ModuleFactory.getAlarmWorkflowRuleModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getField("id", "ID", module, FieldType.NUMBER));
        list.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));
        return list;
    }

    public static List<FacilioField> getStateFlowFields() {
        FacilioModule stateFlowModule = ModuleFactory.getStateFlowModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getField("id", "ID", stateFlowModule, FieldType.NUMBER));
        list.add(getField("defaultStateId", "DEFAULT_STATE_ID", stateFlowModule, FieldType.NUMBER));
        list.add(getField("defaltStateFlow", "DEFAULT_STATE_FLOW", stateFlowModule, FieldType.BOOLEAN));
        list.add(getField("diagramJson", "DIAGRAM_JSON", stateFlowModule, FieldType.STRING));
        list.add(getField("draftParentId", "DRAFT_PARENT_ID", stateFlowModule, FieldType.NUMBER));
        list.add(getField("draft", "DRAFT", stateFlowModule, FieldType.BOOLEAN));
        list.add(getField("publishedDate", "PUBLISHED_DATE", stateFlowModule, FieldType.DATE_TIME));
        list.add(getField("formLevel", "FORM_LEVEL", stateFlowModule, FieldType.BOOLEAN));
        list.add(getField("configJson", "CONFIG_JSON", stateFlowModule, FieldType.STRING));
        return list;
    }

    public static List<FacilioField> getStateFlowScheduleFields() {
        FacilioModule module = ModuleFactory.getStateFlowScheduleModule();
        List<FacilioField> list = new ArrayList<>();

        list.add(getField("id", "ID", module, FieldType.ID));
        list.add(getField("recordId", "RECORD_ID", module, FieldType.NUMBER));
        list.add(getField("transitionId", "TRANSITION_ID", module, FieldType.NUMBER));
        return list;
    }

    public static List<FacilioField> getTimeLogFields(FacilioModule module) {
        List<FacilioField> list = new ArrayList<>();

        list.add(getField("id", "ID", module, FieldType.ID));
        list.add(getField("name", "NAME", module, FieldType.STRING));
        list.add(getField("parentId", "PARENT_ID", module, FieldType.STRING));
        list.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        list.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        list.add(getField("duration", "DURATION", module, FieldType.NUMBER));
        return list;
    }

    public static List<FacilioField> getRuleTemplateRelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getRuleTemplatesRelModule();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setDataType(FieldType.ID);
        id.setColumnName("ID");
        id.setModule(module);
        fields.add(id);

        FacilioField ruleId = new FacilioField();
        ruleId.setName("ruleId");
        ruleId.setDataType(FieldType.NUMBER);
        ruleId.setColumnName("RULE_ID");
        ruleId.setModule(module);
        fields.add(ruleId);

        FacilioField defaultTemplateId = new FacilioField();
        defaultTemplateId.setName("defaultTemplateId");
        defaultTemplateId.setDataType(FieldType.NUMBER);
        defaultTemplateId.setColumnName("DEFAULT_TEMPLATE_ID");
        defaultTemplateId.setModule(module);
        fields.add(defaultTemplateId);

        return fields;
    }

    public static List<FacilioField> getShippedAssetRelFields() {
        FacilioModule module = ModuleFactory.getShippedAssetRelModule();
        List<FacilioField> fields = new ArrayList<>();

        //fields.add(getOrgIdField(module));
        fields.add(getField("assetIdFromStore", "ASSET_ID_FROM_STORE", module, FieldType.NUMBER));
        fields.add(getField("assetIdToStore", "ASSET_ID_TO_STORE", module, FieldType.NUMBER));
        fields.add(getField("shipmentId", "SHIPMENT_ID", module, FieldType.NUMBER));

        return fields;
    }
//	public static List<FacilioField> getStateFields() {
//		FacilioModule stateModule = ModuleFactory.getStateModule();
//		List<FacilioField> list = new ArrayList<>();
//
//		list.add(getField("id", "ID", stateModule, FieldType.NUMBER));
//		list.add(getField("status", "STATUS", stateModule, FieldType.STRING));
//		list.add(getField("displayName", "DISPLAY_NAME", stateModule, FieldType.STRING));
//		list.add(getField("typeCode", "STATUS_TYPE", stateModule, FieldType.NUMBER));
//		return list;
//	}

    public static List<FacilioField> getValidationFields(FacilioModule validationModule) {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("id", "ID", validationModule, FieldType.ID));
        fields.add(getField("ruleId", "RULE_ID", validationModule, FieldType.NUMBER));
        fields.add(getField("name", "NAME", validationModule, FieldType.STRING));
        fields.add(getField("errorMessage", "ERROR_MESSAGE", validationModule, FieldType.STRING));
        fields.add(getField("criteriaId", "CRITERIA_ID", validationModule, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getGraphicsFields() {
        FacilioModule module = ModuleFactory.getGraphicsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("canvas", "CANVAS", module, FieldType.STRING));
        fields.add(getField("variables", "VARIABLES", module, FieldType.STRING));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.NUMBER));
        fields.add(getField("parentFolderId", "PARENT_FOLDER_ID", module, FieldType.NUMBER));
        fields.add(getField("isDefault", "IS_DEFAULT", module, FieldType.BOOLEAN));
        fields.add(getField("applyTo", "APPLY_TO", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getGraphicsFolderFields() {
        FacilioModule module = ModuleFactory.getGraphicsFolderModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));

        return fields;
    }


    public static List<FacilioField> getContractAssociatedAssetModuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getContractAssociatedAssetsModule();
        fields.add(getField("contractId", "CONTRACT_ID", module, FieldType.NUMBER));
        fields.add(getField("assetId", "ASSET_ID", module, FieldType.NUMBER));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getShiftRotationApplicableForModuleFields() {
        FacilioModule module = ModuleFactory.getShiftRotationApplicableForModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("applicableForType", "APPLICABLE_FOR_TYPE", module, FieldType.NUMBER));
        fields.add(getField("applicableForId", "APPLICABLE_FOR_ID", module, FieldType.NUMBER));
        fields.add(getField("shiftRotationId", "SHIFT_ROTATION_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getShiftRotationDetailsModuleFields() {
        FacilioModule module = ModuleFactory.getShiftRotationDetailsModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("shiftRotationId", "SHIFT_ROTATION_ID", module, FieldType.NUMBER));
        fields.add(getField("fromShiftId", "FROM_SHIFT", module, FieldType.NUMBER));
        fields.add(getField("toShiftId", "TO_SHIFT", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getStoreNotificationModuleFields() {
        FacilioModule module = ModuleFactory.getStoreNotificationConfigModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.ID));
        fields.add(getField("storeRoomId", "STORE_ID", module, FieldType.NUMBER));
        fields.add(getField("workflowRuleId", "WORKFLOW_RULE_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getDigestConfigFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getDigestConfigModule();

        fields.add(getIdField(module));
        fields.add(getField("scheduledActionId", "SCHEDULED_ACTION_ID", module, FieldType.NUMBER));
        fields.add(getField("isActive", "IS_ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("defaultTemplateId", "DEFAULT_TEMPLATE_ID", module, FieldType.NUMBER));
        fields.add(getField("scope", "SCOPE", module, FieldType.NUMBER));
        fields.add(getField("siteId", "SITE_ID", module, FieldType.NUMBER));
        fields.add(getField("dashboardId", "DASHBOARD_ID", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getControlGroupFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControlGroupModule();

        fields.add(getIdField(module));
        fields.add(getNameField(module));
        fields.add(getSiteIdField(module));

        fields.add(getField("assetCategoryId", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("mode", "MODE", module, FieldType.NUMBER));
        fields.add(getField("isDeleted", "IS_DELETED", module, FieldType.BOOLEAN));

        return fields;
    }
    
    public static List<FacilioField> getControlPointFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControlPointModule();

        fields.add(getIdField(module));
        fields.add(getField("childRDMId", "CHILD_RDM_ID", module, FieldType.NUMBER));
        fields.add(getField("minValue", "MIN_VALUE", module, FieldType.NUMBER));
        fields.add(getField("maxValue", "MAX_VALUE", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getControllablePointFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControllablePointModule();

        fields.add(getIdField(module));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("controllablePoint", "CONTROLLABLE_POINT_ID", module, FieldType.NUMBER));

        return fields;
    }


    public static List<FacilioField> getControlGroupSpaceFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControlGroupSpaceModule();

        fields.add(getIdField(module));
        fields.add(getField("controlGroupId", "CONTROL_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("spaceId", "SPACE_ID", module, FieldType.LOOKUP));

        return fields;
    }

    public static List<FacilioField> getControlGroupInclExclFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getControlGroupInclExclModule();

        fields.add(getIdField(module));
        fields.add(getField("controlGroupId", "CONTROL_GROUP_ID", module, FieldType.LOOKUP));
        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.LOOKUP));
        fields.add(getField("isInclude", "IS_INCLUDE", module, FieldType.BOOLEAN));

        return fields;
    }

    public static List<FacilioField> getPreferencesMetaFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPreferenceMetaModule();

        fields.add(getIdField(module));
        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.NUMBER));
        fields.add(getField("isActive", "IS_ACTIVE", module, FieldType.BOOLEAN));
        fields.add(getField("recordId", "RECORD_ID", module, FieldType.NUMBER));
        fields.add(getField("preferenceName", "PREFERENCE_NAME", module, FieldType.STRING));
        fields.add(getField("formData", "FORM_DATA", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getPreferencesRuleFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPreferenceRuleModule();

        fields.add(getIdField(module));
        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.NUMBER));
        fields.add(getField("recordId", "RECORD_ID", module, FieldType.NUMBER));
        fields.add(getField("prefName", "PREFERENCE_NAME", module, FieldType.STRING));
        fields.add(getField("ruleId", "RULE_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getDevicePasscodesFields() {
        FacilioModule module = ModuleFactory.getDevicePasscodesModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("code", "CODE", module, FieldType.STRING));
        fields.add(getField("generatedTime", "GENERATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("expiryTime", "EXPIRY_TIME", module, FieldType.NUMBER));
        fields.add(getField("connectedDeviceId", "CONNECTED_DEVICE_ID", module, FieldType.NUMBER));
        fields.add(getField("info", "INFO", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getConnectedDeviceFields() {
        FacilioModule module = ModuleFactory.getConnectedDevicesModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("deviceId", "DEVICE_ID", module, FieldType.NUMBER));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        fields.add(getField("sessionStartTime", "SESSION_START_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getLogBookFields() {
        FacilioModule module = ModuleFactory.getLogBookModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getSiteIdField(module));
        fields.add(getField("orgId", "ORGID", module, FieldType.NUMBER));
        fields.add(getField("logFor", "LOG_FOR", module, FieldType.LOOKUP));
        return fields;
    }

    public static List<FacilioField> getFacilioQueueFields() {
        FacilioModule module = ModuleFactory.getFacilioQueueModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getField("queueName", "QUEUE_NAME", module, FieldType.STRING));
        fields.add(getField("addedTime", "ADDED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("visibilityTimeout", "VISIBILITY_TIMEOUT", module, FieldType.NUMBER));
        fields.add(getField("lastClientReceivedTime", "LAST_CLIENT_RECEIVED_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("data", "DATA", module, FieldType.STRING));
        fields.add(getField("maxClientReceiptCount", "MAX_CLIENT_RECEIPT_COUNT", module, FieldType.NUMBER));
        fields.add(getField("clientReceiptCount", "CLIENT_RECEIPT_COUNT", module, FieldType.NUMBER));
        fields.add(getField("deletedTime", "DELETED_TIME", module, FieldType.DATE_TIME));

        return fields;
    }

    public static List<FacilioField> getServiceCatalogFields() {
        FacilioModule module = ModuleFactory.getServiceCatalogModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(getField("photo", "PHOTO_ID", module, FieldType.FILE));
        fields.add(getField("moduleId", "MODULEID", module, FieldType.NUMBER));
        fields.add(getField("formId", "FORM_ID", module, FieldType.NUMBER));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getServiceCatalogGroupFields() {
        FacilioModule module = ModuleFactory.getServiceCatalogGroupModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("description", "DESCRIPTION", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getVisitorSettingsFields() {
        FacilioModule module = ModuleFactory.getVisitorSettingsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getField("visitorTypeId", "VISITOR_TYPE_ID", FieldType.LOOKUP));
        fields.add(getField("visitorLogFormId", "VISITOR_LOG_FORM_ID", FieldType.LOOKUP));
        fields.add(getField("visitorInviteFormId", "VISITOR_INVITE_FORM_ID", FieldType.LOOKUP));
        fields.add(getField("ndaEnabled", "IS_NDA_ENABLED", FieldType.BOOLEAN));
        fields.add(getField("badgeEnabled", "IS_BADGE_ENABLED", FieldType.BOOLEAN));
        fields.add(getField("photoEnabled", "IS_PHOTO_ENABLED", FieldType.BOOLEAN));
        fields.add(getField("autoSignoutTime", "AUTO_SIGNOUT_TIME", FieldType.NUMBER));
        fields.add(getField("hostSettingsJson", "HOST_SETTINGS_JSON", FieldType.STRING));
        fields.add(getField("photoSettingsJson", "PHOTO_SETTINGS_JSON", FieldType.STRING));
        fields.add(getField("hostEnabled", "HOST_ENABLED", FieldType.BOOLEAN));
        fields.add(getField("idScanEnabled", "IS_ID_SCAN_ENABLED", FieldType.BOOLEAN));
        fields.add(getField("ndaContent", "NDA_CONTENT", FieldType.STRING));
        fields.add(getField("approvalRequiredForInvite", "IS_INVITE_APPROVAL_REQUIRED", FieldType.BOOLEAN));
        fields.add(getField("faceRecognitionEnabled", "FACE_RECOGNITION_ENABLED", FieldType.BOOLEAN));
//		fields.add(getField("isSelfSignoutEnabled","IS_SELF_SIGNOUT_ENABLED",FieldType.BOOLEAN));
        return fields;
    }


    public static List<FacilioField> getRelatedWorkorderFields() {
        FacilioModule module = ModuleFactory.getRelatedWorkorderModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("sourceWo", "SOURCE_WO", module, FieldType.NUMBER));
        fields.add(getField("targetWo", "TARGET_WO", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getVisitorLogTriggerFields() {
        FacilioModule module = ModuleFactory.getVisitorLogTriggersModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("pmId", "VISITOR_LOG_ID", module, FieldType.NUMBER));
        fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));
        fields.add(getField("startTime", "START_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("frequency", "FREQUENCY", module, FieldType.NUMBER));
        fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
        fields.add(getField("triggerExecutionSource", "TRIGGER_EXECUTION_SOURCE", module, FieldType.NUMBER));
        fields.add(getField("custom", "IS_CUSTOM", module, FieldType.BOOLEAN));

        fields.add(getField("endTime", "END_TIME", module, FieldType.DATE_TIME));
        fields.add(getField("lastGeneratedTime", "LOG_GENERATED_UPTO", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getPrinterFields() {
        FacilioModule module = ModuleFactory.getPrinterModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("ip", "IP", module, FieldType.STRING));
        fields.add(getField("info", "INFO", module, FieldType.STRING));
        fields.add(getField("connected", "IS_CONNECTED", module, FieldType.BOOLEAN));
        SystemEnumField printerModel = (SystemEnumField) getField("printerModel", "MODEL", module, FieldType.SYSTEM_ENUM);
        printerModel.setEnumName("PrinterModel");
        printerModel.setValues(FacilioEnum.getEnumValues("PrinterModel"));
        fields.add(printerModel);

        SystemEnumField connectMode = (SystemEnumField) getField("connectMode", "CONNECT_MODE", module, FieldType.SYSTEM_ENUM);
        connectMode.setEnumName("ConnectMode");
        connectMode.setValues(FacilioEnum.getEnumValues("ConnectMode"));
        fields.add(connectMode);


        return fields;
    }

    public static List<FacilioField> getVisitorKioskConfigFields() {
        FacilioModule module = ModuleFactory.getVisitorKioskConfigModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("printerId", "PRINTER_ID", module, FieldType.NUMBER));
        fields.add(getField("kioskForSpaceId", "KIOSK_FOR_SPACE", module, FieldType.NUMBER));

        return fields;
    }


	public static List<FacilioField> getAgentMetricV2Fields() {
		FacilioModule module = ModuleFactory.getAgentMetricsV2Module();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getSiteIdField(module));
		fields.add(getNewAgentIdField(module));
		fields.add(getCreatedTime(module));
		fields.add(getField(AgentConstants.SIZE, "DATA_SIZE", module, FieldType.NUMBER));
        fields.add(getAgentPublishTypeField(module));
        fields.add((getLastUpdatesTimeField(module)));
        fields.add(getField(AgentConstants.NUMBER_OF_MSGS, "NO_OF_MSGS", module, FieldType.NUMBER));
		return fields;
}

    public static FacilioField getAgentPublishTypeField(FacilioModule module) {
        return getField(AgentConstants.PUBLISH_TYPE, "PUBLISH_TYPE", module, FieldType.NUMBER);
    }

    public static FacilioField getLastModifiedTimeField(FacilioModule module) {
        return getField(AgentConstants.LAST_MODIFIED_TIME, "LAST_MODIFIED_TIME", module, FieldType.NUMBER);
    }
    public static FacilioField getLastUpdatesTimeField(FacilioModule module) {
        return getField(AgentConstants.LAST_UPDATED_TIME, "LAST_UPDATED_TIME", module, FieldType.NUMBER);
    }

    public static List<FacilioField> getApplicationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getApplicationModule();

        fields.add(getIdField(module));
        fields.add(getField("name", "APPLICATION_NAME", module, FieldType.STRING));
        fields.add(getField("isDefault", "IS_DEFAULT", module, FieldType.BOOLEAN));
        fields.add(getField("appDomainId", "APP_DOMAIN_ID", module, FieldType.NUMBER));
        fields.add(getField("linkName", "LINK_NAME", module, FieldType.STRING));
        fields.add(getField("layoutType", "LAYOUT_TYPE", module, FieldType.NUMBER));
        fields.add(getField("scopingId", "SCOPING_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWebTabGroupFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWebTabGroupModule();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("iconType", "ICON_TYPE", module, FieldType.NUMBER));
        fields.add(getField("route", "ROUTE", module, FieldType.STRING));
        fields.add(getField("order", "TABGROUP_ORDER", module, FieldType.NUMBER));
        fields.add(getField("appId", "APP_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getWebTabFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getWebTabModule();

        fields.add(getIdField(module));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("groupId", "GROUP_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("order", "TAB_ORDER", module, FieldType.NUMBER));
        fields.add(getField("route", "ROUTE", module, FieldType.STRING));
        fields.add(getField("config", "CONFIG", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getNewPermissionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNewPermissionModule();

        fields.add(getIdField(module));
        fields.add(getField("roleId", "ROLE_ID", module, FieldType.NUMBER));
        fields.add(getField("tabId", "TAB_ID", module, FieldType.NUMBER));
        fields.add(getField("permission", "PERMISSION", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getTabIdAppIdMappingFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getTabIdAppIdMappingModule();

        fields.add(getIdField(module));
        fields.add(getField("appId", "APP_ID", module, FieldType.NUMBER));
        fields.add(getField("tabId", "TAB_ID", module, FieldType.NUMBER));
        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.NUMBER));
        fields.add(getField("specialType", "SPECIAL_TYPE", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getPMJobPlanFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPMJobPlanModule();

        fields.add(getIdField(module));
        fields.add(getField("pmId", "PM_ID", module, FieldType.NUMBER));
        fields.add(getField("jobPlanId", "JOB_PLAN_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getPMJobPlanTriggerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getPMJobPlanTriggerModule();

        fields.add(getIdField(module));
        fields.add(getField("pmjobPlanId", "PM_JOB_PLAN_ID", module, FieldType.NUMBER));
        fields.add(getField("triggerId", "PM_TRIGGER_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getCBModelFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBModelModule();

        fields.add(getIdField(module));
        fields.add(getField("type", "APP_TYPE", module, FieldType.NUMBER));
        fields.add(getField("mlModel", "ML_MODEL_STRING", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getCBModelVersionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBModelVersionModule();

        fields.add(getIdField(module));
        fields.add(getField("modelId", "MODEL_ID", module, FieldType.NUMBER));
        fields.add(getField("versionNo", "VERSION_NO", module, FieldType.NUMBER));
        fields.add(getField("mlModel", "ML_MODEL_ID", module, FieldType.STRING));
        fields.add(getField("latestVersion", "IS_LATEST_VERSION", module, FieldType.BOOLEAN));
        fields.add(getField("accuracyRate", "ACCURACY_RATE", module, FieldType.DECIMAL));

        return fields;
    }

    public static List<FacilioField> getCBIntentFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBIntentModule();

        fields.add(getIdField(module));
        fields.add(getField("modelVersionId", "MODEL_VERSION_ID", module, FieldType.NUMBER));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("displayName", "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField("contextWorkflowId", "CONTEXT_WORKFLOW_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("deleted", "IS_DELETED", module, FieldType.BOOLEAN));
        fields.add(getField("withParams", "IS_WITH_PARAMS", module, FieldType.BOOLEAN));
        fields.add(getField("confirmationNeeded", "IS_CONFIRMATION_NEEDED", module, FieldType.BOOLEAN));
        fields.add(getField("confirmationText", "CONFIRMATION_TEXT", module, FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getCBIntentActionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBIntentActionModule();

        fields.add(getIdField(module));
        fields.add(getField("intentId", "INTENT_ID", module, FieldType.NUMBER));
        fields.add(getField("actionId", "ACTION_ID", module, FieldType.NUMBER));
        fields.add(getField("response", "RESPONSE", module, FieldType.STRING));
        fields.add(getField("responseType", "RESPONSE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
        fields.add(getField("viewName", "VIEW_NAME", module, FieldType.STRING));
        return fields;
    }
    
    public static List<FacilioField> getCBIntentChildFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBIntentChildModule();

        fields.add(getIdField(module));
        fields.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));
        fields.add(getField("childId", "CHILD_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getCBIntentInvokeSamplesFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBIntentInvokeSamplesModule();

        fields.add(getIdField(module));
        fields.add(getField("intentId", "INTENT_ID", module, FieldType.NUMBER));
        fields.add(getField("sample", "SAMPLE", module, FieldType.STRING));
        fields.add(getField("sampleLocalId", "SAMPLE_LOCAL_ID", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getCBIntentParamFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBIntentParamModule();

        fields.add(getIdField(module));
        fields.add(getField("intentId", "INTENT_ID", module, FieldType.NUMBER));
        fields.add(getField("name", "NAME", module, FieldType.STRING));
        fields.add(getField("displayName", "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(getField("askAs", "ASK_AS", module, FieldType.STRING));
        fields.add(getField("mlType", "ML_TYPE", module, FieldType.NUMBER));
        fields.add(getField("dataType", "DATA_TYPE", module, FieldType.NUMBER));
        fields.add(getField("localId", "LOCAL_ID", module, FieldType.NUMBER));
        fields.add(getField("typeConfig", "TYPE_CONFIG", module, FieldType.NUMBER));
        fields.add(getField("editable", "IS_EDITABLE", module, FieldType.BOOLEAN));
        fields.add(getField("fillableByParent", "IS_FILLABLE_BY_PARENT", module, FieldType.BOOLEAN));
        fields.add(getField("addParamTriggerText", "ADD_PARAM_TRIGGER_TEXT", module, FieldType.STRING));
        fields.add(getField("updateParamTriggerText", "UPDATE_PARAM_TRIGGER_TEXT", module, FieldType.STRING));
        fields.add(getField("moduleName", "MODULE_NAME", module, FieldType.STRING));
        fields.add(getField("criteriaId", "CRITERIA_ID", module, FieldType.NUMBER));
        fields.add(getField("multipleAllowed", "IS_MULTIPLE_ALLOWED", module, FieldType.BOOLEAN));
        return fields;
    }

    public static List<FacilioField> getCBSessionFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBSessionModule();

        fields.add(getIdField(module));
        fields.add(getField("userId", "USER_ID", module, FieldType.NUMBER));
        fields.add(getField("intentId", "INTENT_ID", module, FieldType.NUMBER));
        fields.add(getField("queryString", "QUERY", module, FieldType.STRING));
        fields.add(getField("response", "RESPONSE", module, FieldType.STRING));
        fields.add(getField("state", "STATE", module, FieldType.NUMBER));
        fields.add(getField("requiredParamCount", "REQUIRED_PARAM_COUNT", module, FieldType.NUMBER));
        fields.add(getField("recievedParamCount", "RECIEVED_PARAM_COUNT", module, FieldType.NUMBER));
        fields.add(getField("startTime", "START_TIME", module, FieldType.NUMBER));
        fields.add(getField("endTime", "END_TIME", module, FieldType.NUMBER));
        fields.add(getField("parentSessionId", "PARENT_SESSION_ID", module, FieldType.NUMBER));
        fields.add(getField("confirmed", "IS_CONFIRMED", module, FieldType.BOOLEAN));
        fields.add(getField("suggestion", "SUGGESTION", module, FieldType.STRING));
        fields.add(getField("params", "CONTEXT_PARAMS", module, FieldType.STRING));

        return fields;
    }

    public static List<FacilioField> getCBSessionConversationFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBSessionConversationModule();

        fields.add(getIdField(module));
        fields.add(getField("sessionId", "SESSION_ID", module, FieldType.NUMBER));
        fields.add(getField("parentConversationId", "PARENT_CONVERSATION_ID", module, FieldType.NUMBER));
        fields.add(getField("query", "QUERY", module, FieldType.STRING));
        fields.add(getField("responseString", "RESPONSE", module, FieldType.STRING));
        fields.add(getField("state", "STATE", module, FieldType.NUMBER));
        fields.add(getField("intentParamId", "INTENT_PARAM_ID", module, FieldType.NUMBER));
        fields.add(getField("requestedTime", "REQUESTED_TIME", module, FieldType.NUMBER));
        fields.add(getField("respondedTime", "RESPONDED_TIME", module, FieldType.NUMBER));
        fields.add(getField("suggestion", "SUGGESTION", module, FieldType.STRING));

        return fields;
    }
    
    
    public static List<FacilioField> getEnergyStarCustomerFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnergyStarCustomerModule();

        fields.add(getIdField(module));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
        fields.add(getField("createdBy", "CREATED_BY", module, FieldType.NUMBER));
        fields.add(getField("energyStarCustomerId", "ENERGY_STAR_CUSTOMER_ID", module, FieldType.STRING));
        fields.add(getField("userName", "USERNAME", module, FieldType.STRING));
        fields.add(getField("password", "PASSWORD", module, FieldType.STRING));
        fields.add(getField("dataExchangeMode", "DATA_EXCHANGE_MODE", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("shareKey", "SHARE_KEY", module, FieldType.STRING));
        fields.add(getField("shareStatus", "SHARE_STATUS", module, FieldType.NUMBER));

        return fields;
    }
    
    public static List<FacilioField> getEnergyStarPropertyFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnergyStarPropertyModule();

        fields.add(getIdField(module));
        fields.add(getField("buildingId", "BUILDING_ID", module, FieldType.NUMBER));
        fields.add(getField("buildingType", "BUILDING_TYPE", module, FieldType.NUMBER));
        fields.add(getField("energyStarPropertyId", "ES_PROPERTY_ID", module, FieldType.STRING));
        fields.add(getField("energyStarPropertyUseId", "ES_PROPERTY_USE_ID", module, FieldType.STRING));
        fields.add(getField("energyStarDesignId", "ES_DESIGN_ID", module, FieldType.STRING));
        fields.add(getField("yearBuild", "YEAR_BUILD", module, FieldType.STRING));
        fields.add(getField("occupancyPercentage", "OCCUPANCY_PERCENTAGE", module, FieldType.STRING));
        fields.add(getField("meta", "META_JSON", module, FieldType.STRING));

        return fields;
    }
    
    public static List<FacilioField> getEnergyStarPropertyUseFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnergyStarPropertyUseModule();

        fields.add(getIdField(module));
        fields.add(getField("propertyId", "PROPERTY_ID", module, FieldType.NUMBER));
        fields.add(getField("properyUseType", "USE_INT", module, FieldType.NUMBER));
        fields.add(getField("value", "VALUE", module, FieldType.STRING));

        return fields;
    }
    
    public static List<FacilioField> getEnergyStarMeterFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnergyStarMeterModule();

        fields.add(getIdField(module));
        fields.add(getField("propertyId", "PROPERTY_ID", module, FieldType.NUMBER));
        fields.add(getField("type", "TYPE", module, FieldType.NUMBER));
        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        fields.add(getField("energyStarMeterId", "ENERGY_STAR_METER_ID", module, FieldType.STRING));
        fields.add(getField("firstBillDate", "FIRST_BILL_DATE", module, FieldType.STRING));
        fields.add(getField("meta", "META_JSON", module, FieldType.STRING));

        return fields;
    }
    
    public static List<FacilioField> getEnergyStarMeterPointFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getEnergyStarMeterPointModule();

        fields.add(getIdField(module));
        fields.add(getField("meterId", "METER_ID", module, FieldType.NUMBER));
        fields.add(getField("pointId", "POINT_ID", module, FieldType.NUMBER));
        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
        fields.add(getField("aggr", "AGGR", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getAgentAlarmFields() {
        List<FacilioField> fields = new ArrayList<>();

        FacilioModule module = ModuleFactory.getAgentAlarmsModule();
        fields.add(getAgentIdField(module));
        fields.add(getModuleIdField());

        return fields;
    }

    public static List<FacilioField> getCBSessionParamsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getCBSessionParamsModule();

        fields.add(getIdField(module));
        fields.add(getField("sessionId", "SESSION_ID", module, FieldType.NUMBER));
        fields.add(getField("intentParamId", "INTENT_PARAM_ID", module, FieldType.NUMBER));
        fields.add(getField("value", "VALUE", module, FieldType.STRING));

        return fields;
    }

    public static FacilioField getControllerTypeField(FacilioModule module) {
        return getField(AgentConstants.CONTROLLER_TYPE, "Controller_Type", FieldType.NUMBER);
    }

    public static List<FacilioField> getFaceCollectionsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getFaceCollectionsModule();

        fields.add(getIdField(module));
        fields.add(getField("collectionId", "COLLECTION_ID", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getVisitorFacesFields(FacilioModule module) {
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));
        fields.add(getField("photoId", "PHOTO_ID", module, FieldType.NUMBER));
        fields.add(getField("collectionId", "COLLECTION_ID", module, FieldType.NUMBER));
        fields.add(getField("faceId", "FACE_ID", module, FieldType.STRING));
        fields.add(getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getFeedbackKioskFields() {
        FacilioModule module = ModuleFactory.getFeedbackKioskModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("feedbackTypeId", "FEEDBACK_TYPE_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getFeedbackTypeFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField());
        fields.add(getField("name", "NAME", FieldType.STRING));
        return fields;
    }

    public static List<FacilioField> getFeedbackTypeCatalogMappingFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField());
        fields.add(getField("feedbackTypeId", "FEEDBACK_TYPE_ID", FieldType.NUMBER));
        fields.add(getField("catalogId", "CATALOG_ID", FieldType.NUMBER));
        fields.add(getField("order", "CATALOG_ORDER", FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getRecommendedRuleFields() {
        FacilioModule module = ModuleFactory.getRecommendedRuleModule();
        List<FacilioField> fields = new ArrayList<>();

		fields.add(getIdField(module));
		//fields.add(getField("orgId","ORGID",module,FieldType.NUMBER));
		fields.add(getField("id","ID",module, FieldType.NUMBER));
		fields.add(getField("orgid","ORGID",module, FieldType.NUMBER));
		fields.add(getField("ruleid","RULE_ID",module,FieldType.NUMBER));
		fields.add(getField("categoryid","CATEGORY_ID",module,FieldType.NUMBER));
		return  fields;
	}
	public static List<FacilioField> getFloorPlanFields() {
		FacilioModule module = ModuleFactory.getFloorPlanModule();
		List<FacilioField> fields = getSystemPointFields(module);
		fields.add(getIdField(module));
		fields.add(getField("id", "ID", module, FieldType.ID));
		fields.add(getField("fileId","FILE_ID",module,FieldType.NUMBER));
		fields.add(getField("name", "NAME", module, FieldType.STRING));
		fields.add(getField("canvas", "CANVAS", module, FieldType.STRING));
		fields.add(getField("isActive", "IS_ACTIVE", module, FieldType.BOOLEAN));
		fields.add(getField("siteId", "SITE_ID", module, FieldType.NUMBER));
		fields.add(getField("floorId", "FLOOR_ID", module, FieldType.NUMBER));
		fields.add(getField("leagend", "LEAGEND", module, FieldType.STRING));
		return fields;
	}
	public static List<FacilioField> getFloorPlanObjectFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getFloorPlanObjectModule();
		fields.add(getIdField(module));
		fields.add(getField("floorPlanObjectId", "ID", module, FieldType.ID));
		fields.add(getField("floorPlanId", "FLOORPLAN_ID", module, FieldType.ID));
		fields.add(getField("objectType", "OBJECT_TYPE", module, FieldType.ENUM));
		fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.ID));
		fields.add(getField("info", "INFO", module, FieldType.STRING));
		fields.add(getField("x", "x", module, FieldType.NUMBER));
		fields.add(getField("y", "Y", module, FieldType.NUMBER));
		fields.add(getField("w", "W", module, FieldType.NUMBER));
		fields.add(getField("h", "H", module, FieldType.NUMBER));
		return fields;
	}
	
    public static List<FacilioField> getSmartControlKioskFields() {
        FacilioModule module = ModuleFactory.getSmartControlKioskModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getField("id", "ID", module, FieldType.NUMBER));
        fields.add(getField("spaceType", "SPACE_TYPE", module, FieldType.NUMBER));
        fields.add(getField("tenantId", "TENANT_ID", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getRuleRollupSummaryFields() {
        FacilioModule module = ModuleFactory.getRuleRollupSummaryModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("lastRolledUpDate", "LAST_ROLLED_UP_DATE", module, FieldType.NUMBER));

        return fields;
    }

    public static List<FacilioField> getAssetRollupSummaryFields() {
        FacilioModule module = ModuleFactory.getAssetRollupSummaryModule();
        List<FacilioField> fields = new ArrayList<>();

        fields.add(getIdField(module));
        fields.add(getField("lastRolledUpDate", "LAST_ROLLED_UP_DATE", module, FieldType.NUMBER));

        return fields;
    }
    
	public static List<FacilioField> getCommissioningLogFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		fields.add(getIdField(module));
		fields.addAll(getSystemPointFields(module));
		fields.add(getNewAgentIdField(module));
		fields.add(getField("publishedTime", "PUBLISHED_TIME", module, FieldType.DATE_TIME));
		fields.add(getField("controllerType", "CONTROLLER_TYPE", module, FieldType.NUMBER));
		fields.add(getField("pointJsonStr", "POINT_JSON", module, FieldType.STRING));
		fields.add(getField("clientMetaStr", "CLIENT_META_JSON", module, FieldType.STRING));
		return fields;
	}
	
	public static List<FacilioField> getCommissioningLogControllerFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getCommissioningLogControllerModule();
		fields.add(getField("commissioningLogId", "COMMISSIONING_LOG_ID", module, FieldType.LOOKUP));
		fields.add(getField("controllerId", "CONTROLLER_ID", module, FieldType.LOOKUP));
		return fields;
	}
	public static List<FacilioField> getBimIntegrationLogsFields()
	{
 		List<FacilioField> fields = new ArrayList<>();
 		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
 		fields.add(getIdField());
 		fields.add(getField("fileId","File Id","FILEID",module,FieldType.NUMBER));
 		fields.add(getField("fileName","File Name","FILE_NAME",module,FieldType.STRING));
 		fields.add(getField("noOfModules","No of Modules","NO_OF_MODULES",module,FieldType.NUMBER));
 		fields.add(getField("status","Status","STATUS",module,FieldType.NUMBER));
 		fields.add(getField("uploadedBy","Uploaded By","UPLOADED_BY",module,FieldType.NUMBER));
 		fields.add(getField("importedTime","Imported Time","IMPORTED_TIME",module,FieldType.NUMBER));
 		fields.add(getField("thirdParty","Third Party","THIRD_PARTY",module,FieldType.NUMBER));
 		return fields;
	}
	
	public static List<FacilioField> getBimImportProcessMappingFields()
	{
 		List<FacilioField> fields = new ArrayList<>();
 		FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
 		fields.add(getIdField());
 		fields.add(getField("bimId","Bim Id","BIM_ID",module,FieldType.NUMBER));
		fields.add(getField("importProcessId","Import Process Id","IMPORT_PROCESS_ID",module,FieldType.NUMBER));
		fields.add(getField("sheetName","Sheet Name","SHEET_NAME",module,FieldType.STRING));
		fields.add(getField("moduleName","Module Name","MODULE_NAME",module,FieldType.STRING));
 		fields.add(getField("status","Status","STATUS",module,FieldType.NUMBER));

 		return fields;
	}
	
	public static List<FacilioField> getBimDefaultValuesFields()
	{
 		List<FacilioField> fields = new ArrayList<>();
 		FacilioModule module = ModuleFactory.getBimDefaultValuesModule();
 		fields.add(getIdField());
 		fields.add(getField("bimId","Bim Id","BIM_ID",module,FieldType.NUMBER));
		fields.add(getField("moduleId","Module Id","MODULE_ID",module,FieldType.STRING));
		fields.add(getField("fieldId","Field Id","FIELD_ID",module,FieldType.STRING));
 		fields.add(getField("defaultValue","Default Value","DEFAULT_VALUE",module,FieldType.STRING));

 		return fields;
	}
	 public static List<FacilioField> getOperationAlarmHistoricalLogFields() {
	        
	        List<FacilioField> fields = new ArrayList<>();
	        FacilioModule module = ModuleFactory.getOperationAlarmHistoricalLogsModule();

	        fields.add(getIdField(module));
	        fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
	        fields.add(getField("parentId", "PARENT_ID", module, FieldType.NUMBER));
	        fields.add(getField("splitStartTime", "SPLIT_START_TIME", module, FieldType.NUMBER));
	        fields.add(getField("splitEndTime", "SPLIT_END_TIME", module, FieldType.NUMBER));
	        fields.add(getField("status", "STATUS", module, FieldType.NUMBER));
	        fields.add(getField("logState", "LOG_STATE", module, FieldType.NUMBER));
	        fields.add(getField("errorMessage", "ERROR_MESSAGE", module, FieldType.STRING));
	        fields.add(getField("calculationStartTime", "CALCULATION_START_TIME", module, FieldType.NUMBER));
	        fields.add(getField("calculationEndTime", "CALCULATION_END_TIME", module, FieldType.NUMBER));
	        return fields;

	    }
	 
	 public static List<FacilioField> getScopingConfigFields() {
		 List<FacilioField> fields = new ArrayList<>();
	        FacilioModule module = ModuleFactory.getScopingConfigModule();

	        fields.add(getIdField(module));
	        fields.add(getField("scopingId", "SCOPING_ID", module, FieldType.NUMBER));
	        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.NUMBER));
	        fields.add(getField("fieldName", "FIELD_NAME", module, FieldType.STRING));
	        fields.add(getField("operatorId", "OPERATOR_ID", module, FieldType.NUMBER));
	        fields.add(getField("value", "FIELD_VALUE", module, FieldType.STRING));
	        fields.add(getField("fieldValueGenerator", "FIELD_VALUE_GENERATOR", module, FieldType.STRING));
	         	      
	        return fields;
	 }
	 
	 public static List<FacilioField> getScopingFields() {
		 List<FacilioField> fields = new ArrayList<>();
	        FacilioModule module = ModuleFactory.getScopingModule();

	        fields.add(getIdField(module));
	        fields.add(getField("scopeName", "SCOPE_NAME", module, FieldType.STRING));
		     	      
	        return fields;
	 }
	 
	 public static List<FacilioField> getFieldModulePermissionFields() {
		 List<FacilioField> fields = new ArrayList<>();
	        FacilioModule module = ModuleFactory.getFieldModulePermissionModule();

	        fields.add(getIdField(module));
	        fields.add(getField("moduleId", "MODULE_ID", module, FieldType.NUMBER));
	        fields.add(getField("roleId", "ROLE_ID", module, FieldType.NUMBER));
	        fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
	        fields.add(getField("permissionType", "PERMISSION_TYPE", module, FieldType.ENUM));
	        fields.add(getField("subModuleId", "SUB_MODULE_ID", module, FieldType.NUMBER));
	        fields.add(getField("checktype", "CHECK_TYPE", module, FieldType.ENUM));
		      	     	      
	        return fields;
	 }
}
