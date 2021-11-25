package com.facilio.qa.context.client.answers.handler;

import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.StarRatingAnswerContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;

public class StarRatingAnswerHandler extends AnswerHandler<StarRatingAnswerContext> {

    public StarRatingAnswerHandler(Class<StarRatingAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public StarRatingAnswerContext serialize(AnswerContext answer) throws Exception {
        StarRatingAnswerContext starAnswer = new StarRatingAnswerContext();
        starAnswer.setAnswer(answer.getStarRatingAnswer());
        return starAnswer;
    }

    @Override
    public AnswerContext deSerialize(StarRatingAnswerContext answer, QuestionContext question) throws Exception {
        V3Util.throwRestException(answer.getAnswer() < FacilioConstants.QAndA.MIN_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be less than the min value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MIN_VAL));
        V3Util.throwRestException(answer.getAnswer() > FacilioConstants.QAndA.MAX_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer ({0}) cannot be greater than the max value ({1})", answer.getAnswer(), FacilioConstants.QAndA.MAX_VAL));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setStarRatingAnswer(answer.getAnswer());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull(AnswerContext answer) throws Exception {
        return answer.getStarRatingAnswer() == null;
    }

    @Override
    public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
        return answer.getStarRatingAnswer().toString();
    }
}
