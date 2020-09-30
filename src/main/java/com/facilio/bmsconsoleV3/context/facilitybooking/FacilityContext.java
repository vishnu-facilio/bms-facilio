package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class FacilityContext extends V3Context {

    private String name;
    private User manager;
    private String description;
    private String usageGuidance;
    private Long parentModuleId;
    private Long parentId;
    private BaseSpaceContext location;

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

    public static enum FacilityType implements FacilioEnum {
        ASSET, SPACE, OTHERS;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static FacilityType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Integer category;
    private Integer usageCapacity;
    private Long slotDuration;
    private Long maxMultiBookingPerSlot;
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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
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

    public Long getMaxMultiBookingPerSlot() {
        return maxMultiBookingPerSlot;
    }

    public void setMaxMultiBookingPerSlot(Long maxMultiBookingPerSlot) {
        this.maxMultiBookingPerSlot = maxMultiBookingPerSlot;
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
}
