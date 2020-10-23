package com.facilio.bmsconsoleV3.context.facilitybooking;

import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.v3.context.V3Context;

import java.text.DecimalFormat;

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
        if(slotCost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.slotCost = Double.valueOf(df.format(slotCost));
        }
    }

    public String getSlotCostString() {
        if(slotCost != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.slotCost);
        }
        return null;
    }
}
