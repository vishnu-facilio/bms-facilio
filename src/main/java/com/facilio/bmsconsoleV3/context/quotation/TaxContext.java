package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class TaxContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private Double rate;

    public Boolean getIsActive() {
        return isActive;
    }

    private Boolean isActive;
    private Type type;
    private List<TaxContext> childTaxes;

    public List<TaxContext> getChildTaxes() {
        return childTaxes;
    }

    public void setChildTaxes(List<TaxContext> childTaxes) {
        this.childTaxes = childTaxes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Boolean isActive() {
        if(isActive != null) {
            return isActive.booleanValue();
        }
        return false;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Type getTypeEnum() {
        return type;
    }
    public Integer getType() {
        if (type != null) {
            return type.getIndex();
        }
        return null;
    }

    public void setType(Integer type) {
        if (type != null) {
            this.type = Type.valueOf(type);
        }
    }

    public enum Type implements FacilioIntEnum {
        INDIVIDUAL("Individual"),
        GROUP("Group");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type valueOf(int value) {
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
