package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.FileUploadAnswerContext;

public class FileUploadAnswerHandler extends AnswerHandler<FileUploadAnswerContext> {
    public FileUploadAnswerHandler(Class<FileUploadAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public FileUploadAnswerContext serialize(AnswerContext answer) {
        FileUploadAnswerContext fileUploadAnswer = new FileUploadAnswerContext();
        FileUploadAnswerContext.FileAnswer fileAnswer = new FileUploadAnswerContext.FileAnswer(answer.getFileAnswerId(), answer.getFileAnswerFileName(), answer.getFileAnswerUrl(), answer.getFileAnswerContentType());
        fileUploadAnswer.setAnswer(fileAnswer);

        return fileUploadAnswer;
    }

    @Override
    public AnswerContext deSerialize(FileUploadAnswerContext answer, QuestionContext question) {
        AnswerContext answerContext = new AnswerContext();
        answerContext.setFileAnswerId(answer.getAnswer().getId());
        return answerContext;
    }
}
