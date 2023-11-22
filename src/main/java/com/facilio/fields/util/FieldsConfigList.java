package com.facilio.fields.util;

import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
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
    public static final List<String> FLOOR_VIEW_FIXED_FIELDS = Arrays.asList(
            "name"
    );

    public static final List<String> ASSET_FIELDS_INCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","category","sysCreatedBy","sysCreatedTime", "department","id",
            "decommission","manufacturer",
            "sysModifiedTime", "sysModifiedBy", "purchasedDate","rotatingItemType",
            "siteId","space","storeRoom",
            "type","unitPrice","decommissionedBy","moduleState"
    );
    public static final List<String> ASSET_FILTER_FIELDS_EXCLUDE = Arrays.asList( // ADVANCED_FILTER_FIELDS
            "name","category","sysCreatedBy","sysCreatedTime", "department","id",
            "decommission","manufacturer",
            "sysModifiedTime", "sysModifiedBy", "purchasedDate",
            "siteId",
            "type","unitPrice","decommissionedBy","moduleState"
    );
    public static final List<String> SITE_FIELDS_INCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","area","client","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","classification",
            "grossFloorArea","decommission","siteType","id","moduleState","location","territory","decommissionedBy","managedBy"
    );

    public static final List<String> SITE_FILTER_FIELDS_EXCLUDE = Arrays.asList("name","area","client","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime",
            "grossFloorArea","decommission","siteType","id","moduleState","territory","decommissionedBy","managedBy");
    public static final List<String> BUILDING_FIELDS_INCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","area","client","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","classification",
            "managedBy","decommission","site","id","moduleState","location","decommissionedBy"
    );

    public static final List<String> BUILDING_FILTER_FIELDS_EXCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","area","client","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime",
            "managedBy","decommission","id","moduleState","decommissionedBy"
    );
    public static final List<String> SPACE_FIELDS_INCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","site","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","classification",
            "floor","decommission","building","id","moduleState","location","decommissionedBy"
    );

    public static final List<String> SPACE_FILTER_FIELDS_EXCLUDE = Arrays.asList( //SORT, ADVANCED_FILTER_FIELDS
            "name","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime",
            "decommission","id","moduleState","decommissionedBy"
    );

    public static final List<String> SITE_SUMMARY_FIELDS_INCLUDE = Arrays.asList( //Summary fields
            "description","managedBy","siteType","grossFloorArea",
            "area","client","location","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","territory","decommissionedBy"
    );
    public static final List<String> BUILDING_SUMMARY_FIELDS_INCLUDE = Arrays.asList( //Summary fields
            "description","managedBy",
            "area","location","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","site","decommissionedBy"
    );
    public static final List<String> FLOOR_SUMMARY_FIELDS_INCLUDE = Arrays.asList( //Summary fields
            "description","managedBy",
            "building","site","location","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","decommissionedBy"
    );
    public static final List<String> SPACE_SUMMARY_FIELDS_INCLUDE = Arrays.asList(  //Summary fields
            "description","floor",
            "site","building","location","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","decommissionedBy"
    );
    public static final List<String> ASSET_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "hideToCustomer", "parentAssetId", "stateFlowId"
    );
    public static final List<String> ASSET_VIEW_FIELDS = Arrays.asList(
            "name","category","department","manufacturer","moduleState",
            "purchasedDate","rotatingItemType","storeRoom",
            "type","unitPrice","decommissionedBy","decommission",
            "sysCreatedBy","sysCreatedTime","sysModifiedTime", "sysModifiedBy",
            "siteId"
    );

    public static final List<String> ASSET_SUMMARY_FIELDS_INCLUDE = Arrays.asList( //Summary fields
            "description","category","type","department","storeRoom",
            "manufacturer","unitPrice","site","rotatingItemType",
            "sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","decommissionedBy"
    );

    public static final List<String> NEW_ALARMS_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "acknowledged", "acknowledgedBy", "severity",
            "acknowledgedTime", "lastClearedTime", "lastCreatedTime", "lastOccurredTime",
            "resource", "rule", "readingAlarmCategory", "faultType",
            "id", "localId", "costImpact", "energyImpact"
    );

    public static final List<String> BMS_ALARM_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "acknowledgedBy", "acknowledgedTime", "description", "resource", "severity", "lastOccurredTime", "lastClearedTime", "lastOccurrenceId",
            "lastCreatedTime", "noOfOccurrences", "lastWoId", "condition", "source", "controller", "alarmClass"
    );

    public static final List<String> SENSOR_ALARM_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "acknowledgedBy", "acknowledgedTime", "description", "resource", "severity", "lastOccurredTime", "lastClearedTime", "lastOccurrenceId",
            "lastCreatedTime", "noOfOccurrences", "lastWoId", "readingFieldId"
    );

    public static final List<String> NEW_READING_ALARM_FIELDS_INCLUDE = Arrays.asList( //SORT
            "subject", "acknowledgedBy", "acknowledgedTime", "description", "resource", "severity", "lastOccurredTime", "lastClearedTime", "lastOccurrenceId",
            "lastCreatedTime", "noOfOccurrences", "lastWoId", "rule", "readingAlarmCategory", "faultType", "energyImpact",
            "costImpact", "readingAlarmAssetCategory"
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
            "name", "description", "status", "createdTime", "impact", "siteId", "status",
            "sysCreatedTime", "sysCreatedBy", "sysModifiedTime", "sysModifiedBy", "assetCategory"
    );

    public static final List<String> POINT_FIELDS_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "displayName", "name", "categoryId", "resourceId",
            "dataMissing", "interval", "createdTime", "lastRecordedTime",
            "lastRecordedValue", "mappedTime", "configureStatus", "subscribeStatus",
            "writable"
    );

    public static final List<String> PM_FIELDS_INCLUDE = Arrays.asList( //ADVANCED_FILTER_FIELDS
            "name", "type", "priority", "assignmentType",
            "category", "spaceCategory", "assetCategory", "isActive",
            "subject", "tenant", "vendor",
            "description", "sysCreatedTime", "sites", "assignmentGroup",
            "leadTime", "pmStatus");

    public static final List<String> PM_VIEW_FIELDS_INCLUDE = Arrays.asList( //VIEW FIELDS
            "name", "type", "priority", "assignmentType",
            "category", "spaceCategory", "assetCategory",
            "subject", "tenant", "vendor",
            "description", "sysCreatedTime", "sites", "assignmentGroup",
            "leadTime", "pmStatus",
            "modifiedTime", "sysModifiedBy", "estimatedDuration","dueDuration", "createdBy");

    public static final List<String> PM_VIEW_FIXED_FIELDS_INCLUDE = Arrays.asList( //FIXED VIEW FIELDS
            "name", "subject");


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
            "name"
    );

    public static final List<String> BUILDING_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "space1", "space2", "space3", "space4",
            "building", "floor", "controllerId", "sourceType",
            "sourceId", "operatingHour"
    );

    public static final List<String> BUILDING_VIEW_FIXED_FIELDS = Arrays.asList(
            "name"
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
            "decommission","writable","viewRecommissionedBtn","storeRoom"
    );
    public static final List<String> CONTROLLER_FIELDS_TO_INCLUDE = Arrays.asList(
            "name","description","category","sysCreatedBy","createdTime","sysModifiedBy","sysModifiedTime","identifiedLocation"
    );
    public static final List<String> WORKPERMIT_VIEW_FIELDS_EXCLUDE = Arrays.asList(
            "ticket", "approvalFlowId", "stateFlowId", "isRecurring",
            "recurringInfoId", "permitType");
    public static final List<String> RAW_ALARM_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "message", "site", "client", "controller", "alarmDefinition",
            "alarmCategory", "alarmType", "alarmApproach","asset", "parentAlarm", "occurredTime",
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
            "alarmApproach", "filterType", "priority", "status", "filterType",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> FILTERED_ALARM_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "message", "controller", "client", "site",
            "alarmType", "alarmCategory","asset", "occurredTime", "clearedTime",
            "alarm", FilteredAlarmModule.ALARM_FILTER_RULE_FIELD_NAME, FilteredAlarmModule.FLAGGED_ALARM_FIELD_NAME

    );

    public static final List<String> FLAGGED_EVENT_RULE_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "name", "description", "executionType", "priority",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> FLAGGED_EVENT_FIELDS_TO_INCLUDE = Arrays.asList(
            "id", "site", "client", "controller", FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,
            "status", "workorder", "assignedPeople", "team", "asset",
            "sysCreatedTime", "sysCreatedByPeople", "sysModifiedTime",
            "sysModifiedByPeople"
    );

    public static final List<String> UTILITY_INTEGRATION_CUSTOMER_FIELDS_EXCLUDE = Arrays.asList(
            "userUid", "templateUid", "secretState","meta","isArchived","isExpired","isRevoked","formUid","localId"
    );
    public static final List<String> UTILITY_INTEGRATION_TARIFF_FIELDS_EXCLUDE = Arrays.asList(
            "fuelSurcharge", "flatRatePerUnit", "unit","localId","fromDate","toDate"
    );

    public static final List<String> UTILITY_DISPUTE_FIELDS_EXCLUDE = Arrays.asList(
            "approvalFlowId", "approvalStatus", "stateFlowId","localId"
    );

    public static final List<String> UTILITY_INTEGRATION_BILLS_FIELDS_EXCLUDE = Arrays.asList(
            "stateFlowId","approvalFlowId","approvalStatus","localId","meta","sourceDownloadUrl","sourceUrl","sourceType","serviceAddress","sourceType","sourceUrl","supplierServiceId"
    );
    public static final List<String> SITE_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "name","client", "grossFloorArea", "managedBy", "area",
            "siteType", "classification", "moduleState", "decommission",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );

    public static final List<String> BUILDING_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "grossFloorArea", "area","name",
            "site", "classification", "decommission","moduleState",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );
    public static final List<String>  SPACE_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "site", "classification", "decommission","moduleState","building","floor","name",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );
    public static final List<String>  SPACE_VIEW_FIXED_FIELDS = Arrays.asList(
           "name"
    );
    public static final List<String> FLOOR_FIELDS_INCLUDE = Arrays.asList(
            "grossFloorArea", "area","name",
            "site", "classification", "decommission","moduleState",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );

    public static final List<String> FLOOR_FILTER_FIELDS_EXCLUDE = Arrays.asList(
            "grossFloorArea", "area","name",
            "decommission","moduleState",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> FLOOR_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "building", "site", "classification", "decommission","moduleState",
            "decommissionedBy", "sysCreatedBy", "sysCreatedTime", "sysModifiedBy","sysModifiedTime"
    );

    public static final List<String> SERVICE_REQUEST_FIELDS = Arrays.asList(
            "subject","description","moduleState","assignmentGroup","assignedTo","requester","dueDate","classificationType","urgency","resource","requestType","ratingVal","sysCreatedTime","sysCreatedBy","sysModifiedTime","sysModifiedBy","approvalStatus","mode","responseDueDate","affectedPerson","reportedDate","affectedDate","actualStartDate","actualFinishDate","sourceType"
    );

    public static final List<String> INVENTORY_REQUEST_FIELDS = Arrays.asList(
            "name","description","status","requestedTime","requiredTime","requestedBy","localId","storeRoom","requestedFor","isIssued","moduleState","approvalStatus","sysCreatedTime","sysCreatedBy","sysModifiedTime","sysModifiedBy","transactionType","inventoryRequestReservationStatus","workorder"
    );
    public static final List<String> FACILITY_FIELDS = Arrays.asList(
            "localId","name","manager","description","userGuidance","facilityType","slotGeneratedUpto","bookingAdvancePeriodInDays","allowCancellationBefore","location","category","usageCapacity","slotDuration","maxSlotBookingAllowed","maxAttendeeCountPerBooking","isChargeable","securityDeposit","pricePerSlot","isAttendeeListNeeded","approvalStatus","moduleState","isMultiBookingPerSlotAllowed","amenities","sysCreatedTime","sysCreatedBy","sysModifiedTime","sysModifiedBy","isbookable"
    );
    public static final List<String> BOOKING_FIELDS = Arrays.asList(
            "description","localId","bookingDate","reservedFor","noOfAttendees","facility","isCancelled","canEdit","bookingAmount","moduleState","tenant","bookingRequestedBy","approvalStatus","internalAttendees","sysCreatedTime","sysCreatedBy","sysModifiedTime","sysModifiedBy","bookingtype"
    );

    public static final List<String> METER_FIELDS_EXCLUDE = Arrays.asList(
            "qrVal","approvalFlowId", "approvalStatus"
    );

    public static final List<String> VIRTUAL_METER_TEMPLATE_FIELDS_INCLUDE = Arrays.asList(
            "name","utilityType","scope","relationShipId","vmTemplateStatus","sysModifiedTime","sysModifiedByPeople","spaceCategory","assetCategory","sysCreatedTime","sysCreatedByPeople"
    );
    public static final List<String> CONTROL_ACTION_FIELDS_INCLUDE = Arrays.asList(
            "sysModifiedTime","sysModifiedByPeople","sysCreatedTime","sysCreatedByPeople"
    );
    public static final List<String> CONTROL_ACTION_FIELDS_EXCLUDE = Arrays.asList(
            "description","siteCriteriaId","assetCriteriaId","controllerCriteriaId","approvalStatus","approvalFlowId",
            "localId","firstLevelApproval","secondLevelApproval"
    );
    public static final List<String> CONTROL_ACTION_TEMPLATE_FIELDS_INCLUDE = Arrays.asList(
            "controlActionExecutionType","sysModifiedTime","sysModifiedByPeople","sysCreatedTime","sysCreatedByPeople"
    );
    public static final List<String> CONTROL_ACTION_TEMPLATE_FIELDS_EXCLUDE = Arrays.asList(
            "description","siteCriteriaId","assetCriteriaId","controllerCriteriaId","approvalStatus","approvalFlowId",
            "description","controlActionSourceType","scheduledActionDateTime","revertActionDateTime","siteCriteriaId",
            "assetCriteriaId","controllerCriteriaId","approvalStatus","approvalFlowId","controlActionStatus","scheduleActionStatus",
            "revertActionStatus","controlActionExecutionType","isEnableRevert","localId","firstLevelApproval","secondLevelApproval",
            "controlActionTemplate"
    );
    public static final List<String> CALENDAR_FIELDS_INCLUDE = Arrays.asList(
            "name","calendarType","client","sysCreatedByPeople","sysModifiedByPeople","sysCreatedTime","sysModifiedTime"
    );
    public static final List<String> CALENDAR_EVENTS_FIELDS_INCLUDE = Arrays.asList(
            "name","eventType","eventFrequency","validityStartTime","validityEndTime","sysCreatedByPeople","sysModifiedByPeople","sysCreatedTime","sysModifiedTime"
    );
    public static final List<String> CALENDAR_EVENTS_SORT_FIELDS_INCLUDE = Arrays.asList(
            "name","eventType","eventFrequency","sysCreatedByPeople","sysModifiedByPeople","sysCreatedTime","sysModifiedTime"
    );
    public static final List<String> APPOINTMENT_SORT_INCLUDE = Arrays.asList(
            "name","site","territory","client","category","priority","space","asset","fieldAgent","status","vendor","scheduledStartTime","scheduledEndTime","resolutionDueDuration","resolutionDueTime","resolutionDueStatus","estimatedCost","actualCost","actualStartTime","actualEndTime","actualDuration","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> APPOINTMENT_FIELDS_INCLUDE = Arrays.asList(
            "name","site","territory","client","category","priority","space","asset","fieldAgent","status","vendor","scheduledStartTime","scheduledEndTime","resolutionDueDuration","resolutionDueTime","resolutionDueStatus","estimatedCost","actualCost","actualStartTime","actualEndTime","actualDuration","mismatch","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> SERVICE_ORDER_SORT_FIELDS_INCLUDE = Arrays.asList(
            "name","territory","client","category","maintenanceType","priority","space","asset","fieldAgent","status","vendor","preferredStartTime","preferredEndTime","resolutionDueDuration","resolutionDueDate","resolutionDueStatus","sourceType","actualStartTime","actualEndTime","actualDuration","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> SERVICE_ORDER_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "name","site","territory","client","category","maintenanceType","priority","space","asset","fieldAgent","status","vendor","preferredStartTime","preferredEndTime","autoCreateSa","resolutionDueDuration","resolutionDueDate","resolutionDueStatus","sourceType","actualStartTime","actualEndTime","actualDuration","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> SERVICE_ORDER_FILTER_FIELDS_INCLUDE = Arrays.asList(
            "name","site","territory","client","category","maintenanceType","priority","space","asset","fieldAgent","status","vendor","preferredStartTime","preferredEndTime","resolutionDueDuration","resolutionDueDate","resolutionDueStatus","sourceType","actualStartTime","actualEndTime","actualDuration","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> TIME_SHEET_FIELDS_INCLUDE = Arrays.asList(
            "code","startTime","endTime","fieldAgent","serviceAppointment","serviceOrder"
    );
    public static final List<String> TRIP_FIELDS_INCLUDE = Arrays.asList(
            "code","people","startTime","endTime","startLocation","serviceOrder"
    );
    public static final List<String> EMPLOYEE_SORT_FIELDS_INCLUDE = Arrays.asList(
            "name","phone","email","mobile","currentFreeCapacity","rate","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> EMPLOYEE_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "name","avatar","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","rate","dispatchable","trackGeoLocation","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","department","isAssignable","isAppAccess","isLabour","space"
    );
    public static final List<String> EMPLOYEE_FILTER_FIELDS_INCLUDE = Arrays.asList(
            "name","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","rate","dispatchable","trackGeoLocation","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","department","isAssignable","isAppAccess","isLabour","space"
    );

    public static final List<String> VENDOR_CONTACT_SORT_FIELDS_INCLUDE = Arrays.asList(
            "name","phone","email","mobile","currentFreeCapacity","rate","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"
    );

    public static final List<String> VENDOR_CONTACT_VIEW_FIELDS_INCLUDE = Arrays.asList(
            "name","avatar","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","rate","dispatchable","trackGeoLocation","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","vendor","isPrimaryContact","isVendorPortalAccess","isLabour"
    );
    public static final List<String> VENDOR_CONTACT_FILTER_FIELDS_INCLUDE = Arrays.asList(
            "name","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","rate","dispatchable","trackGeoLocation","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime","vendor","isPrimaryContact","isVendorPortalAccess","isLabour"
    );

    public static final List<String> TENANT_MODULE_VIEW_FIELDS_INCLUDE= Arrays.asList("name","inTime","outTime","primaryContactEmail","primaryContactName",
            "primaryContactPhone","status","sysCreatedTime","sysModifiedTime","tenantType","siteId");

    public static final List<String> TENANT_MODULE_SUMMARY_FIELDS_INCLUDE= Arrays.asList("siteId","tenantType","sysCreatedTime","sysModifiedTime","address");
    public static final List<String> TENANT_MODULE_ADVANCED_FIELDS_INCLUDE= Arrays.asList("name","localId","inTime","outTime","primaryContactEmail","primaryContactName",
            "primaryContactPhone","status","sysCreatedTime","sysModifiedTime","tenantType","siteId","address");

    public static final List<String> CLIENT_MODULE_FIELDS_ADVANCE_SEARCH_INCLUDE = Arrays.asList(
            "name","sysCreatedTime","sysCreatedBy","sysModifiedBy","sysModifiedTime","moduleState","id","address"
    );
    public static final List<String> CLIENT_MODULE_FIELDS_VIEW_SEARCH_INCLUDE = Arrays.asList(
            "name","sysCreatedTime","sysCreatedBy","sysModifiedBy","sysModifiedTime","moduleState","primaryContactEmail","primaryContactPhone","primaryContactName"
    );
    public static final List<String> CLIENT_MODULE_SKIP_ONE_LEVEL_FIELDS = Arrays.asList(
            "moduleState"
    );
    public static final List<String> CLIENT_MODULE_SORT_FIELDS_INCLUDE = Arrays.asList(
            "name","sysCreatedTime","sysCreatedBy","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> CLIENT_CONTACT_MODULE_FIELDS_ADVANCE_SEARCH_INCLUDE = Arrays.asList(
            "name","id","client"
    );
    public static final List<String> CLIENT_CONTACT_MODULE_FIELDS_VIEW_SEARCH_INCLUDE = Arrays.asList(
            "name","sysCreatedTime","sysCreatedBy","sysModifiedBy","sysModifiedTime","client","isClientPortalAccess","isPrimaryContact","moduleState"
    );
    public static final List<String> CLIENT_CONTACT_MODULE_SORT_FIELDS_SEARCH_INCLUDE=Arrays.asList(
            "name","id","sysCreatedTime","sysCreatedBy","sysModifiedBy","sysModifiedTime"
    );
    public static final List<String> CLIENT_COMTACT_MODULE_SKIP_ONE_LEVEL_FIELDS = Arrays.asList(
            "locatedSpace","moduleState","endLocation","startLocation"
    );
    public static List<String> getNameField() {
        return Arrays.asList("name");
    }
}
