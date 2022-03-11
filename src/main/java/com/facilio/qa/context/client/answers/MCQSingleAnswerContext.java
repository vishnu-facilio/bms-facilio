package com.facilio.qa.context.client.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MCQSingleAnswerContext extends ClientAnswerContext<MCQSingleAnswerContext.MCQAnswer> {
    private MCQAnswer answer;
    
    @Override
    public Object getActualAnswerObject() {
    	return answer.getSelected();
    }

    @Getter
    @Setter
    public static class MCQAnswer {
        private Long selected;
        private String other;
    }
}
