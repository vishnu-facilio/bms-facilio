package com.facilio.fields.util;

import org.apache.kafka.common.protocol.types.Field;

import java.util.Arrays;
import java.util.List;

public class FieldsConfigList {
    public static final List<String> WORK_ORDER_FIELDS_INCLUDE = Arrays.asList( //SORT
            "actualWorkDuration", "subject", "actualWorkStart", "actualWorkEnd", "siteId", "approvalStatus",
            "assignedBy", "assignedTo", "assignmentGroup", "category",
            "createdBy", "createdTime", "totalCost", "dueDate",
            "modifiedTime", "noOfTasks", "noOfClosedTasks", "priority",
            "requester", "requestedBy", "resource", "scheduledStart",
            "sourceType", "moduleState", "type", "vendor",
            "tenant", "client", "sendForApproval", "responseDueDate",
            "siteId", "serialNumber", "isQuotationNeeded", "id",
            "serviceRequest", "noOfNotes", "noOfAttachments", "estimatedEnd",
            "description", "jobPlan", "pmV2", "parentWO"
    );

    public static final List<String> WORKORDER_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "subject", "localId",
            "category","description","dueDate", "responseDueDate",
            "moduleState", "sourceType", "assignedTo", "createdTime",
            "createdBy", "priority","resource","type",
            "modifiedTime","actualWorkStart","actualWorkEnd","totalCost",
            "siteId","vendor", "requester", "tenant", "client",
            "noOfNotes","noOfTasks","noOfAttachments","attachmentPreview",
            "jobPlan"
    );

    public static final List<String> WORKORDER_VIEW_FIXED_FIELDS = Arrays.asList(
            "subject", "localId"
    );

    public static final List<String> ASSET_FIELDS_INCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "category", "department", "description", "manufacturer",
            "name", "purchasedDate", "qrVal", "retireDate", "serialNumber",
            "state", "supplier", "tagNumber", "type", "space", "unitPrice",
            "warrantyExpiryDate", "distanceMoved", "connected", "sysCreatedTime",
            "sysCreatedBy", "sysModifiedTime", "sysModifiedBy", "moduleState",
            "siteId", "id", "rotatingItemType", "storeRoom"
    );

    public static final List<String> ASSET_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "hideToCustomer", "parentAssetId", "stateFlowId"
    );

    public static final List<String> NEW_ALARMS_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "acknowledged", "acknowledgedBy", "severity",
            "acknowledgedTime", "lastClearedTime", "lastCreatedTime", "lastOccurredTime",
            "resource", "rule", "readingAlarmCategory", "faultType",
            "id", "localId", "costImpact", "energyImpact"
    );

    public static final List<String> NEW_OP_ALARMS_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "severity", "lastClearedTime", "lastCreatedTime",
            "lastOccurredTime", "resource", "id", "localId"
    );

    public static final List<String> QUOTE_FIELDS_INCLUDE = Arrays.asList(  //ADVANCED_FILTER_FIELDS
            "subject", "description", "workorder", "tenant",
            "billDate", "expiryDate", "subTotal", "totalTaxAmount",
            "discountAmount", "shippingCharges", "adjustmentsCost", "miscellaneousCharges",
            "totalCost", "moduleState", "sysCreatedTime", "sysCreatedBy",
            "sysModifiedTime", "sysModifiedBy", "id", "localId",
            "client");

    public static final List<String> SENSOR_ALARM_FIELDS_TO_EXCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "readingFieldId", "type", "noOfNotes", "lastWoId",
            "lastOccurrenceId", "key", "description", "noOfOccurrences"
    );

    public static final List<String> NEW_READING_RULE_FIELDS_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "name", "description", "status", "createdTime",
            "assetCategoryId");

    public static final List<String> POINT_FIELDS_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "displayName", "name", "categoryId", "resourceId",
            "dataMissing", "interval", "createdTime", "lastRecordedTime",
            "lastRecordedValue", "mappedTime", "configureStatus", "subscribeStatus",
            "writable"
    );

    public static final List<String> PM_FIELDS_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "name", "type", "priority", "assignmentType",
            "category", "spaceCategory", "assetCategory", "isActive",
            "subject", "tenant", "vendor", "subject",
            "description", "sysCreatedTime", "sites", "assignmentGroup",
            "leadTime", "pmStatus");

    public static final List<String> OUTGOING_MAIL_LOGGER_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "subject", "from", "to", "cc",
            "bcc", "sysCreatedBy", "sysModifiedBy", "sysCreatedTime",
            "sysModifiedTime", "recordId", "recordsModuleId", "recordCreatedTime",
            "sourceType", "recipientCount", "inProgressCount", "deliveredCount",
            "bouncedCount", "mailStatus");

    public static final List<String> WORKFLOW_LOG_FIELDS_INCLUDE = Arrays.asList(
            "recordId", "recordModuleId", "status", "logType"
    );

    public static final List<String> BASEMAIL_LOG_FIELDS_INCLUDE = Arrays.asList(
            "bcc", "cc", "contentType", "from", "recipient",
            "subject", "to", "status", "parentRecordId"
    );


    public static final List<String> AGENT_ALARM_FILTER_FIELDS_INCLUDE = Arrays.asList(
            "agent", "agentAlarmType", "severity", "lastClearedTime",
            "lastCreatedTime", "lastOccurredTime");

    public static final List<String> AGENT_ALARM_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "subject", "lastOccurredTime", "lastCreatedTime", "acknowledged",
            "readingalarmcategory", "acknowledgedBy", "acknowledgedTime", "severity",
            "noOfOccurrences", "resource"
            //"asset","space"

    );

    public static final List<String> AGENT_ALARM_VIEW_FIXED_FIELDS = Arrays.asList(
            "subject", "lastOccurredTime", "lastCreatedTime",
            "acknowledged", "readingalarmcategory", "acknowledgedBy",
            "acknowledgedTime", "severity");

    public static final List<String> AGENT_ALARM_VIEW_FIXED_SELECTABLE_FIELDS = Arrays.asList("noOfOccurrences");


    public static final List<String> AGENT_FIELDS_INCLUDE = Arrays.asList(
            "connected", "displayName", "interval", "lastModifiedTime",
            "createdTime", "lastDataReceivedTime", "siteId", "writable",
            "deletedTime", "agentType", "name"
    );

    public static final List<String> SHIFT_FIELDS_INCLUDE = Arrays.asList(
            "name", "startTime", "endTime", "defaultShift",
            "isActive", "colorCode", "moduleState"
    );

    public static final List<String> BREAK_FIELDS_INCLUDE = Arrays.asList(
            "name", "breakType", "shifts"
    );

    public static final List<String> WORKFLOW_RULE_LOGS_FIELDS_INCLUDE = Arrays.asList(
            "workflowRuleName", "workflowLoggableRuleType", "ruleStatus", "executedBy",
            "recordId"
    );

    public static final List<String> SITE_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "space1", "space2", "space3", "space4",
            "building", "floor", "controllerId", "sourceType",
            "sourceId", "operatingHour"
    );

    public static final List<String> SITE_VIEW_FIXED_FIELDS = Arrays.asList(
            "name", "id"
    );

    public static final List<String> BUILDING_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "space1", "space2", "space3", "space4",
            "building", "floor", "controllerId", "sourceType",
            "sourceId", "operatingHour"
    );

    public static final List<String> BUILDING_VIEW_FIXED_FIELDS = Arrays.asList(
            "name", "id"
    );

    public static final List<String> SERVICE_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "status", "description", "duration", "sellingPrice",
            "buyingPrice", "paymentType"
    );
    public static final List<String> TRANSFER_REQUEST_VIEW_FIXED_FIELDS = Arrays.asList(
            "requestSubject", "id"
    );
    public static final List<String> CONTROLLER_FIELDS_TO_EXCLUDE = Arrays.asList(
            "description", "photoId", "resourceType", "space",
            "controllerId", "qrVal", "operatingHour", "sourceType",
            "sourceId", "failureClass", "state", "type",
            "category", "parentAssetId", "department", "manufacturer",
            "model", "serialNumber", "tagNumber", "partNumber",
            "unitPrice", "supplier", "purchasedDate", "retireDate",
            "warrantyExpiryDate", "localId", "hideToCustomer", "geoLocation",
            "currentLocation", "boundaryRadius", "designatedLocation", "distanceMoved",
            "geoLocationEnabled", "identifiedLocation", "rotatingItem", "rotatingTool",
            "purchaseOrder", "isUsed", "connected", "downtimeStatus",
            "lastDowntimeId", "moduleState", "stateFlowId", "salvageAmount",
            "currentPrice", "approvalStatus", "approvalFlowId", "classification",
            "agentId", "lastIssuedToUser", "lastIssuedToWo", "lastIssuedTime",
            "moveApprovalNeeded", "currentSpaceId", "controllerType", "commissionedTime",
            "decommission"
    );
    public static final List<String> WORKPERMIT_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "ticket", "approvalFlowId", "stateFlowId", "isRecurring",
            "recurringInfoId", "permitType");
    public static final List<String> RAW_ALARM_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "message", "site", "client", "controller", "alarmDefinition",
            "alarmCategory", "alarmType", "strategy","asset", "parentAlarm", "occurredTime",
            "clearedTime", "filtered", "processed", "sysCreatedByPeople", "sysCreatedTime"
    );

    public static final List<String> ALARM_DEFINITION_MAPPING_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "alarmDefinition", "regularExpression",
            "priority", "client", "sysCreatedTime", "sysCreatedByPeople",
            "sysModifiedTime", "sysModifiedByPeople"
    );

    public static final List<String> ALARM_DEFINITION_TAGGING_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "alarmDefinition", "client", "alarmType",
            "alarmCategory", "controllerType", "sysCreatedTime",
            "sysCreatedByPeople", "sysModifiedTime", "sysModifiedByPeople"
    );

    public static final List<String> ALARM_TYPE_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "description", "linkName", "sysCreatedTime",
            "sysCreatedByPeople", "sysModifiedTime", "sysModifiedByPeople"
    );

    public static final List<String> ALARM_CATEGORY_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "description", "client", "sysCreatedTime",
            "sysCreatedByPeople", "sysModifiedTime", "sysModifiedByPeople"

    );

    public static final List<String> ALARM_FILTER_RULE_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "description", "client", "alarmType",
            "strategy", "filterType", "priority", "status", "filterType",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> FILTERED_ALARM_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "message", "controller", "client", "site",
            "alarmType", "alarmCategory","asset", "occurredTime", "clearedTime",
            "rawAlarm", "alarmFilterRule", "flaggedEvent"

    );

    public static final List<String> FLAGGED_EVENT_RULE_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "description", "executionType", "priority",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> FLAGGED_EVENT_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "site", "client", "controller", "flaggedEventRule",
            "status", "workorder", "assignedPeople", "team",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> UTILITY_INTEGRATION_CUSTOMER_FIELDS_EXCLUDE = Arrays.asList(
            "userUid", "templateUid", "secretState","meta","isArchived","isExpired","isRevoked","formUid","localId"
    );
    public static final List<String> UTILITY_INTEGRATION_TARIFF_FIELDS_EXCLUDE = Arrays.asList(
            "fuelSurcharge", "flatRatePerUnit", "unit","localId"
    );

    public static final List<String> UTILITY_DISPUTE_FIELDS_EXCLUDE = Arrays.asList(
            "approvalFlowId", "approvalStatus", "stateFlowId","localId"
    );

    public static final List<String> UTILITY_INTEGRATION_BILLS_FIELDS_EXCLUDE = Arrays.asList(
            "stateFlowId","approvalFlowId","approvalStatus","localId","meta","sourceDownloadUrl","sourceUrl","sourceType","serviceAddress","sourceType","sourceUrl","supplierServiceId"
    );

    public static List<String> getNameField() {
        return Arrays.asList("name");
    }
}
