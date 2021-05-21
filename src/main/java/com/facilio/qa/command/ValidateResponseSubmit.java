package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidateResponseSubmit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ResponseContext> responses = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(responses)) {
            Map<Long, ? extends ResponseContext> oldResponseMap = Constants.getOldRecordMap(context);
            for (ResponseContext response : responses) {
                ResponseContext oldResponse = oldResponseMap.get(response._getId());
                V3Util.throwRestException(oldResponse == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid response ID ({0}) is specified for updating", response._getId()));
                V3Util.throwRestException(response.getParent() != null && response.getParent().getId() > 0 && response.getParent().getId() != oldResponse.getParent().getId(), ErrorCode.VALIDATION_ERROR, MessageFormat.format("Cannot update parent of response : {0}", response._getId()));
                if (response.getResStatus() != oldResponse.getResStatus() && response.getResStatus() == ResponseContext.ResponseStatus.COMPLETED) {
                    validateResponseComplete(response);
                }
            }
        }

        return false;
    }

    private void validateResponseComplete (ResponseContext response) throws Exception {
        List<QuestionContext> questions = QAndAUtil.getChildRecordsFromTemplate(FacilioConstants.QAndA.QUESTION, response.getParent().getId());
        if (CollectionUtils.isNotEmpty(questions)) {
            List<AnswerContext> answers = QAndAUtil.getChildRecordsFromTemplate(FacilioConstants.QAndA.ANSWER, response.getParent().getId());
            Map<Long, AnswerContext> questionVsAnswer = answers.stream().collect(Collectors.toMap(a -> a.getQuestion()._getId(), Function.identity()));
            for (QuestionContext question : questions) {
                AnswerContext answer = questionVsAnswer.get(question._getId());
                if (answer != null) {
                    answer.setQuestion(question);
                }

                if (question.mandatory()) {
                    boolean answered = answer != null && !question.getQuestionType().getAnswerHandler().checkIfAnswerIsNull(answer);
                    if (!answered) {
                        getUnAnsweredMandatoryQuestion().add(question);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(unAnsweredMandatoryQuestion)) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, MessageFormat.format("Cannot submit response ({0}) since all mandatory questions haven't been answered", response._getId()), constructErrorMessage(unAnsweredMandatoryQuestion));
            }
        }
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
            error.put("id", question._getId());
            error.put("page", question.getPage()._getId());

            unAnswered.add(error);
        }
        JSONObject errorData = new JSONObject();
        errorData.put("unAnswered", unAnswered);
        return errorData;
    }
}
