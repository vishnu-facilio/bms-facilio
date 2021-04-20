package com.facilio.qa.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class MultiFileAnswerContext extends V3Context {
    private Long fileAnswerId;
    private String fileAnswerUrl, fileAnswerFileName, fileAnswerContentType;
    private File fileAnswer;
    private String remarks;
    private AnswerContext parent;
}
