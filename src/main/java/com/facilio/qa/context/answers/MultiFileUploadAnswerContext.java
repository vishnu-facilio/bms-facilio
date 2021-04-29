package com.facilio.qa.context.answers;

import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.MultiFileAnswerContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiFileUploadAnswerContext extends ClientAnswerContext<List<MultiFileUploadAnswerContext.MultiFileAnswer>> {

    private List<MultiFileAnswer> answer;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MultiFileAnswer extends FileUploadAnswerContext.FileAnswer {

        public MultiFileAnswer (MultiFileAnswerContext answerContext, boolean isRemarksEnabled) {
            this.setId(answerContext.getFileAnswerId());
            this.setFileName(answerContext.getFileAnswerFileName());
            this.setUrl(answerContext.getFileAnswerUrl());
            this.setContentType(answerContext.getFileAnswerContentType());
            if (isRemarksEnabled) {
                this.setRemarks(answerContext.getRemarks());
            }
        }

        private String remarks;
    }
}
