package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

public class V3TermsAndConditionContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String name;
    private String termType;
    private String shortDesc;
    private String longDesc;
    public Boolean isEditable;

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
}
