package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2AnalyticsReportResponseContext{

    public long id = -1;
    public long moduleId = -1;
    public String name;
    public String description;
    public String moduleName;
    public String moduleDisplayName;
    public Long createdTime;
    public Long modifiedTime;
    public Long createdBy;
    public Long modifiedBy;
    public Long reportFolderId;
    public Integer type;
    public Boolean kpi;
}
