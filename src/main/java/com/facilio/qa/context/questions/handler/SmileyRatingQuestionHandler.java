package com.facilio.qa.context.questions.handler;

import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.questions.RatingQuestionContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SmileyRatingQuestionHandler extends RatingQuestionHandler {

    @Override
    public void afterFetch(List<RatingQuestionContext> questions) throws Exception {
        computeRatingScaleRange(questions);
    }

    @Override
    public void afterSave(List<RatingQuestionContext> questions) throws Exception {
        computeRatingScaleRange(questions);
    }

    private void computeRatingScaleRange(List<RatingQuestionContext> questions) {
        if (CollectionUtils.isNotEmpty(questions)) {
            for (RatingQuestionContext rq : questions) {
                QAndAUtil.computeRatingScaleRange(rq);
            }
        }
    }
}
