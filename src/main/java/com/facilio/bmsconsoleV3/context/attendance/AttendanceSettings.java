package com.facilio.bmsconsoleV3.context.attendance;


import com.facilio.modules.FacilioStringEnum;
import lombok.Data;

@Data
public class AttendanceSettings {

    AttendanceSettings(){
    }
    public AttendanceSettings(WorkingHoursMode mode, long minWorkHours) {
        this.workingHoursMode = mode;
        this.minWorkHours = minWorkHours;
    }

    private Long minWorkHours;

    private WorkingHoursMode workingHoursMode;

    public enum WorkingHoursMode implements FacilioStringEnum {
        SHIFT,
        CUSTOM,
    }

}
