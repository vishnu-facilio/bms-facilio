package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.BooleanQuestionContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;

public class BooleanQuestionHandler implements QuestionHandler<BooleanQuestionContext> {
    @Override
    public void validateSave(List<BooleanQuestionContext> questions) throws Exception {
        for (BooleanQuestionContext q : questions) {
            String trueLabel = q.getTrueLabel();
            String falseLabel = q.getFalseLabel();
            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(trueLabel) && StringUtils.isNotEmpty(falseLabel), MessageFormat.format("True label cannot be empty when False label ({0}) is not empty", falseLabel));
            FacilioUtil.throwIllegalArgumentException(StringUtils.isNotEmpty(trueLabel) && StringUtils.isEmpty(falseLabel), MessageFormat.format("False label cannot be empty when True label ({0}) is not empty", trueLabel));
        }
    }

    @Override
    public void afterSave(List<BooleanQuestionContext> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<BooleanQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterUpdate(List<BooleanQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<BooleanQuestionContext> questions) throws Exception {

    }
}
