package com.facilio.qa.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ResponseContext extends V3Context {
    private Integer totalQuestions;
    private Integer totalAnswered;

    public abstract QAndATemplateContext getParent();
}
