package com.facilio.qa.context;

import com.facilio.qa.context.answers.MultiFileAnswerContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class AnswerContext extends V3Context {
    private QAndATemplateContext parent;
    private ResponseContext response;
    private QuestionContext question;
    private Long enumAnswer;
    private String enumOtherAnswer;
    private Long numberAnswer;
    private Double decimalAnswer;
    private Boolean booleanAnswer;
    private String shortAnswer, longAnswer;
    private Long dateTimeAnswer;

    private Long fileAnswerId;
    private String fileAnswerUrl, fileAnswerFileName, fileAnswerContentType;
    private File fileAnswer;

    // Multi record answers and these field names should be added as fetchsupplement in AddAnswerSupplementsCommand
    private List<MCQOptionContext> multiEnumAnswer; // Multi lookup field
    private List<MultiFileAnswerContext> multiFileAnswer; // Line Item field
}
