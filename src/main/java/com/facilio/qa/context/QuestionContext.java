package com.facilio.qa.context;

import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@ToString
@JsonTypeInfo(
        defaultImpl = QuestionContext.class,
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonTypeIdResolver(QuestionContext.QuestionTypeIdResolver.class)
public class QuestionContext extends V3Context {
    private QAndATemplateContext parent;
    private PageContext page;
    private String question;
    private String description;
    private Boolean mandatory;
    private String commentsLabel, attachmentLabel;
    private Integer answerFieldId;
    private QuestionType questionType;
    private Integer position;

    public Integer getType() {
        return questionType == null ? null : questionType.getIndex();
    }
    public void setType(Integer type) {
        questionType = type == null ? null : QuestionType.valueOf(type);
    }

    public static class QuestionTypeIdResolver extends FacilioEnumClassTypeIdResolverBase<QuestionContext> {
        @Override
        protected Class<? extends QuestionContext> getSubClass(int index) {
            QuestionType typeEnum = QuestionType.valueOf(index);
            FacilioUtil.throwIllegalArgumentException(typeEnum == null, "Invalid type for question");
            return typeEnum.getSubClass();
        }
    }
}
