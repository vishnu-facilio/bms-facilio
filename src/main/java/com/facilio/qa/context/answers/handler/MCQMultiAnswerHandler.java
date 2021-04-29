package com.facilio.qa.context.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.answers.MCQMultiAnswerContext;
import com.facilio.qa.context.questions.MCQMultiContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCQMultiAnswerHandler extends AnswerHandler<MCQMultiAnswerContext> {
    public MCQMultiAnswerHandler(Class<MCQMultiAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MCQMultiAnswerContext serialize(AnswerContext answer) {
        MCQMultiAnswerContext mcqMultiAnswer = new MCQMultiAnswerContext();
        MCQMultiAnswerContext.MCQMultiAnswer multiAnswer = new MCQMultiAnswerContext.MCQMultiAnswer();

        if (CollectionUtils.isNotEmpty(answer.getMultiEnumAnswer())) { // Check is for handling answers only with 'other' option
            multiAnswer.setSelected(answer.getMultiEnumAnswer().stream()
                                        .map(MCQOptionContext::_getId)
                                        .collect(Collectors.toList()));
        }
        if ( StringUtils.isNotEmpty(((MCQMultiContext) answer.getQuestion()).getOtherOptionLabel()) ) {
            multiAnswer.setOther(answer.getEnumOtherAnswer());
        }
        mcqMultiAnswer.setAnswer(multiAnswer);

        return mcqMultiAnswer;
    }

    @Override
    public AnswerContext deSerialize(MCQMultiAnswerContext answer, QuestionContext question) {
        MCQMultiContext mcqQuestion = (MCQMultiContext) question;
        boolean isOther = StringUtils.isNotEmpty(mcqQuestion.getOtherOptionLabel());
        List<Long> selected = answer.getAnswer().getSelected();
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(selected)
                                                    && (!isOther || StringUtils.isEmpty(answer.getAnswer().getOther()))
                                                    , "At least one option need to be selected for MCQ");
        AnswerContext answerContext = new AnswerContext();
        if (CollectionUtils.isNotEmpty(selected)) { // Check is for handling answers only with 'other' option
            answerContext.setMultiEnumAnswer(selected.stream()
                    .map(id -> validateAndCreateMCQOption(id, mcqQuestion))
                    .collect(Collectors.toList()));
        }
        else {
            answerContext.setMultiEnumAnswer(Collections.emptyList()); // To handle deletion
        }
        if (isOther) {
            answerContext.setEnumOtherAnswer(answer.getAnswer().getOther());
        }
        else {
            answer.getAnswer().setOther(null);
        }
        return answerContext;
    }

    private MCQOptionContext validateAndCreateMCQOption (Long selected, MCQMultiContext mcqQuestion) {
        FacilioUtil.throwIllegalArgumentException(selected != null && !mcqQuestion.getOptions().stream().anyMatch(o -> o._getId() == selected)
                , MessageFormat.format("Invalid select option ({0}) is specified while adding MCQ Answer", selected));
        return new MCQOptionContext(selected);
    }
}
