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
    public static final int BIG_STRING_MAX_LENGTH = 2000;
    public static final int SHORT_STRING_MAX_LENGTH = 255;

    private Function<Q, Integer> maxLength;
    private int maxAllowedLen;

    private void commonValidate (List<Q> questions) {
        for (Q q : questions) {
            Integer maxLen = maxLength.apply(q);
            FacilioUtil.throwIllegalArgumentException(maxLen != null && maxLen <= 0, MessageFormat.format("Invalid maxLength ({0}) specified while adding string question", maxLen));
            FacilioUtil.throwIllegalArgumentException(maxLen != null && maxLen > maxAllowedLen, MessageFormat.format("Max Length ({0}) cannot be greater than {1}", maxLen, maxAllowedLen));
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
