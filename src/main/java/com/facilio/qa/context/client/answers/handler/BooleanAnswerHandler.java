package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.BooleanAnswerContext;

public class BooleanAnswerHandler extends AnswerHandler<BooleanAnswerContext> {
    public BooleanAnswerHandler(Class<BooleanAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public BooleanAnswerContext serialize(AnswerContext answer) {
        BooleanAnswerContext booleanAnswer = new BooleanAnswerContext();
        booleanAnswer.setAnswer(answer.getBooleanAnswer());
        return booleanAnswer;
    }

    @Override
    public AnswerContext deSerialize(BooleanAnswerContext answer, QuestionContext question) {
        AnswerContext answerContext = new AnswerContext();
        answerContext.setBooleanAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getBooleanAnswer() == null;
    }
}
