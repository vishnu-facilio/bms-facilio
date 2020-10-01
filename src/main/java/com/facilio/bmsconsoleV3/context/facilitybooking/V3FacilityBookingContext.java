package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.reservation.ExternalAttendeeContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3FacilityBookingContext extends V3Context {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private User reservedFor;
    public User getReservedFor() {
        return reservedFor;
    }
    public void setReservedFor(User reservedFor) {
        this.reservedFor = reservedFor;
    }

    private Long scheduledStartTime ;
    public Long getScheduledStartTime() {
        return scheduledStartTime;
    }
    public void setScheduledStartTime(Long scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    private Long scheduledEndTime ;
    public Long getScheduledEndTime() {
        return scheduledEndTime;
    }
    public void setScheduledEndTime(Long scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    private Integer noOfAttendees ;
    public Integer getNoOfAttendees() {
        return noOfAttendees;
    }
    public void setNoOfAttendees(Integer noOfAttendees) {
        this.noOfAttendees = noOfAttendees;
    }

    private Long actualStartTime ;
    public Long getActualStartTime() {
        return actualStartTime;
    }
    public void setActualStartTime(Long actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    private Long actualEndTime ;
    public Long getActualEndTime() {
        return actualEndTime;
    }
    public void setActualEndTime(Long actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    private V3FacilityBookingContext.DurationType durationType;
    public V3FacilityBookingContext.DurationType getDurationTypeEnum() {
        return durationType;
    }
    public void setDurationType(V3FacilityBookingContext.DurationType durationType) {
        this.durationType = durationType;
    }
    public int getDurationType() {
        if (durationType != null) {
            return durationType.getIndex();
        }
        return -1;
    }
    public void setDurationType(int durationType) {
        this.durationType = V3FacilityBookingContext.DurationType.valueOf(durationType);
    }

    private V3FacilityBookingContext.ReservationStatus status;
    public V3FacilityBookingContext.ReservationStatus getStatusEnum() {
        return status;
    }
    public void setStatus(V3FacilityBookingContext.ReservationStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = V3FacilityBookingContext.ReservationStatus.valueOf(status);
    }

    private SpaceContext space;
    public SpaceContext getSpace() {
        return space;
    }
    public void setSpace(SpaceContext space) {
        this.space = space;
    }

    private List<User> internalAttendees;
    public List<User> getInternalAttendees() {
        return internalAttendees;
    }
    public void setInternalAttendees(List<User> internalAttendees) {
        this.internalAttendees = internalAttendees;
    }

    private List<ExternalAttendeeContext> externalAttendees;
    public List<ExternalAttendeeContext> getExternalAttendees() {
        return externalAttendees;
    }
    public void setExternalAttendees(List<ExternalAttendeeContext> externalAttendees) {
        this.externalAttendees = externalAttendees;
    }

    public static enum DurationType implements FacilioEnum {
        HALF_AN_HOUR ("30 Minutes"),
        ONE_HOUR("1 Hour"),
        ONE_AND_HALF_HOUR("90 Minutes"),
        TWO_HOURS("2 Hours"),
        ALL_DAY ("All Day"),
        CUSTOM("Custom")
        ;

        private String name;
        DurationType(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static V3FacilityBookingContext.DurationType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static enum ReservationStatus implements FacilioEnum {
        SCHEDULED ("Scheduled"),
        ON_GOING ("On Going"),
        FINISHED ("Finished"),
        CANCELLED ("Cancelled")
        ;

        private String name;
        ReservationStatus (String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static V3FacilityBookingContext.ReservationStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
