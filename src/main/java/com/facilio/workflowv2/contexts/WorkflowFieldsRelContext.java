package com.facilio.workflowv2.contexts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowFieldsRelContext implements Comparable<WorkflowFieldsRelContext> {
    long moduleId;
    long fieldId;
    long workflowId;

    @Override
    public int compareTo(WorkflowFieldsRelContext relContext) {
        if(relContext.getFieldId() != this.fieldId){
            return 1;
        }
        return 0;
    }
}
