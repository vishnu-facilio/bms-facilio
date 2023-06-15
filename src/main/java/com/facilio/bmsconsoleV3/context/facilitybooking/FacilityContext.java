package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3PhotosContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class FacilityContext extends V3Context {

    private String name;
    private V3PeopleContext manager;
    private String description;
    private String usageGuidance;
    private Long parentModuleId;
    private Long parentId;
    private BaseSpaceContext location;

    public Long getBookingCanceledCount() {
        return bookingCanceledCount;
    }

    public void setBookingCanceledCount(Long bookingCanceledCount) {
        this.bookingCanceledCount = bookingCanceledCount;
    }

    private Long bookingCanceledCount;

    private FacilityType facilityType;
    public Integer getFacilityType() {
        if (facilityType != null) {
            return facilityType.getIndex();
        }
        return null;
    }
    public void setFacilityType(Integer facilityType) {
        if(facilityType != null) {
            this.facilityType = FacilityType.valueOf(facilityType);
        }
    }
    public FacilityType getFacilityTypeEnum() {
        return facilityType;
    }

    public static enum FacilityType implements FacilioIntEnum {
        ASSET("Asset"),
        SPACE("Space"),
        OTHERS("Others");

        private String name;

        FacilityType(String name) {
            this.name = name;
        }

        public static FacilityType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    private Integer category;
    private Integer usageCapacity;
    private Long slotDuration=-1l;
    private Long maxSlotBookingAllowed;
    private Boolean isChargeable;

    public Boolean getIsChargeable() {
        return isChargeable;
    }

    public void setIsChargeable(Boolean isChargeable) {
        this.isChargeable = isChargeable;
    }

    public Boolean isChargeable() {
        if (isChargeable != null) {
            return isChargeable.booleanValue();
        }
        return false;
    }

    private Double securityDeposit;
    private Double pricePerSlot;

    private Boolean isAttendeeListNeeded;

    public Boolean getIsAttendeeListNeeded() {
        return isAttendeeListNeeded;
    }

    public void setIsAttendeeListNeeded(Boolean isAttendeeListNeeded) {
        this.isAttendeeListNeeded = isAttendeeListNeeded;
    }

    public Boolean isAttendeeListNeeded() {
        if (isAttendeeListNeeded != null) {
            return isAttendeeListNeeded.booleanValue();
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public V3PeopleContext getManager() {
        return manager;
    }

    public void setManager(V3PeopleContext manager) {
        this.manager = manager;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsageGuidance() {
        return usageGuidance;
    }

    public void setUsageGuidance(String usageGuidance) {
        this.usageGuidance = usageGuidance;
    }

    public Long getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(Long parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public BaseSpaceContext getLocation() {
        return location;
    }

    public void setLocation(BaseSpaceContext location) {
        this.location = location;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getUsageCapacity() {
        return usageCapacity;
    }

    public void setUsageCapacity(Integer usageCapacity) {
        this.usageCapacity = usageCapacity;
    }

    public Long getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(Long slotDuration) {
        this.slotDuration = slotDuration;
    }

    public Long getMaxSlotBookingAllowed() {
        return maxSlotBookingAllowed;
    }

    public void setMaxSlotBookingAllowed(Long maxSlotBookingAllowed) {
        this.maxSlotBookingAllowed = maxSlotBookingAllowed;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public Double getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(Double pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    private Boolean isMultiBookingPerSlotAllowed;

    public Boolean getIsMultiBookingPerSlotAllowed() {
        return isMultiBookingPerSlotAllowed;
    }

    public void setIsMultiBookingPerSlotAllowed(Boolean isMultiBookingPerSlotAllowed) {
        this.isMultiBookingPerSlotAllowed = isMultiBookingPerSlotAllowed;
    }

    public Boolean isMultiBookingPerSlotAllowed() {
        if (isMultiBookingPerSlotAllowed != null) {
            return isMultiBookingPerSlotAllowed.booleanValue();
        }
        return false;
    }

    private List<FacilityAmenitiesContext> amenities;

    public List<FacilityAmenitiesContext> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<FacilityAmenitiesContext> amenities) {
        this.amenities = amenities;
    }

    private List<WeekDayAvailability> weekDayAvailabilities;

    public List<WeekDayAvailability> getWeekDayAvailabilities() {
        return weekDayAvailabilities;
    }

    public void setWeekDayAvailabilities(List<WeekDayAvailability> weekDayAvailabilities) {
        this.weekDayAvailabilities = weekDayAvailabilities;
    }

    private List<FacilitySpecialAvailabilityContext> facilitySpecialAvailabilities;

    public List<FacilitySpecialAvailabilityContext> getFacilitySpecialAvailabilities() {
        return facilitySpecialAvailabilities;
    }

    public void setFacilitySpecialAvailabilities(List<FacilitySpecialAvailabilityContext> facilitySpecialAvailabilities) {
        this.facilitySpecialAvailabilities = facilitySpecialAvailabilities;
    }

    private List<SlotContext> slots;

    public List<SlotContext> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotContext> slots) {
        this.slots = slots;
    }

    private Long slotGeneratedUpto;

    public Long getSlotGeneratedUpto() {
        return slotGeneratedUpto;
    }

    public void setSlotGeneratedUpto(Long slotGeneratedUpto) {
        this.slotGeneratedUpto = slotGeneratedUpto;
    }

    private Long bookingAdvancePeriodInDays;

    public Long getBookingAdvancePeriodInDays() {
        return bookingAdvancePeriodInDays;
    }

    public void setBookingAdvancePeriodInDays(Long bookingAdvancePeriodInDays) {
        this.bookingAdvancePeriodInDays = bookingAdvancePeriodInDays;
    }
    private Integer maxAttendeeCountPerBooking;

    public Integer getMaxAttendeeCountPerBooking() {
        return maxAttendeeCountPerBooking;
    }

    public void setMaxAttendeeCountPerBooking(Integer maxAttendeeCountPerBooking) {
        this.maxAttendeeCountPerBooking = maxAttendeeCountPerBooking;
    }

    private List<V3PhotosContext> photos;

    public List<V3PhotosContext> getPhotos() {
        return photos;
    }

    public void setPhotos(List<V3PhotosContext> photos) {
        this.photos = photos;
    }

    private Long allowCancellationBefore;

    public Long getAllowCancellationBefore() {
        return allowCancellationBefore;
    }

    public void setAllowCancellationBefore(Long allowCancellationBefore) {
        this.allowCancellationBefore = allowCancellationBefore;
    }
}
