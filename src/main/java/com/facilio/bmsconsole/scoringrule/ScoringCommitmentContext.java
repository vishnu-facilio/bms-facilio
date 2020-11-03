package com.facilio.bmsconsole.scoringrule;

import com.facilio.db.criteria.manager.NamedCriteria;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ScoringCommitmentContext implements Serializable {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long namedCriteriaId = -1;
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

    private int order = -1;
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

    private List<BaseScoringContext> baseScoringContexts;
    public List<BaseScoringContext> getBaseScoringContexts() {
        return baseScoringContexts;
    }
    public void setBaseScoringContexts(List<BaseScoringContext> baseScoringContexts) {
        this.baseScoringContexts = baseScoringContexts;
    }

    private long scoringRuleId = -1;
    public long getScoringRuleId() {
        return scoringRuleId;
    }
    public void setScoringRuleId(long scoringRuleId) {
        this.scoringRuleId = scoringRuleId;
    }

    public boolean evaluate(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        boolean result = true;
        if (namedCriteria != null) {
            result = namedCriteria.evaluate(record, context, placeHolders);
        }
        return result;
    }
}
