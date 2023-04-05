package com.facilio.permission.context.TypeItem;

import com.facilio.modules.FacilioStringEnum;
import com.facilio.permission.handlers.item.GroupItemHandler;
import com.facilio.permission.handlers.item.ModuleGroupItemHandler;
import lombok.Getter;

@Getter
public enum GroupingType implements FacilioStringEnum {
    MODULE("Module",new ModuleGroupItemHandler()),
    APP("Application",null);

    String displayName;
    GroupItemHandler handler;
    GroupingType(String displayName, GroupItemHandler handler) {
        this.displayName = displayName;
        this.handler = handler;
    }
}