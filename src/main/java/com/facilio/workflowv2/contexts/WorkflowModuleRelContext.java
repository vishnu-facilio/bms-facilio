package com.facilio.workflowv2.contexts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowModuleRelContext implements Comparable<WorkflowModuleRelContext> {
    private long workflowId;
    private long moduleId;

    @Override
    public int compareTo(WorkflowModuleRelContext relContext) {
        if(relContext.getModuleId() != this.moduleId){
            return 1;
        }
        return 0;
    }
}

