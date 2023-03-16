package com.facilio.qa.context.questions.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agent.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class MatrixQuestionHandler extends BaseMatrixQuestionHandler implements QuestionHandler<MatrixQuestionContext> {

	@Override
	public void validateSave(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		commonValidate(questions);

		for(MatrixQuestionContext question : questions) {
			createAnswerModuleAndSetField(question);
	         
	         if(question.getRows() != null) {
	        	 for(MatrixQuestionRow row : question.getRows()) {
	        		 V3Util.throwRestException(row.getName() == null || row.getName().isEmpty(), ErrorCode.VALIDATION_ERROR, "errors.qa.matrixQuestionHandler.rowNameCheck",true,null);
					 //V3Util.throwRestException(row.getName() == null || row.getName().isEmpty(), ErrorCode.VALIDATION_ERROR, "Row name cannot be empty",true,null);
	        	 }
	         }
		}
	}
	
	@Override
	public void afterSave(List<MatrixQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		
		for(MatrixQuestionContext question : questions) {
			
			List<Map<String, Object>> rowRecordList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> columnRecordList = new ArrayList<Map<String,Object>>();
			
			if(question.getRows() !=null) {
				for(MatrixQuestionRow row : question.getRows()) {
					row.setParentId(question.getId());
				}
				rowRecordList.addAll(FieldUtil.getAsMapList(question.getRows(), MatrixQuestionRow.class));
			}
			
			columnRecordList.addAll(getColumnRecordList(question));
			
			
			FacilioContext rowContext = V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW), rowRecordList, null, null);
			
			FacilioContext columnContext = V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN), columnRecordList, null, null);
			
			List<ModuleBaseWithCustomFields> rowRecordListAfterAdd = ((Map<String, List<ModuleBaseWithCustomFields>>) rowContext.get(Constants.RECORD_MAP)).get(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW);
			
			List<ModuleBaseWithCustomFields> columnRecordListAfterAdd = ((Map<String, List<ModuleBaseWithCustomFields>>) columnContext.get(Constants.RECORD_MAP)).get(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN);
			
			for(int i=0;i<rowRecordListAfterAdd.size();i++) {
				
				ModuleBaseWithCustomFields rowRecord = rowRecordListAfterAdd.get(i);
				
				question.getRows().get(i).setId(rowRecord.getId());
			}
			
			for(int i=0;i<columnRecordListAfterAdd.size();i++) {
				
				ModuleBaseWithCustomFields columnRecord = columnRecordListAfterAdd.get(i);
				
				question.getColumns().get(i).setId(columnRecord.getId());
			}
		}
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
	@Override
	public void addAnswerDisplayLogicActions(QuestionContext question, DisplayLogicContext displayLogic, JSONObject actionJson) {
		
		MatrixQuestionContext matrixQuestion = (MatrixQuestionContext) question;
		
		if(displayLogic.getRowId() != null) {
			
			Map<Long, MatrixQuestionRow> rowMap = matrixQuestion.getRows().stream().collect(Collectors.toMap(MatrixQuestionRow::getId, Function.identity()));
			
			MatrixQuestionRow row = rowMap.get(displayLogic.getRowId());
			
			JSONArray displayLogicMeta = row.getDisplayLogicMeta() == null ? new JSONArray() :  row.getDisplayLogicMeta();
			
			displayLogicMeta.add(actionJson);
			
			row.setDisplayLogicMeta(displayLogicMeta);
		}
		else if (displayLogic.getColumnId() != null) {
			
			Map<Long, MatrixQuestionColumn> columnMap = matrixQuestion.getColumns().stream().collect(Collectors.toMap(MatrixQuestionColumn::getId, Function.identity()));
			
			MatrixQuestionColumn column = columnMap.get(displayLogic.getColumnId());
			
			JSONArray displayLogicMeta = column.getDisplayLogicMeta() == null ? new JSONArray() :  column.getDisplayLogicMeta();
			
			displayLogicMeta.add(actionJson);
			
			column.setDisplayLogicMeta(displayLogicMeta);
		}
    }
	
}
