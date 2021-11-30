package com.facilio.qa.context.questions;

import com.facilio.qa.context.QuestionContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Getter
@Setter
@Log4j
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public class RatingQuestionContext extends QuestionContext {
    private Integer ratingScale;
    private String displayFormat;
    private List<RatingQuestionContext.OptionSummary> summary;

    @Getter
    @AllArgsConstructor
    public static class OptionSummary {
        private Long option;
        private Float percent;
        private Integer count;
    }
}
