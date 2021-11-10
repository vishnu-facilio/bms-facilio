package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.QuestionContext;

public class ActionRule extends QAndARule<ActionRuleCondition> {

    @Override
    public void manipulateAnswer(QuestionContext question, AnswerContext answer) {

        answer.setClientAnswerContext(answer);
    }
}
