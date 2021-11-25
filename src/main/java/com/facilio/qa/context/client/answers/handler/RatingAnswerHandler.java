package com.facilio.qa.context.client.answers.handler;

import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.RatingAnswerContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;

public class RatingAnswerHandler extends AnswerHandler<RatingAnswerContext> {

    public RatingAnswerHandler(Class<RatingAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public RatingAnswerContext serialize(AnswerContext answer) throws Exception {
        RatingAnswerContext starAnswer = new RatingAnswerContext();
        starAnswer.setAnswer(answer.getRatingAnswer());
        return starAnswer;
    }

    @Override
    public AnswerContext deSerialize(RatingAnswerContext answer, QuestionContext question) throws Exception {
        V3Util.throwRestException(answer.getAnswer() < FacilioConstants.QAndA.MIN_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MIN_VAL));
        V3Util.throwRestException(answer.getAnswer() > FacilioConstants.QAndA.MAX_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MAX_VAL));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setRatingAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull(AnswerContext answer) throws Exception {
        return answer.getRatingAnswer() == null;
    }

    @Override
    public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
        return answer.getRatingAnswer().toString();
    }
}
