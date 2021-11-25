package com.facilio.qa.context.questions.handler;

import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;
import java.util.List;

public class RatingQuestionHandler implements QuestionHandler<RatingQuestionContext> {

    private void commonValidate(List<RatingQuestionContext> questions) throws Exception {
        for (RatingQuestionContext q : questions) {
            Integer starCount = q.getRatingScale();
            V3Util.throwRestException(starCount != null && starCount != null && starCount < FacilioConstants.QAndA.MIN_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Star Rating value ({0}) cannot be less than Min value ({1})", starCount, FacilioConstants.QAndA.MIN_VAL));
            V3Util.throwRestException(starCount != null && starCount != null && starCount > FacilioConstants.QAndA.MAX_VAL, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Star Rating value ({0}) cannot be more than Max value ({1})", starCount, FacilioConstants.QAndA.MAX_VAL));
        }
    }


    @Override
    public void validateSave(List<RatingQuestionContext> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterSave(List<RatingQuestionContext> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<RatingQuestionContext> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterUpdate(List<RatingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<RatingQuestionContext> questions) throws Exception {

    }
}
