package com.facilio.qa.context.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.MultiFileAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.answers.MultiFileUploadAnswerContext;
import com.facilio.qa.context.questions.MultiFileUploadQuestionContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MultiFileUploadHandler extends AnswerHandler<MultiFileUploadAnswerContext> {
    public MultiFileUploadHandler(Class<MultiFileUploadAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public MultiFileUploadAnswerContext serialize(AnswerContext answer) {
        MultiFileUploadAnswerContext multiFileUploadAnswer = new MultiFileUploadAnswerContext();
        boolean isRemarksEnabled = StringUtils.isNotEmpty(((MultiFileUploadQuestionContext) answer.getQuestion()).getIndividualRemarksLabel());
        List<MultiFileUploadAnswerContext.MultiFileAnswer> answers = answer.getMultiFileAnswer().stream()
                                                                    .map(a -> new MultiFileUploadAnswerContext.MultiFileAnswer(a, isRemarksEnabled))
                                                                    .collect(Collectors.toList());
        multiFileUploadAnswer.setAnswer(answers);
        return multiFileUploadAnswer;
    }

    @Override
    public AnswerContext deSerialize(MultiFileUploadAnswerContext answer, QuestionContext question) {
        boolean isRemarksEnabled = StringUtils.isNotEmpty(((MultiFileUploadQuestionContext) question).getIndividualRemarksLabel());
        List<MultiFileAnswerContext> multiAnswers = answer.getAnswer().stream()
                                                        .map(a -> constructMultiFileAnswerContext(a, isRemarksEnabled))
                                                        .collect(Collectors.toList());
        AnswerContext answerContext = new AnswerContext();
        answerContext.setMultiFileAnswer(multiAnswers);
        return answerContext;
    }

    private MultiFileAnswerContext constructMultiFileAnswerContext (MultiFileUploadAnswerContext.MultiFileAnswer answer, boolean isRemarksEnabled) {
        FacilioUtil.throwIllegalArgumentException(answer.getId() == null, "File ID cannot be null while adding file answers"); //Assuming we'll upload file first and send only id
        MultiFileAnswerContext answerContext = new MultiFileAnswerContext();
        answerContext.setFileAnswerId(answer.getId());
        if (isRemarksEnabled) {
            answerContext.setRemarks(answer.getRemarks());
        }
        return answerContext;
    }
}
