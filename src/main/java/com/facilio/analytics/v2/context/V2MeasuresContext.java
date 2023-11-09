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

    public long id;
    public Long fieldId;
    public String name;
    public String unit;
    public String moduleName;
    public String parentModuleName;
    public Long category;
    public String displayName;
    public Long parent_lookup_fieldId;
    /**
     *  dataType key is used to calcluate aggrgation baseed on datatype
     */
    public Integer dataType;

    public Criteria criteria;
    public long criteriaId;
    private Map<String, String> aliases;
    private int type = ReportDataPointContext.DataPointType.MODULE.getValue();
    public int criteriaType;
    public Criteria_Type criteriaTypeEnum;
    private boolean duplicateDataPoint;
    private boolean defaultSortPoint;
    public int aggr;
    public Long dynamicKpiId;
    public int limit = -1;
    /**
     * below keys added for supporting relationship measure specific relationship
     */
    public Long relationship_id;
    public String parent_measure_alias;

    /**
     * above keys added for supporting relationship measure specific relationship
     */
    /**
     *  params for data filter criteria starts here
     */

    private Long data_criteriaId;
    private Criteria data_criteria;
    private String data_moduleName;
    private int data_aggr;

    /**
     *  params for data filter criteria ends here
     */

    private ReportDataPointContext.OrderByFunction orderByFunction;

    public void setAggr(int aggr){
        this.aggr = aggr;
    }
    @JsonIgnore
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

    public ReportDataPointContext.OrderByFunction getOrderByFunction() {
        return orderByFunction;
    }
    public void setOrderByFunction(int orderByFunction) {
        this.orderByFunction = ReportDataPointContext.OrderByFunction.valueOf(orderByFunction);;
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
