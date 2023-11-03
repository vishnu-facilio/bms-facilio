package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V2ReportFiltersContext {

    private Long criteriaId;
    private Criteria criteria;
    private String moduleName;
}
