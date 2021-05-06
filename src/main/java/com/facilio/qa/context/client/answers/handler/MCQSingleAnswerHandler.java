package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MCQSingleAnswerContext;
import com.facilio.qa.context.questions.MCQSingleContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

public class MCQSingleAnswerHandler extends AnswerHandler<MCQSingleAnswerContext> {
    public MCQSingleAnswerHandler(Class<MCQSingleAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQSingleAnswerContext serialize(AnswerContext answer) {
        MCQSingleAnswerContext mcqSingleAnswer = new MCQSingleAnswerContext();
        MCQSingleAnswerContext.MCQAnswer mcqAnswer = new MCQSingleAnswerContext.MCQAnswer();
        mcqAnswer.setSelected(answer.getEnumAnswer());
        if ( StringUtils.isNotEmpty(((MCQSingleContext) answer.getQuestion()).getOtherOptionLabel()) ) {
            mcqAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqSingleAnswer.setAnswer(mcqAnswer);
        return mcqSingleAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQSingleAnswerContext answer, QuestionContext question) throws Exception {
        MCQSingleContext mcqQuestion = (MCQSingleContext) question;
        boolean isOther = StringUtils.isNotEmpty(mcqQuestion.getOtherOptionLabel());
        Long selected = answer.getAnswer().getSelected();
        V3Util.throwRestException(selected == null
                                                    && (!isOther || StringUtils.isEmpty(answer.getAnswer().getOther()))
                , ErrorCode.VALIDATION_ERROR, "At least one option need to be selected for MCQ");
        V3Util.throwRestException(selected != null && !mcqQuestion.getOptions().stream().anyMatch(o -> o._getId() == selected)
                , ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setEnumAnswer(selected);
        if ( isOther ) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        else {
            answer.getAnswer().setOther(null);
        }
        return answerContext;
    }
}
