package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.*;
import com.facilio.qa.context.client.answers.handler.NumberAnswerHandler;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConstructAnswerPOJOsCommand extends FacilioCommand {

    private boolean onlyValidate = false;
    public ConstructAnswerPOJOsCommand(boolean onlyValidate) {
        this.onlyValidate = onlyValidate;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
        List<Map<String, Object>> answers = answerData == null ? null : (List<Map<String, Object>>) answerData.get("answers");
        V3Util.throwRestException(CollectionUtils.isEmpty(answers), ErrorCode.VALIDATION_ERROR, "Answers cannot be empty while add or updating answers");

        List<Long> questionIds = answers.stream().map(this::fetchQuestionId).collect(Collectors.toList());
        Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);

        List<ClientAnswerContext> answerContextList = new ArrayList<>();
        Map<Long, AnswerContext> questionVsAnswer = new HashMap<>();
        for (Map<String, Object> prop : answers) {
            try {
                Long questionId = (Long) prop.get("question");
                QuestionContext question = questions.get(questionId);
                V3Util.throwRestException(question == null || (!onlyValidate && question.getParent().getId() != response.getParent().getId()), ErrorCode.VALIDATION_ERROR, "Invalid question specified during add/ update answers");
                AnswerHandler handler = question.getQuestionType().getAnswerHandler();
                handler.validateAnswer(prop);
                ClientAnswerContext answer = FieldUtil.<ClientAnswerContext>getAsBeanFromMap(prop, handler.getAnswerClass());
                V3Util.throwRestException(answer.getAnswer() == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Answer cannot be null for question : {0}", questionId));

                AnswerContext answerContext = handler.deSerialize(answer, question);
                answerContext.setQuestion(question);
                answerContext.setParent(question.getParent());
                if (StringUtils.isNotEmpty(question.getCommentsLabel())) { // Adding comments only if it's enabled
                    answerContext.setComments(answer.getComments());
                } else {
                    answer.setComments(null); // To handle response
                }

                if (!onlyValidate) {
                    answerContext.setResponse(response);
                    answerContext._setId(answer.getId());

                    if (answerContext.getId() < 0) {
                        getToBeAdded().add(answerContext);
                    } else {
                        getToBeUpdated().add(answerContext); // Assumption is always all the props of answer will be updated
                    }
                    questionVsAnswer.put(questionId, answerContext);
                }
                answerContextList.add(answer);
            }
            catch (Exception e) {
                prop.put("error", FacilioUtil.constructMessageFromException(e));
                getErrors().add(prop);
            }
        }

        if (CollectionUtils.isNotEmpty(toBeAdded)) {
            context.put(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED, toBeAdded);
        }
        if (CollectionUtils.isNotEmpty(toBeUpdated)) {
            context.put(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED, toBeUpdated);
        }
        if (CollectionUtils.isNotEmpty(errors)) {
            context.put(FacilioConstants.QAndA.Command.ANSWER_ERRORS, errors);
        }
        context.put(FacilioConstants.QAndA.Command.CLIENT_ANSWER_LIST, answerContextList);
        context.put(FacilioConstants.QAndA.Command.QUESTION_VS_ANSWER, questionVsAnswer);
        context.put(FacilioConstants.QAndA.Command.QUESTION_MAP, questions);
        context.put(FacilioConstants.QAndA.Command.ANSWER_LIST, questionVsAnswer.values());

        return false;
    }

    private List<Map<String, Object>> errors = null;
    private List<Map<String, Object>> getErrors() {
        errors = errors == null ? new ArrayList<>() : errors;
        return errors;
    }

    private List<AnswerContext> toBeAdded = null;
    private List<AnswerContext> getToBeAdded() {
        toBeAdded = toBeAdded == null ? new ArrayList<>() : toBeAdded;
        return toBeAdded;
    }

    private List<AnswerContext> toBeUpdated = null;
    private List<AnswerContext> getToBeUpdated() {
        toBeUpdated = toBeUpdated == null ? new ArrayList<>() : toBeUpdated;
        return toBeUpdated;
    }

    @SneakyThrows
    private Long fetchQuestionId (Map<String, Object> answer)  {
        Long questionId = (Long) answer.get("question");
        V3Util.throwRestException(questionId == null, ErrorCode.VALIDATION_ERROR, "Question cannot be null while add/ update of answer");
        return questionId;
    }
}
