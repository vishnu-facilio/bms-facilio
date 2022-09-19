package com.facilio.bmsconsoleV3.context;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Setter
@Getter
@Log4j
public class V3DashboardActionContext {

    private static final long serialVersionUID = 1L;

    public Criteria criteria;
    public Criteria trigger_widget_criteria;
}
