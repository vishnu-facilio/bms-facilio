package com.facilio.qa.context.client.answers.handler;

import com.facilio.modules.FieldType;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.DecimalAnswerContext;
import com.facilio.qa.context.questions.DecimalQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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
    public AnswerContext deSerialize(DecimalAnswerContext answer, QuestionContext question) throws Exception {
        Double minValue = ((DecimalQuestionContext) question).getMinValue();
        Double maxValue = ((DecimalQuestionContext) question).getMaxValue();
        Map<String, Object> errorParams = new HashMap<>();
        errorParams.put("answer",answer.getAnswer());
        errorParams.put("minValue",minValue);
        errorParams.put("maxValue",maxValue);
        V3Util.throwRestException(minValue != null && answer.getAnswer() < minValue, ErrorCode.VALIDATION_ERROR, "errors.qa.decimalAnswerHandler.minValueCheck",true,errorParams);
        V3Util.throwRestException(maxValue != null && answer.getAnswer() > maxValue, ErrorCode.VALIDATION_ERROR, "errors.qa.decimalAnswerHandler.maxValueCheck",true,errorParams);
//        V3Util.throwRestException(minValue != null && answer.getAnswer() < minValue, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), minValue),true,errorParams);
//        V3Util.throwRestException(maxValue != null && answer.getAnswer() > maxValue, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), maxValue),true,errorParams);
        AnswerContext answerContext = new AnswerContext();
        answerContext.setDecimalAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getDecimalAnswer() == null;
    }
    
    @Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
        return answer.getDecimalAnswer().toString();
	}
}
