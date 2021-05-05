package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConstructAnswerPOJOsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
        List<Map<String, Object>> answers = (List<Map<String, Object>>) answerData.get("answers");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(answers), "Answers cannot be empty while add or updating answers");

        List<Long> questionIds = answers.stream().map(this::fetchQuestionId).collect(Collectors.toList());
        Map<Long, QuestionContext> questions = QAndAUtil.fetchExtendedQuestionMap(questionIds, true);

        List<ClientAnswerContext> answerContextList = new ArrayList<>();
        Map<Long, AnswerContext> questionVsAnswer = new HashMap<>();
        for (Map<String, Object> prop : answers) {
            try {
                Long questionId = (Long) prop.get("question");
                QuestionContext question = questions.get(questionId);
                FacilioUtil.throwIllegalArgumentException(question == null || question.getParent().getId() != response.getParent().getId(), "Invalid question specified during add/ update answers");
                AnswerHandler handler = question.getQuestionType().getAnswerHandler();
                ClientAnswerContext answer = FieldUtil.<ClientAnswerContext>getAsBeanFromMap(prop, handler.getAnswerClass());
                FacilioUtil.throwIllegalArgumentException(answer.getAnswer() == null, MessageFormat.format("Answer cannot be null for question : {0}", questionId));

                AnswerContext answerContext = handler.deSerialize(answer, question);
                answerContext.setQuestion(question);
                answerContext.setParent(question.getParent());
                answerContext.setResponse(response);
                answerContext._setId(answer.getId());

                if (answerContext.getId() < 0) {
                    getToBeAdded().add(answerContext);
                } else {
                    getToBeUpdated().add(answerContext); // Assumption is always all the props of answer will be updated
                }
                answerContextList.add(answer);
                questionVsAnswer.put(questionId, answerContext);
            }
            catch (Exception e) {

            }
        }

        if (CollectionUtils.isNotEmpty(toBeAdded)) {
            context.put(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED, toBeAdded);
        }
        if (CollectionUtils.isNotEmpty(toBeUpdated)) {
            context.put(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED, toBeUpdated);
        }
        context.put(FacilioConstants.QAndA.Command.ANSWER_LIST, answerContextList);
        context.put(FacilioConstants.QAndA.Command.QUESTION_VS_ANSWER, questionVsAnswer);

        return false;
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

    private Long fetchQuestionId (Map<String, Object> answer) {
        Long questionId = (Long) answer.get("question");
        FacilioUtil.throwIllegalArgumentException(questionId == null, "Question cannot be null while add/ update of answer");
        return questionId;
    }
}
