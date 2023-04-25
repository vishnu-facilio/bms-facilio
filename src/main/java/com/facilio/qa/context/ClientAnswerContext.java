package com.facilio.qa.context;

import java.util.List;

import com.facilio.bmsconsole.context.AttachmentContext;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientAnswerContext<A> extends V3Context {
//    private Long id;
    private Long question;
    private String comments;
    private QAndATemplateContext parent;
    private ResponseContext response;
    private Long responseId;
    private List<AttachmentContext> attachmentList;

    private Double fullScore, score;
    private Float scorePercent;

    public void addQuestionId (QuestionContext question) {
        this.question = question == null ? null : question.getId();
    }

    public abstract A getAnswer();
    public Object getActualAnswerObject() {
    	return getAnswer();
    }
}
