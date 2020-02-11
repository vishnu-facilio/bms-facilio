package com.facilio.modules.fields;

import java.util.Collection;
import java.util.List;

public class MultiLookupMeta extends MultiLookupField {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public MultiLookupMeta(MultiLookupField field) {
        // TODO Auto-generated constructor stub
        super(field);
    }

    private List<FacilioField> selectFields;
    public List<FacilioField> getSelectFields() {
        return selectFields;
    }
    public void setSelectFields(List<FacilioField> selectFields) {
        this.selectFields = selectFields;
    }

    private Collection<LookupField> childLookupFields;
    public Collection<LookupField> getChildLookupFields() {
        return childLookupFields;
    }
    public void setChildLookupFields(Collection<LookupField> childLookupFields) {
        this.childLookupFields = childLookupFields;
    }

    private Collection<MultiLookupField> childMultiLookupFields;
    public Collection<MultiLookupField> getChildMultiLookupFields() {
        return childMultiLookupFields;
    }
    public void setChildMultiLookupFields(Collection<MultiLookupField> childMultiLookupFields) {
        this.childMultiLookupFields = childMultiLookupFields;
    }
}
