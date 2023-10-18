package com.facilio.bmsconsoleV3.context.quotation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberFormatContext {
    private static final long serialVersionUID = 1L;
    Long Id;
    Long orgId;
    private Boolean enableNumberFormat;
    private Integer numberOfDecimal;
    private Boolean canTruncateValue;

}