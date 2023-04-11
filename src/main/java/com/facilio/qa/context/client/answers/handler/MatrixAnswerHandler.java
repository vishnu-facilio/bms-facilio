package com.facilio.qa.context.client.answers.handler;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.MatrixAnswer;
import com.facilio.qa.context.questions.BaseMatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.qa.context.questions.handler.CommonStringQuestionHandler;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

		List<MatrixQuestionColumn> columns = ((BaseMatrixQuestionContext) question).getColumns();

		Map<Long, FieldType> columnIdVsFieldType = columns.stream().collect(Collectors.toMap(column->column.getId(), column->column.getField().getDataTypeEnum()));
		List<MatrixAnswerContext.ColumnAnswer> columnAnswers= (answer.getAnswer().getRowAnswer().stream().map(row->row.getColumnAnswer()).collect(Collectors.toList())).stream().flatMap(collist->collist.stream()).collect(Collectors.toList());

		for(MatrixAnswerContext.ColumnAnswer columnAnswer:columnAnswers) {
			if(columnAnswer.getAnswer()!=null) {
				FieldType fieldType = columnIdVsFieldType.get(columnAnswer.getColumn());
				switch (fieldType) {
					case STRING:
						V3Util.throwRestException(columnAnswer.getAnswer().toString().length() > CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH, ErrorCode.VALIDATION_ERROR, MessageFormat.format("String answer length ({0}) is greater than the max length 255 allowed",columnAnswer.getAnswer().toString().length()));
						break;
					case NUMBER:
						V3Util.throwRestException(!StringUtils.isNumeric(columnAnswer.getAnswer().toString()), ErrorCode.VALIDATION_ERROR, "Not a valid number format");
						break;
					default:
						break;
				}
			}
		}

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
	public void populateRelatedRecordsForAnswer(AnswerContext answer,QuestionContext question) throws Exception{

		FacilioChain chain = QAndAReadOnlyChainFactory.fetchMatrixRelatedRecordChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.QAndA.ANSWER,answer);
		context.put(FacilioConstants.QAndA.QUESTION,question);
		chain.execute();

	}
}
