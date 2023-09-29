package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmDefinitionTaggingContext extends V3Context {
    private String name;
    private V3ClientContext client;
    private AlarmCategoryContext alarmCategory;
    private AlarmDefinitionContext alarmDefinition;
    private AlarmTypeContext alarmType;
    private ControllerType controllerType;
    public Integer getControllerType() {
        if (controllerType != null) {
            return controllerType.getIndex();
        }
        return null;
    }
    public void setControllerType(Integer controllerType) {
        if(controllerType != null) {
            this.controllerType = ControllerType.valueOf(controllerType);
        }
    }
}