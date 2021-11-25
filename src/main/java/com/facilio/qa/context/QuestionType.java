package com.facilio.qa.context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.FieldType;
import com.facilio.qa.context.client.answers.*;
import com.facilio.qa.context.client.answers.handler.*;
import com.facilio.qa.context.questions.*;
import com.facilio.qa.context.questions.handler.*;
import com.facilio.qa.rules.pojo.BooleanRuleHandler;
import com.facilio.qa.rules.pojo.DefaultRuleHandler;
import com.facilio.qa.rules.pojo.MCQRuleHandler;
import com.facilio.qa.rules.pojo.RatingRuleHandler;
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
    HEADING (
            FacilioConstants.QAndA.Questions.HEADING_QUESTION,
            HeadingQuestionContext.class,
            null, // If Answer handler is null, it's assumed no answer is required for this question type
            HeadingQuestionHandler.INSTANCE,
            null,
            null
    ),
    NUMBER (
            FacilioConstants.QAndA.Questions.NUMBER_QUESTION,
            NumberQuestionContext.class,
            new NumberAnswerHandler(NumberAnswerContext.class),
            new CommonNumberQuestionHandler<>(NumberQuestionContext::getMinValue, NumberQuestionContext::getMaxValue),
            DefaultRuleHandler.NUMBER,
            FieldType.NUMBER
    ),
    DECIMAL (
            FacilioConstants.QAndA.Questions.DECIMAL_QUESTION,
            DecimalQuestionContext.class,
            new DecimalAnswerHandler(DecimalAnswerContext.class),
            new CommonNumberQuestionHandler<>(DecimalQuestionContext::getMinValue, DecimalQuestionContext::getMaxValue),
            DefaultRuleHandler.DECIMAL,
            FieldType.DECIMAL
    ),

    SHORT_ANSWER (
            FacilioConstants.QAndA.Questions.SHORT_STRING_QUESTION,
            ShortStringQuestionContext.class,
            new StringAnswerHandler(StringAnswerContext.class, false),
            new CommonStringQuestionHandler<>(ShortStringQuestionContext::getMaxLength, CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH),
            DefaultRuleHandler.SHORT_ANSWER,
            FieldType.STRING
    ),
    LONG_ANSWER (
            FacilioConstants.QAndA.Questions.LONG_STRING_QUESTION,
            LongStringQuestionContext.class,
            new StringAnswerHandler(StringAnswerContext.class, true ),
            new CommonStringQuestionHandler<>(LongStringQuestionContext::getMaxLength, CommonStringQuestionHandler.BIG_STRING_MAX_LENGTH),
            DefaultRuleHandler.LONG_ANSWER,
            FieldType.BIG_STRING
    ),
    DATE_TIME (
            FacilioConstants.QAndA.Questions.DATE_TIME_QUESTION,
            DateTimeQuestionContext.class,
            new DateTimeAnswerHandler(DateTimeAnswerContext.class),
            DefaultRuleHandler.DATE_TIME,
            FieldType.DATE_TIME
    ),

    MULTIPLE_CHOICE_ONE (
            FacilioConstants.QAndA.Questions.MCQ_SINGLE,
            MCQSingleContext.class,
            new MCQSingleAnswerHandler(MCQSingleAnswerContext.class),
            new MCQHandler<MCQSingleContext>(FacilioConstants.QAndA.Questions.MCQ_SINGLE_OPTIONS),
            MCQRuleHandler.SINGLE
    ),
    MULTIPLE_CHOICE_MANY (
            FacilioConstants.QAndA.Questions.MCQ_MULTI,
            MCQMultiContext.class,
            new MCQMultiAnswerHandler(MCQMultiAnswerContext.class),
            new MCQHandler<MCQMultiContext>(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS),
            MCQRuleHandler.MULTI
    ),
    FILE_UPLOAD (
            FacilioConstants.QAndA.Questions.FILE_UPLOAD_QUESTION,
            FileUploadQuestionContext.class,
            new FileUploadAnswerHandler(FileUploadAnswerContext.class),
            null
    ),

    MULTI_FILE_UPLOAD (
            FacilioConstants.QAndA.Questions.MULTI_FILE_UPLOAD_QUESTION,
            MultiFileUploadQuestionContext.class,
            new MultiFileUploadHandler(MultiFileUploadAnswerContext.class),
            null
    ),
    BOOLEAN (
            FacilioConstants.QAndA.Questions.BOOLEAN_QUESTION,
            BooleanQuestionContext.class,
            new BooleanAnswerHandler(BooleanAnswerContext.class),
            new BooleanQuestionHandler(),
            BooleanRuleHandler.INSTANCE,
            FieldType.BOOLEAN
    ),
    STAR_RATING(
            FacilioConstants.QAndA.Questions.STAR_RATING_QUESTION,
            RatingQuestionContext.class,
            new RatingAnswerHandler(RatingAnswerContext.class),
            new RatingQuestionHandler(),
            RatingRuleHandler.STAR_RATING
    ),
    SMILEY_RATING(
            FacilioConstants.QAndA.Questions.SMILEY_RATING_QUESTION,
            RatingQuestionContext.class,
            new RatingAnswerHandler(RatingAnswerContext.class),
            new RatingQuestionHandler(),
            RatingRuleHandler.SMILEY_RATING
    );

    private static final Map<String, QuestionType> MODULE_VS_TYPE = initModuleVsTypeMap();
    private final String subModuleName;
    private final Class<? extends QuestionContext> subClass;
    private final AnswerHandler answerHandler;
    private final QuestionHandler questionHandler;
    private final RuleHandler ruleHandler;
    private final FieldType answerFieldType;

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (String subModuleName, Class<Q> subClass, AnswerHandler<A> answerHandler, RuleHandler ruleHandler) {
        this (subModuleName, subClass, answerHandler, ruleHandler, null);
    }

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (String subModuleName, Class<Q> subClass, AnswerHandler<A> answerHandler, RuleHandler ruleHandler, FieldType answerFieldType) {
        this (subModuleName, subClass, answerHandler, null, ruleHandler, answerFieldType);
    }

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (@NonNull String subModuleName, @NonNull Class<Q> subClass, AnswerHandler<A> answerHandler, QuestionHandler<Q> questionHandler, RuleHandler ruleHandler) {
        this (subModuleName, subClass, answerHandler, questionHandler, ruleHandler, null);
    }

    private <Q extends QuestionContext, A extends ClientAnswerContext> QuestionType (@NonNull String subModuleName, @NonNull Class<Q> subClass, AnswerHandler<A> answerHandler, QuestionHandler<Q> questionHandler, RuleHandler ruleHandler, FieldType answerFieldType) {
        this.subClass = subClass;
        this.subModuleName = subModuleName;
        this.answerHandler = answerHandler;
        this.questionHandler = questionHandler;
        this.ruleHandler = ruleHandler;
        this.answerFieldType = answerFieldType;
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

    public boolean isOperatorSupportedForRules() {
        return isRuleSupported() && getAnswerFieldType() != null && !getAnswerFieldType().isRelRecordField();
    }

    public boolean isRuleSupported() {
        return ruleHandler != null;
    }
}
