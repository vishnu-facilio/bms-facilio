package com.facilio.qa.context.questions.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class MultiQuestionHandler extends BaseMatrixQuestionHandler implements QuestionHandler<MultiQuestionContext> {

	@Override
	public void validateSave(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		for(MultiQuestionContext question : questions) {
			
			createAnswerModuleAndSetField(question);
			
			if(question.getRow() == null) {
				MatrixQuestionRow row = new MatrixQuestionRow();
				row.setName("dummy row");
				question.setRow(row);
			}
	         if(question.getRow() != null) {
	        	 V3Util.throwRestException(question.getRow().getName() == null, ErrorCode.VALIDATION_ERROR, "Row name cannot be empty");
	         }
		}
	}
	
	@Override
	public void afterSave(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		List<Map<String, Object>> rowRecordList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> columnRecordList = new ArrayList<Map<String,Object>>();
		
		for(MultiQuestionContext question : questions) {
			question.getRow().setParentId(question.getId());
			rowRecordList.add(FieldUtil.getAsProperties(question.getRow()));
			
			columnRecordList.addAll(getColumnRecordList(question));
		}
		V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW), rowRecordList, null, null);
		
		V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN), columnRecordList, null, null);
	}

	@Override
	public void validateUpdate(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		updateColumns(questions);
	}
	
	@Override
	public void afterFetch(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		Map<Long, List<MatrixQuestionRow>> rowMap = getAlreadyExistingRow(questions);
		Map<Long, List<MatrixQuestionColumn>> columnMap = getAlreadyExistingColumn(questions);
		
		for(MultiQuestionContext question : questions) {
			
			question.setRow(rowMap.get(question.getId()).get(0));
			question.setColumns(columnMap.get(question.getId()));
			
			if(question.getColumns() != null) {
				for(MatrixQuestionColumn column : question.getColumns()) {
					if(column.getField() == null && column.getFieldId() > 0) {
						column.setField(modBean.getField(column.getFieldId()));
					}
				}
			}
		}
		
	}
	
	@Override
	public void addAnswerDisplayLogicActions(QuestionContext question, DisplayLogicContext displayLogic, JSONObject actionJson) {
		
		MultiQuestionContext multiQuestionContext = (MultiQuestionContext) question;
		
		if (displayLogic.getColumnId() != null) {
			
			Map<Long, MatrixQuestionColumn> columnMap = multiQuestionContext.getColumns().stream().collect(Collectors.toMap(MatrixQuestionColumn::getId, Function.identity()));
			
			MatrixQuestionColumn column = columnMap.get(displayLogic.getColumnId());
			
			JSONArray displayLogicMeta = column.getDisplayLogicMeta() == null ? new JSONArray() :  column.getDisplayLogicMeta();
			
			displayLogicMeta.add(actionJson);
			
			column.setDisplayLogicMeta(displayLogicMeta);
		}
    }

}
