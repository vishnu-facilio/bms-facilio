package com.facilio.qa.rules.pojo;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;

public class ActionRule extends QAndARule<ActionRuleCondition> {

    @Override
    public void manipulateAnswer(QuestionContext question, AnswerContext answer) {

        answer.setAnswerContext(question,answer);
    }
}
