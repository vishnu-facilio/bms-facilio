package com.facilio.bmsconsoleV3.signup.workordersurvey;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QuestionType;
import com.facilio.qa.context.questions.RatingQuestionContext;
import com.facilio.qa.rules.commands.QAndARuleTransactionChainFactory;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class AddServiceRequestDeafultSurvey extends SignUpData {
    @Override
    public void addData() throws Exception {
        addDefaultSurveyTemplate();
    }

    public void addDefaultSurveyTemplate() throws Exception {

        //Default Email Template for survey

        FacilioChain addEmailStructureChain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext emailStructureContext = addEmailStructureChain.getContext();

        EMailStructure eMailStructureObj=new EMailStructure();
        eMailStructureObj.setDraft(false);
        eMailStructureObj.setHtml(true);

        String redirectURL= getPortalURL()+"/tenant/surveys/response/${surveyResponse.id}/attend";

        eMailStructureObj.setMessage("<!DOCTYPE html>\n<html lang=\"en\">\n  <head>\n    <meta charset=\"UTF-8\" />\n    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n    <title>Default Email Template</title>\n  </head>\n  <body\n    style=\"\n      background-color: #5735b7;\n      font-family: Helvetica;\n      text-align: center;\n    \"\n  >\n    <div\n      class=\"email-template-parent-div\"\n      style=\"\n        margin: 30px;\n        max-width: 45%;\n        background-color: white;\n        padding: 20px;\n        display: inline-block;\n      \"\n    >\n  <div\n        class=\"email-template-header\"\n        style=\"color: #202020; text-align: center\"\n      >\n        <h1>We'd love your feedback!</h1>\n      </div>\n      <div\n        class=\"email-template-content\"\n        style=\"text-align: justify; color: #202020\";width=\"100%\"\n        height=\"auto\"\n      >\n        <span\n          >We noticed that you recently raised a work request about ${surveyResponse.serviceRequestId.subject}. Please take a minute to provide your feedback to help us improve and serve you better.</span\n        >\n      </div>\n      <div\n        class=\"email-template-takesurvey-button\"\n        style=\"text-align: center; margin-top: 30px\"\n      >\n        <a\n          style=\"\n            border: 1px solid #5735b7;\n            text-decoration: none;\n            padding-top:15px; padding-right: 20px; padding-bottom:15px;padding-left: 20px;\n            color: white;\n            background-color: #5735b7;\n            font-weight: bold;\n            border-radius: 25px;\n          \"\n          class=\"take-survey-button\"\n          href=\""+redirectURL+"\"\n          target=\"_blank\"\n          >Take Survey</a\n        >\n      </div>\n      <div\n        class=\"break-line\"\n        style=\"\n          margin-top: 40px;\n          margin-right: -20px;\n          margin-left: -20px;\n          border-top: 2px solid #cecaca;\n        \"\n      ></div>\n      <div>\n        <footer>\n          <div\n            class=\"email-template-footer-content\"\n            style=\"\n              color: #656565;\n              font-size: 11px;\n              text-align: center;\n              margin-top: 20px;\n              font-weight: bold;\n            \"\n          >\n            <span>Powered by </span>\n          </div>\n          <div\n            class=\"email-template-footer-img\"\n            style=\"text-align: center; margin-bottom: -10px\"\n          >\n            <img\n              src=\"https://media.licdn.com/dms/image/C510BAQE2Uw6HD7nQZA/company-logo_200_200/0/1536843205636?e=2147483647&v=beta&t=Z546XssB6CwBt-LhLUHKJgRWFJTuf0laE_Pmx6-r2vc\"\n              width=\"60\"\n              height=\"50\"\n            />\n          </div>\n        </footer>\n      </div>\n    </div>\n </body>\n</html>\n  </body>\n</html>\n");
        eMailStructureObj.setName("Default Service Request Survey Email Template");
        eMailStructureObj.setSubject("Your feedback matters to us!");

        WorkflowContext workflowContextObj=new WorkflowContext();
        JSONArray jsonArrayObj=new JSONArray();
        Map<String,Object> expression1=new HashMap<>();
        expression1.put("constant","${surveyResponse.serviceRequestId.subject}");
        expression1.put("name","surveyResponse.serviceRequestId.subject");
        Map<String,Object> expression2=new HashMap<>();
        expression2.put("constant","${surveyResponse.id}");
        expression2.put("name","surveyResponse.id");
        Map<String,Object> expression3=new HashMap<>();
        expression3.put("constant","${surveyResponse.serviceRequestId}");
        expression3.put("name","surveyResponse.serviceRequestId");
        jsonArrayObj.add(expression1);
        jsonArrayObj.add(expression2);
        jsonArrayObj.add(expression3);
        workflowContextObj.setExpressions(jsonArrayObj);

        List<ParameterContext> ParameterContextKey=new ArrayList<>();
        ParameterContext parameterContext1=new ParameterContext();
        parameterContext1.setName("surveyResponse.serviceRequestId.subject");
        parameterContext1.setTypeString("String");
        ParameterContext parameterContext2=new ParameterContext();
        parameterContext2.setName("surveyResponse.id");
        parameterContext2.setTypeString("String");
        ParameterContext parameterContext3=new ParameterContext();
        parameterContext3.setName("surveyResponse.serviceRequestId");
        parameterContext3.setTypeString("String");
        ParameterContextKey.add(parameterContext1);
        ParameterContextKey.add(parameterContext2);
        ParameterContextKey.add(parameterContext3);

        workflowContextObj.setParameters(ParameterContextKey);

        eMailStructureObj.setWorkflow(workflowContextObj);

        emailStructureContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.Survey.SURVEY_RESPONSE);
        emailStructureContext.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, eMailStructureObj);
        emailStructureContext.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, new ArrayList<>());

        addEmailStructureChain.execute();

        EMailStructure emailStructureContext1= (EMailStructure) emailStructureContext.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE);
        Long emailStructureContextId = emailStructureContext1.getId();

        List<ActionContext> executeCreateActions=new ArrayList<>();
        ActionContext actionContext=new ActionContext();
        actionContext.setActionType(3);

        ModuleBean modBean1 = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> emailFromAddressField = modBean1.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);

        long idd= AccountUtil.getCurrentOrg().getOrgId();

        SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
                .beanClass(EmailFromAddress.class)
                .select(emailFromAddressField)
                .moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
                .andCondition(CriteriaAPI.getOrgIdCondition(idd,modBean1.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), "defaultReplyMail", StringOperators.IS));

        Long fromAddrId = select.get().get(0).getId();

        JSONObject templateJsonObj=new JSONObject();
        templateJsonObj.put("bcc",new ArrayList<>());
        templateJsonObj.put("cc",new ArrayList<>());
        templateJsonObj.put("attachmentList",new ArrayList<>());
        templateJsonObj.put("emailStructureId",emailStructureContextId);
        templateJsonObj.put("fromAddr",fromAddrId);
        templateJsonObj.put("ftl",false);
        templateJsonObj.put("isAttachmentAdded",false);
        templateJsonObj.put("html",false);
        templateJsonObj.put("isPushNotification",false);
        templateJsonObj.put("message","");
        templateJsonObj.put("name","New Service Request Survey Email Template");
        templateJsonObj.put("sendAsSeparateMail",true);
        templateJsonObj.put("subject","");
        templateJsonObj.put("templateFiles",new ArrayList<>());
        templateJsonObj.put("templateUrls",new ArrayList<>());
        templateJsonObj.put("to","${surveyResponse.assignedTo.email:-}");
        templateJsonObj.put("type",1);
        templateJsonObj.put("userWorkflow",null);

        Map<String,Object> workflowMap=new HashMap<>();

        List<Object> expressionKey=new ArrayList<>();
        Map<String,Object> exp1=new HashMap<>();
        exp1.put("constant","${org.domain}");
        exp1.put("name","org.domain");
        Map<String,Object> exp3=new HashMap<>();
        exp3.put("constant","${surveyResponse.assignedTo.email}");
        exp3.put("name","surveyResponse.assignedTo.email");
        expressionKey.add(exp1);
        expressionKey.add(exp3);

        workflowMap.put("expressions",expressionKey);

        List<Object> parameterKey=new ArrayList<>();
        Map<String,Object> parameter1=new HashMap<>();
        parameter1.put("name","org.domain");
        parameter1.put("typeString","String");
        Map<String,Object> parameter3=new HashMap<>();
        parameter3.put("name","surveyResponse.assignedTo.email");
        parameter3.put("typeString","String");
        Map<String,Object> parameter4=new HashMap<>();
        parameter4.put("name","surveyResponse");
        parameter4.put("typeString","String");
        parameterKey.add(parameter1);
        parameterKey.add(parameter3);
        parameterKey.add(parameter4);

        workflowMap.put("parameters",parameterKey);

        templateJsonObj.put("workflow",workflowMap);

        actionContext.setTemplateJson(templateJsonObj);

        executeCreateActions.add(actionContext);


        //Default builder for survey

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Survey.SURVEY_TEMPLATE);

        SurveyTemplateContext defaultTemplateObj = new SurveyTemplateContext();
        defaultTemplateObj.setName("Customer Satisfaction Survey For SR");
        Map<String, Object> dataMap = FieldUtil.getAsProperties(defaultTemplateObj);
        FacilioContext surveyTemplatecontext = V3Util.createRecord(module, dataMap);

        List<QAndATemplateContext> request = Constants.getRecordList(surveyTemplatecontext);

        PageContext page = new PageContext();
        page.setName("Page 1");
        page.setParent(request.get(0));
        page.setPosition(1);
        FacilioModule module2 = modBean.getModule(FacilioConstants.QAndA.PAGE);
        Map<String, Object> dataMap2 = FieldUtil.getAsProperties(page);
        FacilioContext pagecontext = V3Util.createRecord(module2, dataMap2);

        List<PageContext> request1 = Constants.getRecordList(pagecontext);

        //add questions

        RatingQuestionContext question1 = new RatingQuestionContext();
        question1.setQuestion("My initial request was dealt in a professional and timely manner");
        question1.setQuestionType(QuestionType.STAR_RATING);
        question1.setPage(request1.get(0));
        question1.setPosition(1);
        question1.setRatingScale(5);
        FacilioModule module3 = modBean.getModule(FacilioConstants.QAndA.QUESTION);
        Map<String, Object> dataMap3 = FieldUtil.getAsProperties(question1);

        RatingQuestionContext question2 = new RatingQuestionContext();
        question2.setQuestion("The technician told me what they would be doing and informed me of any effect/disruption to the environment while the task was being carried out");
        question2.setQuestionType(QuestionType.STAR_RATING);
        question2.setPage(request1.get(0));
        question2.setPosition(2);
        question2.setRatingScale(5);
        Map<String, Object> dataMap4 = FieldUtil.getAsProperties(question2);

        RatingQuestionContext question3 = new RatingQuestionContext();
        question3.setQuestion("Works were carried out in professional manner and to my satisfaction");
        question3.setQuestionType(QuestionType.STAR_RATING);
        question3.setPage(request1.get(0));
        question3.setPosition(3);
        question3.setRatingScale(5);
        Map<String, Object> dataMap5 = FieldUtil.getAsProperties(question3);

        RatingQuestionContext question4 = new RatingQuestionContext();
        question4.setQuestion("Work area was left clean and tidy");
        question4.setQuestionType(QuestionType.STAR_RATING);
        question4.setPage(request1.get(0));
        question4.setPosition(4);
        question4.setRatingScale(5);
        Map<String, Object> dataMap6 = FieldUtil.getAsProperties(question4);

        RatingQuestionContext question5 = new RatingQuestionContext();
        question5.setQuestion(" Additional Feedback");
        question5.setQuestionType(QuestionType.LONG_ANSWER);
        question5.setPage(request1.get(0));
        question5.setPosition(5);
        Map<String, Object> dataMap7 = FieldUtil.getAsProperties(question5);

        FacilioContext questionContext1=V3Util.createRecord(module3, dataMap3);
        FacilioContext questionContext2=V3Util.createRecord(module3, dataMap4);
        FacilioContext questionContext3=V3Util.createRecord(module3, dataMap5);
        FacilioContext questionContext4=V3Util.createRecord(module3, dataMap6);
        V3Util.createRecord(module3, dataMap7);

        //set scorings for first question

        FacilioChain addOrUpdateRulesChain = QAndARuleTransactionChainFactory.addRules();
        List<Map<String, Object>> rules=new ArrayList<>();
        Map<String,Object> score1=new HashMap<>();
        List<Map<String, Object>> ruleCondition1=new ArrayList<>();
        Map<String,Object> ruleConditionMap1=new HashMap<>();
        ruleConditionMap1.put("option",1l);
        ruleConditionMap1.put("score",5);
        ruleCondition1.add(ruleConditionMap1);

        Map<String,Object> ruleConditionMap2=new HashMap<>();
        ruleConditionMap2.put("option",2l);
        ruleConditionMap2.put("score",10);
        ruleCondition1.add(ruleConditionMap2);

        Map<String,Object> ruleConditionMap3=new HashMap<>();
        ruleConditionMap3.put("option",3l);
        ruleConditionMap3.put("score",15);
        ruleCondition1.add(ruleConditionMap3);

        Map<String,Object> ruleConditionMap4=new HashMap<>();
        ruleConditionMap4.put("option",4l);
        ruleConditionMap4.put("score",20);
        ruleCondition1.add(ruleConditionMap4);

        Map<String,Object> ruleConditionMap5=new HashMap<>();
        ruleConditionMap5.put("option",5l);
        ruleConditionMap5.put("score",25);
        ruleCondition1.add(ruleConditionMap5);

        score1.put("question","My initial request was dealt with in a professional and timely manner ");
        List<RatingQuestionContext> questionContextList1 = Constants.getRecordList(questionContext1);
        score1.put("questionId",questionContextList1.get(0).getId());
        score1.put("questionType",QuestionType.STAR_RATING);
        score1.put("conditions",ruleCondition1);
        rules.add(score1);

        //set scoring for second question

        Map<String,Object> score2=new HashMap<>();

        score2.put("question","The technician told me what they would be doing and informed me of any effect/disruption to the environment while the task was being carried out");
        List<RatingQuestionContext> questionContextList2 = Constants.getRecordList(questionContext2);
        score2.put("questionId",questionContextList2.get(0).getId());
        score2.put("questionType",QuestionType.STAR_RATING);

        List<Map<String, Object>> ruleCondition2=new ArrayList<>();
        Map<String,Object> ruleConditionMapTwo1=new HashMap<>();
        ruleConditionMapTwo1.put("option",1l);
        ruleConditionMapTwo1.put("score",5);
        ruleCondition2.add(ruleConditionMapTwo1);

        Map<String,Object> ruleConditionMapTwo2=new HashMap<>();
        ruleConditionMapTwo2.put("option",2l);
        ruleConditionMapTwo2.put("score",10);
        ruleCondition2.add(ruleConditionMapTwo2);

        Map<String,Object> ruleConditionMapTwo3=new HashMap<>();
        ruleConditionMapTwo3.put("option",3l);
        ruleConditionMapTwo3.put("score",15);
        ruleCondition2.add(ruleConditionMapTwo3);

        Map<String,Object> ruleConditionMapTwo4=new HashMap<>();
        ruleConditionMapTwo4.put("option",4l);
        ruleConditionMapTwo4.put("score",20);
        ruleCondition2.add(ruleConditionMapTwo4);

        Map<String,Object> ruleConditionMapTwo5=new HashMap<>();
        ruleConditionMapTwo5.put("option",5l);
        ruleConditionMapTwo5.put("score",25);
        ruleCondition2.add(ruleConditionMapTwo5);

        score2.put("conditions",ruleCondition2);
        rules.add(score2);

        //set scoring for third question

        Map<String,Object> score3=new HashMap<>();

        score3.put("question","Works were carried out in professional manner and to my satisfaction");
        List<RatingQuestionContext> questionContextList3 = Constants.getRecordList(questionContext3);
        score3.put("questionId",questionContextList3.get(0).getId());
        score3.put("questionType",QuestionType.STAR_RATING);

        List<Map<String, Object>> ruleCondition3=new ArrayList<>();
        Map<String,Object> ruleConditionMapThree1=new HashMap<>();
        ruleConditionMapThree1.put("option",1l);
        ruleConditionMapThree1.put("score",5);
        ruleCondition3.add(ruleConditionMapThree1);

        Map<String,Object> ruleConditionMapThree2=new HashMap<>();
        ruleConditionMapThree2.put("option",2l);
        ruleConditionMapThree2.put("score",10);
        ruleCondition3.add(ruleConditionMapThree2);

        Map<String,Object> ruleConditionMapThree3=new HashMap<>();
        ruleConditionMapThree3.put("option",3l);
        ruleConditionMapThree3.put("score",15);
        ruleCondition3.add(ruleConditionMapThree3);

        Map<String,Object> ruleConditionMapThree4=new HashMap<>();
        ruleConditionMapThree4.put("option",4l);
        ruleConditionMapThree4.put("score",20);
        ruleCondition3.add(ruleConditionMapThree4);

        Map<String,Object> ruleConditionMapThree5=new HashMap<>();
        ruleConditionMapThree5.put("option",5l);
        ruleConditionMapThree5.put("score",25);
        ruleCondition3.add(ruleConditionMapThree5);

        score3.put("conditions",ruleCondition3);
        rules.add(score3);


        //set score for fourth questions

        Map<String,Object> score4=new HashMap<>();

        score4.put("question","Work area was left clean and tidy");
        List<RatingQuestionContext> questionContextList4 = Constants.getRecordList(questionContext4);
        score4.put("questionId",questionContextList4.get(0).getId());
        score4.put("questionType",QuestionType.STAR_RATING);

        List<Map<String, Object>> ruleCondition4=new ArrayList<>();
        Map<String,Object> ruleConditionMapFour1=new HashMap<>();
        ruleConditionMapFour1.put("option",1l);
        ruleConditionMapFour1.put("score",5);
        ruleCondition4.add(ruleConditionMapFour1);

        Map<String,Object> ruleConditionMapFour2=new HashMap<>();
        ruleConditionMapFour2.put("option",2l);
        ruleConditionMapFour2.put("score",10);
        ruleCondition4.add(ruleConditionMapFour2);

        Map<String,Object> ruleConditionMapFour3=new HashMap<>();
        ruleConditionMapFour3.put("option",3l);
        ruleConditionMapFour3.put("score",15);
        ruleCondition4.add(ruleConditionMapFour3);

        Map<String,Object> ruleConditionMapFour4=new HashMap<>();
        ruleConditionMapFour4.put("option",4l);
        ruleConditionMapFour4.put("score",20);
        ruleCondition4.add(ruleConditionMapFour4);

        Map<String,Object> ruleConditionMapFour5=new HashMap<>();
        ruleConditionMapFour5.put("option",5l);
        ruleConditionMapFour5.put("score",25);
        ruleCondition4.add(ruleConditionMapFour5);

        score4.put("conditions",ruleCondition4);
        rules.add(score4);

        FacilioContext context1 = addOrUpdateRulesChain.getContext();
        context1.put(com.facilio.qa.rules.Constants.Command.RULE_TYPE, QAndARuleType.SCORING);
        context1.put(FacilioConstants.QAndA.Command.PAGE_ID, request1.get(0).getId());
        context1.put(com.facilio.qa.rules.Constants.Command.RULES, rules);

        addOrUpdateRulesChain.execute();

        //add workflow rule for survey configuration

        WorkflowRuleContext rule = new WorkflowRuleContext();

        rule.setName("Customer Satisfaction Survey");
        ActionContext action = new ActionContext();
        action.setActionType(36);
        JSONObject jsonObj = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("fieldId", Constants.getModBean().getField("requester", "serviceRequest").getFieldId());
        map.put("expiryDay", 3);
        map.put("isRetakeAllowed", false);
        map.put("retakeExpiryDay", 3);
        map.put("sharingType", 1);
        map.put("qandaTemplateId", request.get(0).getId());
        jsonObj.putAll(map);
        action.setTemplateJson(jsonObj);

        WorkflowEventContext event = new WorkflowEventContext();
        event.setActivityType(1048576);
        event.setModuleName("serviceRequest");

        FacilioStatus closed = TicketAPI.getStatus(Constants.getModBean().getModule(FacilioConstants.ContextNames.SERVICE_REQUEST),"Closed");
        //criteria for default survey

        Criteria criteriaMap1=new Criteria();
        Condition conditionMap1=new Condition();
        conditionMap1.setFieldName("moduleState");
        conditionMap1.setOperatorId(36);
        conditionMap1.setValue(String.valueOf(closed.getId()));
        criteriaMap1.setPattern("( 1 )");
        Map<String,Condition> conditionMap2=new HashMap<>();
        conditionMap2.put("1",conditionMap1);
        criteriaMap1.setConditions(conditionMap2);

        List<FieldChangeFieldContext> fieldsList=new ArrayList<>();
        FieldChangeFieldContext fieldObj1=new FieldChangeFieldContext();

        //Get FieldId for WO

        ModuleBean moduleBean2=Constants.getModBean();
        List<FacilioField> woFields=moduleBean2.getAllFields("serviceRequest");
        Map<String,FacilioField> woFieldMap=FieldFactory.getAsMap(woFields);

        fieldObj1.setField(woFieldMap.get("moduleState"));
        fieldsList.add(fieldObj1);

        rule.setActions(Collections.singletonList(action));
        rule.setEvent(event);
        rule.setRuleType(46);
        rule.setScheduleType(3);
        rule.setCriteria(criteriaMap1);
        rule.setFields(fieldsList);


        FacilioChain addRule = TransactionChainFactory.addSurveyRuleChain();
        FacilioContext context5 = addRule.getContext();
        context5.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
        context5.put("executeCreateActions", executeCreateActions);
        context5.put("executeResponseActions", null);


        addRule.execute();

        Map<String, Object> updateprops = new HashMap<>();
        updateprops.put("status", false);

        WorkflowRuleContext workflowRuleContext = (WorkflowRuleContext) context5.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        Long workflowRuleContextId = workflowRuleContext.getId();

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWorkflowRuleModule().getTableName())
                .fields(FieldFactory.getWorkflowRuleFields())
                .andCondition(CriteriaAPI.getIdCondition(workflowRuleContextId, ModuleFactory.getWorkflowRuleModule()));

        updateBuilder.update(updateprops);


    }
    public static StringBuilder getPortalURL() throws Exception {
        List<AppDomain> appDomains = null;
        appDomains = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.TENANT_PORTAL, AccountUtil.getCurrentOrg().getId());
        StringBuilder appUrl;
        if(appDomains!=null && !appDomains.isEmpty()){
            appUrl = new StringBuilder(SSOUtil.getProtocol())
                    .append("://")
                    .append(appDomains.get(0).getDomain());
        }
        else{
            appUrl=new StringBuilder(SSOUtil.getProtocol())
                    .append("://")
                    .append("maintenance");
        }
        return appUrl;

    }
}
