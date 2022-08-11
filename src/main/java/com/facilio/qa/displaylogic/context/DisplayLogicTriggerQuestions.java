package com.facilio.qa.displaylogic.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayLogicTriggerQuestions {

	Long id;
	Long displayLogicId;
	Long triggerQuestionId;
	Long triggerRowId;
	Long triggerColumnId;
	
	public DisplayLogicTriggerQuestions() {
		
	}
	
	public DisplayLogicTriggerQuestions (Long triggerQuestionId) {
		this.triggerQuestionId = triggerQuestionId;
	}
	public DisplayLogicTriggerQuestions (Long triggerQuestionId,Long triggerRowId,Long triggerColumnId) {
		this.triggerQuestionId = triggerQuestionId;
		this.triggerRowId = triggerRowId;
		this.triggerColumnId = triggerColumnId;
	}
}
