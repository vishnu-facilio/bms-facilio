package com.facilio.bmsconsole.scoringrule;

import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ConditionScoringContext extends BaseScoringContext {

    private long namedCriteriaId = -1L;
    public long getNamedCriteriaId() {
        return namedCriteriaId;
    }
    public void setNamedCriteriaId(long namedCriteriaId) {
        this.namedCriteriaId = namedCriteriaId;
    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    @Override
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (namedCriteria != null) {
            boolean criteriaFlag = namedCriteria.evaluate(record, context, placeHolders);
            return criteriaFlag ? 1f : 0f;
        }
        return 0;
    }

    @Override
    public String getScoreType() {
        return "condition";
    }

    @Override
    public void validate() {
        super.validate();
        if (namedCriteria == null) {
            throw new IllegalArgumentException("Criteria cannot be empty");
        }
        namedCriteria.validate();
    }

    @Override
    public void saveChildren() throws Exception {
        namedCriteriaId = NamedCriteriaAPI.addNamedCriteria(namedCriteria);
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }
}
