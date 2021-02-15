package com.facilio.bmsconsole.scoringrule;

import com.facilio.db.criteria.manager.NamedCriteria;
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
    public float evaluatedScore(Object record, Context context, Map<String, Object> placeHolders, long moduleId) throws Exception {
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
    public void validate() throws Exception {
        super.validate();
        if (namedCriteriaId < 0) {
            throw new IllegalArgumentException("NamedCriteria cannot be empty");
        }
    }

    @Override
    public void saveChildren() throws Exception {
//        namedCriteriaId = NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria);
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }
}
