package com.facilio.qa.context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.answers.MultiFileAnswerContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AnswerContext extends V3Context {
    private QAndATemplateContext parent;
    private ResponseContext response;
    private QuestionContext question;
    private String comments;
    private List<AttachmentContext> attachmentList;
    private List<Long> attachmentFileIds;
    private List<Long> deletedAttachmentFileIds;

    private Long enumAnswer;
    private String enumOtherAnswer;
    private Long numberAnswer;
    private Double decimalAnswer;
    private Boolean booleanAnswer;
    private String shortAnswer, longAnswer;
    private Long dateTimeAnswer;
    private Integer ratingAnswer;

    private Long fileAnswerId;
    private String fileAnswerUrl, fileAnswerFileName, fileAnswerContentType,fileAnswerDownloadUrl;
    private File fileAnswer;

    // Multi record answers and these field names should be added as fetchsupplement in AddAnswerSupplementsCommand
    private List<MCQOptionContext> multiEnumAnswer; // Multi lookup field
    private List<MultiFileAnswerContext> multiFileAnswer; // Line Item field
    
    private MatrixAnswerContext.MatrixAnswer matrixAnswer;

    // Score fields
    private Double fullScore, score;
    private Float scorePercent;

    public double scoreWithZeroOnNull() {
        return score == null ? 0 : score;
    }

    @JsonIgnore @JSON(serialize = false)
    public Long getResponseId() {
        return response == null ? null : response.getId();
    }

    @JsonIgnore
     private ClientAnswerContext answerContext;

    @JsonIgnore
    public void setAnswerContext(QuestionContext question) throws Exception {
        answerContext = QAndAUtil.serializedAnswer(question,this);
    }
    
    public Long getQuestionId() {
    	return question == null? null : question.getId(); 
    }
}
