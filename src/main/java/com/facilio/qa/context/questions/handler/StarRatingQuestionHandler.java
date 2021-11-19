package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.StarRatingQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;
import java.util.List;

public class StarRatingQuestionHandler implements QuestionHandler<StarRatingQuestionContext> {

    private void commonValidate(List<StarRatingQuestionContext> questions) throws Exception {
        Integer minVal = 1;
        Integer maxVal = 10;
        for (StarRatingQuestionContext q : questions) {
            Integer starCount = q.getNumberOfStars();
            V3Util.throwRestException(starCount != null && starCount != null && starCount < minVal, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Star Rating value ({0}) cannot be less than Min value ({1})", starCount, minVal));
            V3Util.throwRestException(starCount != null && starCount != null && starCount > maxVal, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Star Rating value ({0}) cannot be more than Max value ({1})", starCount, maxVal));
        }
    }


    @Override
    public void validateSave(List<StarRatingQuestionContext> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterSave(List<StarRatingQuestionContext> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<StarRatingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterUpdate(List<StarRatingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<StarRatingQuestionContext> questions) throws Exception {

    }
}
