package com.facilio.bmsconsoleV3.context.report;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Log4j
@Setter
@Getter
public class ReportDynamicKpiContext
{
    public String dynamicKpi;
    public List<Long> parentId;
    public Long category;
    public boolean isV2Analytics;
}
