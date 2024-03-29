package com.facilio.mailtracking.context;

import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import lombok.Data;

@Data
public class V3OutgoingMailLogContext extends BaseMailMessageContext {
    private static final long serialVersionUID = 1L;

    private Long mapperId;
    private Long recordId;
    private Long recordsModuleId;
    private Long recordCreatedTime;
    private MailSourceType sourceType;
    private MailEnums.MailStatus mailStatus;
    private String maskUrl;

    //to keep original mail format
    private String originalTo;
    private String originalCc;
    private String originalBcc;

    //record id module name
    private String recordsModuleName;

    //Rollup fields - automated
    private int recipientCount;
    private int deliveredCount;
    private int bouncedCount;
    private int inProgressCount;
    private int sentCount;

}
