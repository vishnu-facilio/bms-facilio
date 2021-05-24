package com.facilio.qa.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.time.DateRange;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QAndAAction extends RESTAPIHandler {
    public String addOrUpdateAnswers() throws Exception {
        FacilioChain addOrUpdateAnswersChain = QAndATransactionChainFactory.addOrUpdateAnswersChain();
        FacilioContext context = addOrUpdateAnswersChain.getContext();
        context.put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());
        addOrUpdateAnswersChain.execute();

        List<ClientAnswerContext> answers = (List<ClientAnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
        this.setData("answers", answers);
        List<Map<String, Object>> errors = (List<Map<String, Object>>) context.get(FacilioConstants.QAndA.Command.ANSWER_ERRORS);
        if (CollectionUtils.isNotEmpty(errors)) {
            this.setData("errors", errors);
        }

        return SUCCESS;
    }

    public String validateAnswers() throws Exception {
        FacilioChain validateAnswersChain = QAndAReadOnlyChainFactory.validateAnswersChain();
        FacilioContext context = validateAnswersChain.getContext();
        context.put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());
        validateAnswersChain.execute();

        List<Map<String, Object>> errors = (List<Map<String, Object>>) context.get(FacilioConstants.QAndA.Command.ANSWER_ERRORS);
        JSONObject data = new JSONObject();
        if (CollectionUtils.isNotEmpty(errors)) {
            data.put("errors", errors);
        }
        this.setData(data);

        return SUCCESS;
    }

    public String executeTemplate() throws Exception {
        FacilioChain executeTemplateChain = QAndATransactionChainFactory.executeTemplateChain();
        FacilioContext context = executeTemplateChain.getContext();
        Constants.setModuleName(context, this.getModuleName());
        Constants.setRecordId(context, this.getId());
        executeTemplateChain.execute();

        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
        handleSummaryRequest(response.getQAndAType().getResponseModule(), response._getId());

        return SUCCESS;
    }

    private long questionId;
    private long startTime, endTime;

    public String fetchAnswers() throws Exception {
        FacilioChain fetchAnswersChain = QAndAReadOnlyChainFactory.fetchAnswersOfQuestion();
        FacilioContext context = fetchAnswersChain.getContext();

        context.put(FacilioConstants.QAndA.Command.QUESTION_ID, questionId);
        context.put(FacilioConstants.QAndA.Command.ANSWER_RANGE, new DateRange(startTime ,endTime));

        fetchAnswersChain.execute();
        List<ClientAnswerContext> answers = (List<ClientAnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
        this.setData("answers", answers);

        return SUCCESS;
    }

    public String fetchOtherOptions() throws Exception {
        FacilioChain fetchOtherOptionsChain = QAndAReadOnlyChainFactory.fetchOtherOptionsOfMCQ();
        FacilioContext context = fetchOtherOptionsChain.getContext();

        context.put(FacilioConstants.QAndA.Command.QUESTION_ID, questionId);
        context.put(FacilioConstants.QAndA.Command.ANSWER_RANGE, new DateRange(startTime ,endTime));

        fetchOtherOptionsChain.execute();
        this.setData("otherResponses", context.get(FacilioConstants.QAndA.Command.OTHER_RESPONSES));

        return SUCCESS;
    }
}
