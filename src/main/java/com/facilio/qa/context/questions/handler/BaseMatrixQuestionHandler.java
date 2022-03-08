package com.facilio.qa.context.questions.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.qa.context.questions.BaseMatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class BaseMatrixQuestionHandler {
	
	public List<Map<String, Object>> getColumnRecordList (BaseMatrixQuestionContext question) throws Exception {

		if(question.getColumns() !=null) {
			for(MatrixQuestionColumn column : question.getColumns()) {
				column.setParentId(question.getId());
			}
			return FieldUtil.getAsMapList(question.getColumns(), MatrixQuestionColumn.class);
		}
		return null;
	}

	public void createAnswerModuleAndSetField(BaseMatrixQuestionContext question) throws Exception {
		
		V3Util.throwRestException(question.getQuestion() == null || question.getQuestion().isBlank(), ErrorCode.VALIDATION_ERROR, "Question cannot be empty");
		
		String questionName = question.getQuestion().length() <= 50 ? question.getQuestion() : question.getQuestion().substring(0, 49);  
		
   	 	FacilioModule answerModule = new FacilioModule(FacilioConstants.QAndA.Answers.MATRIX_ANSWER +"_"+ questionName.replaceAll(" ", ""),
                "Q And A Matrix Answer "+ questionName,
                "Q_And_A_Matrix_Answers",
                FacilioModule.ModuleType.SUB_ENTITY);

        List<FacilioField> answerFields = getAnswerDefaultField();

        if(question.getColumns() != null) {
       	 for(MatrixQuestionColumn column : question.getColumns()) {
       		 
       		 V3Util.throwRestException(column.getField() == null, ErrorCode.VALIDATION_ERROR, "Field cannot be empty in matrix column");
       		 V3Util.throwRestException(column.getName() == null, ErrorCode.VALIDATION_ERROR, "Column name cannot be empty");
       		 answerFields.add(column.getField());
       	 }
        }
        
        answerModule.setFields(answerFields);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(answerModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, true);
        addModuleChain.getContext().put(FacilioConstants.Module.SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK, false);
        addModuleChain.execute();
        
        question.setAnswerModuleId(answerModule.getModuleId());
        
        if(question.getColumns() != null) {
       	 for(MatrixQuestionColumn column : question.getColumns()) {
       		 column.setFieldId(column.getField().getFieldId());
       	 }
        }
	}
	
	protected void updateColumns(List<? extends BaseMatrixQuestionContext> questions) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();

		List<Long> columnIdsToBeDeleted = new ArrayList<Long>();
		
		List<MatrixQuestionColumn> columnsTobeAdded = new ArrayList<MatrixQuestionColumn>();
		
		Map<Long, List<MatrixQuestionColumn>> alreadyExistingColumns = getAlreadyExistingColumn(questions);
		
		for(BaseMatrixQuestionContext question : questions) {
			
			List<Long> currentlyAvailabeColumnIDs = new ArrayList<Long>();
			
			if(question.getColumns() != null) {
				
				for(MatrixQuestionColumn column : question.getColumns()) {
					if(column.getId() <= 0) {
						column.setParentId(question.getId());
						
						FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
						
						FacilioContext context = addFieldsChain.getContext();
						
						column.getField().setModule(modBean.getModule(question.getAnswerModuleId()));
						
						context.put(FacilioConstants.ContextNames.MODULE_NAME, modBean.getModule(question.getAnswerModuleId()).getName());
						context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(column.getField()));
						
						addFieldsChain.execute();
						
						column.setFieldId(column.getField().getId());
						
						columnsTobeAdded.add(column);
					}
					else {
						
						FacilioChain updateFieldChain = FacilioChainFactory.getUpdateFieldChain();
						
						FacilioContext context = updateFieldChain.getContext();
						context.put(FacilioConstants.ContextNames.MODULE_FIELD, column.getField());
						context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, false);

						updateFieldChain.execute();
						
						UpdateRecordBuilder<MatrixQuestionColumn> update = new UpdateRecordBuilder<MatrixQuestionColumn>()
								.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)
								.fields(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN))
								.andCondition(CriteriaAPI.getIdCondition(column.getId(), modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)))
								;
						
						update.update(column);
						
						currentlyAvailabeColumnIDs.add(column.getId());
					}
				}
			}
			
			if(alreadyExistingColumns.containsKey(question.getId())) {
				
				List<Long> alreadyExistingRowIDs = alreadyExistingColumns.get(question.getId()).stream().mapToLong(MatrixQuestionColumn::getId).boxed().collect(Collectors.toList());
				alreadyExistingRowIDs.removeAll(currentlyAvailabeColumnIDs);
				
				columnIdsToBeDeleted.addAll(alreadyExistingRowIDs);
			}
		}
		
		if(columnsTobeAdded.size() > 0 ) {
			V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN), FieldUtil.getAsMapList(columnsTobeAdded, MatrixQuestionColumn.class), null, null);
		}
		if(columnIdsToBeDeleted.size() > 0 ) {
			
			DeleteRecordBuilder<MatrixQuestionColumn> delete = new DeleteRecordBuilder<MatrixQuestionColumn>()
					.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)
					.andCondition(CriteriaAPI.getIdCondition(columnIdsToBeDeleted, modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)))
					;
			
			delete.markAsDelete();
		}
	}

	protected Map<Long, List<MatrixQuestionColumn>> getAlreadyExistingColumn(List<? extends BaseMatrixQuestionContext> questions) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN));
		
		SelectRecordsBuilder<MatrixQuestionColumn> select = new SelectRecordsBuilder<MatrixQuestionColumn>()
				.select(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN))
				.beanClass(MatrixQuestionColumn.class)
				.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), questions.stream().mapToLong(BaseMatrixQuestionContext::getId).boxed().collect(Collectors.toList()), NumberOperators.EQUALS));
		
		List<MatrixQuestionColumn> matrixColumns = select.get();
		
		Map<Long, List<MatrixQuestionColumn>> groupedcolumns = matrixColumns.stream().collect(Collectors.groupingBy(MatrixQuestionColumn::getParentId));
		
		return groupedcolumns;
	}
	
	private List<FacilioField> getAnswerDefaultField() throws Exception {
		
		ModuleBean modBean = Constants.getModBean();
		
		List<FacilioField> answerFields = new ArrayList<>();
		
		LookupField questionField = (LookupField) FieldFactory.getDefaultField("question", "Question", "QUESTION_ID", FieldType.LOOKUP);
		questionField.setLookupModule(modBean.getModule(FacilioConstants.QAndA.QUESTION));
        answerFields.add(questionField);
		
		LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent Answer", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(modBean.getModule(FacilioConstants.QAndA.ANSWER));
        answerFields.add(parentField);
        
        LookupField rowField = (LookupField) FieldFactory.getDefaultField("row", "Parent Row", "ROW_ID", FieldType.LOOKUP);
        rowField.setLookupModule(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW));
        answerFields.add(rowField);
        
        return answerFields;
	}
	
	public Map<Long, List<MatrixQuestionRow>> getAlreadyExistingRow(List<? extends BaseMatrixQuestionContext> questions) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW));
		
		SelectRecordsBuilder<MatrixQuestionRow> select = new SelectRecordsBuilder<MatrixQuestionRow>()
				.select(modBean.getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW))
				.beanClass(MatrixQuestionRow.class)
				.moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), questions.stream().mapToLong(BaseMatrixQuestionContext::getId).boxed().collect(Collectors.toList()), NumberOperators.EQUALS));
		
		List<MatrixQuestionRow> matrixRows = select.get();
		
		Map<Long, List<MatrixQuestionRow>> groupedRows = matrixRows.stream().collect(Collectors.groupingBy(MatrixQuestionRow::getParentId));
		
		return groupedRows;
	}
}
