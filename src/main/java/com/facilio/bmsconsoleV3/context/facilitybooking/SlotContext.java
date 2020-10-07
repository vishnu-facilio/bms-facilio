package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.v3.context.V3Context;

public class SlotContext extends V3Context {

    private Long slotTime;
    private Double slotCost;

    public Long getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(Long slotTime) {
        this.slotTime = slotTime;
    }

    public Double getSlotCost() {
        return slotCost;
    }

    public void setSlotCost(Double slotCost) {
        this.slotCost = slotCost;
    }
}
