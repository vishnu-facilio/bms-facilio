package com.facilio.bmsconsole.context;

import java.io.IOException;

import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class PMTriggerV2 extends V3Context {
    private String name;
    private long pmId;
    private String schedule;
    private PMTriggerFrequency frequency;

    private long startTime;
    private long planEndTime;
    private long endTime;

    private long cutOffTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
    
    public ScheduleInfo getScheduleInfo() throws Exception {
		
    	if(this.getSchedule() != null) {
    		JSONParser parser = new JSONParser();
            ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(this.getSchedule()), ScheduleInfo.class);
            return schedule;
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
        QUARTERLY(366, "Quarterly"),
        HALF_YEARLY(366, "Half Yearly"),
        ANNUALLY(366, "Annually"),
        CUSTOM(-1, "Custom"),
        HOURLY(-1, "Hourly"),
        FIFTEEN_MINUTES(-1, "Quarter Hour"),
        TEN_MINUTES(-1, "One-Sixth Hour");

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
    private String scheduleMsg;
    public String getScheduleMsg(){
        return this.scheduleMsg;
    }
    public void setScheduleMsg(String scheduleMsg){
        this.scheduleMsg = scheduleMsg;
    }
}
