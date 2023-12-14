package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.modules.*;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionType;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.ColumnAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.MatrixAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.RowAnswer;
import com.facilio.qa.context.questions.BaseMatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.v3.context.Constants;

public class AddOrUpdateMatrixAnswersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
		
		for(AnswerContext answer : answers) {
			
			QuestionContext question = answer.getQuestion();
			
			if(question.getQuestionType() == QuestionType.MATRIX || question.getQuestionType() == QuestionType.MULTI_QUESTION) {
				BaseMatrixQuestionContext matrixQuestion = (BaseMatrixQuestionContext) question;
				
				MatrixAnswer matrixAnswer = answer.getMatrixAnswer();
				
				List<RowAnswer> rowAnswers = matrixAnswer.getRowAnswer();
				
				for(RowAnswer rowAnswer :rowAnswers) {
					
					addOrUpdateRowAnswer(rowAnswer, answer, matrixQuestion);
				}
			}
		}
		
		return false;
	}

	
	void addOrUpdateRowAnswer(RowAnswer rowAnswer,AnswerContext answer,BaseMatrixQuestionContext matrixQuestion) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();
		
		FacilioModule answerModule = modBean.getModule(matrixQuestion.getAnswerModuleId());
		List<FacilioField> answerModuleFields = modBean.getAllFields(answerModule.getName());
		
		Map<String, FacilioField> answerModuleFieldsMap = FieldFactory.getAsMap(answerModuleFields);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(answerModule)
				.select(answerModuleFields)
				.beanClass(ModuleBaseWithCustomFields.class)
				.andCondition(CriteriaAPI.getCondition(answerModuleFieldsMap.get("row"), rowAnswer.getRow()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(answerModuleFieldsMap.get("question"), matrixQuestion.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(answerModuleFieldsMap.get("parent"), answer.getId()+"", NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = select.getAsProps();
		List<SupplementRecord> supplementFields = answerModuleFields.stream().filter((field)->field.getDataTypeEnum()==FieldType.MULTI_ENUM).map(SupplementRecord.class::cast).collect(Collectors.toList());
		
		if(props == null || props.isEmpty()) {	//add here
			
			ModuleBaseWithCustomFields newRecord = constructMatrixRowRecord(rowAnswer, answer, matrixQuestion,true);
			
			InsertRecordBuilder<ModuleBaseWithCustomFields> insert = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
					.module(answerModule)
					.fields(answerModuleFields)
					.addRecord(newRecord);

			if(!supplementFields.isEmpty()) {
				insert.insertSupplements(supplementFields);
			}

			insert.save();
		}	
		else {									//update here
			
			ModuleBaseWithCustomFields newRecord = constructMatrixRowRecord(rowAnswer, answer, matrixQuestion,false);
			
			Long oldRecordId = (Long)props.get(0).get("id");
			UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(answerModule)
					.fields(answerModuleFields)
					.andCondition(CriteriaAPI.getIdCondition(oldRecordId, answerModule))
					;

			if(!supplementFields.isEmpty()) {
				update.updateSupplements(supplementFields);
			}

			update.update(newRecord);
		}
	}
	
	public ModuleBaseWithCustomFields constructMatrixRowRecord(RowAnswer rowAnswer,AnswerContext answer,BaseMatrixQuestionContext matrixQuestion,boolean isAdd) {
		
		Map<Long, FacilioField> columnVsFieldMap = matrixQuestion.getColumnVsFieldMap();
		
		ModuleBaseWithCustomFields record = new ModuleBaseWithCustomFields();
		if(isAdd) {
			record.setDatum("question", matrixQuestion.getId());
			record.setDatum("parent", answer.getId());
			record.setDatum("row", rowAnswer.getRow());
		}
		
		for(ColumnAnswer columnAnswer : rowAnswer.getColumnAnswer()) {
			
			FacilioField field = columnVsFieldMap.get(columnAnswer.getColumn());
			
			record.setDatum(field.getName(), columnAnswer.getAnswer());
		}
		
		return record;
	}

}
