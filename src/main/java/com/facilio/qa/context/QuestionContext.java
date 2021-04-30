package com.facilio.qa.context;

import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
@Log4j
@ToString
@NoArgsConstructor
@JsonTypeInfo(
        defaultImpl = QuestionContext.class,
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "questionType",
        visible = true
)
@JsonTypeIdResolver(QuestionContext.QuestionTypeIdResolver.class)
public class QuestionContext extends V3Context {

    /**
     *
     * Copy the following to all classes extending this class
     * @JsonTypeInfo(
     *         use = JsonTypeInfo.Id.NONE
     * )
     *
     */

    public QuestionContext (Long id) {
        super(id);
    }

    private QAndATemplateContext parent;
    private PageContext page;
    private String question;
    private String description;
    private Boolean mandatory;
    private String commentsLabel, attachmentLabel;
    private Integer answerFieldId;
    private QuestionType questionType;
    private Integer position;
    private ClientAnswerContext answer;

    public void setAnswer (List<ClientAnswerContext> answers) {
        answer = CollectionUtils.isEmpty(answers) ? null : answers.get(0);
    }

    public static class QuestionTypeIdResolver extends FacilioEnumClassTypeIdResolverBase<QuestionContext> {
        @Override
        protected Class<? extends QuestionContext> getSubClass(String value) {
            QuestionType typeEnum = QuestionType.valueOf(value);
            FacilioUtil.throwIllegalArgumentException(typeEnum == null, "Invalid type for question");
            return typeEnum.getSubClass();
        }
    }
}
