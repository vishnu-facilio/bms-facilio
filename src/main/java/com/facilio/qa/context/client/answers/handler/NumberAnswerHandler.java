package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.NumberAnswerContext;
import com.facilio.qa.context.questions.NumberQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

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
    public AnswerContext deSerialize(NumberAnswerContext answer, QuestionContext question) throws Exception {
        Long minValue = ((NumberQuestionContext) question).getMinValue();
        Long maxValue = ((NumberQuestionContext) question).getMaxValue();
        V3Util.throwRestException(minValue != null && answer.getAnswer() < minValue, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), minValue));
        V3Util.throwRestException(maxValue != null && answer.getAnswer() > maxValue, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), maxValue));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setNumberAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public void validateAnswer(Map<String, Object> props) throws Exception {
        String answer = (String) props.get("answer");
        V3Util.throwRestException(!StringUtils.isNumeric(answer), ErrorCode.VALIDATION_ERROR, "Not a valid number format");
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getNumberAnswer() == null;
    }
    
    @Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
        return answer.getNumberAnswer().toString();
	}
}
