package com.facilio.bmsconsoleV3.context.SFG20JobPlan;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SFG20SyncHistoryContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private Long jobPlanCreatedCount;
    private Long jobPlanUpdatedCount;
    private Long syncStartTime;
    private Long syncEndTime;

    private Status status;
    public Integer getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return null;
    }
    public void setStatus(Integer status) {
        if(status != null) {
            this.status = Status.valueOf(status);
        }
    }
    public Status getStatusEnum() {
        return status;
    }

    public String getStatusEnumName() {
         if(status!=null)
         {
             return status.name;
         }
         return null;
    }

    public static enum Status implements FacilioIntEnum {
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        ERROR("Error");

        private String name;

        Status(String name) {
            this.name = name;
        }

        public static Status valueOf(int value) {
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

}
