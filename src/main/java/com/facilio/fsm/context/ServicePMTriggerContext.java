package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.V3Context;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Data
public class ServicePMTriggerContext extends V3Context {
    private String name;
    private ServicePlannedMaintenanceContext servicePlannedMaintenance;
    private ServicePMTemplateContext servicePMTemplate;
    private String schedule;
    private ServicePMTriggerFrequency frequency;
    private Long startTime;
    private Long endTime;

    public ScheduleInfo getScheduleInfo() throws Exception {

        if(this.getSchedule() != null) {
            JSONParser parser = new JSONParser();
            ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(this.getSchedule()), ScheduleInfo.class);
            return schedule;
        }
        return null;
    }
    public ServicePMTriggerFrequency getFrequencyEnum() {
        return frequency;
    }

    public void setFrequencyEnum(ServicePMTriggerFrequency frequency) {
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
            frequency = ServicePMTriggerFrequency.valueOf(freq);
        } else {
            frequency = null;
        }
    }
    public enum ServicePMTriggerFrequency implements FacilioIntEnum {
        DAILY(21, "Daily"),
        WEEKLY(8 * 2, "Weekly"),
        MONTHLY(366, "Monthly"),
        QUARTERLY(366, "Quarterly"),
        HALF_YEARLY(366, "Half Yearly"),
        ANNUALLY(366, "Annually");

        private int days;
        private String name;

        ServicePMTriggerFrequency(int days, String name) {
            this.days = days;
            this.name = name;
        }

        public int getMaxSchedulingDays() {
            return this.days;
        }
        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }
        @Override
        public String getValue() {
            return name;
        }
        public static ServicePMTriggerFrequency valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }
    }
}
