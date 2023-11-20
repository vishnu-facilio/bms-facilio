package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class V2ReportFiltersContext {

    /**
     *  params for global filter criteria starts here
     */
    private V2MeasuresContext.Criteria_Type criteria_type;
    private Long criteriaId;
    private Criteria criteria;
    private String moduleName;

    /**
     *  params for data filter criteria starts here
     */

    private List<V2AnalyticUserFilterContext> analytics_user_filters;

}
