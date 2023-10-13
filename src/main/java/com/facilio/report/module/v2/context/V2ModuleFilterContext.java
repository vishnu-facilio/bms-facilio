package com.facilio.report.module.v2.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.report.context.ReportHavingContext;
import com.facilio.report.context.ReportUserFilterContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V2ModuleFilterContext {

    public List<ReportHavingContext> dataFilter;
    public List<ReportUserFilterContext> userFilters;
    public Criteria globalCriteria;
}
