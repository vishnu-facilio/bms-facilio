package com.facilio.qa.rules.bean;

import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.qa.rules.pojo.ScoringRule;

import java.util.Collection;
import java.util.List;

public interface QAndARuleBean {
    public List<QAndARule> getRulesOfTemplate (long templateId) throws Exception;

    public List<ScoringRule> getScoringRulesOfTemplate (long templateId) throws Exception;

    public <T extends QAndARule> List<T> getRulesOfTemplateOfType (long templateId, QAndARuleType type) throws Exception;

    public <T extends QAndARule> List<T> getRulesOfQuestionsOfType (long templateId, Collection<Long> questionIds, QAndARuleType type) throws Exception;

    public <T extends QAndARule> void addRules (List<T> rules, QAndARuleType type) throws Exception;

    public <T extends QAndARule> int updateRules (List<T> rules, QAndARuleType type) throws Exception;

    public <T extends QAndARule> int deleteRules (List<T> rules, QAndARuleType type) throws Exception;

    public <C extends RuleCondition> void addConditions (List<C> conditions, QAndARuleType type) throws Exception;

    public <C extends RuleCondition> int updateCondition (List<C> condition, QAndARuleType type) throws Exception;

    public <C extends RuleCondition> int deleteConditions (List<C> conditions, QAndARuleType type) throws Exception;
}
