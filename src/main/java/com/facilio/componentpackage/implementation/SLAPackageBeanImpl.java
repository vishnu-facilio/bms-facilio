package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.xml.builder.XMLBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

@Log4j
public class SLAPackageBeanImpl implements PackageBean<WorkflowRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.SLA_POLICY_RULE);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.SLA_POLICY_RULE, ids);
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext slaRule, XMLBuilder element) throws Exception {
        element.element(PackageConstants.NAME).text(slaRule.getName());
        element.element(PackageConstants.DESCRIPTION).text(slaRule.getDescription());
        element.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(slaRule.getStatus()));
        element.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                .text(slaRule.getActivityTypeEnum() != null ? slaRule.getActivityTypeEnum().name() : null);
        element.element(PackageConstants.MODULENAME).text(slaRule.getModuleName());
        element.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(slaRule.getRuleTypeEnum().name());
        Criteria commitmentCriteria = new Criteria();
        commitmentCriteria.addAndCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", String.valueOf(slaRule.getId()), NumberOperators.EQUALS));
        List<WorkflowRuleContext> commitments = WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE, true, commitmentCriteria, null, null);
        XMLBuilder commitmentsBuilder = element.element(PackageConstants.WorkFlowRuleConstants.COMMITMENTS);

        Criteria slaCriteria = slaRule.getCriteria();
        if (slaCriteria != null){
                LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + slaRule.getModuleName() + " SLA Rule - " + slaRule.getName());
                element.addElement(PackageBeanUtil.constructBuilderFromCriteria(slaCriteria, element.element(PackageConstants.CriteriaConstants.CRITERIA), slaRule.getModuleName()));
        }

        if (CollectionUtils.isNotEmpty(commitments)) {

            for (WorkflowRuleContext slaWorkflowCommitment : commitments) {
                XMLBuilder commitmentBuilder = commitmentsBuilder.element(PackageConstants.WorkFlowRuleConstants.COMMITMENT);
                SLAWorkflowCommitmentRuleContext commitment = (SLAWorkflowCommitmentRuleContext) slaWorkflowCommitment;
                Criteria criteria = commitment.getCriteria();
                commitmentBuilder.element(PackageConstants.MODULENAME).text(commitment.getModuleName());
                commitmentBuilder.element(PackageConstants.NAME).text(commitment.getName());
                commitmentBuilder.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                        .text(commitment.getActivityTypeEnum() != null ? commitment.getActivityTypeEnum().name() : null);
                commitmentBuilder.element(PackageConstants.WorkFlowRuleConstants.PARENT_RULE).text(slaRule.getName());
                commitmentBuilder.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(commitment.getRuleTypeEnum().name());
                if (criteria != null) {
                    LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + commitment.getModuleName() + " SLA Rule - " + slaRule.getName() + " Commitment - " + commitment.getName());
                    commitmentBuilder.addElement(PackageBeanUtil.constructBuilderFromCriteria(criteria, element.element(PackageConstants.CriteriaConstants.CRITERIA), commitment.getModuleName()));
                }
                XMLBuilder entitiesBuilder = commitmentBuilder.element(PackageConstants.WorkFlowRuleConstants.ENTITIES);
                List<SLAWorkflowCommitmentRuleContext.SLAEntityDuration> slaEntities = commitment.getSlaEntities();
                for (SLAWorkflowCommitmentRuleContext.SLAEntityDuration entity : slaEntities) {
                    XMLBuilder entityBuilder = entitiesBuilder.element("Entity");
                    if (entity.getAddDuration() > 0) {
                        entityBuilder.element("AddDuration").text(String.valueOf(entity.getAddDuration()));
                    }
                    if (StringUtils.isNotEmpty(entity.getDurationPlaceHolder())) {
                        entityBuilder.element("DurationPlaceHolder").text(entity.getDurationPlaceHolder());
                    }
                    entityBuilder.element("SlaCommitmentName").text(commitment.getName());
                    long entityId = entity.getSlaEntityId();
                    SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(entityId);
                    entityBuilder.element("SlaEntityName").text(slaEntity.getName());
                    entityBuilder.element("Type").text(String.valueOf(entity.getType()));
                    entityBuilder.element("TypeOrDefault").text(String.valueOf(entity.getTypeOrDefault()));
                }
            }
        }

        XMLBuilder escalationsBuilder = element.element(PackageConstants.WorkFlowRuleConstants.ESCALATIONS);
        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = ((SLAPolicyContext) slaRule).getEscalations();
        if (CollectionUtils.isNotEmpty(escalations)) {
            for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : escalations) {
                XMLBuilder escalationBuilder = escalationsBuilder.element("Escalation");
                long entityId = escalation.getSlaEntityId();
                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(entityId);
                escalationBuilder.element("SlaEntityName").text(slaEntity.getName());
                List<SLAWorkflowEscalationContext> levels = escalation.getLevels();
                XMLBuilder levelsBuilder = escalationBuilder.element(PackageConstants.WorkFlowRuleConstants.LEVELS);
                for (SLAWorkflowEscalationContext level : levels) {
                    XMLBuilder levelBuilder = levelsBuilder.element("Level");
                    levelBuilder.element("Interval").text(String.valueOf(level.getInterval()));
                    levelBuilder.element("SlaEntityName").text(slaEntity.getName());
                    levelBuilder.element("SLAPolicyName").text(String.valueOf(slaRule.getName()));
                    levelBuilder.element("type").text(String.valueOf(level.getType()));
                    levelBuilder.element("typeEnum").text(String.valueOf(level.getTypeEnum()));
                    if (CollectionUtils.isNotEmpty(level.getActions())) {
                        constructBuilderFromSLAActionsList(slaRule.getName(), slaEntity.getName(), level.getActions(), levelBuilder.element(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST));
                    }
                }
            }
        }


    }

    public static void constructBuilderFromSLAActionsList(String slaRuleName, String slaEntityName, List<ActionContext> actionContextList, XMLBuilder actionElements) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();

        for (ActionContext actionContext : actionContextList) {
            XMLBuilder actionElement = actionElements.element(PackageConstants.WorkFlowRuleConstants.ACTION);

            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_STATUS).text(String.valueOf(actionContext.isActive()));
            actionElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_TYPE).text(actionContext.getActionTypeEnum().name());

            if (actionContext.getTemplateJson() != null) {
                JSONObject templateJson = actionContext.getTemplateJson();
                XMLBuilder templateElement = actionElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE);
                templateElement.element(PackageConstants.WorkFlowRuleConstants.IS_FTL).text(String.valueOf(templateJson.get("ftl")));
                if (templateJson.get("workflow") != null) {
                    XMLBuilder workflow = templateElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW);

                    Object workflowJson = templateJson.get("workflow");
                    ObjectMapper objectMapper = new ObjectMapper();
                    String workflowString = objectMapper.writeValueAsString(workflowJson);
                    org.json.JSONObject jsonWorkflow = new org.json.JSONObject(workflowString);
                    org.json.JSONArray expressionArray = jsonWorkflow.getJSONArray("expressions");
                    XMLBuilder expressionsElement = workflow.element(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS);
                    for (Object obj : expressionArray){
                        XMLBuilder expressionElement = expressionsElement.element("Expression");
                        org.json.JSONObject expressionObj = (org.json.JSONObject) obj;
                        String constant = expressionObj.opt("constant") != null ?  expressionObj.get("constant").toString() : null;
                        String name = expressionObj.opt("name") != null ? expressionObj.get("name").toString() : null;
                        if (StringUtils.isNotEmpty(constant)){
                            expressionElement.element("Constant").text(constant);
                        }
                        if (StringUtils.isNotEmpty(name)){
                            expressionElement.element("Name").text(name);
                        }

                        String aggregateString = expressionObj.opt("aggregateString") != null ?  expressionObj.get("aggregateString").toString() : null;
                        String fieldName = expressionObj.opt("fieldName") != null ? expressionObj.get("fieldName").toString() : null;
                        String moduleName = expressionObj.opt("moduleName") != null ?  expressionObj.get("moduleName").toString() : null;
                        org.json.JSONObject criteria = expressionObj.opt("criteria") != null ? new org.json.JSONObject(expressionObj.get("criteria").toString()) : null;
                        JSONObject expressionSimple = new JSONObject();
                        if (criteria != null) {
                            for (String key : criteria.keySet()) {
                                expressionSimple.put(key, criteria.get(key));
                            }
                            Criteria expresionCriteria = FieldUtil.getAsBeanFromJson(expressionSimple, Criteria.class);
                            LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + moduleName + " SLA Rule - " + slaRuleName + " SlaEntityName - " + slaEntityName);
                            expressionElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(expresionCriteria, actionElements.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                            Map<String, Condition> conditions = expresionCriteria.getConditions();
                            XMLBuilder ouidListElement = expressionElement.element("OuidList");
                            for (String key : conditions.keySet()) {
                                Condition condition = conditions.get(key);
                                if (condition.getFieldName().equals("ouid")){
                                    String[] ouids = condition.getValue().trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
                                    for (String str : ouids) {
                                        XMLBuilder ouid = ouidListElement.element("Ouid");
                                        Long ouidLong = Long.parseLong(str);
                                        PackageBeanUtil.userXMLBuilder(ouid, ouidLong);
                                    }
                                }
                            }

                        }
                        if (StringUtils.isNotEmpty(aggregateString)){
                            expressionElement.element("AggregateString").text(aggregateString);
                        }
                        if (StringUtils.isNotEmpty(fieldName)){
                            expressionElement.element("FieldName").text(fieldName);
                        }
                        if (StringUtils.isNotEmpty(moduleName)){
                            expressionElement.element("ModuleName").text(moduleName);
                        }
                    }

                    XMLBuilder parameterBuilder = workflow.element(PackageConstants.WorkFlowRuleConstants.PARAMETERS);
                    List<Map<String, Object>> parameterMap = (List<Map<String, Object>>) ((HashMap) templateJson.get("workflow")).get("parameters");
                    List<ParameterContext> parameterList = FieldUtil.getAsBeanListFromMapList(parameterMap, ParameterContext.class);
                    for (ParameterContext parameter : parameterList) {
                        XMLBuilder parameters = parameterBuilder.element("parameter");
                        parameters.element("name").text(parameter.getName());
                        parameters.element("typeString").text(parameter.getTypeString());
                        if (parameter.getValue() != null) {
                            parameters.element("value").text(String.valueOf(parameter.getValue()));
                        }
                        parameters.element("workflowFieldType").text(String.valueOf(parameter.getWorkflowFieldType()));
                    }
                    if (((HashMap) templateJson.get("workflow")).get("workflowString") != null) {
                        workflow.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(((HashMap) templateJson.get("workflow")).get("workflowString").toString());
                    }
                }

                if (templateJson.get("userWorkflow") != null) {
                    XMLBuilder workFlowElement = templateElement.element(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW);
                    workFlowElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(((HashMap) templateJson.get("userWorkflow")).get("isV2Script")));
                    workFlowElement.element("workflowV2String").cData(String.valueOf(((HashMap) templateJson.get("userWorkflow")).get("workflowV2String")));
                }

                XMLBuilder templateJsonElement = templateElement.element(PackageConstants.WorkFlowRuleConstants.TEMPLATE_JSON);

                switch (actionContext.getActionTypeEnum()) {

                    case CHANGE_STATE:
                        String newStateId = String.valueOf(templateJson.get("new_state"));
                        long ticketStatusId = StringUtils.isNotEmpty(newStateId) ? Long.parseLong(newStateId) : -1;
                        FacilioStatus ticketStatus = ticketStatusId != -1 ? TicketAPI.getStatus(ticketStatusId) : null;
                        if (ticketStatus != null) {
                            FacilioModule parentModule = moduleBean.getModule(ticketStatus.getParentModuleId());
                            XMLBuilder newStateElement = templateJsonElement.element(PackageConstants.VALUE_ELEMENT);
                            newStateElement.element(PackageConstants.WorkFlowRuleConstants.STATUS_NAME).text(ticketStatus.getStatus());
                            newStateElement.element(PackageConstants.WorkFlowRuleConstants.PARENT_MODULE_NAME).text(parentModule.getName());
                        }
                        break;

                    case FIELD_CHANGE:
                        for (Object key : templateJson.keySet()) {
                            Object value = templateJson.get(key);
                            XMLBuilder valueElement = templateJsonElement.element(PackageConstants.VALUE_ELEMENT);
                            valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_NAME).text(String.valueOf(key));
                            if (value instanceof JSONObject) {
                                valueElement.attribute(PackageConstants.WorkFlowRuleConstants.CONTAINS_RECORD_ID_MAP, Boolean.TRUE.toString());
                                XMLBuilder actionFieldValueElement = valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE);
                                actionFieldValueElement.element("id").text(String.valueOf(((JSONObject) value).get("id")));
                            } else {
                                valueElement.element(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE).text(String.valueOf(value));
                            }
                        }
                        break;

                    case WORKFLOW_ACTION:
                        if (templateJson.containsKey("workflowContext")) {
                            XMLBuilder workFlowElement = templateJsonElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT);
                            JSONObject workFlowContext = (JSONObject) templateJson.get("workflowContext");
                            if (workFlowContext.containsKey("isV2Script")) {
                                workFlowElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(workFlowContext.get("isV2Script")));
                            }
                            if (workFlowContext.containsKey("workflowV2String")) {
                                workFlowElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(String.valueOf(workFlowContext.get("workflowV2String")));
                            }
                        }
                        break;

                    case BULK_EMAIL_NOTIFICATION:
                        XMLBuilder emailElement = templateJsonElement.element(PackageConstants.VALUE_ELEMENT);
                        Integer emailStructureId = (Integer) templateJson.get("emailStructureId");
                        EMailStructure eMailTemplate = new EMailStructure();
                        if (emailStructureId != null) {
                             eMailTemplate = (EMailStructure) TemplateAPI.getTemplate(emailStructureId.longValue());
                        }
                        String emailStructureName = eMailTemplate != null ? eMailTemplate.getName() : null;
                        emailElement.element(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_NAME).text(emailStructureName);
                        emailElement.element("name").text(String.valueOf(templateJson.get("name")));
                        emailElement.element("bcc").text(String.valueOf(templateJson.get("bcc")));
                        emailElement.element("cc").text(String.valueOf(templateJson.get("cc")));
                        emailElement.element("html").text(String.valueOf(templateJson.get("html")));
                        if (eMailTemplate != null) {
                            emailElement.element("message").cData(String.valueOf(eMailTemplate.getMessage()));
                            emailElement.element("subject").text(eMailTemplate.getSubject());
                        } else {
                            emailElement.element("message").cData(String.valueOf(templateJson.get("message")));
                            emailElement.element("subject").text(String.valueOf(templateJson.get("subject")));
                        }
                        emailElement.element("sendAsSeparateMail").text(String.valueOf(templateJson.get("sendAsSeparateMail")));
                        emailElement.element("sender").text(String.valueOf(templateJson.get("sender")));
                        emailElement.element("to").text(String.valueOf(templateJson.get("to")));
                        break;

                    case PUSH_NOTIFICATION:
                        XMLBuilder pushNotificationElement = templateJsonElement.element(PackageConstants.VALUE_ELEMENT);
                        Integer appId = (Integer) templateJson.get("application");
                        String appLinkName = appId > 0 ? ApplicationApi.getApplicationForId(appId.longValue()).getLinkName() : null;
                        pushNotificationElement.element("name").text(String.valueOf(templateJson.get("name")));
                        JSONObject obj = new JSONObject();
                        if (templateJson.get("body") != null) {
                            JSONParser parser = new JSONParser();
                            String body = templateJson.get("body").toString();
                            Boolean isSendNotification = Boolean.parseBoolean(templateJson.get("isSendNotification").toString());
                            Long application = Long.parseLong(templateJson.get("application").toString());
                            try {
                                obj = (JSONObject) parser.parse(body);
                                obj.put("isSendNotification", isSendNotification);
                                obj.put("application", application);
                            } catch (ParseException e) {

                            }
                        }
                        UserNotificationContext userNotification = UserNotificationContext.instance(obj);
                        XMLBuilder bodyBuilder = pushNotificationElement.element("data");
                        if (userNotification.getExtraParams() != null) {
                            String clickAction = userNotification.getExtraParams().get("click_action") != null ? userNotification.getExtraParams().get("click_action").toString() : null;
                            bodyBuilder.element("click_action").text(clickAction);
                            String contentAvailable = userNotification.getExtraParams().get("content_available") != null ? userNotification.getExtraParams().get("content_available").toString() : null;
                            bodyBuilder.element("content_available").text(contentAvailable);
                            String priority = userNotification.getExtraParams().get("priority") != null ? userNotification.getExtraParams().get("priority").toString() : null;
                            bodyBuilder.element("priority").text(priority);
                            String sound = userNotification.getExtraParams().get("sound") != null ? userNotification.getExtraParams().get("sound").toString() : null;
                            bodyBuilder.element("sound").text(sound);
                            String summaryId = userNotification.getExtraParams().get("summary_id") != null ? userNotification.getExtraParams().get("summary_id").toString() : null;
                            bodyBuilder.element("summary_id").text(summaryId);
                        }

                        bodyBuilder.element("text").text(userNotification.getSubject());
                        bodyBuilder.element("title").text(userNotification.getTitle());
                        String moduleName = (String) ((JSONObject) obj.get("notification")).get("module_name");
                        long parentModuleId = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName).getModuleId() : moduleBean.getModule(FacilioConstants.ContextNames.USER_NOTIFICATION).getModuleId();
                        userNotification.setParentModule(parentModuleId);
                        FacilioModule parentModule = moduleBean.getModule(parentModuleId);
                        bodyBuilder.element("module_name").text(parentModule.getName());
                        JSONObject structureObj = UserNotificationContext.getFcmObjectMaintainence(userNotification);
                        pushNotificationElement.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(appLinkName);
                        pushNotificationElement.element("id").text((String) templateJson.get("to"));
                        pushNotificationElement.element("isSendNotification").text(String.valueOf(templateJson.get("isPushNotification")));
                        pushNotificationElement.element("name").text(String.valueOf(templateJson.get("name")));
                        pushNotificationElement.element("data").text(structureObj.get("data").toString());
                        pushNotificationElement.element("notification").text(structureObj.get("notification").toString());
                        pushNotificationElement.element("body").text(structureObj.toJSONString());
                        break;

                    default:
                        break;

                }
            }
        }
    }

    public static List<ActionContext> constructSLAActionContextsFromBuilder(XMLBuilder actionsList) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        List<ActionContext> actionContextList = new ArrayList<>();

        if (actionsList.getElement(PackageConstants.WorkFlowRuleConstants.ACTION) != null) {

            List<XMLBuilder> actionsListElementList = actionsList.getElementList(PackageConstants.WorkFlowRuleConstants.ACTION);
            for (XMLBuilder actionElement : actionsListElementList) {

                String actionTypeStr = actionElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_TYPE).getText();
                ActionType actionType = StringUtils.isNotEmpty(actionTypeStr) ? ActionType.valueOf(actionTypeStr) : null;

                ActionContext actionContext = new ActionContext();
                actionContext.setActionType(actionType);
                actionContextList.add(actionContext);

                XMLBuilder templateElement = actionElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE);
                if (templateElement != null) {
                    if (templateElement != null) {
                        boolean isFtl = Boolean.parseBoolean(templateElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_FTL).getText());
                        WorkflowContext userWorkflowContext = new WorkflowContext();
                        if (templateElement.getElement(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW) != null) {
                            XMLBuilder userWorkflowElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.USER_WORKFLOW);
                            userWorkflowContext.setIsV2Script(Boolean.parseBoolean(userWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText()));
                            userWorkflowContext.setWorkflowV2String(userWorkflowElement.getElement("workflowV2String").getCData());
                        }
                        XMLBuilder emailWorkflowElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW);
                        WorkflowContext workflowParamExp = new WorkflowContext();
                        if (emailWorkflowElement != null) {
                            XMLBuilder expressionsBuilder = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.EXPRESSIONS);
                            List<XMLBuilder> expressionBuilder = expressionsBuilder.getElementList("Expression");
                            JSONArray expressionArray = new JSONArray();

                            for (XMLBuilder expression : expressionBuilder){
                                Map<String,Object> expressionProps = new HashMap<>();
                                if (expression.getElement("Constant") != null){
                                    expressionProps.put("constant",expression.getElement("Constant").getText());
                                }
                                if (expression.getElement("Name") != null){
                                    expressionProps.put("name",expression.getElement("Name").getText());
                                }
                                if (expression.getElement("AggregateString") != null){
                                    expressionProps.put("aggregateString",expression.getElement("AggregateString").getText());
                                }
                                if (expression.getElement("FieldName") != null){
                                    expressionProps.put("fieldName",expression.getElement("FieldName").getText());
                                }
                                if (expression.getElement("ModuleName") != null){
                                    expressionProps.put("moduleName",expression.getElement("ModuleName").getText());
                                }
                                XMLBuilder criteriaElement = expression.getElement(PackageConstants.CriteriaConstants.CRITERIA);
                                if (criteriaElement != null){
                                    Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                                    XMLBuilder ouidList = expression.getElement("OuidList");
                                    List<XMLBuilder> ouids = ouidList.getElementList("Ouid");
                                    List<Long> ouIds = new ArrayList<>();
                                    for (XMLBuilder ouid : ouids){
                                        ouIds.add(PackageBeanUtil.userValueBuilder(ouid));
                                    }
                                    String ouidString = StringUtils.join(ouIds,',');
                                    for (String key : criteria.getConditions().keySet()) {
                                        Condition condition = criteria.getConditions().get(key);
                                        if (condition.getFieldName().equals("ouid")){
                                            condition.setValue(ouidString);
                                        }
                                    }
                                    expressionProps.put("criteria",criteria);
                                }
                                expressionArray.add(expressionProps);
                            }

                            workflowParamExp.setExpressions(expressionArray);

                            XMLBuilder parameterBuilder = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.PARAMETERS);
                            List<ParameterContext> parameterContexts = new ArrayList<>();
                            if (parameterBuilder != null) {
                                List<XMLBuilder> paramBuilderList = parameterBuilder.getElementList("parameter");
                                for (XMLBuilder parameter : paramBuilderList) {
                                    ParameterContext param = new ParameterContext();
                                    String name = parameter.getElement("name").getText();
                                    String typeString = parameter.getElement("typeString").getText();
                                    String value = null;
                                    if (parameter.getElement("value") != null) {
                                        value = parameter.getElement("value").getText();
                                    }
                                    WorkflowFieldType workflowFieldType = WorkflowFieldType.valueOf(parameter.getElement("workflowFieldType").getText());
                                    param.setName(name);
                                    param.setTypeString(typeString);
                                    param.setValue(value);
                                    param.setWorkflowFieldType(workflowFieldType);

                                    parameterContexts.add(param);
                                }
                            }
                            workflowParamExp.setParameters(parameterContexts);
                            if (emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING) != null) {
                                String workflowString = emailWorkflowElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
                                workflowParamExp.setWorkflowString(workflowString);
                            }
                        }

                        XMLBuilder templateJsonElement = templateElement.getElement(PackageConstants.WorkFlowRuleConstants.TEMPLATE_JSON);
                        List<XMLBuilder> allValueElements = templateJsonElement.getElementList(PackageConstants.VALUE_ELEMENT);
                        JSONObject templateJson = new JSONObject();

                        switch (actionType) {

                            case CHANGE_STATE:
                                for (XMLBuilder valueElement : allValueElements) {
                                    String statusName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.STATUS_NAME).getText();
                                    String parentModuleName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.PARENT_MODULE_NAME).getText();
                                    FacilioModule parentModule = moduleBean.getModule(parentModuleName);
                                    if (parentModule != null) {
                                        FacilioStatus ticketStatus = TicketAPI.getStatus(parentModule, statusName);
                                        if (ticketStatus != null) {
                                            templateJson.put("new_state", ticketStatus.getId());
                                        }
                                    }
                                }
                                break;

                            case FIELD_CHANGE:
                                ArrayList<Map<String, Object>> fieldMatcher = new ArrayList<>();
                                for (XMLBuilder valueElement : allValueElements) {
                                    Map<String, Object> fieldChangeMap = new HashMap<>();
                                    boolean containsRecordIdMap = Boolean.parseBoolean(valueElement.getAttribute(PackageConstants.WorkFlowRuleConstants.CONTAINS_RECORD_ID_MAP));
                                    String actionFieldName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_NAME).getText();
                                    XMLBuilder actionValueElement = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.ACTION_FIELD_VALUE);
                                    if (containsRecordIdMap) {
                                        String id = actionValueElement.getElement("id").getText();
                                        JSONObject idObject = new JSONObject();
                                        idObject.put("id", id);

                                        fieldChangeMap.put("value", idObject);
                                    } else {
                                        String actionValue = actionValueElement.getText();
                                        fieldChangeMap.put("value", actionValue);
                                    }
                                    fieldChangeMap.put("field", actionFieldName);
                                    fieldMatcher.add(fieldChangeMap);
                                }
                                templateJson.put("fieldMatcher", fieldMatcher);
                                break;

                            case WORKFLOW_ACTION:
                                JSONObject resultWorkflowContext = new JSONObject();
                                XMLBuilder workFlowElement = templateJsonElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_CONTEXT);
                                if (workFlowElement != null) {
                                    boolean isV2Script = Boolean.parseBoolean(workFlowElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
                                    String workflowV2String = workFlowElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
                                    resultWorkflowContext.put("isV2Script", isV2Script);
                                    resultWorkflowContext.put("workflowV2String", workflowV2String);
                                }
                                templateJson.put("resultWorkflowContext", resultWorkflowContext);
                                break;

                            case BULK_EMAIL_NOTIFICATION:
                                List<XMLBuilder> valueElements = templateJsonElement.getElementList(PackageConstants.VALUE_ELEMENT);

                                for (XMLBuilder valueElement : valueElements) {
                                    String emailStructureName = valueElement.getElement(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_NAME).getText();
                                    if(emailStructureName != null && !emailStructureName.isEmpty()){
                                        Template emailStructureTemplate = TemplateAPI.getTemplate(emailStructureName, Template.Type.EMAIL_STRUCTURE);
                                        if (emailStructureTemplate != null) {
                                            templateJson.put(PackageConstants.WorkFlowRuleConstants.EMAIL_STRUCTURE_ID, emailStructureTemplate.getId());
                                        }
                                    }
                                    String templateName = valueElement.getElement("name").getText();
                                    templateJson.put("message", valueElement.getElement("message").getCData());
                                    templateJson.put("name", templateName);
                                    templateJson.put("subject", valueElement.getElement("subject").getText());
                                    templateJson.put("ftl", isFtl);
                                    templateJson.put("sendAsSeparateMail", Boolean.parseBoolean(valueElement.getElement("sendAsSeparateMail").getText()));
                                    templateJson.put("to", valueElement.getElement("to").getText());
                                    templateJson.put("type", Template.Type.EMAIL);
                                    templateJson.put("userWorkflow", FieldUtil.getAsProperties(userWorkflowContext));
                                    templateJson.put("workflow", FieldUtil.getAsProperties(workflowParamExp));
                                }
                                break;

                            case PUSH_NOTIFICATION:
                                List<XMLBuilder> pushNotificationValues = templateJsonElement.getElementList(PackageConstants.VALUE_ELEMENT);

                                for (XMLBuilder valueElement : pushNotificationValues) {
                                    String templateName = valueElement.getElement("name").getText();
                                    String appLinkName = valueElement.getElement(PackageConstants.AppXMLConstants.APP_LINK_NAME).getText();
                                    long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                                    JSONObject dataObj = new JSONObject();
                                    XMLBuilder dataBuilder = valueElement.getElement("data");
                                    dataObj.put("click_action", dataBuilder.getElement("click_action").getText());
                                    dataObj.put("content_available", dataBuilder.getElement("content_available").getText());
                                    dataObj.put("module_name", dataBuilder.getElement("module_name").getText());
                                    dataObj.put("priority", dataBuilder.getElement("priority").getText());
                                    dataObj.put("sound", dataBuilder.getElement("sound").getText());
                                    dataObj.put("summary_id", dataBuilder.getElement("summary_id").getText());
                                    dataObj.put("text", dataBuilder.getElement("text").getText());
                                    dataObj.put("title", dataBuilder.getElement("title").getText());
                                    JSONObject body = new JSONObject();
                                    body.put("name", templateName);
                                    body.put("data", dataObj);
                                    body.put("notification", dataObj);
                                    templateJson.put(PackageConstants.AppXMLConstants.APPLICATION, appId);
                                    templateJson.put("id", valueElement.getElement("id").getText());
                                    templateJson.put("body", body.toJSONString());
                                    templateJson.put("isPushNotification", Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                    templateJson.put("isSendNotification", Boolean.parseBoolean(valueElement.getElement("isSendNotification").getText()));
                                    templateJson.put("message", dataBuilder.getElement("text").getText());
                                    templateJson.put("subject", dataBuilder.getElement("title").getText());
                                    templateJson.put("name", templateName);
                                    templateJson.put("to", valueElement.getElement("id").getText());
                                    templateJson.put("type", Template.Type.PUSH_NOTIFICATION);
                                    templateJson.put("userWorkflow", FieldUtil.getAsProperties(userWorkflowContext));
                                    templateJson.put("workflow", FieldUtil.getAsProperties(workflowParamExp));
                                }
                                break;

                        }
                        actionContext.setTemplateJson(templateJson);
                    }

                }

            }
        }
        return actionContextList;
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueNameVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        SLAPolicyContext slaPolicy = new SLAPolicyContext();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            slaPolicy = constructSLAContextFromXML(builder, moduleBean);

            long slaPolicyId = addOrUpdateSLAPolicy(slaPolicy);
            if (slaPolicyId > 0) {
                uniqueNameVsComponentId.put(idVsData.getKey(), slaPolicyId);
            }
        }

        return uniqueNameVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SLAPolicyContext slaPolicy;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            if (ruleId == null || ruleId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            slaPolicy = constructSLAContextFromXML(builder, moduleBean);
            slaPolicy.setId(ruleId);

            addOrUpdateSLAPolicy(slaPolicy);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private long addOrUpdateSLAPolicy(SLAPolicyContext slaPolicy) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddSLAPolicyWithChildrenChain();
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(slaPolicy.getModuleId());

        WorkflowRuleContext existingStateTransition = WorkflowRuleAPI.getWorkflowRule(slaPolicy.getName(), module,
                slaPolicy.getRuleTypeEnum(), false);

        if (existingStateTransition != null) {
            slaPolicy.setId(existingStateTransition.getId());
        }

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY, slaPolicy);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        chain.execute();

        slaPolicy = (SLAPolicyContext) context.get(FacilioConstants.ContextNames.SLA_POLICY);
        return slaPolicy.getId();
    }

    private SLAPolicyContext constructSLAContextFromXML(XMLBuilder builder, ModuleBean moduleBean) throws Exception {

        SLAPolicyContext slapolicy = new SLAPolicyContext();
        String name = builder.getElement(PackageConstants.NAME).getText();
        String moduleName = builder.getElement(PackageConstants.MODULENAME).getText();
        FacilioModule module = moduleBean.getModule(moduleName);
        String activityTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE).getText();
        boolean status = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.STATUS).getText());
        String ruleTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).getText();
        String description = builder.getElement(PackageConstants.DESCRIPTION).getText();

        List<XMLBuilder> commitmentBuilders = builder.getElementList(PackageConstants.WorkFlowRuleConstants.COMMITMENTS);
        List<SLAWorkflowCommitmentRuleContext> commitments = new ArrayList<>();
        for (XMLBuilder commitmentBuilder : commitmentBuilders) {
            SLAWorkflowCommitmentRuleContext commitment = new SLAWorkflowCommitmentRuleContext();
            String commitmentName = commitmentBuilder.getElement(PackageConstants.NAME).getText();
            String commitmentActivityTypeStr = commitmentBuilder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE).getText();
            String ruleType = commitmentBuilder.getElement(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).getText();
            XMLBuilder criteriaElement = commitmentBuilder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
            if (criteriaElement != null) {
                Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                commitment.setCriteria(criteria);
            }

            XMLBuilder entitiesBuilder = commitmentBuilder.getElement(PackageConstants.WorkFlowRuleConstants.ENTITIES);
            List<XMLBuilder> entityBuilders = entitiesBuilder.getElementList("Entity");
            List<SLAWorkflowCommitmentRuleContext.SLAEntityDuration> entities = new ArrayList<>();
            for (XMLBuilder entityBuilder : entityBuilders) {
                SLAWorkflowCommitmentRuleContext.SLAEntityDuration entity = new SLAWorkflowCommitmentRuleContext.SLAEntityDuration();
                String slaEntityName = entityBuilder.getElement("SlaEntityName").getText();
                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(module.getModuleId(), slaEntityName);
                entity.setSlaEntityId(slaEntity.getId());
                int type = Integer.parseInt(entityBuilder.getElement("Type").getText());
                entity.setType(type);
                WorkflowRuleContext slaCommitmentRule = WorkflowRuleAPI.getWorkflowRule(commitmentName, module, WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE, false);
                entity.setDurationPlaceHolder(entityBuilder.getElement("DurationPlaceHolder").getText());
                entity.setSlaCommitmentId(slaCommitmentRule != null ? slaCommitmentRule.getId() : -1);
                entities.add(entity);
            }

            commitment.setName(commitmentName);
            EventType commitmentActivityType = StringUtils.isNotEmpty(commitmentActivityTypeStr) ? EventType.valueOf(commitmentActivityTypeStr) : null;
            commitment.setActivityType(commitmentActivityType);
            WorkflowRuleContext.RuleType commitmentRuleType = StringUtils.isNotEmpty(ruleType) ? WorkflowRuleContext.RuleType.valueOf(ruleType) : null;
            commitment.setRuleType(commitmentRuleType);
            commitment.setModuleName(moduleName);
            commitment.setSlaEntities(entities);
            commitments.add(commitment);
        }

        XMLBuilder escalationsBuilder = builder.getElement(PackageConstants.WorkFlowRuleConstants.ESCALATIONS);
        List<XMLBuilder> escalationBuilders = escalationsBuilder.getElementList("Escalation");
        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = new ArrayList<>();
        for (XMLBuilder escalationBuilder : escalationBuilders) {
            SLAPolicyContext.SLAPolicyEntityEscalationContext escalation = new SLAPolicyContext.SLAPolicyEntityEscalationContext();
            String slaEntityName = escalationBuilder.getElement("SlaEntityName").getText();
            SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(module.getModuleId(), slaEntityName);
            escalation.setSlaEntityId(slaEntity.getId());

            XMLBuilder levelsBuilder = escalationBuilder.getElement(PackageConstants.WorkFlowRuleConstants.LEVELS);
            List<XMLBuilder> levelBuilders = levelsBuilder.getElementList("Level");
            List<SLAWorkflowEscalationContext> levels = new ArrayList<>();
            for (XMLBuilder levelBuilder : levelBuilders) {
                SLAWorkflowEscalationContext level = new SLAWorkflowEscalationContext();
                long interval = Long.valueOf(levelBuilder.getElement("Interval").getText());
                level.setInterval(interval);
                int type = Integer.valueOf(levelBuilder.getElement("type").getText());
                level.setType(type);
                XMLBuilder actionsList = levelBuilder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST);
                if (actionsList != null) {
                    List<ActionContext> actionContextList = constructSLAActionContextsFromBuilder(actionsList);
                    String actionArray = null;
                    if (CollectionUtils.isNotEmpty(actionContextList)) {
                        try {
                            actionArray =  FieldUtil.getAsJSONArray(actionContextList, ActionContext.class).toJSONString();
                        } catch (Exception e) {}
                    }
                    level.setActionArray(actionArray);
                    level.setActions(actionContextList);
                }
                levels.add(level);
            }
            escalation.setLevels(levels);
            escalations.add(escalation);
        }

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null){
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            slapolicy.setCriteria(criteria);
        }

        slapolicy.setName(name);
        slapolicy.setModuleName(moduleName);
        slapolicy.setModule(module);
        EventType activityType = StringUtils.isNotEmpty(activityTypeStr) ? EventType.valueOf(activityTypeStr) : null;
        slapolicy.setActivityType(activityType);
        slapolicy.setStatus(status);
        WorkflowRuleContext.RuleType ruleType = StringUtils.isNotEmpty(ruleTypeStr) ? WorkflowRuleContext.RuleType.valueOf(ruleTypeStr) : null;
        slapolicy.setRuleType(ruleType);
        slapolicy.setDescription(description);
        slapolicy.setCommitments(commitments);
        slapolicy.setEscalations(escalations);
        return slapolicy;
    }

}
