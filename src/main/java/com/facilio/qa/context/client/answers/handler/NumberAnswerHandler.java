package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.NumberAnswerContext;
import com.facilio.qa.context.questions.NumberQuestionContext;
import com.facilio.util.FacilioUtil;

import java.text.MessageFormat;

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
    public AnswerContext deSerialize(NumberAnswerContext answer, QuestionContext question) {
        Long minValue = ((NumberQuestionContext) question).getMinValue();
        Long maxValue = ((NumberQuestionContext) question).getMaxValue();
        FacilioUtil.throwIllegalArgumentException(minValue != null && answer.getAnswer() < minValue, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), minValue));
        FacilioUtil.throwIllegalArgumentException(maxValue != null && answer.getAnswer() > maxValue, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), maxValue));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setNumberAnswer(answer.getAnswer());
        return answerContext;
    }
}
