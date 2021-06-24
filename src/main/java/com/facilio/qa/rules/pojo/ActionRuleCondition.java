package com.facilio.qa.rules.pojo;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class ActionRuleCondition extends RuleCondition {
    private List<ActionContext> actions;

    @Override
    public boolean hasAction() {
        return CollectionUtils.isNotEmpty(actions);
    }

    @Override
    public void executeTrueAction(QuestionContext question, AnswerContext answer) throws Exception {

    }
}
