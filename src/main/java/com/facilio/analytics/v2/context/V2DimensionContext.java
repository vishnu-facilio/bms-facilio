package com.facilio.analytics.v2.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.report.context.ReadingAnalysisContext;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2DimensionContext {

    public int dimension_type;
    public Long fieldId;
    public ReadingAnalysisContext.ReportMode reportModeEnum;
    private AggregateOperator xAggr;
    public int getxAggr() {
        return xAggr != null ? xAggr.getValue() : -1;
    }
    public void setxAggr(int aggr){
        this.xAggr = AggregateOperator.getAggregateOperator(aggr);
    }
    public AggregateOperator getxAggrEnum() {
        return xAggr;
    }
    public void setxAggrEnum(AggregateOperator aggr) {
        this.xAggr = aggr;
    }
    public DimensionType dimensionTypeEnum;

    public void setDimension_type(int dimension_type) {
        this.dimension_type = dimension_type;
        this.setDimensionTypeEnum(dimension_type);
    }

    public void setDimensionTypeEnum(int dimensionType) {
        this.dimensionTypeEnum = DimensionType.valueOf(dimensionType);
    }

    public enum DimensionType implements FacilioIntEnum
    {
        TIME("Time"), READING("Reading"), ASSET("Asset"), SITE("Site"), FLOOR("Floor"), SPACE("Space"), MV("M&V");

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
