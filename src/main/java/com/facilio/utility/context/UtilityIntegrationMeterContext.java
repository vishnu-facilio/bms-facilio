package com.facilio.utility.context;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilityIntegrationMeterContext extends V3Context {

    private static final long serialVersionUID = 1L;

    Long createdTime;
    String meterUid;
    String customerUid;
    String userEmail;
    long userUid;
    Boolean isArchived;
    Boolean isActivated;
    long billCount;
    long intervalCount;
    String status;
    String utilityID;
    String meta;
    UtilityIntegrationCustomerContext utilityIntegrationCustomer;
    String frequency;
    Long nextPrepay;
    Long nextRefresh;
    String prepay;
    String serviceTariff;
    String serviceClass;
    UtilityIntegrationTariffContext utilityIntegrationTariff;
    V3MeterContext meter;
    private MeterState meterState;

    public MeterState getMeterStateEnum() {
        return meterState;
    }

    public Integer getMeterState() {
        if(meterState != null) {
            return meterState.getIndex();
        }
        else {
            return null;
        }
    }
    public void setMeterState(Integer meterState) {
        if (meterState != null) {
            this.meterState = MeterState.valueOf(meterState);
        }
    }
    public static enum MeterState implements FacilioIntEnum {

        UNMAPPED(1, "Unmapped"),
        PENDING(2, "Pending for Activation"),
        ACTIVATED(3, "Activated"),
        ERRORED(4, "Errored"),
        ;


        Integer intVal;
        String value;
        MeterState(Integer intVal,String value) {
            this.intVal = intVal;
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public Integer getIntVal() {
            return intVal;
        }
        public void setIntVal(Integer intVal) {
            this.intVal = intVal;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }


        public static MeterState valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }










    }

}
