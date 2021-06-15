package com.facilio.qa.rules.pojo;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.rules.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum QAndARuleType implements FacilioStringEnum {
    SCORING (
            ScoringRule.class,
            ScoringRule::new,
            ScoringRuleCondition.class,
            ScoringRuleCondition::new,
            Constants.ModuleFactory.scoringRuleModule(),
            Constants.FieldFactory.scoringRuleFields(),
            Constants.ModuleFactory.scoringRuleConditionModule(),
            Constants.FieldFactory.scoringRuleConditionFields()
    ),
    WORKFLOW(
            ActionRule.class,
            ActionRule::new,
            ActionRuleCondition.class,
            ActionRuleCondition::new,
            Constants.ModuleFactory.qandaRuleModule(),
            Constants.FieldFactory.qandaRuleFields(),
            Constants.ModuleFactory.ruleConditionModule(),
            Constants.FieldFactory.ruleConditionFields()
    )
    ;

    @NonNull
    private Class<? extends QAndARule> ruleClass;
    public <T extends QAndARule> Class<T> getRuleClass() {
        return (Class<T>) ruleClass;
    }

    @NonNull
    private Supplier<? extends QAndARule> ruleConstructor;
    public <T extends QAndARule> T constructRule() {
        return (T) ruleConstructor.get();
    }

    @NonNull
    private Class<? extends RuleCondition> ruleConditionClass;
    public <T extends RuleCondition> Class<T> getRuleConditionClass() {
        return (Class<T>) ruleConditionClass;
    }

    @NonNull
    private Supplier<? extends RuleCondition> conditionConstructor;
    public <T extends RuleCondition> T constructCondition() {
        return (T) conditionConstructor.get();
    }

    @NonNull
    private FacilioModule ruleModule;
    @NonNull
    private List<FacilioField> ruleFields;
    @NonNull
    private FacilioModule ruleConditionsModule;
    @NonNull
    private List<FacilioField> ruleConditionFields;
}
