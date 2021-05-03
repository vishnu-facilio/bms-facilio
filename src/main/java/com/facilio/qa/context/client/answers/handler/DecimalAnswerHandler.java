package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.DecimalAnswerContext;
import com.facilio.qa.context.questions.DecimalQuestionContext;
import com.facilio.qa.context.questions.NumberQuestionContext;
import com.facilio.util.FacilioUtil;

import java.text.MessageFormat;

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
        Double minValue = ((DecimalQuestionContext) question).getMinValue();
        Double maxValue = ((DecimalQuestionContext) question).getMaxValue();
        FacilioUtil.throwIllegalArgumentException(minValue != null && answer.getAnswer() < minValue, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), minValue));
        FacilioUtil.throwIllegalArgumentException(maxValue != null && answer.getAnswer() > maxValue, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), maxValue));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setDecimalAnswer(answer.getAnswer());
        return answerContext;
    }
}
