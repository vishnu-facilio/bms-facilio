package com.facilio.qa.context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.qa.context.answers.*;
import com.facilio.qa.context.client.answers.*;
import com.facilio.qa.context.client.answers.handler.*;
import com.facilio.qa.context.questions.*;
import com.facilio.qa.context.questions.handler.BooleanQuestionHandler;
import com.facilio.qa.context.questions.handler.CommonNumberQuestionHandler;
import com.facilio.qa.context.questions.handler.CommonStringQuestionHandler;
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
public enum QuestionType implements FacilioStringEnum {
    HEADING (FacilioConstants.QAndA.Questions.HEADING_QUESTION, HeadingQuestionContext.class, null),
    NUMBER ( FacilioConstants.QAndA.Questions.NUMBER_QUESTION, NumberQuestionContext.class, new NumberAnswerHandler(NumberAnswerContext.class), new CommonNumberQuestionHandler<>(NumberQuestionContext::getMinValue, NumberQuestionContext::getMaxValue)),
    DECIMAL (FacilioConstants.QAndA.Questions.DECIMAL_QUESTION, DecimalQuestionContext.class, new DecimalAnswerHandler(DecimalAnswerContext.class), new CommonNumberQuestionHandler<>(DecimalQuestionContext::getMinValue, DecimalQuestionContext::getMaxValue)),

    SHORT_ANSWER (FacilioConstants.QAndA.Questions.SHORT_STRING_QUESTION, ShortStringQuestionContext.class, new StringAnswerHandler(StringAnswerContext.class, false), new CommonStringQuestionHandler<>(ShortStringQuestionContext::getMaxLength, CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH)),
    LONG_ANSWER (FacilioConstants.QAndA.Questions.LONG_STRING_QUESTION, LongStringQuestionContext.class, new StringAnswerHandler(StringAnswerContext.class, true ), new CommonStringQuestionHandler<>(LongStringQuestionContext::getMaxLength, CommonStringQuestionHandler.BIG_STRING_MAX_LENGTH)),
    DATE_TIME (FacilioConstants.QAndA.Questions.DATE_TIME_QUESTION, DateTimeQuestionContext.class, new DateTimeAnswerHandler(DateTimeAnswerContext.class)),

    MULTIPLE_CHOICE_ONE (FacilioConstants.QAndA.Questions.MCQ_SINGLE, MCQSingleContext.class, new MCQSingleAnswerHandler(MCQSingleAnswerContext.class), new MCQHandler<MCQSingleContext>(FacilioConstants.QAndA.Questions.MCQ_SINGLE_OPTIONS)),
    MULTIPLE_CHOICE_MANY (FacilioConstants.QAndA.Questions.MCQ_MULTI, MCQMultiContext.class, new MCQMultiAnswerHandler(MCQMultiAnswerContext.class), new MCQHandler<MCQMultiContext>(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS)),
    FILE_UPLOAD (FacilioConstants.QAndA.Questions.FILE_UPLOAD_QUESTION, FileUploadQuestionContext.class, new FileUploadAnswerHandler(FileUploadAnswerContext.class)),

    MULTI_FILE_UPLOAD (FacilioConstants.QAndA.Questions.MULTI_FILE_UPLOAD_QUESTION, MultiFileUploadQuestionContext.class, new MultiFileUploadHandler(MultiFileUploadAnswerContext.class)),
    BOOLEAN (FacilioConstants.QAndA.Questions.BOOLEAN_QUESTION, BooleanQuestionContext.class, new BooleanAnswerHandler(BooleanAnswerContext.class), new BooleanQuestionHandler())
    ;

    private static final Map<String, QuestionType> MODULE_VS_TYPE = initModuleVsTypeMap();
    private final String subModuleName;
    private final Class<? extends QuestionContext> subClass;
    private final AnswerHandler answerHandler;
    private final QuestionHandler questionHandler;

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (String subModuleName, Class<Q> subClass, AnswerHandler<A> answerHandler) {
        this (subModuleName, subClass, answerHandler, null);
    }

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (@NonNull String subModuleName, @NonNull Class<Q> subClass, AnswerHandler<A> answerHandler, QuestionHandler<Q> questionHandler) {
        this.subClass = subClass;
        this.subModuleName = subModuleName;
        this.answerHandler = answerHandler;
        this.questionHandler = questionHandler;
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
