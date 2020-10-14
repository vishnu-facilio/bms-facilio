package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class SlotContext extends V3Context {

    private Long slotStartTime;
    private Double slotCost;
    private Long slotEndTime;

    public Long getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(Long slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public Long getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(Long slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public Double getSlotCost() {
        return slotCost;
    }

    public void setSlotCost(Double slotCost) {
        this.slotCost = slotCost;
    }
}
