package com.facilio.readingrule.rca.context;

import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ReadingRuleRCAContext extends V3Context {

    Long ruleId;

    Long dataSetInterval;

    Long ruleInterval;

    Long readingModuleId;

    List<Long> rcaRuleIds;

    List<NewReadingRuleContext> rcaRules;

    List<RCAGroupContext> groups;

    {
        groups = new ArrayList<>();
    }
}
