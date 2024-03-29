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
public class DateTimeQuestionContext extends QuestionContext {
    private Boolean showTime;
    private String placeHolder;

    public boolean showTime() {
        return showTime == null ? false : showTime;
    }
}
