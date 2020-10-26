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

    private FacilityContext facility;

    public FacilityContext getFacility() {
        return facility;
    }

    public void setFacility(FacilityContext facility) {
        this.facility = facility;
    }

    private Boolean isCancelled;
    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean isCancelled() {
        if (isCancelled != null) {
            return isCancelled.booleanValue();
        }
        return false;
    }
}
