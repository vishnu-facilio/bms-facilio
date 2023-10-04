package com.facilio.analytics.v2.context;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.report.context.ReportDataPointContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.util.Map;

@Setter
@Getter
public class V2MeasuresContext {

    public Long fieldId;
    public String name;
    public String unit;
    public String moduleName;
    public String parentModuleName;
    public Long category;

    public Criteria criteria;
    public long criteriaId;
    private Map<String, String> aliases;
    private int type = ReportDataPointContext.DataPointType.MODULE.getValue();
    public int criteriaType;
    public Criteria_Type criteriaTypeEnum;
    private boolean duplicateDataPoint;
    private boolean defaultSortPoint;
    public int aggr;
    public AggregateOperator aggrEnum;
    private ReportDataPointContext.OrderByFunction orderByFunction;

    public void setAggr(int aggr){
        this.aggr = aggr;
        this.setAggrEnum(AggregateOperator.getAggregateOperator(aggr));
    }
    @JsonIgnore
    public void setAggrEnum(AggregateOperator aggr) {
        this.aggrEnum = aggr;
    }
    public AggregateOperator getAggrEnum(){
        return AggregateOperator.getAggregateOperator(this.aggr);
    }

    public void setCriteriaType(int criteriaType) {
        this.criteriaType = criteriaType;
        this.setCriteriaTypeEnum(Criteria_Type.valueOf(criteriaType));
    }

    @JsonIgnore
    public void setCriteriaTypeEnum(Criteria_Type criteriaType) {
        this.criteriaTypeEnum = criteriaType;
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
