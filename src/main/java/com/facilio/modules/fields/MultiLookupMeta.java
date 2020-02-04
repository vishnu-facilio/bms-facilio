package com.facilio.modules.fields;

import java.util.Collection;
import java.util.List;

public class MultiLookupMeta extends MultiLookup {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public MultiLookupMeta(MultiLookup field) {
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

    private Collection<MultiLookup> childMultiLookupFields;
    public Collection<MultiLookup> getChildMultiLookupFields() {
        return childMultiLookupFields;
    }
    public void setChildMultiLookupFields(Collection<MultiLookup> childMultiLookupFields) {
        this.childMultiLookupFields = childMultiLookupFields;
    }
}
