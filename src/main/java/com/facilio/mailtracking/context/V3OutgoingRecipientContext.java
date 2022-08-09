package com.facilio.mailtracking.context;

import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class V3OutgoingRecipientContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3OutgoingMailLogContext logger;
    private String name;
    private String recipient;
    private Integer status;
    private Integer bounceType;
    private String bounceReason;
    private String diagnosticCode;

}