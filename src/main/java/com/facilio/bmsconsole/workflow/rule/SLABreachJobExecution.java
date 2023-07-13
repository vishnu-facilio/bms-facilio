package com.facilio.bmsconsole.workflow.rule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SLABreachJobExecution implements Serializable {
    long id;
    long recordId;
    long moduleId;
    long slaEntityId;
    Long dueDateValue;
    long slaPolicyId;
}
