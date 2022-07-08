package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class PMTriggerV2 extends V3Context {
    private long pmId;
    private String schedule;
    private PMTriggerFrequency frequency;

    private long startTime;
    private long planEndTime;
    private long endTime;

    private long cutOffTime;

    public PMTriggerFrequency getFrequencyEnum() {
        return frequency;
    }

    public void setFrequencyEnum(PMTriggerFrequency frequency) {
        this.frequency = frequency;
    }

    public Integer getFrequency() {
        if (frequency == null) {
            return null;
        }
        return frequency.getIndex();
    }

    public void setFrequency(Integer freq) {
        if (freq != null) {
            frequency = PMTriggerFrequency.valueOf(freq);
        } else {
            frequency = null;
        }
    }

    private PMTriggerType type;

    public void setTypeEnum(PMTriggerType triggerType) {
        this.type = triggerType;
    }

    public PMTriggerType getTypeEnum() {
        return type;
    }

    public void setType(Integer type) {
        if (type != null) {
            this.type = PMTriggerType.valueOf(type);
        } else {
            this.type = null;
        }
    }

    public Integer getType() {
        if (this.type != null) {
            return this.type.getIndex();
        }
        return null;
    }




    public enum PMTriggerType implements FacilioIntEnum {
        SCHEDULE,
        READING,
        ALARMRULE,
        USER,
        CUSTOM;

        public static PMTriggerType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }
    }

    public enum PMTriggerFrequency implements FacilioIntEnum {
        DO_NOT_REPEAT(-1, "Do Not Repeat"),
        DAILY(21, "Daily"),
        WEEKLY(8 * 2, "Weekly"),
        MONTHLY(366, "Monthly"),
        QUARTERTLY(366, "Quarterly"),
        HALF_YEARLY(366, "Half Yearly"),
        ANNUALLY(366, "Annually"),
        CUSTOM(-1, "Custom"),
        HOURLY(-1, "Hourly"),
        FIFTEEN_MINUTES(-1, "Quarter Hour"),
        TEN_MINUTES(-1, "One-Sixth Hour")
        ;

        private int days;
        private String name;

        PMTriggerFrequency(int days, String name) {
            this.days = days;
            this.name = name;
        }

        public int getMaxSchedulingDays() {
            return this.days;
        }

        public String getName() {
            return this.name;
        }

        public static PMTriggerFrequency valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }
    }
}
