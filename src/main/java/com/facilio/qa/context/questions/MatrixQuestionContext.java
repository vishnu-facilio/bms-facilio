package com.facilio.qa.context.questions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QuestionContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public class MatrixQuestionContext extends BaseMatrixQuestionContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<MatrixQuestionRow> rows;
}
