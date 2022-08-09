package com.facilio.mailtracking.context;

import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import lombok.Data;

import java.util.Map;

@Data
public class V3OutgoingMailLogContext extends BaseMailMessageContext {
    private static final long serialVersionUID = 1L;

    private Long mapperId;
    private Long recordId;
    private Long recordsModuleId;
    private String sender;
    private String message;
    private MailSourceType sourceType;

    //to keep original mail format
    private Map<String, String> originalTo;
    private Map<String, String> originalCc;
    private Map<String, String> originalBcc;

    //record id module name
    private String recordsModuleName;

    //Rollup fields - automated
    private int recipientCount;
    private int deliveredCount;
    private int bouncedCount;
    private int inProgressCount;
    private int sentCount;

}
