package com.facilio.qa.context.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.answers.NumberAnswerContext;

public class NumberAnswerHandler extends AnswerHandler<NumberAnswerContext> {

    public NumberAnswerHandler(Class<NumberAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public NumberAnswerContext serialize(AnswerContext answer) {
        NumberAnswerContext numberAnswer = new NumberAnswerContext();
        numberAnswer.setAnswer(answer.getNumberAnswer());
        return numberAnswer;
    }

    @Override
    public AnswerContext deSerialize(NumberAnswerContext answer) {
        AnswerContext answerContext = new AnswerContext();
        answerContext.setNumberAnswer(answer.getAnswer());
        return answerContext;
    }
}
