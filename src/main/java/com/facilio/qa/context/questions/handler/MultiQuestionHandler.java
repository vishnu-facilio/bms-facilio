package com.facilio.qa.context.questions.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.client.answers.handler.MultiQuestionAnswerHandler;
import com.facilio.qa.context.questions.*;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class MultiQuestionHandler extends BaseMatrixQuestionHandler implements QuestionHandler<MultiQuestionContext> {

	@Override
	public void validateSave(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub

		commonValidate(questions);

		for(MultiQuestionContext question : questions) {
			
			createAnswerModuleAndSetField(question);
			
			if(question.getRow() == null) {
				MatrixQuestionRow row = new MatrixQuestionRow();
				row.setName("dummy row");
				question.setRow(row);
			}
		}
	}
	
	@Override
	public void afterSave(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		for(MultiQuestionContext question : questions) {
			
			Map<String, Object> rowRecord = new HashMap<String,Object>();
			
			List<Map<String, Object>> columnRecordList = new ArrayList<Map<String,Object>>();
			
			question.getRow().setParentId(question.getId());
			rowRecord = FieldUtil.getAsProperties(question.getRow());
			
			columnRecordList.addAll(getColumnRecordList(question));
			
			FacilioContext rowContext = V3Util.createRecord(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW), rowRecord);
			
			ModuleBaseWithCustomFields rowAfterAdd = ((Map<String, List<ModuleBaseWithCustomFields>>) rowContext.get(Constants.RECORD_MAP)).get(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW).get(0);
			
			question.getRow().setId(rowAfterAdd.getId());
			
			FacilioContext columnContext = V3Util.createRecordList(modBean.getModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN), columnRecordList, null, null);
			
			List<ModuleBaseWithCustomFields> columnRecordListAfterAdd = ((Map<String, List<ModuleBaseWithCustomFields>>) columnContext.get(Constants.RECORD_MAP)).get(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN);
				
			for(int i=0;i<columnRecordListAfterAdd.size();i++) {
				
				ModuleBaseWithCustomFields columnRecord = columnRecordListAfterAdd.get(i);
				
				question.getColumns().get(i).setId(columnRecord.getId());
			}
		}
	}

	@Override
	public void validateUpdate(List<MultiQuestionContext> questions) throws Exception {
		// TODO Auto-generated method stub
		commonValidate(questions);
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
