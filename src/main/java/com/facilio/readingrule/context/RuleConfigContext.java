package com.facilio.readingrule.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleConfigContext {
    Long id;
    Long orgId;
    Long ruleId;
    Long workflowId;
    int occurrences;
}
