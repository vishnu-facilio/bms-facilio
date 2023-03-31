package com.facilio.qa.context.client.answers;

import com.facilio.qa.context.ClientAnswerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class FileUploadAnswerContext extends ClientAnswerContext<FileUploadAnswerContext.FileAnswer> {

    private FileAnswer answer;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileAnswer {
        private Long id;
        private String fileName, url, contentType;
        private String downloadUrl;
    }
}
