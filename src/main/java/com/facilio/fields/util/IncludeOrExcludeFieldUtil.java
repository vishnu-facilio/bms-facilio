package com.facilio.fields.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IncludeOrExcludeFieldUtil {

    public static final List<String> WORK_ORDER_FIELDS_INCLUDE = Collections.unmodifiableList(Arrays.asList(new String[]{
            "actualWorkDuration",
            "subject",
            "actualWorkStart",
            "actualWorkEnd",
            "assignedBy",
            "assignedTo",
            "assignmentGroup",
            "category",
            "createdBy",
            "createdTime",
            "totalCost",
            "dueDate",
//                "trigger",
            "modifiedTime",
            "noOfTasks",
            "noOfClosedTasks",
            "priority",
            "requester",
            "requestedBy",
            "resource",
            "scheduledStart",
            "sourceType",
            "moduleState",
            "type",
            "vendor",
            "tenant",
            "client",
            "sendForApproval",
//                "prerequisiteEnabled",
//                "preRequestStatus",
            "responseDueDate",
            "siteId",
            "serialNumber",
            "isQuotationNeeded",
            "id",
            "serviceRequest",
            "noOfNotes",
            "noOfAttachments",
            "estimatedEnd",
            "description",
            "jobPlan",
            "pmV2",
            "parentWO"
    }));
}
