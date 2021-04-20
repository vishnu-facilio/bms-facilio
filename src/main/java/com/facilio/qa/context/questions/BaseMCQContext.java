package com.facilio.qa.context.questions;

import com.facilio.qa.context.QuestionContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Getter
@Setter
@Log4j
public abstract class BaseMCQContext extends QuestionContext {
    private String displayFormat;
    private List<MCQOptionContext> options;
    private String otherOptionLabel;
    private Boolean showOtherOption;
}
