package com.facilio.bmsconsole.workflow.rule;

public interface ReadingRuleInterface {
    long getId();
    void setName(String name);
    String getName();
    String getDescription();
    boolean isActive();
}
