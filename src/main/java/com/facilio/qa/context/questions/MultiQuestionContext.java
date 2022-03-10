package com.facilio.qa.context.questions;

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

public class MultiQuestionContext extends BaseMatrixQuestionContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MatrixQuestionRow row;
}
