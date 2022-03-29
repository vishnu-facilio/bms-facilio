package com.facilio.ns.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class NameSpaceField {

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

    public String fieldKey() {
        return isEnabledCompaction ? compactedKey() : baseKey();
    }

    @JsonIgnore
    private String compactedKey() {
        return baseKey() + "_COMPACT";
    }

    @JsonIgnore
    private String baseKey() {
        return "NS" + nsId + "_R" + resourceId + "_FLD" + fieldId + "_" + aggregationType;
    }

    public String toString() {
        return new HashMap<String, Object>() {
            {
                put("nsid", nsId);
                put("varname", varName);
                put("resource id ", resourceId);
                put("field id ", fieldId);
            }
        }.toString();
    }
}
