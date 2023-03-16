package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingQuestionHandler implements QuestionHandler<RatingQuestionContext> {

    @Override
    public boolean showSummaryOfResponses(QuestionContext question) throws Exception {
        return true;
    }

    @Override
    public boolean hideShowResponseButton(QuestionContext question) throws Exception {
        return true;
    }


    private void commonValidate(List<RatingQuestionContext> questions) throws Exception {
        Map<String, Number> errorParams = new HashMap<>();
        for (RatingQuestionContext q : questions) {
            Integer ratingScale = q.getRatingScale();
            if((ratingScale < RatingQuestionContext.MIN_RATING || ratingScale > RatingQuestionContext.MAX_RATING)) {
                errorParams.put("ratingScale",ratingScale);
                errorParams.put("minRating",RatingQuestionContext.MIN_RATING);
                errorParams.put("maxRating",RatingQuestionContext.MAX_RATING);
            }
            V3Util.throwRestException((ratingScale < RatingQuestionContext.MIN_RATING || ratingScale > RatingQuestionContext.MAX_RATING), ErrorCode.VALIDATION_ERROR, "errors.qa.ratingQuestionHandler.ratingCheck",true,errorParams);
            //V3Util.throwRestException((ratingScale < RatingQuestionContext.MIN_RATING || ratingScale > RatingQuestionContext.MAX_RATING), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Rating scale ({0}) cannot be less than the min value ({1}) and more than max value ({2})", ratingScale, RatingQuestionContext.MIN_RATING, RatingQuestionContext.MAX_RATING),true,errorParams);
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
