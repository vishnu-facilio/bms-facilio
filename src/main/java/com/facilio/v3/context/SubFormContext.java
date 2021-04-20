package com.facilio.v3.context;

import java.io.Serializable;
import java.util.List;

public class SubFormContext<T extends V3Context> implements Serializable {
    private long fieldId;
    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private List<Long> deleteIds;

    public List<Long> getDeleteIds() {
        return deleteIds;
    }

    public void setDeleteIds(List<Long> deleteIds) {
        this.deleteIds = deleteIds;
    }
}
