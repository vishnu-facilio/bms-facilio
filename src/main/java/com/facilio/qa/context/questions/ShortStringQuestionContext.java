package com.facilio.qa.context.questions;

import com.facilio.qa.context.QuestionContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public class ShortStringQuestionContext extends QuestionContext {
    private int maxLength;
    private String placeHolder;
}
