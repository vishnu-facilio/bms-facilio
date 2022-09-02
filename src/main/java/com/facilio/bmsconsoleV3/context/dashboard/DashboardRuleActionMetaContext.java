package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class DashboardRuleActionMetaContext {

    public Long id = -1l;
    public Long actionId;
    public String action_deatils;
    public Long criteriaId;
    public Criteria criteria;
    public Long scriptId;
}
