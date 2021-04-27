package com.facilio.qa.context.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberAnswerContext extends ClientAnswerContext<Long> {
    private Long answer;
}
