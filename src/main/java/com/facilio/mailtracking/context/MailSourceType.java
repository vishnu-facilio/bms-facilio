package com.facilio.mailtracking.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;

@Getter
public enum MailSourceType implements FacilioStringEnum {
    WORKFLOW ("Workflow Rule"),
    SCRIPT ("Facilio Script"),
    RULE_NOTIFICATION ("Notification"),
    INVITE_MAIL ("Invitation Mail")
    ;

    private final String moduleName;
    MailSourceType(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSourceType() {
        return this.name();
    }

    @Override
    public String getValue() {
        return this.moduleName;
    }

}
