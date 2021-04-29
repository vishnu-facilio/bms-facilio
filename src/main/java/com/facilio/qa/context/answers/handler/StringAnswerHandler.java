package com.facilio.qa.context.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.answers.StringAnswerContext;

public class StringAnswerHandler extends AnswerHandler<StringAnswerContext> {
    private boolean isBigString;
    public StringAnswerHandler(Class<StringAnswerContext> answerClass, boolean isBigString) {
        super(answerClass);
        this.isBigString = isBigString;
    }

    @Override
    public StringAnswerContext serialize(AnswerContext answer) {
        StringAnswerContext stringAnswer = new StringAnswerContext();
        stringAnswer.setAnswer(isBigString ? answer.getLongAnswer() : answer.getShortAnswer());
        return stringAnswer;
    }

    @Override
    public AnswerContext deSerialize(StringAnswerContext answer, QuestionContext question) {
        AnswerContext answerContext = new AnswerContext();
        if (isBigString) {
            answerContext.setLongAnswer(answer.getAnswer());
        }
        else {
            answerContext.setShortAnswer(answer.getAnswer());
        }
        return answerContext;
    }
}
