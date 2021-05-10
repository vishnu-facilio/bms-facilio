package com.facilio.qa.context.client.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MCQMultiAnswerContext extends ClientAnswerContext<MCQMultiAnswerContext.MCQMultiAnswer>  {

    private MCQMultiAnswer answer;

    @Getter
    @Setter
    public static class MCQMultiAnswer {
        private Set<Long> selected;
        private String other;
    }
}
