package com.facilio.qa.displaylogic.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayLogicTriggerQuestions {

	Long id;
	Long displayLogicId;
	Long triggerQuestionId;
	
	public DisplayLogicTriggerQuestions() {
		
	}
	
	public DisplayLogicTriggerQuestions (Long triggerQuestionId) {
		this.triggerQuestionId = triggerQuestionId;
	}
}
