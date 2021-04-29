package com.facilio.qa.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class AnswerHandler<A extends ClientAnswerContext> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Class<A> answerClass;

    public abstract A serialize (AnswerContext answer);
    public abstract AnswerContext deSerialize(A answer, QuestionContext question); // Validation can be done in this method itself
}
