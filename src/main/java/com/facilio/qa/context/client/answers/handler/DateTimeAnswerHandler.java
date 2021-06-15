package com.facilio.qa.context.client.answers.handler;

import com.facilio.modules.FieldType;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.DateTimeAnswerContext;

public class DateTimeAnswerHandler extends AnswerHandler<DateTimeAnswerContext> {
    public DateTimeAnswerHandler(Class<DateTimeAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public DateTimeAnswerContext serialize(AnswerContext answer) {
        DateTimeAnswerContext dateTimeAnswer = new DateTimeAnswerContext();
        dateTimeAnswer.setAnswer(answer.getDateTimeAnswer());
        return dateTimeAnswer;
    }

    @Override
    public AnswerContext deSerialize(DateTimeAnswerContext answer, QuestionContext question) {
        AnswerContext answerContext = new AnswerContext();
        answerContext.setDateTimeAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getDateTimeAnswer() == null;
    }
}
