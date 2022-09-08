package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.db.criteria.Criteria;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class DashboardRuleActionMetaContext {

    private static final long serialVersionUID = 1L;
    public Long id = -1l;
    public Long actionId;
    public JSONObject action_detail;
    public Long criteriaId;
    public String moduleName;
    public Criteria criteria;
    public Long scriptId;
    @JSON(serialize=false)
    public String getAction_detailStr() {
        if (action_detail != null) {
            return action_detail.toJSONString();
        }
        return null;
    }
    public void setAction_detailStr(String configStr) throws ParseException {
        this.action_detail = FacilioUtil.parseJson(configStr);
    }
}
