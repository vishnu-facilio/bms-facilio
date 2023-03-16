package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.FileUploadAnswerContext;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class FileUploadAnswerHandler extends AnswerHandler<FileUploadAnswerContext> {
    public FileUploadAnswerHandler(Class<FileUploadAnswerContext> answerClass) {
        super(answerClass);
    }

    @Override
    public FileUploadAnswerContext serialize(AnswerContext answer) {
        FileUploadAnswerContext fileUploadAnswer = new FileUploadAnswerContext();
        FileUploadAnswerContext.FileAnswer fileAnswer = new FileUploadAnswerContext.FileAnswer(answer.getFileAnswerId(), answer.getFileAnswerFileName(), answer.getFileAnswerUrl(), answer.getFileAnswerContentType(), answer.getFileAnswerDownloadUrl());
        fileUploadAnswer.setAnswer(fileAnswer);

        return fileUploadAnswer;
    }

    @Override
    public AnswerContext deSerialize(FileUploadAnswerContext answer, QuestionContext question) throws Exception {
        AnswerContext answerContext = new AnswerContext();
        V3Util.throwRestException(checkIfAnswerIsNull(answer, question), ErrorCode.VALIDATION_ERROR, "errors.qa.fileUploadAnswerHandler.fileIdCheck",true,null);
        //V3Util.throwRestException(checkIfAnswerIsNull(answer, question), ErrorCode.VALIDATION_ERROR, "File ID cannot be null while adding file upload answer",true,null);
        answerContext.setFileAnswerId(answer.getAnswer().getId());
        return answerContext;
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return answer.getFileAnswerId() == null;
    }

	@Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
		FileStore fs = FacilioFactory.getFileStore();
		return fs.getFileInfo(answer.getFileAnswerId()).getFileName();
	}
}
