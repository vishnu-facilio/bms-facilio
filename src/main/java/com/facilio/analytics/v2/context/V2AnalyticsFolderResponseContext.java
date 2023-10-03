package com.facilio.analytics.v2.context;

import com.facilio.report.context.ReportContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class V2AnalyticsFolderResponseContext {

    public long id;
    public String name;
    public long moduleId;
    public String moduleName;
    List<V2AnalyticsReportResponseContext> reports;
}
