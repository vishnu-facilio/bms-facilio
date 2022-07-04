package com.facilio.qa.command;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionType;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.ColumnAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.MatrixAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.RowAnswer;
import com.facilio.qa.context.questions.BaseMatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.v3.context.Constants;

public class FetchRelatedRecordsFormMatrixQuestionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(answers)) {
        	Map<Long, QuestionContext> questionMap = fetchQuestions(answers);
        	for(AnswerContext answer : answers) {
        		QuestionContext question = answer.getQuestion();
        		
        		question = questionMap.get(question.getId());

				question.getQuestionType().getAnswerHandler().populateRelatedRecordsForAnswer(answer);
    			
    			if(question.getQuestionType() == QuestionType.MATRIX || question.getQuestionType() == QuestionType.MULTI_QUESTION) {
    				
    				BaseMatrixQuestionContext matrixQuestion = (BaseMatrixQuestionContext) question;
    				
    				ModuleBean modBean = Constants.getModBean();
    				
    				FacilioModule answerModule = modBean.getModule(matrixQuestion.getAnswerModuleId());
    				List<FacilioField> answerModuleFields = modBean.getAllFields(answerModule.getName());
    				
    				Map<String, FacilioField> answerModuleFieldsMap = FieldFactory.getAsMap(answerModuleFields);
    				
    				SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
    						.module(answerModule)
    						.select(answerModuleFields)
    						.beanClass(ModuleBaseWithCustomFields.class)
    						.andCondition(CriteriaAPI.getCondition(answerModuleFieldsMap.get("question"), matrixQuestion.getId()+"", NumberOperators.EQUALS))
    						.andCondition(CriteriaAPI.getCondition(answerModuleFieldsMap.get("parent"), answer.getId()+"", NumberOperators.EQUALS));
    						;
    						
    				MatrixAnswer matrixAnswer  = new MatrixAnswer();
    				
    				 List<Map<String, Object>> rows = select.getAsProps();
    				 
    				 if(CollectionUtils.isNotEmpty(rows)) {
    					 
    					 Map<Long, Map<String, Object>> rowRecordMap = recordByRowMap(rows);
    					 
    					 List<MatrixQuestionRow> rowContexts = null;
    					 if(question.getQuestionType() == QuestionType.MATRIX) {
    						 rowContexts = ((MatrixQuestionContext) matrixQuestion).getRows();
    					 }
    					 else if (question.getQuestionType() == QuestionType.MULTI_QUESTION) {
    						 rowContexts = Collections.singletonList(((MultiQuestionContext) matrixQuestion).getRow());
    					 }
    					 
    					 if(rowContexts != null) {
    						 for(MatrixQuestionRow rowContext: rowContexts) {
    		    					
		    					RowAnswer rowAns = new RowAnswer();
		    					Map<String, Object> rowRecord = rowRecordMap.get(rowContext.getId());
		    					
		    					rowAns.setRow(rowContext.getId());
		    					
		    					if(MapUtils.isNotEmpty(rowRecord)) {
		    						for(MatrixQuestionColumn column : matrixQuestion.getColumns()) {
			    						
			    						FacilioField field = matrixQuestion.getColumnVsFieldMap().get(column.getId());
			    						
			    						Object answerObj = rowRecord.get(field.getName());
			    						
			    						ColumnAnswer collumnAns = new ColumnAnswer(column.getId(), answerObj);
			    						
			    						rowAns.addColumnAnswer(collumnAns);
			    					}
		    					}
		    					matrixAnswer.addRowAnswer(rowAns);
		    				}
    					}
	    				answer.setMatrixAnswer(matrixAnswer);
    				 }
    			}
        	}
        }
		return false;
	}

	private Map<Long, Map<String, Object>> recordByRowMap(List<Map<String, Object>> rows) {
		// TODO Auto-generated method stub
		
		Map<Long, Map<String, Object>> rowMap = new HashMap<Long, Map<String,Object>>();
		
		for(Map<String, Object> row : rows) {
			
			Long rowId  = (Long)((Map<String, Object>)row.get("row")).get("id");
			
			rowMap.put(rowId, row);
		}
		
		return rowMap;
	}


	private Map<Long, QuestionContext> fetchQuestions (Collection<AnswerContext> answers) throws Exception {
        List<Long> questionIds = answers.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
        Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);
        return questions;
    }
}
