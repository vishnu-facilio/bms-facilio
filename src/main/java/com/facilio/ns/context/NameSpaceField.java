package com.facilio.ns.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
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

    Long resourceId;

    Long fieldId;

    Long moduleId;

    @JsonIgnore
    FacilioField field;

    @JsonIgnore
    FacilioModule module;

    Long relMapId;

    Long parentResourceId; // used in storm, when related nsFields are created, their resIds are their relations, but the parentResourceId is the field's resource Id

    Long dataInterval;

    RelationMappingContext relMapContext;

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

    Integer relAggregationType; // related fields aggregation type

    @JsonIgnore
    public AggregationType getRelAggregationTypeEnum(){
        return AggregationType.valueOf(relAggregationType);
    }

    boolean isEnabledCompaction;

    public String fieldKey() {
        return isEnabledCompaction ? compactedKey() : baseKey();
    }

    @JsonIgnore
    private String compactedKey() {
        return baseKey() + "_COMPACT";
    }

    @JsonIgnore
    private String baseKey() {
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
                ", relMapId=" + relMapId +
                ", dataInterval=" + dataInterval +
                ", aggregationType=" + aggregationType +
                ", relAggregationType=" + relAggregationType +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
