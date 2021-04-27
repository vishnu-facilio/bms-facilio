package com.facilio.qa.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientAnswerContext<A> {
    private Long id;
    private Long question;

    public abstract A getAnswer();
}
