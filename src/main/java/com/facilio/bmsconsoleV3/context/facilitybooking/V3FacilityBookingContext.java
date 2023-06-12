package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
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

    private V3PeopleContext reservedFor;
    public V3PeopleContext getReservedFor() {
        return reservedFor;
    }
    public void setReservedFor(V3PeopleContext reservedFor) {
        this.reservedFor = reservedFor;
    }

    private Integer noOfAttendees ;
    public Integer getNoOfAttendees() {
        return noOfAttendees;
    }
    public void setNoOfAttendees(Integer noOfAttendees) {
        this.noOfAttendees = noOfAttendees;
    }

    private List<V3InternalAttendeeContext> internalAttendees;

    public List<V3InternalAttendeeContext> getInternalAttendees() {
        return internalAttendees;
    }

    public void setInternalAttendees(List<V3InternalAttendeeContext> internalAttendees) {
        this.internalAttendees = internalAttendees;
    }

    private List<V3ExternalAttendeeContext> facilityBookingExternalAttendee;

    public List<V3ExternalAttendeeContext> getFacilityBookingExternalAttendee() {
        return facilityBookingExternalAttendee;
    }

    public void setFacilityBookingExternalAttendee(List<V3ExternalAttendeeContext> facilityBookingExternalAttendee) {
        this.facilityBookingExternalAttendee = facilityBookingExternalAttendee;
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

    private Double bookingAmount;

    public Double getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(Double bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    private Boolean canEdit;
    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean canEdit() {
        if (canEdit != null) {
            return canEdit.booleanValue();
        }
        return false;
    }

    private List<BookingSlotsContext> slotList;

    public List<BookingSlotsContext> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<BookingSlotsContext> slotList) {
        this.slotList = slotList;
    }

    private Long bookingDate;

    public Long getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Long bookingDate) {
        this.bookingDate = bookingDate;
    }

    private V3TenantContext tenant;
    private User bookingRequestedBy;

    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    public User getBookingRequestedBy() {
        return bookingRequestedBy;
    }

    public void setBookingRequestedBy(User bookingRequestedBy) {
        this.bookingRequestedBy = bookingRequestedBy;
    }

    public Boolean getCanShowCancel() {
        return canShowCancel;
    }

    public void setCanShowCancel(Boolean canShowCancel) {
        this.canShowCancel = canShowCancel;
    }

    private Boolean canShowCancel;
}
