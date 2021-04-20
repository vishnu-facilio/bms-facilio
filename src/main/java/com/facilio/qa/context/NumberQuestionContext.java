package com.facilio.qa.context;

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
public class NumberQuestionContext extends QuestionContext {
    private long minValue, maxValue;
    private String placeHolder;
}
