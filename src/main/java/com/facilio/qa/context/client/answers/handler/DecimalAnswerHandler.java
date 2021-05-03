package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.DecimalAnswerContext;

public class DecimalAnswerHandler extends AnswerHandler<DecimalAnswerContext> {

    public DecimalAnswerHandler(Class<DecimalAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public DecimalAnswerContext serialize(AnswerContext answer) {
        DecimalAnswerContext decimalAnswer = new DecimalAnswerContext();
        decimalAnswer.setAnswer(answer.getDecimalAnswer());
        return decimalAnswer;
    }

    @Override
    public AnswerContext deSerialize(DecimalAnswerContext answer, QuestionContext question) {
        AnswerContext answerContext = new AnswerContext();
        answerContext.setDecimalAnswer(answer.getAnswer());
        return answerContext;
    }
}
