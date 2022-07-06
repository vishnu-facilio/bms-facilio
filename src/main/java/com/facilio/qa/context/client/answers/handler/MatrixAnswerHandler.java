package com.facilio.qa.context.client.answers.handler;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.MatrixAnswer;

public class MatrixAnswerHandler extends AnswerHandler<MatrixAnswerContext> {

	public MatrixAnswerHandler(Class<MatrixAnswerContext> answerClass) {
		super(answerClass);
	}

	@Override
	public MatrixAnswerContext serialize(AnswerContext answer) throws Exception {
		// TODO Auto-generated method stub
		MatrixAnswer matrixAnswer = answer.getMatrixAnswer();
		
		MatrixAnswerContext answerContext = new MatrixAnswerContext();
		
		answerContext.setAnswer(matrixAnswer);
		return answerContext;
	}

	@Override
	public AnswerContext deSerialize(MatrixAnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
		AnswerContext answerContext = new AnswerContext();
		answerContext.setMatrixAnswer(answer.getAnswer());
		return answerContext;
	}

	@Override
	public boolean checkIfAnswerIsNull(AnswerContext answer) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAnswerStringValue(AnswerContext answer, QuestionContext question) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateRelatedRecordsForAnswer(AnswerContext answer) throws Exception{

		FacilioChain chain = QAndAReadOnlyChainFactory.fetchMatrixRelatedRecordChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.QAndA.ANSWER,answer);
		chain.execute();

	}
}
