package com.facilio.ns.context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;

public enum NSType implements FacilioIntEnum {
    READING_RULE("Reading Rule", FacilioConstants.ReadingRules.NEW_READING_RULE),
    FAULT_IMPACT_RULE("Fault Impact Rule",FacilioConstants.FaultImpact.MODULE_NAME),
    KPI_RULE("KPI",FacilioConstants.ReadingKpi.READING_KPI),
    SENSOR_RULE("Sensor Rule",FacilioConstants.ContextNames.SENSOR_RULE_MODULE),
    VIRTUAL_METER("Virtual Meter",FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING)
    ;

    private String name;
    private String moduleName;

    NSType(String name,String moduleName) {
        this.name = name;
        this.moduleName=moduleName;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    public static NSType valueOf(int type) {
        if (type > 0 && type <= values().length) {
            return values()[type - 1];
        }
        return null;
    }

    public  String getModuleName(){
        return moduleName;
    }
}
