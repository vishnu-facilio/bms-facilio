package com.facilio.ns.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

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

    public String toString() {
        return new HashMap<String, Object>() {
            {
                put("nsid", nsId);
                put("varname", varName);
                put("resource id ", resourceId);
                put("field id ", fieldId);
                put("agg", aggregationType);
                put("interval", dataInterval);
                put("relmapid", relMapId);
            }
        }.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
