package com.facilio.qa.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientAnswerContext<A> {
    private Long id;
    private Long question;
    private String comments;
    private Long responseId;

    private Double fullScore, score;
    private Float scorePercent;

    public void addQuestionId (QuestionContext question) {
        this.question = question == null ? null : question._getId();
    }

    public abstract A getAnswer();
}
