package com.facilio.qa.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.time.DateRange;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Log4j
public class QAndAAction extends RESTAPIHandler {
    public String addOrUpdateAnswers() throws Exception {
//        boolean isDisplayLogic = false;
        FacilioChain addOrUpdateAnswersChain = QAndATransactionChainFactory.addOrUpdateAnswersChain();
        FacilioContext context = addOrUpdateAnswersChain.getContext();
        context.put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());
//        context.put("isDisplayLogic", false);
        addOrUpdateAnswersChain.execute();

        List<ClientAnswerContext> answers = (List<ClientAnswerContext>) context.get(FacilioConstants.QAndA.Command.CLIENT_ANSWER_LIST);
        this.setData("answers", answers);
        List<Map<String, Object>> errors = (List<Map<String, Object>>) context.get(FacilioConstants.QAndA.Command.ANSWER_ERRORS);
        if (CollectionUtils.isNotEmpty(errors)) {
            this.setData("errors", errors);
        }
        else {
            try {
                FacilioChain addChain = QAndATransactionChainFactory.executeDisplayLogicChain();

                addChain.getContext().put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());

                addChain.execute();

                setData("displayLogicResult", addChain.getContext().get(DisplayLogicUtil.DISPLAY_LOGIC_RULE_RESULT_JSON));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return SUCCESS;
    }

    public String executeDisplayLogic() throws Exception{
        try {
            FacilioChain addChain = QAndATransactionChainFactory.executeDisplayLogicChain();
            addChain.getContext().put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());
            addChain.getContext().put("isDisplayLogic", true);
            addChain.execute();
            setData("displayLogicResult", addChain.getContext().get(DisplayLogicUtil.DISPLAY_LOGIC_RULE_RESULT_JSON));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
		Constants.setModuleName(context,this.getModuleName());
		Constants.setRecordId(context,this.getId());
		context.put(FacilioConstants.ContextNames.RESOURCE_LIST,resources);
		executeTemplateChain.execute();
//        ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
//        handleSummaryRequest(response.getQAndAType().getResponseModule(), response._getId());

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
        List<ClientAnswerContext> answers = (List<ClientAnswerContext>) context.get(FacilioConstants.QAndA.Command.CLIENT_ANSWER_LIST);
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

    private long pageId;
    public String clonePage() throws Exception {
        FacilioChain clonePage = QAndATransactionChainFactory.clonePageChain();
        clonePage.getContext().put(FacilioConstants.QAndA.Command.PAGE_ID, pageId);
        clonePage.execute();

        PageContext clonedPage = (PageContext) clonePage.getContext().get(FacilioConstants.QAndA.PAGE);
        this.setData(FacilioConstants.QAndA.PAGE, clonedPage);

        return SUCCESS;
    }
    
    List<ResourceContext> resources;

    String templateName;
    public String cloneTemplate() throws Exception {
        FacilioChain cloneTemplate = QAndATransactionChainFactory.cloneTemplateChain();
        cloneTemplate.getContext().put(FacilioConstants.QAndA.OLD_TEMPLATE_ID, this.getId());
        cloneTemplate.getContext().put(FacilioConstants.QAndA.CLONED_TEMPLATE_NAME, this.getTemplateName());
        cloneTemplate.execute();

        this.setData(FacilioConstants.QAndA.CLONED_Q_AND_A_TEMPLATE, cloneTemplate.getContext().get(FacilioConstants.QAndA.CLONED_Q_AND_A_TEMPLATE));
        return SUCCESS;
    }

    public String publishTemplate() throws Exception {
        FacilioChain publishTemplate = QAndATransactionChainFactory.publishTemplateChain();
        publishTemplate.getContext().put(FacilioConstants.QAndA.TEMPLATE_ID, this.getId());
        publishTemplate.execute();
        return SUCCESS;
    }
}
