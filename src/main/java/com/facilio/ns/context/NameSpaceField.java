package com.facilio.ns.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameSpaceField {

    Long orgId;
    Long nsId;
    String varName;

    Long resourceId;
    Long fieldId;

    String module;
    String field;

    Long dataInterval;
    int aggregationTypeI;

    public void setAggregationTypeI(int aggregationTypeI) {
        this.aggregationTypeI = aggregationTypeI;
        this.aggregationType = AggregationType.valueOf(aggregationTypeI);
    }

    AggregationType aggregationType;

    public void setAggregationType(AggregationType typ) {
        this.aggregationType = typ;
        this.aggregationTypeI = typ.getIndex();
    }

    boolean isEnabledCompaction;

    NameSpaceContext namespace;

    @JsonIgnore
    Key fieldKey;

    public void setFieldKey(Key fieldKey) {
        this.fieldKey = new Key(this);
    }

    @JsonIgnore
    public Key getFieldKey() {
        return fieldKey;
    }

    public class Key {
        String key;
        boolean isCompaction;
        Long fieldId;

        private Key(String key, boolean isCompaction, Long fieldId) {
            this.key = key;
            this.isCompaction = isCompaction;
            this.fieldId = fieldId;
        }

        public Key(NameSpaceField field) {
            this("NS" + field.getNamespace().getId() + "_R" + field.getResourceId() + "_FLD" + field.getFieldId() + "_" + field.getAggregationType(), field.isEnabledCompaction(), field.getFieldId());
        }

        public String getCompactedKey() {
            return key + "_COMPACT";
        }

        public String getKey() {
            return isCompaction ? getCompactedKey() : key;
        }

        public String getBaseKey() {
            return key;
        }

        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object grpKey) {
            if (grpKey instanceof Key) {
                return ((Key) grpKey).getKey().equals(getKey());
            }
            return Boolean.FALSE;
        }

        public String toString() {
            return key;
        }
    }

    public void setNullForResponse() {
        fieldKey = null;
        namespace = null;
    }
}
