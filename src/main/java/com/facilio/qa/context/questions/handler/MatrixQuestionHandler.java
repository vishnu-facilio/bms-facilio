package com.facilio.qa.context.questions.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class MatrixQuestionHandler extends BaseMatrixQuestionHandler implements QuestionHandler<MatrixQuestionContext> {

	@Override
	public void validateSave(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		for(MatrixQuestionContext question : questions) {
			
			createAnswerModuleAndSetField(question);
	         
	         if(question.getRows() != null) {
	        	 for(MatrixQuestionRow row : question.getRows()) {
	        		 V3Util.throwRestException(row.getName() == null, ErrorCode.VALIDATION_ERROR, "Row name cannot be empty");
	        	 }
	         }
		}
	}
	
	@Override
	public void afterSave(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		List<Map<String, Object>> rowRecordList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> columnRecordList = new ArrayList<Map<String,Object>>();
		
		for(MatrixQuestionContext question : questions) {
			if(question.getRows() !=null) {
				for(MatrixQuestionRow row : question.getRows()) {
					row.setParentId(question.getId());
				}
				rowRecordList.addAll(FieldUtil.getAsMapList(question.getRows(), MatrixQuestionRow.class));
			}
			
			columnRecordList.addAll(getColumnRecordList(question));
		}
		V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW), rowRecordList, null, null);
		
		V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN), columnRecordList, null, null);
	}

	@Override
	public void validateUpdate(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		updateRows(questions);
		updateColumns(questions);
	}
	
	private void updateRows(List<MatrixQuestionContext> questions) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();

		List<Long> rowIdsToBeDeleted = new ArrayList<Long>();
		
		List<MatrixQuestionRow> rowsTobeAdded = new ArrayList<MatrixQuestionRow>();
		
		Map<Long, List<MatrixQuestionRow>> alreadyExistingRows = getAlreadyExistingRow(questions);
		
		for(MatrixQuestionContext question : questions) {
			
			List<Long> currentlyAvailaberowIDs = new ArrayList<Long>();
			
			if(question.getRows() != null) {
				
				for(MatrixQuestionRow row : question.getRows()) {
					if(row.getId() <= 0) {
						row.setParentId(question.getId());
						rowsTobeAdded.add(row);
					}
					else {
						
						UpdateRecordBuilder<MatrixQuestionRow> update = new UpdateRecordBuilder<MatrixQuestionRow>()
								.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW)
								.fields(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW))
								.andCondition(CriteriaAPI.getIdCondition(row.getId(), modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW)))
								;
						update.update(row);
						
						currentlyAvailaberowIDs.add(row.getId());
					}
				}
			}
			if(alreadyExistingRows.containsKey(question.getId())) {
				List<Long> alreadyExistingRowIDs = alreadyExistingRows.get(question.getId()).stream().mapToLong(MatrixQuestionRow::getId).boxed().collect(Collectors.toList());
				alreadyExistingRowIDs.removeAll(currentlyAvailaberowIDs);
				
				rowIdsToBeDeleted.addAll(alreadyExistingRowIDs);
			}
		}
		
		if(rowsTobeAdded.size() > 0 ) {
			V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW), FieldUtil.getAsMapList(rowsTobeAdded, MatrixQuestionRow.class), null, null);
		}
		if(rowIdsToBeDeleted.size() > 0 ) {
			
			DeleteRecordBuilder<MatrixQuestionRow> delete = new DeleteRecordBuilder<MatrixQuestionRow>()
					.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW)
					.andCondition(CriteriaAPI.getIdCondition(rowIdsToBeDeleted, modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW)))
					;
			
			delete.markAsDelete();
		}
	}
	
	@Override
	public void afterFetch(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		Map<Long, List<MatrixQuestionRow>> rowMap = getAlreadyExistingRow(questions);
		Map<Long, List<MatrixQuestionColumn>> columnMap = getAlreadyExistingColumn(questions);
		
		for(MatrixQuestionContext question : questions) {
			
			question.setRows(rowMap.get(question.getId()));
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
	
}
