package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEmailTemplateForWorkorder extends SignUpData {
    static FacilioModule workorderModule = null;
    @Override
    public void addData() throws Exception {
        workorderModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.WORK_ORDER);
        addTechnicianClosesWorkorder();
        addAssignTechTemplate();
        addAssignTeamTemplate();
        addCommentTemplate();
        addTechnicianResolvesWorkorder();
        //addTechnicianCloseWorkorder();
        addWorkorderOnHold();
    }

    public static WorkflowEventContext constructEvent(EventType activityType){
        WorkflowEventContext event = new WorkflowEventContext();
        event.setActivityType(activityType);
        event.setModule(workorderModule);
        event.setModuleName(workorderModule.getName());
        return event;
    }
    public static void addWorkorderOnHold() throws Exception{
        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignedBy.email");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.assignedTo.name");
        workflowExpressions.add("workorder.description");
        workflowExpressions.add("workorder.status.status");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParamters = new ArrayList<>();
        workflowParamters.addAll(workflowExpressions);
        workflowParamters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        List<ParameterContext> parameters = getParametersList(workflowParamters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p><span style=\"font-family: Arial\">Dear </span><span><span data-id=\"{workorder.assignedBy.name}\" class=\"mention\" data-type=\"mention\">${workorder.assignedBy.name}</span> </span><span style=\"font-family: Arial\">,  </span></p><p></p><p><span style=\"font-family: Arial\">This is to inform you that the Work Order with the ID </span><span><span data-id=\"{workorder.id}\" class=\"mention\" data-type=\"mention\">${workorder.id}</span>  - ${workorder.url} </span><span style=\"font-family: Arial\">has been paused. </span></p><p><span style=\"font-family: Arial\">Work Order: </span><span><span data-id=\"{workorder.subject}\" class=\"mention\" data-type=\"mention\">${workorder.subject}</span> </span></p>";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,"Your Work order has been paused",workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject","Work order put on hold");
        templateJson.put("to","${workorder.assignedBy.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.assignedTo.id");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.assignedTo.name");
        pushWorkflowExpressions.add("workorder.description");
        pushWorkflowExpressions.add("workorder.status.status");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParamters = new ArrayList<>();
        pushWorkflowParamters.addAll(pushWorkflowExpressions);
        pushWorkflowParamters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParamters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.assignedBy.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been paused. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order put on hold\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been paused. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order put on hold\"},\"id\":\"${workorder.assignedBy.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","Work Order has been paused. Please review the details and proceed accordingly");
        pushNotificationTemplateJson.put("subject","Work order put on hold");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedBy.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext rule = new WorkflowRuleContext();
        rule.setDescription("Workorder on hold");
        rule.setName("Workorder on hold");
        rule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        rule.setCriteria(getCriteria("assignedBy",2));
        rule.setModule(workorderModule);
        rule.setActivityType(EventType.HOLD_WORK_ORDER);
        rule.setActions(actionContextList);
        WorkflowEventContext event = constructEvent(EventType.HOLD_WORK_ORDER);
        rule.setEvent(event);

        addRule(rule);
    }


    public static void addTechnicianCloseWorkorder() throws Exception{

        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignedTo.email");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.assignedTo.name");
        workflowExpressions.add("workorder.description");
        workflowExpressions.add("workorder.status.status");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParamters = new ArrayList<>();
        workflowParamters.addAll(workflowExpressions);
        workflowParamters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        List<ParameterContext> parameters = getParametersList(workflowParamters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p>The following work order ${workorder.subject} has been closed by ${workorder.assignedTo.name}. </p><p></p><p>Subject : ${workorder.subject} </p><p>Description : ${workorder.description:-} </p><p>Status : ${workorder.status.status} </p><p></p><p>Please view the closed work order here - ${workorder.url} </p><p></p><p>Regards, </p><p>Team ${org.brand:-Facilio}.\"</p>";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,"Work order closed",workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();


        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject","Work order closed");
        templateJson.put("to","${workorder.assignedTo.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.assignedTo.id");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.assignedTo.name");
        pushWorkflowExpressions.add("workorder.description");
        pushWorkflowExpressions.add("workorder.status.status");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParamters = new ArrayList<>();
        pushWorkflowParamters.addAll(pushWorkflowExpressions);
        pushWorkflowParamters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParamters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been closed. Please review the details\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been closed. Please review the details\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"id\":\"${workorder.assignedTo.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","Work Order has been closed. Please review the details");
        pushNotificationTemplateJson.put("subject","Work order closed");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext rule = new WorkflowRuleContext();
        rule.setDescription("Workorder closed");
        rule.setName("Technician Closes Workorder");
        rule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        rule.setCriteria(getCriteria("assignedTo",2));
        rule.setActivityType(EventType.CLOSE_WORK_ORDER);
        rule.setActions(actionContextList);
        rule.setModule(workorderModule);
        WorkflowEventContext event = constructEvent(EventType.CLOSE_WORK_ORDER);
        rule.setEvent(event);

        addRule(rule);
    }

    public static void addTechnicianResolvesWorkorder() throws Exception{
        WorkflowContext workflowContext = new WorkflowContext();
        List<String> workflowExpressions = new ArrayList<>();
        workflowExpressions.add("org.domain");
        workflowExpressions.add("workorder.assignedBy.email");
        workflowExpressions.add("workorder.subject");
        workflowExpressions.add("workorder.url");
        workflowExpressions.add("org.brand");

        List<String> workflowParamters = new ArrayList<>();
        workflowParamters.addAll(workflowExpressions);
        workflowParamters.add("workorder");

        JSONArray expressions = getExpressionList(workflowExpressions);
        List<ParameterContext> parameters = getParametersList(workflowParamters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p><span style=\"font-family: Arial\">Dear </span><span><span data-id=\"{workorder.assignedBy.name}\" class=\"mention\" data-type=\"mention\">${workorder.assignedBy.name}</span> </span><span style=\"font-family: Arial\">,  </span></p><p></p><p><span style=\"font-family: Arial\">This is to inform you that the Work Order with the ID </span><span><span data-id=\"{workorder.id}\" class=\"mention\" data-type=\"mention\">${workorder.id}</span> - ${workorder.url} </span><span style=\"font-family: Arial\">has been resolved. </span></p><p><span style=\"font-family: Arial\">Work Order: </span><span><span data-id=\"{workorder.subject}\" class=\"mention\" data-type=\"mention\">${workorder.subject}</span> </span></p>";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,"Your workorder resolved",workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();


        templateJson.put("fromAddr",null);
        templateJson.put("ftl",false);
        templateJson.put("html",false);
        templateJson.put("isAttachmentAdded",false);
        templateJson.put("name","New WorkOrder Email Template");
        templateJson.put("message",message);
        templateJson.put("subject","Workorder resolved");
        templateJson.put("to","${workorder.assignedBy.email:-}");
        templateJson.put("type",1);
        templateJson.put("emailStructureId",eMailStructure.getId());
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowContext pushWorkflowContext = new WorkflowContext();
        List<String> pushWorkflowExpressions = new ArrayList<>();
        pushWorkflowExpressions.add("org.domain");
        pushWorkflowExpressions.add("workorder.assignedBy.id");
        pushWorkflowExpressions.add("workorder.subject");
        pushWorkflowExpressions.add("workorder.url");
        pushWorkflowExpressions.add("org.brand");
        pushWorkflowExpressions.add("workorder.id");

        List<String> pushWorkflowParamters = new ArrayList<>();
        pushWorkflowParamters.addAll(pushWorkflowExpressions);
        pushWorkflowParamters.add("workorder");

        JSONArray pushExpressions = getExpressionList(pushWorkflowExpressions);
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParamters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.assignedBy.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been resolved. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Workorder resolved\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been resolved. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Workorder resolved\"},\"id\":\"${workorder.assignedBy.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","Work Order has been resolved. Please review the details and proceed accordingly");
        pushNotificationTemplateJson.put("subject","Workorder resolved");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedBy.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext rule = new WorkflowRuleContext();
        rule.setDescription("Technician Resolves Workorder");
        rule.setName("Technician Resolved Workorder");
        rule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        rule.setActivityType(EventType.SOLVE_WORK_ORDER);
        rule.setActions(actionContextList);
        rule.setModule(workorderModule);
        WorkflowEventContext event = constructEvent(EventType.SOLVE_WORK_ORDER);
        rule.setEvent(event);

        addRule(rule);
    }

    public static void addTechnicianClosesWorkorder() throws Exception{

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
        List<ParameterContext> parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p><span style=\"font-family: Arial\">Dear </span><span data-id=\"{workorder.assignedBy.name}\" class=\"mention\" data-type=\"mention\">${workorder.assignedBy.name}</span> <span style=\"font-family: Arial\">, </span></p><p></p><p><span style=\"font-family: Arial\">This is to inform you that the Work Order with the ID </span><span>${workorder.id} </span><span style=\"font-family: Arial\">- ${workorder.url} has been successfully Closed.</span></p><p><span style=\"font-family: Arial\">Work Order: </span><span>${workorder.subject}</span></p>";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,"Your work order closed",workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();


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
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

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
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.requester.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been closed. Please review the details\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been closed. Please review the details\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Work order closed\"},\"id\":\"${workorder.assignedTo.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","Work Order has been closed. Please review the details");
        pushNotificationTemplateJson.put("subject","Work order closed");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.requester.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Workorder closed");
        ruleContext.setName("Technician Closes Workorder");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteria("requester",2));
        ruleContext.setActivityType(EventType.CLOSE_WORK_ORDER);
        ruleContext.setActions(actionContextList);
        ruleContext.setModule(workorderModule);
        WorkflowEventContext event = constructEvent(EventType.CLOSE_WORK_ORDER);
        ruleContext.setEvent(event);

        addRule(ruleContext);
    }

    public static void addAssignTechTemplate() throws Exception{
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
        List<ParameterContext> parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message =  "<p><span style=\"font-family: sans-serif\">Dear </span><span data-id=\"{workorder.assignedBy.name}\" class=\"mention\" data-type=\"mention\">${workorder.assignedBy.name}</span><span style=\"font-family: sans-serif\">, </span></p><p></p><p><span style=\"font-family: sans-serif\">You've been assigned for a Work Order - ${workorder.url} </span></p><p><span style=\"font-family: sans-serif\">Work Order: </span><span data-id=\"{workorder.subject}\" class=\"mention\" data-type=\"mention\">${workorder.subject}</span> </p><p>Description: <span data-id=\"{workorder.description}\" class=\"mention\" data-type=\"mention\">${workorder.description}</span> </p><p></p>";
        String subject = "New Workorder Assigned";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject,workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();


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
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

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
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been assigned to you. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Assign Workorder\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"Work Order has been assigned to you. Please review the details and proceed accordingly\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"Assign Workorder\"},\"id\":\"${workorder.assignedBy.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","Work Order has been assigned to you. Please review the details and proceed accordingly");
        pushNotificationTemplateJson.put("subject","Assign Workorder");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

        pushNotificationAction.setActionType(7);
        pushNotificationAction.setTemplateJson(pushNotificationTemplateJson);
        actionContextList.add(pushNotificationAction);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Assign Workorder");
        ruleContext.setName("Assign Tech");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteria("assignedTo",2));
        ruleContext.setActions(actionContextList);
        ruleContext.setModule(workorderModule);
        ruleContext.setActivityType(EventType.ASSIGN_TICKET);
        WorkflowEventContext event = constructEvent(EventType.ASSIGN_TICKET);
        ruleContext.setEvent(event);

        addRule(ruleContext);
    }

    public static void addAssignTeamTemplate() throws Exception{

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
        List<ParameterContext> parameters = getParametersList(workflowParameters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p><span style=\"font-family: sans-serif\">Dear Team, <br><br>You've been assigned for a Work Order - ${workorder.url} <br>Work Order: <span data-id=\"{workorder.subject}\" class=\"mention\" data-type=\"mention\">${workorder.subject}</span> </span></p><p><span style=\"font-family: sans-serif\">Description: <span data-id=\"{workorder.description}\" class=\"mention\" data-type=\"mention\">${workorder.description}</span> </span></p><p></p>";
        String subject = "New Workorder Assigned";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject,workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

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
        templateJson.put("workflow",FieldUtil.getAsProperties(workflowContext));

        action.setActionType(3);
        action.setTemplateJson(templateJson);
        actionContextList.add(action);

        WorkflowRuleContext ruleContext = new WorkflowRuleContext();
        ruleContext.setDescription("Assign Workorder");
        ruleContext.setName("Assign Team");
        ruleContext.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        ruleContext.setCriteria(getCriteriaForTeams());
        ruleContext.setActions(actionContextList);
        ruleContext.setModule(workorderModule);
        ruleContext.setActivityType(EventType.ASSIGN_TICKET);
        WorkflowEventContext event = constructEvent(EventType.ASSIGN_TICKET);
        ruleContext.setEvent(event);
        addRule(ruleContext);
    }

    public static void addCommentTemplate() throws Exception{

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
        List<ParameterContext> parameters = getParametersList(workflowParamters);

        workflowContext.setExpressions(expressions);
        workflowContext.setParameters(parameters);

        String message = "<p><span style=\"font-family: Arial\">Dear </span><span>${workorder.assignedBy.name}</span> ,</p><p></p><p><span style=\"font-family: Arial\">${comment.createdBy.name} has added a new comment to your workorder titled '${workorder.subject}'. You can view and reply to the comments by accessing the work order through the following link: ${workorder.url}. </span></p><p></p>";
        String subject = "New Comment added";
        EMailStructure eMailStructure = getEmailStructure(message,false,false,subject,workflowContext);

        List<ActionContext> actionContextList =  new ArrayList<>();
        ActionContext action = new ActionContext();
        JSONObject templateJson = new JSONObject();

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
        templateJson.put("workflow", FieldUtil.getAsProperties(workflowContext));

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
        List<ParameterContext> pushParameters = getParametersList(pushWorkflowParameters);

        pushWorkflowContext.setExpressions(pushExpressions);
        pushWorkflowContext.setParameters(pushParameters);

        ActionContext pushNotificationAction = new ActionContext();
        JSONObject pushNotificationTemplateJson = new JSONObject();
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        pushNotificationTemplateJson.put("application", app.getId());
        pushNotificationTemplateJson.put("id","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("body","{\"name\":\"WORKORDER_PUSH_NOTIFICATION\",\"notification\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"${comment.createdBy.name} has added a new comment to your workorder '${workorder.subject}'.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"New Comment\"},\"data\":{\"content_available\":true,\"summary_id\":\"${workorder.id}\",\"sound\":\"default\",\"module_name\":\"workorder\",\"priority\":\"high\",\"text\":\"${comment.createdBy.name} has added a new comment to your workorder '${workorder.subject}'.\",\"click_action\":\"WORKORDER_SUMMARY\",\"title\":\"New Comment\"},\"id\":\"${workorder.assignedTo.id:-}\"}");
        pushNotificationTemplateJson.put("isPushNotification",false);
        pushNotificationTemplateJson.put("isSendNotification",false);
        pushNotificationTemplateJson.put("message","${comment.createdBy.name} has added a new comment to your workorder '${workorder.subject}'.");
        pushNotificationTemplateJson.put("subject","New Comment");
        pushNotificationTemplateJson.put("name","New WorkOrder Push Notification Template");
        pushNotificationTemplateJson.put("to","${workorder.assignedTo.id:-}");
        pushNotificationTemplateJson.put("type", Template.Type.PUSH_NOTIFICATION);
        pushNotificationTemplateJson.put("userWorkflow", null);
        pushNotificationTemplateJson.put("workflow",FieldUtil.getAsProperties(pushWorkflowContext));

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
        ruleContext.setActivityType(EventType.ADD_TICKET_NOTE);
        ruleContext.setModule(workorderModule);
        WorkflowEventContext event = constructEvent(EventType.ADD_TICKET_NOTE);
        ruleContext.setEvent(event);

        addRule(ruleContext);
    }

    public static void addRule(WorkflowRuleContext rule) throws Exception{
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
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

    public static EMailStructure getEmailStructure(String message, boolean isDraft, boolean isHtml, String subject,WorkflowContext workflow) throws Exception{
        EMailStructure emailStructure = new EMailStructure();
        emailStructure.setDraft(isDraft);
        emailStructure.setMessage(message);
        emailStructure.setHtml(isHtml);
        emailStructure.setSubject(subject);
        emailStructure.setName(subject);
        emailStructure.setWorkflow(workflow);

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

    public static List<ParameterContext> getParametersList(List<String> parameterConstants){
        List<ParameterContext> parameters = new ArrayList<>();
        for(String name: parameterConstants){
           ParameterContext param = new ParameterContext();
            param.setName(name);
            param.setTypeString("String");
            parameters.add(param);
        }

        return parameters;
    }
}
