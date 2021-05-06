package com.facilio.qa.context.client.answers.handler;

import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.client.answers.StringAnswerContext;
import com.facilio.qa.context.questions.LongStringQuestionContext;
import com.facilio.qa.context.questions.ShortStringQuestionContext;
import com.facilio.qa.context.questions.handler.CommonStringQuestionHandler;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

public class StringAnswerHandler extends AnswerHandler<StringAnswerContext> {
    private boolean isBigString;
    public StringAnswerHandler(Class<StringAnswerContext> answerClass, boolean isBigString) {
        super(answerClass);
        this.isBigString = isBigString;
    }

    @Override
    public StringAnswerContext serialize(AnswerContext answer) {
        StringAnswerContext stringAnswer = new StringAnswerContext();
        stringAnswer.setAnswer(isBigString ? answer.getLongAnswer() : answer.getShortAnswer());
        return stringAnswer;
    }

    @Override
    public AnswerContext deSerialize(StringAnswerContext answer, QuestionContext question) throws Exception {
        V3Util.throwRestException(StringUtils.isEmpty(answer.getAnswer()), ErrorCode.VALIDATION_ERROR, "String answer cannot be empty");
        int maxLength = maxLength(question);
        V3Util.throwRestException(answer.getAnswer().length() > maxLength, ErrorCode.VALIDATION_ERROR, MessageFormat.format("String answer length ({0}) is greater than the max length ({1}) allowed", answer.getAnswer().length(), maxLength));
        AnswerContext answerContext = new AnswerContext();
        if (isBigString) {
            answerContext.setLongAnswer(answer.getAnswer());
        }
        else {
            answerContext.setShortAnswer(answer.getAnswer());
        }
        return answerContext;
    }

    private int maxLength(QuestionContext question) {
        if (isBigString) {
            return ((LongStringQuestionContext) question).getMaxLength() == null ? CommonStringQuestionHandler.BIG_STRING_MAX_LENGTH : ((LongStringQuestionContext) question).getMaxLength();
        }
        else {
            return ((ShortStringQuestionContext) question).getMaxLength() == null ? CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH : ((ShortStringQuestionContext) question).getMaxLength();
        }
    }

    @Override
    public boolean checkIfAnswerIsNull (AnswerContext answer) throws Exception {
        return StringUtils.isEmpty(isBigString ? answer.getLongAnswer() : answer.getShortAnswer());
    }

    @Override
    public boolean checkIfAnswerIsNull (StringAnswerContext answer, QuestionContext question) throws Exception {
        return StringUtils.isEmpty(answer.getAnswer());
    }
}
