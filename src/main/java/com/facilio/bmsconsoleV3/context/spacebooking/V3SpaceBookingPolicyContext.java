package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Getter @Setter
public class V3SpaceBookingPolicyContext extends V3Context {
    private String name;
    private String description;
    private SpaceCategoryContext spaceCategory;
    private String moduleName;


    public V3SpaceBookingPolicyJSONContext getPolicy() {
        if (policy == null) {
            return new V3SpaceBookingPolicyJSONContext();
        }
        return policy;
    }

    public void setPolicy(V3SpaceBookingPolicyJSONContext policy) {
        this.policy = policy;
    }

    private V3SpaceBookingPolicyJSONContext policy;

    public String getPolicyJson()  throws Exception {
        if (getPolicy() != null) {
            return FieldUtil.getAsJSON(getPolicy()).toJSONString();
        }
        return policyJson;
    }

    public void setPolicyJson(String policyJson)  throws Exception{
        if (policyJson != null && !policyJson.trim().isEmpty()) {
            JSONObject policyJSONObj = (JSONObject) new JSONParser().parse(policyJson);
            this.policy = FieldUtil.getAsBeanFromJson(policyJSONObj,V3SpaceBookingPolicyJSONContext.class );
            this.policyJson = policyJson;
        }

    }

    private String policyJson;

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private long criteriaId = -1;
    public long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(long criteriaId) {
        this.criteriaId = criteriaId;
    }



}
