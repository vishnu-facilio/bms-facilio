package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MCQSingleAnswerContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.qa.context.questions.MCQSingleContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Optional;

public class MCQSingleAnswerHandler extends AnswerHandler<MCQSingleAnswerContext> {
    public MCQSingleAnswerHandler(Class<MCQSingleAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQSingleAnswerContext serialize(AnswerContext answer) {
        MCQSingleAnswerContext mcqSingleAnswer = new MCQSingleAnswerContext();
        MCQSingleAnswerContext.MCQAnswer mcqAnswer = new MCQSingleAnswerContext.MCQAnswer();
        mcqAnswer.setSelected(answer.getEnumAnswer());
        if ( StringUtils.isNotEmpty(answer.getEnumOtherAnswer()) )  { // Assumption is we'll validate option before adding and so not doing any check here
            mcqAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqSingleAnswer.setAnswer(mcqAnswer);
        return mcqSingleAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQSingleAnswerContext answer, QuestionContext question) throws Exception {
        MCQSingleContext mcqQuestion = (MCQSingleContext) question;
        Long selected = answer.getAnswer().getSelected();
        V3Util.throwRestException(selected == null, ErrorCode.VALIDATION_ERROR, "At least one option need to be selected for MCQ");
        Optional<MCQOptionContext> option = mcqQuestion.getOptions().stream().filter(o -> o._getId() == selected).findFirst();
        V3Util.throwRestException(!option.isPresent(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected));
        AnswerContext answerContext = new AnswerContext();
        answerContext.setEnumAnswer(selected);
        if ( option.get().otherEnabled() && StringUtils.isNotEmpty(answer.getAnswer().getOther()) ) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        else {
            answer.getAnswer().setOther(null);
        }
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getEnumAnswer() == null;
    }

    @Override
    public boolean checkIfAnswerIsNull (MCQSingleAnswerContext answer, QuestionContext question) throws Exception {
        return answer.getAnswer().getSelected() == null;
    }
}
