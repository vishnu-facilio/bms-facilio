package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionType;
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
    private Long sysCreatedTime;
    private Long sysCreatedBy;
    private Long sysModifiedTime;
    private Long sysModifiedBy;
    private List<ActionContext> actions;

    private List<Condition> ruleConditions; // For internal purpose
    public void setRuleConditions(List<Condition> ruleConditions) {
        if (type != null && CollectionUtils.isNotEmpty(ruleConditions) && !type.getRuleConditionClass().isAssignableFrom(ruleConditions.get(0).getClass())) {
            throw new RuntimeException("Invalid condition object passed in rules");
        }
        this.ruleConditions = ruleConditions;
    }

    // Following props are just for client response
    private String question;
    private QuestionType questionType;
    private String questionDescription;
    private List<Map<String, Object>> conditions;

    public void beforeSaveHook(QuestionContext question) {

    }

    public final void beforeSave (QuestionContext question) {
        FacilioUtil.throwIllegalArgumentException(questionId == null, "Question id cannot be null while calling before save");
        FacilioUtil.throwIllegalArgumentException(question == null || question.getId() < 0 || question.getId() != questionId, "Invalid question passed to hook");
        beforeSaveHook(question);
    }

    public void copyDefaultProps (QAndARule newRule) {
        newRule.id = this.id;
        newRule.sysCreatedTime = this.sysCreatedTime;
        newRule.sysCreatedBy = this.sysCreatedBy;
        newRule.sysModifiedTime = this.sysModifiedTime;
        newRule.sysModifiedBy = this.sysModifiedBy;
    }


    public abstract void manipulateAnswer(QuestionContext question, AnswerContext answer) throws Exception;
}
