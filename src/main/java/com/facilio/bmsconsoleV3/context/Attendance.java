package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

public class Attendance extends V3Context {

    public V3PeopleContext getPeople() {
        return people;
    }

    public void setPeople(V3PeopleContext people) {
        this.people = people;
    }

    private V3PeopleContext people;

    private Long checkInTime;
    public Long getCheckInTime() {
        return checkInTime;
    }
    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    private Long checkOutTime;
    public Long getCheckOutTime() {
        return checkOutTime;
    }
    public void setCheckOutTime(Long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    private Long workingHours;
    public Long getWorkingHours() {
        return workingHours;
    }
    public void setWorkingHours(Long workingHours) {
        this.workingHours = workingHours;
    }

    private Long day;
    public Long getDay() {
        return day;
    }
    public void setDay(Long day) {
        this.day = day;
    }

    private Status status;
    public Status getStatusEnum() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getStatus() {
        if (status != null) {
            return status.getValue();
        }
        return null;
    }

    public void setStatus(Integer status) {
        this.status = Status.valueOf(status);
    }

    public enum Status {
        PRESENT,
        ABSENT,
        LEAVE,
        HOLIDAY;

        public int getValue() {
            return ordinal()+1;
        }

        public static Status valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Long lastCheckInTime;
    public Long getLastCheckInTime() {
        return lastCheckInTime;
    }
    public void setLastCheckInTime(Long lastCheckInTime) {
        this.lastCheckInTime = lastCheckInTime;
    }

    private Long totalPaidBreakHrs;
    public Long getTotalPaidBreakHrs() {
        return totalPaidBreakHrs;
    }
    public void setTotalPaidBreakHrs(Long totalPaidBreakHrs) {
        this.totalPaidBreakHrs = totalPaidBreakHrs;
    }

    private Long lastBreakStartTime;
    public Long getLastBreakStartTime() {
        return lastBreakStartTime;
    }
    public void setLastBreakStartTime(Long lastBreakStartTime) {
        this.lastBreakStartTime = lastBreakStartTime;
    }

    private BreakContext lastBreakId;
    public BreakContext getLastBreakId() {
        return lastBreakId;
    }
    public void setLastBreakId(BreakContext lastBreakId) {
        this.lastBreakId = lastBreakId;
    }

    private Long totalUnpaidBreakHrs;

    public Long getTotalUnpaidBreakHrs() {
        return totalUnpaidBreakHrs;
    }

    public void setTotalUnpaidBreakHrs(Long totalUnpaidBreakHrs) {
        this.totalUnpaidBreakHrs = totalUnpaidBreakHrs;
    }

    @Getter
    @Setter
    private Shift shift;

    public Long getTotalWorkingHrs() {
        if(workingHours > 0) {
            if(totalPaidBreakHrs > 0) {
                return workingHours+totalPaidBreakHrs;
            }
            return workingHours;
        }
        return null;
    }
}
