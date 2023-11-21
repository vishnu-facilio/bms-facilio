package com.facilio.ns.context;

import com.facilio.connected.FacilioDataProcessing;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NameSpaceField implements Cloneable, Serializable {

    Long id;

    Long orgId;

    Long nsId;

    String varName;

    String varDataType;

    Long resourceId;

    Long fieldId;

    Long moduleId;

    @JsonIgnore
    FacilioField field;

    @JsonIgnore
    FacilioModule module;

    @FacilioDataProcessing
    Long parentResourceId; // used in storm, when related nsFields are created, their resIds are their relations, but the parentResourceId is the field's resource Id

    Long dataInterval;

    NamespaceFieldRelated relatedInfo;

    public Long getDataInterval() {
        return dataInterval != null ? dataInterval : -1L;
    }

    int aggregationTypeI;

    public void setAggregationTypeI(int aggregationTypeI) {
        if (aggregationType != null) {
            this.aggregationTypeI = aggregationType.getIndex();
        } else {
            this.aggregationTypeI = aggregationTypeI;
            this.aggregationType = AggregationType.valueOf(aggregationTypeI);
        }
    }

    AggregationType aggregationType;

    public void setAggregationType(AggregationType typ) {
        this.aggregationType = typ;
        this.aggregationTypeI = typ.getIndex();
    }

    public AggregationType getAggregation() {
        return (aggregationType != null) ? aggregationType : AggregationType.valueOf(aggregationTypeI);
    }

    NsFieldType nsFieldType;

    public void setNsFieldType(NsFieldType typ) {
        this.nsFieldType = typ;
        this.nsFieldTypeI = typ.getIndex();
    }

    int nsFieldTypeI;

    public void setNsFieldTypeI(int nsFieldTypeI) {
        if (nsFieldType != null) {
            this.nsFieldTypeI = nsFieldType.getIndex();
        } else {
            this.nsFieldTypeI = nsFieldTypeI;
            this.nsFieldType = NsFieldType.valueOf(nsFieldTypeI);
        }
    }

    DefaultExecutionMode defaultExecutionMode;

    public void setDefaultExecutionMode(DefaultExecutionMode typ) {
        this.defaultExecutionMode = typ;
        this.defaultExecutionModeI = typ.getIndex();
    }

    int defaultExecutionModeI;

    public void setDefaultExecutionModeI(int defaultExecutionModeI) {
        if (defaultExecutionMode != null) {
            this.defaultExecutionModeI = defaultExecutionMode.getIndex();
        } else {
            this.defaultExecutionModeI = defaultExecutionModeI;
            this.defaultExecutionMode = DefaultExecutionMode.valueOf(defaultExecutionModeI);
        }
    }

    Long defaultValue;

    public String fieldKey() {
        return "O" + orgId + "_NS" + nsId + "_R" + resourceId + "_FLD" + fieldId + "_" + aggregationType;
    }

    Boolean primary;

    @Override
    public String toString() {
        return "NameSpaceField{" +
                "nsId=" + nsId +
                ", varName='" + varName + '\'' +
                ", resourceId=" + resourceId +
                ", fieldId=" + fieldId +
                ", dataInterval=" + dataInterval +
                ", aggregationType=" + aggregationType +
                ", relatedInfo=" + relatedInfo +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum DefaultExecutionMode implements FacilioIntEnum {
        SKIP,
        DEFAULT;

        public static DefaultExecutionMode valueOf(int idx) {
            if (idx > 0 && idx <= values().length) {
                return values()[idx - 1];
            }
            return null;
        }
    }
}
