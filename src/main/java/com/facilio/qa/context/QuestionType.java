package com.facilio.qa.context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioEnum;
import com.facilio.qa.context.questions.*;
import com.facilio.qa.context.questions.handler.MCQHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Log4j
public enum QuestionType implements FacilioEnum<QuestionType> {
    HEADING (null, FacilioConstants.QAndA.QUESTION, HeadingQuestionContext.class),
    NUMBER ("numberAnswer", FacilioConstants.QAndA.Questions.NUMBER_QUESTION, NumberQuestionContext.class),
    DECIMAL ("decimalAnswer", FacilioConstants.QAndA.Questions.DECIMAL_QUESTION, DecimalQuestionContext.class),

    SHORT_ANSWER ("shortAnswer",FacilioConstants.QAndA.Questions.SHORT_STRING_QUESTION, ShortStringQuestionContext.class),
    LONG_ANSWER ("longAnswer",FacilioConstants.QAndA.Questions.LONG_STRING_QUESTION, LongStringQuestionContext.class),
    DATE_TIME ("dateTimeAnswer",FacilioConstants.QAndA.Questions.DATE_TIME_QUESTION, DateTimeQuestionContext.class),

    MULTIPLE_CHOICE_ONE ("enumAnswer",FacilioConstants.QAndA.Questions.MCQ_SINGLE, MCQSingleContext.class, new MCQHandler<MCQSingleContext>(FacilioConstants.QAndA.Questions.MCQ_SINGLE_OPTIONS)),
    MULTIPLE_CHOICE_MANY ("multiEnumAnswer",FacilioConstants.QAndA.Questions.MCQ_MULTI, MCQMultiContext.class, new MCQHandler<MCQMultiContext>(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS)),
    FILE_UPLOAD ("fileAnswer",FacilioConstants.QAndA.Questions.FILE_UPLOAD_QUESTION, FileUploadQuestionContext.class),

    MULTI_FILE_UPLOAD ("multiFileAnswer",FacilioConstants.QAndA.Questions.MULTI_FILE_UPLOAD_QUESTION, MultiFileUploadQuestionContext.class),
    BOOLEAN ("booleanAnswer",FacilioConstants.QAndA.Questions.BOOLEAN_QUESTION, BooleanQuestionContext.class),
    ;

    private static final Map<String, QuestionType> MODULE_VS_TYPE = initModuleVsTypeMap();
    private String fieldName, subModuleName;
    private Class<? extends QuestionContext> subClass;

    private QuestionHandler handler;

    private <Q extends QuestionContext> QuestionType (String fieldName, String subModuleName, Class<Q> subClass) {
        this (fieldName, subModuleName, subClass, null);
    }

    private <Q extends QuestionContext> QuestionType (String fieldName,@NonNull String subModuleName,@NonNull Class<Q> subClass, QuestionHandler<Q> handler) {
//        Objects.requireNonNull(subClass, "Sub class cannot be null for a type");
        this.fieldName = fieldName;
        this.subClass = subClass;
        this.subModuleName = subModuleName;
        this.handler = handler;
    }

    public static QuestionType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    public static QuestionType getTypeFromSubModule(String subModuleName) {
        return MODULE_VS_TYPE.get(subModuleName);
    }

    private static Map<String, QuestionType> initModuleVsTypeMap() {
        long time = System.currentTimeMillis();
        Map<String, QuestionType> moduleVsType = new HashMap<>(values().length);
        for (QuestionType type : values()) {
            moduleVsType.put(type.getSubModuleName(), type);
        }
        LOGGER.info(MessageFormat.format("Init module vs type called. Time taken : {0}", (System.currentTimeMillis() - time)));
        return Collections.unmodifiableMap(moduleVsType);
    }
}
