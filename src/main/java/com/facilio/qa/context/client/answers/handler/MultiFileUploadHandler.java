package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.answers.MultiFileAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MultiFileUploadAnswerContext;
import com.facilio.qa.context.questions.MultiFileUploadQuestionContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
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
    public AnswerContext deSerialize(MultiFileUploadAnswerContext answer, QuestionContext question) throws Exception {
        boolean isRemarksEnabled = StringUtils.isNotEmpty(((MultiFileUploadQuestionContext) question).getIndividualRemarksLabel());
        V3Util.throwRestException(checkIfAnswerIsNull(answer, question), ErrorCode.VALIDATION_ERROR, "File IDs cannot be empty while adding file upload answer");
        List<MultiFileAnswerContext> multiAnswers = answer.getAnswer().stream()
                                                        .map(a -> constructMultiFileAnswerContext(a, isRemarksEnabled))
                                                        .collect(Collectors.toList());
        AnswerContext answerContext = new AnswerContext();
        answerContext.setMultiFileAnswer(multiAnswers);
        return answerContext;
    }

    @SneakyThrows
    private MultiFileAnswerContext constructMultiFileAnswerContext (MultiFileUploadAnswerContext.MultiFileAnswer answer, boolean isRemarksEnabled) {
        V3Util.throwRestException(answer.getId() == null, ErrorCode.VALIDATION_ERROR, "File ID cannot be null while adding file answers"); //Assuming we'll upload file first and send only id
        MultiFileAnswerContext answerContext = new MultiFileAnswerContext();
        answerContext.setFileAnswerId(answer.getId());
        if (isRemarksEnabled) {
            answerContext.setRemarks(answer.getRemarks());
        }
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return CollectionUtils.isEmpty(answer.getMultiFileAnswer());
    }

    @Override
    public boolean checkIfAnswerIsNull (MultiFileUploadAnswerContext answer, QuestionContext question) throws Exception {
        return CollectionUtils.isEmpty(answer.getAnswer()) || checkEachAnswerIsNull(answer.getAnswer());
    }

    private boolean checkEachAnswerIsNull (List<MultiFileUploadAnswerContext.MultiFileAnswer> files) {
        return files.stream().anyMatch(f -> f.getId() == null);
    }
}
