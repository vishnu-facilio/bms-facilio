package com.facilio.workflowv2.contexts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowFieldsRelContext {
    long moduleId;
    long fieldId;
    long workflowId;
}
