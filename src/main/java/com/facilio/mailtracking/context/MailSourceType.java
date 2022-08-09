package com.facilio.mailtracking.context;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum MailSourceType implements Serializable {
    WORKFLOW("workflow"),
    SCRIPT("script"),
    RULE_NOTIFICATION("rulenotification");

    private final String moduleName;
    private MailSourceType(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSourceType() {
        return this.name();
    }
}
