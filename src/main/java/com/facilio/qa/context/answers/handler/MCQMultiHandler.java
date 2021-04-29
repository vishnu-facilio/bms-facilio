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

import java.util.Collections;
import java.util.stream.Collectors;

public class MCQMultiHandler extends AnswerHandler<MCQMultiAnswerContext> {
    public MCQMultiHandler(Class<MCQMultiAnswerContext> answerClass) {
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
        boolean isOther = StringUtils.isNotEmpty(((MCQMultiContext) question).getOtherOptionLabel());
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(answer.getAnswer().getSelected())
                                                    && (!isOther || StringUtils.isEmpty(answer.getAnswer().getOther()))
                                                    , "At least one option need to be selected for MCQ");
        AnswerContext answerContext = new AnswerContext();
        if (CollectionUtils.isNotEmpty(answer.getAnswer().getSelected())) { // Check is for handling answers only with 'other' option
            answerContext.setMultiEnumAnswer(answer.getAnswer().getSelected().stream()
                    .map(MCQOptionContext::new)
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
}
