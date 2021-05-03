package com.facilio.qa.context.client.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringAnswerContext extends ClientAnswerContext<String> {
    private String answer;
}
