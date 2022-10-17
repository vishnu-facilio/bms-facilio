package com.facilio.bmsconsoleV3.context.report;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V3DashboardRuleDPContext {

    public Criteria criteria;
    public String datapoint_link;
}
