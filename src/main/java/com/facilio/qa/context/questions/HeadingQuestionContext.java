package com.facilio.qa.context.questions;

import com.facilio.qa.context.QuestionContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public class HeadingQuestionContext extends QuestionContext {
    private String richText;
}
