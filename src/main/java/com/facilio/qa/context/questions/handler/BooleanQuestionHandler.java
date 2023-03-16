package com.facilio.qa.context.questions.handler;

import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.BooleanQuestionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooleanQuestionHandler implements QuestionHandler<BooleanQuestionContext> {

    private void commonValidate (List<BooleanQuestionContext> questions) throws RESTException {
        Map<String, Object> errorParams = new HashMap<>();
        for (BooleanQuestionContext q : questions) {
            String trueLabel = q.getTrueLabel();
            String falseLabel = q.getFalseLabel();
            errorParams.put("falseLabel",falseLabel);
            errorParams.put("trueLabel",trueLabel);
            V3Util.throwRestException(StringUtils.isEmpty(trueLabel) && StringUtils.isNotEmpty(falseLabel), ErrorCode.VALIDATION_ERROR, "errors.qa.booleanQuestionHandler.trueLabelEmptyCheck",true,errorParams);
            V3Util.throwRestException(StringUtils.isNotEmpty(trueLabel) && StringUtils.isEmpty(falseLabel), ErrorCode.VALIDATION_ERROR, "errors.qa.booleanQuestionHandler.falseLabelEmptyCheck",true,errorParams);
//            V3Util.throwRestException(StringUtils.isEmpty(trueLabel) && StringUtils.isNotEmpty(falseLabel), ErrorCode.VALIDATION_ERROR, MessageFormat.format("True label cannot be empty when False label ({0}) is not empty", falseLabel),true,errorParams);
//            V3Util.throwRestException(StringUtils.isNotEmpty(trueLabel) && StringUtils.isEmpty(falseLabel), ErrorCode.VALIDATION_ERROR, MessageFormat.format("False label cannot be empty when True label ({0}) is not empty", trueLabel),true,errorParams);
        }
    }

    @Override
    public void validateSave(List<BooleanQuestionContext> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterSave(List<BooleanQuestionContext> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<BooleanQuestionContext> questions) throws Exception {
        commonValidate(questions);
    }

    @Override
    public void afterUpdate(List<BooleanQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<BooleanQuestionContext> questions) throws Exception {

    }
}
