package com.facilio.ns.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.relation.context.RelationMappingContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NamespaceFieldRelated {

    Long nameSpaceFieldId;

    RelationMappingContext relMapContext;

    Long relMapId;

    Long criteriaId;

    Criteria criteria;

    AggregationType relAggregationType; // related fields aggregation type

    public void setRelAggregationType(AggregationType relAggregationType) {
        this.relAggregationType = relAggregationType;
        this.relAggregationTypeInt = relAggregationType.getIndex();
    }

    Integer relAggregationTypeInt;

    public void setRelAggregationTypeInt(int relAggregationTypeInt) {
        if (relAggregationType != null) {
            this.relAggregationTypeInt = relAggregationType.getIndex();
        } else {
            this.relAggregationTypeInt = relAggregationTypeInt;
            this.relAggregationType = AggregationType.valueOf(relAggregationTypeInt);
        }
    }

    @JsonIgnore
    List<Long> resourceIds; // used in storm to prevent double fetch

    @Override
    public String toString() {
        return "NamespaceFieldRelated{" +
                "nameSpaceFieldId=" + nameSpaceFieldId +
                ", relMapContext=" + relMapContext +
                ", relMapId=" + relMapId +
                ", criteriaId=" + criteriaId +
                ", relAggregationType=" + relAggregationType +
                '}';
    }

}
