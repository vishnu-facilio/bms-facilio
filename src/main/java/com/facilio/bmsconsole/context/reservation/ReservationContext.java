package com.facilio.bmsconsole.context.reservation;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ReservationContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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

    private long scheduledStartTime = -1;
    public long getScheduledStartTime() {
        return scheduledStartTime;
    }
    public void setScheduledStartTime(long scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    private long scheduledEndTime = -1;
    public long getScheduledEndTime() {
        return scheduledEndTime;
    }
    public void setScheduledEndTime(long scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    private int noOfAttendees = -1;
    public int getNoOfAttendees() {
        return noOfAttendees;
    }
    public void setNoOfAttendees(int noOfAttendees) {
        this.noOfAttendees = noOfAttendees;
    }

    private long actualStartTime = -1;
    public long getActualStartTime() {
        return actualStartTime;
    }
    public void setActualStartTime(long actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    private long actualEndTime = -1;
    public long getActualEndTime() {
        return actualEndTime;
    }
    public void setActualEndTime(long actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    private DurationType durationType;
    public DurationType getDurationTypeEnum() {
        return durationType;
    }
    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }
    public int getDurationType() {
        if (durationType != null) {
            return durationType.getIndex();
        }
        return -1;
    }
    public void setDurationType(int durationType) {
        this.durationType = DurationType.valueOf(durationType);
    }

    private ReservationStatus status;
    public ReservationStatus getStatusEnum() {
        return status;
    }
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = ReservationStatus.valueOf(status);
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

    public static enum DurationType implements FacilioIntEnum {
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
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static DurationType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static enum ReservationStatus implements FacilioIntEnum {
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
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static ReservationStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
