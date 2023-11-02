package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidateMandatoryQuestions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
        List<QuestionContext> questions = (List<QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_LIST);

        if (CollectionUtils.isNotEmpty(questions)) {
            List<AnswerContext> answers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
            Map<Long, AnswerContext> questionVsAnswer = answers == null ? Collections.EMPTY_MAP : answers.stream().collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity()));
            for (QuestionContext question : questions) {
                AnswerContext answer = questionVsAnswer.get(question.getId());
                if (answer != null) {
                    answer.setQuestion(question);
                }

                if (question.mandatory()) {
                    List<Long> activeQuestionIds = (List<Long>) context.get("activeQuestionIds");
                    if(activeQuestionIds!=null && !activeQuestionIds.isEmpty() && activeQuestionIds.contains(question.getId())) {
                        boolean answered = answer != null && !question.getQuestionType().getAnswerHandler().checkIfAnswerIsNull(answer);
                        if (!answered) {
                            getUnAnsweredMandatoryQuestion().add(question);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(unAnsweredMandatoryQuestion)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, MessageFormat.format("Cannot submit response ({0}) since all mandatory questions haven't been answered", response.getId()), constructErrorMessage(unAnsweredMandatoryQuestion));
            }
        }
        return false;
    }

    private List<QuestionContext> unAnsweredMandatoryQuestion = null;
    private List<QuestionContext> getUnAnsweredMandatoryQuestion() {
        unAnsweredMandatoryQuestion = unAnsweredMandatoryQuestion == null ? new ArrayList<>() : unAnsweredMandatoryQuestion;
        return unAnsweredMandatoryQuestion;
    }

    private JSONObject constructErrorMessage (List<QuestionContext> unAnsweredMandatoryQuestion) {
        JSONArray unAnswered = new JSONArray();
        for (QuestionContext question : unAnsweredMandatoryQuestion) {
            JSONObject error = new JSONObject();
            error.put("question", question.getQuestion());
            error.put("id", question.getId());
            error.put("page", question.getPage().getId());

            unAnswered.add(error);
        }
        JSONObject errorData = new JSONObject();
        errorData.put("unAnswered", unAnswered);
        return errorData;
    }
}
