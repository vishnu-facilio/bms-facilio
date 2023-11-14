package com.facilio.report.module.v2.context;

import com.facilio.analytics.v2.context.V2TimeFilterContext;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class V2ModuleContextForDashboardFilter {
    private String db_user_filter;
    private V2TimeFilterContext timeFilter;
}
