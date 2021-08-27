package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

public class V3TermsAndConditionContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String name;
    private String termType;
    private String shortDesc;
    private String longDesc;
    public Boolean isEditable;
    public Boolean defaultOnPo;
    public Boolean defaultOnQuotation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean isEditable() {
        if (isEditable != null) {
            return isEditable.booleanValue();
        }
        return false;
    }
    public Boolean getDefaultOnPo() {
                if (defaultOnPo != null) {
                        return defaultOnPo.booleanValue();
                    }
                return false;
            }

            public void setDefaultOnPo(Boolean defaultOnPo) {
                this.defaultOnPo = defaultOnPo;
            }



            public Boolean getDefaultOnQuotation() {
                if (defaultOnQuotation!= null) {
                        return defaultOnQuotation.booleanValue();
                    }
                return false;
            }

            public void setDefaultOnQuotation(Boolean defaultOnQuotation) {
                this.defaultOnQuotation = defaultOnQuotation;
            }

}

