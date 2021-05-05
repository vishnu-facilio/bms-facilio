package com.facilio.qa.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Action;
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
        this.getData().put("answers", answers);
        List<Map<String, Object>> errors = (List<Map<String, Object>>) context.get(FacilioConstants.QAndA.Command.ANSWER_ERRORS);
        if (CollectionUtils.isNotEmpty(errors)) {
            this.getData().put("errors", errors);
        }

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
}
