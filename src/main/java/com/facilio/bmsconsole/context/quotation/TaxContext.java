package com.facilio.bmsconsole.context.quotation;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.List;

public class TaxContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private Double rate;
    private boolean isActive;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Type getTypeEnum() {
        return type;
    }
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

    public void setType(int type) {
        this.type = Type.valueOf(type);
    }

    public enum Type implements FacilioEnum {
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
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }


}
