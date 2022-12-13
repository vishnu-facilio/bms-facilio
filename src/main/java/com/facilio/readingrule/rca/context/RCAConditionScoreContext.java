package com.facilio.readingrule.rca.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RCAConditionScoreContext extends V3Context {

    Long groupId;

    Long criteriaId;

    Double score;

    Criteria criteria;

}
