package com.facilio.flows.context;

import com.facilio.modules.FacilioIntEnum;

public enum FlowType implements FacilioIntEnum {

    GENERIC(getExponentValue(1)),
    MODULE(getExponentValue(2)),
    KPI(getExponentValue(3))
    ;

    @Override
    public Integer getIndex() {
        return FacilioIntEnum.super.getIndex();
    }

    @Override
    public String getValue() {
        return FacilioIntEnum.super.getValue();
    }

    public long getKey() {
        return key;
    }

    public static FlowType valueOf(int value){
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
    private static long getExponentValue(int exponent) {
        return (long) Math.pow(2, (exponent));
    }
    long key;
    FlowType(long key){
        this.key = key;
    }
}