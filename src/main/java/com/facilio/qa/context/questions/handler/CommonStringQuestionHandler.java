package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.util.FacilioUtil;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Function;


@AllArgsConstructor
public class CommonStringQuestionHandler<Q extends QuestionContext> implements QuestionHandler<Q> {
    private Function<Q, Integer> maxLength;

    @Override
    public void validateSave(List<Q> questions) throws Exception {
        for (Q q : questions) {
            Integer maxLen = maxLength.apply(q);
            FacilioUtil.throwIllegalArgumentException(maxLen != null && maxLen <= 0, MessageFormat.format("Invalid maxLength ({0}) specified while adding string question", maxLen));
        }
    }

    @Override
    public void afterSave(List<Q> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<Q> questions) throws Exception {

    }

    @Override
    public void afterUpdate(List<Q> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<Q> questions) throws Exception {

    }
}
