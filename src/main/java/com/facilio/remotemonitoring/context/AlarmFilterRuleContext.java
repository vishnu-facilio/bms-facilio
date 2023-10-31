package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
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
    private AlarmApproach alarmApproach;
    private V3ClientContext client;
    private List<FilterRuleCriteriaContext> filterRuleCriteriaList;
    private Long siteCriteriaId;
    private Long controllerCriteriaId;
    private Criteria siteCriteria;
    private Criteria controllerCrtieria;
    private Boolean status;
    private RuleType ruleType;
    public void setAlarmApproach(Integer type) {
        if (type != null) {
            this.alarmApproach = AlarmApproach.valueOf(type);
        }
    }
    public AlarmApproach getAlarmApproachEnum() {
        return alarmApproach;
    }
    public Integer getAlarmApproach() {
        if (alarmApproach != null) {
            return alarmApproach.getIndex();
        }
        return null;
    }

    public boolean isEnabled() {
        if(status == null) {
            return false;
        }
        return status;
    }
}