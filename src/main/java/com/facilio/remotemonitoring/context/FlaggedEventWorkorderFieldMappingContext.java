package com.facilio.remotemonitoring.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FlaggedEventWorkorderFieldMappingContext implements Serializable {
    long orgId;
    String leftFieldName;
    Long leftFieldId;
    Long rightFieldId;
    Long parentId;
    String valueText;
}
