package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class WorkPermitChecklistContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private V3WorkPermitContext workPermit;

    private WorkPermitTypeChecklistContext checklist;
    private String remarks;


    public Boolean getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(Boolean reviewed) {
        isReviewed = reviewed;
    }

    public Boolean isReviewed() {
        if (isReviewed != null) {
            return isReviewed.booleanValue();
        }
        return false;
    }

    private Boolean isReviewed;
    private Required required;


    public V3WorkPermitContext getWorkPermit() {
        return workPermit;
    }

    public void setWorkPermit(V3WorkPermitContext workPermit) {
        this.workPermit = workPermit;
    }

    public WorkPermitTypeChecklistContext getChecklist() {
        return checklist;
    }

    public void setChecklist(WorkPermitTypeChecklistContext checklist) {
        this.checklist = checklist;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getRequiredEnum() {
        if (required != null) {
           return required.getValue();
        }
        return null;
    }

    public Integer getRequired() {
        if (required != null) {
            return required.getIndex();
        }
        return null;
    }

    public void setRequired(Integer required) {
        if (required != null) {
            this.required = Required.valueOf(required);
        }
    }


    public enum Required implements FacilioEnum {
        YES("Yes"),
        NO("No"),
        NA("N/A");
        private String name;

        Required(String name) {
            this.name = name;
        }

        public static Required valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
