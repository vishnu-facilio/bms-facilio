package com.facilio.qa.context.questions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QuestionContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public abstract class BaseMatrixQuestionContext extends QuestionContext {

	private Long answerModuleId;
	List<MatrixQuestionColumn> columns;

	
	public Map<Long, FacilioField> getColumnVsFieldMap() {
		Map<Long,FacilioField> map = new HashMap<Long,FacilioField>();
		if(columns != null) {
			for(MatrixQuestionColumn column : columns) {
				map.put(column.getId(), column.getField());
			}
		}
		return map;
	}
}
