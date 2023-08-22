package com.facilio.workflowv2.contexts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowNameSpaceRelContext implements Comparable<WorkflowNameSpaceRelContext> {
    private long workflowId;
    private long functionId;

    @Override
    public int compareTo(WorkflowNameSpaceRelContext relContext) {
        if(relContext.getFunctionId() != this.functionId){
            return 1;
        }
        return 0;
    }
}
