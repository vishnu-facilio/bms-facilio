package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.ResourceContext;

import java.util.List;
import java.util.Map;

public interface ReadingRuleInterface {
    long getId();

    void setName(String name);

    String getName();

    String getDescription();

    boolean isActive();

    void setMatchedResources(Map<Long, ResourceContext> matchedResources);

    List<ActionContext> getActions();

    void setActions(List<ActionContext> actions);
}
