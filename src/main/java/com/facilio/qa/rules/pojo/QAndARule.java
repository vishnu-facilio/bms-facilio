package com.facilio.qa.rules.pojo;

import com.facilio.qa.context.QuestionContext;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class QAndARule<Condition extends RuleCondition> {

    private Long id;
    private Long templateId;
    private Long questionId;
    private QAndARuleType type;

    private List<Condition> ruleConditions; // For internal purpose
    public void setRuleConditions(List<Condition> ruleConditions) {
        if (type != null && CollectionUtils.isNotEmpty(ruleConditions) && !type.getRuleConditionClass().isAssignableFrom(ruleConditions.get(0).getClass())) {
            throw new RuntimeException("Invalid condition object passed in rules");
        }
        this.ruleConditions = ruleConditions;
    }

    // Following props are just for client response
    private String question;
    private List<Map<String, Object>> conditions;

    public void beforeSaveHook(QuestionContext question) {

    }

    public final void beforeSave (QuestionContext question) {
        FacilioUtil.throwIllegalArgumentException(questionId == null, "Question id cannot be null while calling before save");
        FacilioUtil.throwIllegalArgumentException(question == null || question.getId() < 0 || question.getId() != questionId, "Invalid question passed to hook");
        beforeSaveHook(question);
    }
}
