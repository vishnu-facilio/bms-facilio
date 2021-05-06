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

    public abstract A serialize (AnswerContext answer) throws Exception;
    public abstract AnswerContext deSerialize(A answer, QuestionContext question) throws Exception; // Validation can be done in this method itself
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return false;
    }
    public boolean checkIfAnswerIsNull (A answer) throws Exception {
        return false;
    }

}
