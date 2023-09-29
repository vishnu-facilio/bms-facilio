package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AlarmFilterRuleContext extends V3Context {
    private String name;
    private AlarmTypeContext alarmType;
    private String description;
    private AlarmStrategy strategy;
    private V3ClientContext client;
    private List<FilterRuleCriteriaContext> filterRuleCriteriaList;
    private Long siteCriteriaId;
    private Long controllerCriteriaId;
    private Criteria siteCriteria;
    private Criteria controllerCrtieria;
    private Boolean status;

    public void setStrategy(Integer type) {
        if (type != null) {
            this.strategy = AlarmStrategy.valueOf(type);
        }
    }
    public AlarmStrategy getStrategyEnum() {
        return strategy;
    }
    public Integer getStrategy() {
        if (strategy != null) {
            return strategy.getIndex();
        }
        return null;
    }
}