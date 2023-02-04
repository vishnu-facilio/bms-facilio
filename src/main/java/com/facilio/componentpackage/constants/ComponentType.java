package com.facilio.componentpackage.constants;

import com.facilio.componentpackage.implementation.AppPackageBeanImpl;
import com.facilio.componentpackage.implementation.FieldPackageBeanImpl;
import com.facilio.componentpackage.implementation.ModulePackageBeanImpl;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ComponentType implements FacilioIntEnum {

    MODULE(ModulePackageBeanImpl.class, null, false),
    FIELD(FieldPackageBeanImpl.class, MODULE, false),
    APP(AppPackageBeanImpl.class, null, true);

    Class<? extends PackageBean> componentClass;
    ComponentType parentComponentType;
    boolean isReUpdateRequired;

    @Override
    public String getValue() {
        return name();
    }

    public static ComponentType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

}
