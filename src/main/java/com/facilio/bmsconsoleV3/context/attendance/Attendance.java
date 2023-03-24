package com.facilio.bmsconsoleV3.context.attendance;


import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class Attendance extends V3Context {
    public Attendance(){

    }
    public Attendance(long day, Status status) {
        this.day = day;
        this.status = status;
    }

    private V3PeopleContext people;

    private Long checkInTime;

    private Long checkOutTime;

    private Long workingHours = 0L;

    private Long day;

    private Status status;

    private Long totalPaidBreakHrs = 0L;

    private Long totalUnpaidBreakHrs = 0L;

    private Shift shift;



    public enum Status implements FacilioStringEnum {
        PRESENT,
        ABSENT,
        WEEKLY_OFF,
        LEAVE
    }
}
