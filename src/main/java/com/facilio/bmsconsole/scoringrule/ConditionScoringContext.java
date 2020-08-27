package com.facilio.bmsconsole.scoringrule;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ConditionScoringContext extends BaseScoringContext {

    private long criteriaId = -1;
    public long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(long criteriaId) {
        this.criteriaId = criteriaId;
    }

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (criteria != null) {
            boolean criteriaFlag = criteria.computePredicate(placeHolders).evaluate(record);
            return criteriaFlag ? 1f : 0f;
        }
        return 0;
    }

    @Override
    public void validate() {
        if (criteria == null || criteria.isEmpty()) {
            throw new IllegalArgumentException("Criteria cannot be empty");
        }
    }

    @Override
    public void saveChildren() throws Exception {
        criteriaId = CriteriaAPI.addCriteria(criteria);
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }
}
