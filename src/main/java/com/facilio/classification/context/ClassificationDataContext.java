package com.facilio.classification.context;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassificationDataContext extends V3Context {
    private ModuleBaseWithCustomFields record;
    private ClassificationAttributeContext attribute;
    private long numberValue = -1L;
    private double decimalValue = -1L;
    private String textValue;
    private String textAreaValue;
    private Boolean booleanValue = null;
    private long dateTimeValue = -1L;
}
