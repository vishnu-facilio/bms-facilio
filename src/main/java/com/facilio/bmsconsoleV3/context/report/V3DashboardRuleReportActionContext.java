package com.facilio.bmsconsoleV3.context.report;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Setter
@Getter
@Log4j
public class V3DashboardRuleReportActionContext {

    private static final long serialVersionUID = 1L;

    public Criteria criteria;
    public Criteria trigger_widget_criteria;
    public List<V3DashboardRuleDPContext> datapointList;
}
