package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.workflows.context.WorkflowContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEmailTemplateForWorkorder extends SignUpData {
    @Override
    public void addData() throws Exception {
        addTechnicianClosesWorkorder();
        addAssignTechTemplate();
        addAssignTeamTemplate();
        addCommentTemplate();
    }

    public static void addTechnicianClosesWorkorder() throws Exception{
        String message =  "The following work order ${workorder.subject} has been closed by ${workorder.assignedBy.name}.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description:-}\nStatus : ${workorder.status.status}\n\nPlease view the closed work order here - ${workorder.url}\n\nRegards,\nTeam ${org.brand:-Facilio}.";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,"Work order closed");

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.requester.email");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.assignedBy.name");
        workflowExpressions.add("workorder.description");
        workflowExpressions.add("workorder.status.status");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParameters = new ArrayList<>();
        workflowParameters.addAll(workflowExpressions);
        workflowParameters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        JSONArray parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject","Work order closed");
        templateJson.put("to","${workorder.requester.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",workflowContext);

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.requester.id");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.assignedBy.name");
        pushWorkflowExpressions.add("workorder.description");
        pushWorkflowExpressions.add("workorder.status.status");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParameters = new ArrayList<>();
        pushWorkflowParameters.addAll(pushWorkflowExpressions);
        pushWorkflowParameters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        JSONArray pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        pushNotificationTemplateJson.put("id","${workorder.requester.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"The following work order ${workorder.subject} has been closed by ${workorder.assignedBy.name}.\\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nStatus : ${workorder.status.status}\\n\\nPlease view the closed work order here - ${workorder.url}\\n\\n - Team ${org.brand:-Facilio}.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"The following work order ${workorder.subject} has been closed by ${workorder.assignedBy.name}.\\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nStatus : ${workorder.status.status}\\n\\nPlease view the closed work order here - ${workorder.url}\\n\\n - Team ${org.brand:-Facilio}.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"id\":\"${workorder.requester.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","The following work order ${workorder.subject} has been closed by ${workorder.assignedBy.name}.\\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nStatus : ${workorder.status.status}\\n\\nPlease view the closed work order here - ${workorder.url}\\n\\n - Team ${org.brand:-Facilio}.");
        pushNotificationTemplateJson.put("subject","Work order closed");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.requester.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",pushWorkflowContext);

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Workorder closed");
        ruleContext.setName("Technician Closes Workorder");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteria("requester",2));
        ruleContext.setActions(actionContextList);

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
        chain.execute();
    }

    public static void addAssignTechTemplate() throws Exception{
        String message =  "${workorder.assignedBy.name} has assigned a new workorder to you.\\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nDue by : ${workorder.dueDateString:-}\\nPlease view the new workorder here - ${workorder.url}\\n\\nRegards,\\nTeam ${org.brand:-Facilio}.";
        String subject = "New Workorder Assigned";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignedTo.email");
        workflowExpressions.add("workorder.assignedBy.name");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.description");
        workflowExpressions.add("workorder.dueDateString");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParameters = new ArrayList<>();
        workflowParameters.addAll(workflowExpressions);
        workflowParameters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        JSONArray parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject",subject);
        templateJson.put("to","${workorder.assignedTo.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",workflowContext);

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.assignedTo.id");
        pushWorkflowExpressions.add("workorder.assignedBy.name");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.description");
        pushWorkflowExpressions.add("workorder.dueDateString");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParameters = new ArrayList<>();
        pushWorkflowParameters.addAll(pushWorkflowExpressions);
        pushWorkflowParameters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        JSONArray pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        pushNotificationTemplateJson.put("id","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("body","{\\\"name\\\":\\\"WORKORDER_PUSH_NOTIFICATION\\\",\\\"notification\\\":{\\\"content_available\\\":true,\\\"summary_id\\\":\\\"${workorder.id}\\\",\\\"sound\\\":\\\"default\\\",\\\"module_name\\\":\\\"workorder\\\",\\\"priority\\\":\\\"high\\\",\\\"text\\\":\\\"${workorder.assignedBy.name} has assigned a new workorder to you. \\\\n\\\\nSubject : ${workorder.subject}\\\\nDescription : \\\\n${workorder.description:-}\\\\nDue by : ${workorder.dueDateString:-}\\\\nPlease view the work order here - ${workorder.url}\\\\n\\\\n - Team ${org.brand:-Facilio}.\\\",\\\"click_action\\\":\\\"WORKORDER_SUMMARY\\\",\\\"title\\\":\\\"Assign Workorder\\\"},\\\"data\\\":{\\\"content_available\\\":true,\\\"summary_id\\\":\\\"${workorder.id}\\\",\\\"sound\\\":\\\"default\\\",\\\"module_name\\\":\\\"workorder\\\",\\\"priority\\\":\\\"high\\\",\\\"text\\\":\\\"${workorder.assignedBy.name} has assigned a new workorder to you. \\\\n\\\\nSubject : ${workorder.subject}\\\\nDescription : \\\\n${workorder.description:-}\\\\nDue by : ${workorder.dueDateString:-}\\\\nPlease view the work order here - ${workorder.url}\\\\n\\\\n - Team ${org.brand:-Facilio}.\\\",\\\"click_action\\\":\\\"WORKORDER_SUMMARY\\\",\\\"title\\\":\\\"Assign Workorder\\\"},\\\"id\\\":\\\"${workorder.assignedTo.id:-}\\\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","${workorder.assignedBy.name} has assigned a new workorder to you. \\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nDue by : ${workorder.dueDateString:-}\\nPlease view the work order here - ${workorder.url}\\n\\n - Team ${org.brand:-Facilio}.");
        pushNotificationTemplateJson.put("subject","Assign Workorder");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",pushWorkflowContext);

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Assign Workorder");
        ruleContext.setName("Assign Tech");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteria("assignedTo",2));
        ruleContext.setActions(actionContextList);

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
        chain.execute();
    }

    public static void addAssignTeamTemplate() throws Exception{
        String message = "${workorder.assignedBy.name} has assigned a new workorder to your team - ${workorder.assignmentGroup.name}. \\n\\nSubject : ${workorder.subject}\\nDescription : \\n${workorder.description:-}\\nDue by : ${workorder.dueDateString:-}\\nPlease view the work order here - ${workorder.url}\\n\\nRegards,\\nTeam ${org.brand:-Facilio}.";
        String subject = "New Workorder Assigned";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignmentGroup.email");
        workflowExpressions.add("workorder.assignedBy.name");
        workflowExpressions.add("workorder.assignmentGroup.name");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.description");
        workflowExpressions.add("workorder.dueDateString");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParameters = new ArrayList<>();
        workflowParameters.addAll(workflowExpressions);
        workflowParameters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        JSONArray parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject",subject);
        templateJson.put("to","${workorder.assignmentGroup.email:-}");
        templateJson.put("type",1);
        templateJson.put("sendAsSeparateMail",false);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",workflowContext);

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Assign Workorder");
        ruleContext.setName("Assign Team");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteriaForTeams());
        ruleContext.setActions(actionContextList);

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
        chain.execute();
    }

    public static void addCommentTemplate() throws Exception{
        String message = "${comment.createdBy.name} has  added a new comment in your workorder ${workorder.subject} . Please view the workorder here - ${workorder.url} to reply/view the comments.\\n\\nRegards,\\nTeam ${org.brand:-Facilio}.";
        String subject = "New Comment added";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignedTo.email");
        workflowExpressions.add("comment.createdBy.name");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParamters = new ArrayList<>();
        workflowParamters.addAll(workflowExpressions);
        workflowParamters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        JSONArray parameters = getParametersList(workflowParamters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject",subject);
        templateJson.put("to","${workorder.assignedTo.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",workflowContext);

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.assignedTo.id");
        pushWorkflowExpressions.add("comment.createdBy.name");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParameters = new ArrayList<>();
        pushWorkflowParameters.addAll(pushWorkflowExpressions);
        pushWorkflowParameters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        JSONArray pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        pushNotificationTemplateJson.put("id","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"${comment.createdBy.name} has  added a new comment in your workorder ${workorder.subject} . Please view the workorder here - ${workorder.url} to reply/view the comments.\\n - Team ${org.brand:-Facilio}.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"New Comment\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"${comment.createdBy.name} has  added a new comment in your workorder ${workorder.subject} . Please view the workorder here - ${workorder.url} to reply/view the comments.\\n - Team ${org.brand:-Facilio}.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"New Comment\"},\"id\":\"${workorder.assignedTo.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","${comment.createdBy.name} has  added a new comment in your workorder ${workorder.subject} . Please view the workorder here - ${workorder.url} to reply/view the comments.\\n - Team ${org.brand:-Facilio}.");
        pushNotificationTemplateJson.put("subject","New Comment");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",pushWorkflowContext);

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Comment added in Ticket");
        ruleContext.setName("Add Comment");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(null);
        ruleContext.setActivityType(EventType.ADD_TICKET_NOTE);
        ruleContext.setActions(actionContextList);

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
        chain.execute();
    }

    public static Criteria getCriteria(String fieldName, int operatorId){
        Criteria criteria = new Criteria();
        Condition condition = new Condition();
        Map<String, Condition> map = new HashMap<>();
        condition.setOperatorId(operatorId);
        condition.setFieldName(fieldName);
        map.put("1",condition);

        criteria.setConditions(map);
        criteria.setPattern("( 1 )");

        return criteria;
    }

    public static Criteria getCriteriaForTeams(){
        Criteria criteria = new Criteria();
        Map<String, Condition> map = new HashMap<>();

        Condition condition = new Condition();
        condition.setOperatorId(1);
        condition.setFieldName("assignedTo");
        map.put("1",condition);

        Condition condition1 = new Condition();
        condition1.setOperatorId(2);
        condition1.setFieldName("assignmentGroup");
        map.put("2",condition1);

        criteria.setConditions(map);
        criteria.setPattern("( 1 and 2 )");

        return criteria;
    }

    public static EMailStructure getEmailStructure(String message, boolean isDraft, boolean isHtml, String subject) throws Exception{
        EMailStructure emailStructure = new EMailStructure();
        emailStructure.setDraft(isDraft);
        emailStructure.setMessage(message);
        emailStructure.setHtml(isHtml);
        emailStructure.setSubject(subject);

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateEmailStructureChain();
        FacilioContext context1 = chain.getContext();
        context1.put(FacilioConstants.ContextNames.MODULE_NAME, "workorder");
        context1.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, emailStructure);

        chain.execute();

       return (EMailStructure) context1.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE);
    }

    public static JSONArray getExpressionList(List<String> expressionConstants){
        JSONArray expressions = new JSONArray();
        for(String name: expressionConstants){
            JSONObject expressionObj = new JSONObject();
            expressionObj.put("name",name);
            expressionObj.put("constant","${" + name + "}");
            expressions.add(expressionObj);
        }

        return expressions;
    }

    public static JSONArray getParametersList(List<String> parameterConstants){
        JSONArray parameters = new JSONArray();
        for(String name: parameterConstants){
            JSONObject expressionObj = new JSONObject();
            expressionObj.put("name",name);
            expressionObj.put("type","String");
            parameters.add(expressionObj);
        }

        return parameters;
    }
}
