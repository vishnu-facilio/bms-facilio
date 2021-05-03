package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.util.FacilioUtil;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public class CommonNumberQuestionHandler<Q extends QuestionContext> implements QuestionHandler<Q> {

    private Function<Q, Number> minValue, maxValue;

    private void commonValidate (List<Q> questions) {
        for (Q q : questions) {
            Number minVal = minValue.apply(q);
            Number maxVal = maxValue.apply(q);
            FacilioUtil.throwIllegalArgumentException(minVal != null && maxVal != null && minVal.doubleValue() > maxVal.doubleValue(), MessageFormat.format("Max value ({0}) cannot be less than Min value ({1})", maxVal, minVal));
        }
    }

    @Override
    public void validateSave(List<Q> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterSave(List<Q> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<Q> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterUpdate(List<Q> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<Q> questions) throws Exception {

    }
}
