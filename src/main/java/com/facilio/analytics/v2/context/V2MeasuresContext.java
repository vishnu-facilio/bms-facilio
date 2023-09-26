package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.report.context.ReportDataPointContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class V2MeasuresContext {

    public Long fieldId;
    public String name;
    public String changed_unit;
    public String moduleName;
    public String parentModuleName;
    public Long category;

    public Criteria criteria;
    private Map<String, String> aliases;
    private int type = ReportDataPointContext.DataPointType.MODULE.getValue();
    public int criteriaType;
    public Criteria_Type criteriaTypeEnum;
    private boolean duplicateDataPoint;
    private boolean defaultSortPoint;
    public AggregateOperator aggr;
    private ReportDataPointContext.OrderByFunction orderByFunction;

    public void setAggr(int aggr) {
        this.aggr = AggregateOperator.getAggregateOperator(aggr);
    }
    public int getAggr(){
        return this.aggr.getValue();
    }
    public void setAggrEnum(AggregateOperator aggr) {
        this.aggr = aggr;
    }

    public void setCriteriaType(int criteriaType) {
        this.criteriaType = criteriaType;
        this.setCriteriaTypeEnum(criteriaType);
    }

    public void setCriteriaTypeEnum(int criteriaType) {
        this.criteriaTypeEnum = Criteria_Type.valueOf(criteriaType);
    }


    public static enum Criteria_Type implements FacilioIntEnum
    {
        ALL("all"), CRITERIA("Criteria"), RELATIONSHIP("Relationship");

        public static Criteria_Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        private String name;

        Criteria_Type(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return this.name;
        }
    }
}
