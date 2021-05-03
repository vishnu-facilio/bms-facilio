package com.facilio.qa.context.client.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanAnswerContext extends ClientAnswerContext<Boolean> {
    private Boolean answer;
}
