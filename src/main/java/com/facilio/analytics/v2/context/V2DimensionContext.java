package com.facilio.analytics.v2.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.report.context.ReadingAnalysisContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2DimensionContext {

    public int dimension_type;
    public DimensionType dimensionTypeEnum;
    public Long fieldId;
    public int xAggr = BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getValue();

    public AggregateOperator xAggrEnum;
    public int getxAggr() {
        return xAggr;
    }
    public void setxAggr(int aggr){
        this.xAggr = aggr;
        this.setxAggrEnum(AggregateOperator.getAggregateOperator(aggr));
    }
    @JsonIgnore
    public void setxAggrEnum(AggregateOperator xAggrEnum) {
        this.xAggrEnum = xAggrEnum;
    }
    public AggregateOperator getxAggrEnum(){
        return AggregateOperator.getAggregateOperator(this.xAggr);
    }

    public void setDimension_type(int dimension_type) {
        this.dimension_type = dimension_type;
        this.setDimensionTypeEnum(DimensionType.valueOf(dimension_type));
    }

    public enum DimensionType implements FacilioIntEnum
    {
        TIME("Time"), READING("Reading"), ASSET("Asset"), SITE("Site"), FLOOR("Floor"), SPACE("Space"), MV("M&V"), METER("Meter");

        private String name;

        DimensionType(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        public static V2DimensionContext.DimensionType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
