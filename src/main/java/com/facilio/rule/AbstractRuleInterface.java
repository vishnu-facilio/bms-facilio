package com.facilio.rule;

import com.facilio.ns.context.NameSpaceContext;
import com.facilio.workflows.context.WorkflowContext;

public interface AbstractRuleInterface {

    long getId();

    long getOrgId();

    Long getWorkflowId();

//    void setWorkflow(WorkflowContext workflow);
//
//    WorkflowContext getWorkflow();

    void setNs(NameSpaceContext ns);

    NameSpaceContext getNs();

}
