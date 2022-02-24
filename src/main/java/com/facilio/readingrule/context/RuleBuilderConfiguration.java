package com.facilio.readingrule.context;

import lombok.Getter;
import lombok.Setter;
import com.facilio.ns.context.NameSpaceContext;

@Getter
@Setter
public class RuleBuilderConfiguration {
    Long id;
    Long orgId;
    Long ruleId;
    String script;

    NameSpaceContext ns;

    public void setNullForResponse() {
        ns.setNullForResponse();
    }
}